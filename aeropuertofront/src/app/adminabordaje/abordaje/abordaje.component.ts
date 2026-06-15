import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { catchError, distinctUntilChanged, finalize, forkJoin, of, switchMap, tap } from 'rxjs';

import {
  AbordajePasajeroResponse,
  AbordajeService,
  AbordajeVueloResponse,
  VueloEscalaResponse
} from '../../services/abordaje/abordaje.service';

@Component({
  selector: 'app-abordaje',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './abordaje.component.html',
  styleUrls: ['./abordaje.component.css']
})
export class AbordajeComponent implements OnInit {

  vuelosProgramados: AbordajeVueloResponse[] = [];
  vuelos: AbordajeVueloResponse[] = [];

  vueloSeleccionado: AbordajeVueloResponse | null = null;
  pasajeroEncontrado: AbordajePasajeroResponse | null = null;

  formBusqueda!: FormGroup;
  formAbordaje!: FormGroup;

  cargandoVuelos = false;
  buscandoPasajero = false;
  abordandoPasajero = false;
  finalizandoAbordaje = false;
  iniciandoAbordajeId: number | null = null;

  mensajeError = '';
  mensajeExito = '';

  pasoActual = 1;

  constructor(
    private fb: FormBuilder,
    private abordajeService: AbordajeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormularios();
    this.configurarCambiosFormulario();
    this.cargarDatosIniciales();
  }

  get pesosMaletas(): FormArray {
    return this.formAbordaje.get('pesosMaletas') as FormArray;
  }

  inicializarFormularios(): void {
    this.formBusqueda = this.fb.group({
      noPasaporte: ['', [Validators.required]]
    });

    this.formAbordaje = this.fb.group({
      cantidadMaletasPresentadas: [
        null,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],
      pesosMaletas: this.fb.array([])
    });
  }

  generarCamposPeso(cantidad: number): void {
    this.pesosMaletas.clear();

    if (!cantidad || cantidad <= 0) {
      return;
    }

    for (let i = 0; i < cantidad; i++) {
      this.pesosMaletas.push(
        this.fb.control(null, [
          Validators.required,
          Validators.min(0.01),
          Validators.max(999.99)
        ])
      );
    }
  }

  configurarCambiosFormulario(): void {
    this.formBusqueda.get('noPasaporte')?.valueChanges
      .pipe(distinctUntilChanged())
      .subscribe(() => {
        this.pasajeroEncontrado = null;
        this.formAbordaje.reset();
        this.pesosMaletas.clear();
        this.limpiarMensajes();
        this.refrescarVista();
      });

    this.formAbordaje.get('cantidadMaletasPresentadas')?.valueChanges
      .pipe(distinctUntilChanged())
      .subscribe((cantidad) => {
        this.generarCamposPeso(Number(cantidad || 0));
        this.refrescarVista();
      });
  }

  cargarDatosIniciales(): void {
    this.cargandoVuelos = true;
    this.limpiarMensajes();

    this.cargarVuelosAbordaje()
      .pipe(
        finalize(() => {
          this.cargandoVuelos = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ programados, vuelos }) => {
          this.vuelosProgramados = [...programados];
          this.vuelos = [...vuelos];

          if (this.vuelosProgramados.length === 0 && this.vuelos.length === 0) {
            this.mensajeError = 'No hay vuelos disponibles';
          }
        },
        error: (error) => {
          this.vuelosProgramados = [];
          this.vuelos = [];
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  iniciarAbordaje(vuelo: AbordajeVueloResponse): void {
    this.limpiarMensajes();

    const confirmar = window.confirm(
      `¿Está seguro de iniciar el abordaje del vuelo ${vuelo.codigoVuelo}?`
    );

    if (!confirmar) {
      return;
    }

    this.iniciandoAbordajeId = vuelo.idVuelo;
    this.refrescarVista();

    this.abordajeService.iniciarAbordaje(vuelo.idVuelo)
      .pipe(
        switchMap(() => this.cargarVuelosAbordaje()),
        finalize(() => {
          this.iniciandoAbordajeId = null;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ programados, vuelos }) => {
          this.vuelosProgramados = [...programados];
          this.vuelos = [...vuelos];
          this.mensajeExito = `Se inició el abordaje del vuelo ${vuelo.codigoVuelo}.`;
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  seleccionarVuelo(vuelo: AbordajeVueloResponse): void {
    this.vueloSeleccionado = vuelo;
    this.pasajeroEncontrado = null;
    this.formBusqueda.reset();
    this.formAbordaje.reset();
    this.pesosMaletas.clear();
    this.limpiarMensajes();
    this.pasoActual = 2;
    this.refrescarVista();
  }

  nuevoVuelo(): void {
    this.vueloSeleccionado = null;
    this.pasajeroEncontrado = null;
    this.formBusqueda.reset();
    this.formAbordaje.reset();
    this.pesosMaletas.clear();
    this.limpiarMensajes();
    this.pasoActual = 1;
    this.cargarDatosIniciales();
  }

  buscarPasajero(): void {
    if (!this.vueloSeleccionado) {
      this.mensajeError = 'Debe seleccionar un vuelo';
      return;
    }

    if (this.formBusqueda.invalid) {
      this.formBusqueda.markAllAsTouched();
      this.mensajeError = 'Debe ingresar los campos obligatorios';
      return;
    }

    const noPasaporte = this.formBusqueda.value.noPasaporte?.trim();

    this.buscandoPasajero = true;
    this.pasajeroEncontrado = null;
    this.formAbordaje.reset();
    this.pesosMaletas.clear();
    this.limpiarMensajes();

    this.abordajeService.buscarPasajero(
      this.vueloSeleccionado.idVuelo,
      noPasaporte
    )
      .pipe(
        tap((pasajero) => {
          const cantidad = pasajero.cantidadMaletasReservadas ?? 0;

          this.formAbordaje.patchValue({
            cantidadMaletasPresentadas: cantidad
          });

          this.generarCamposPeso(cantidad);
        }),
        finalize(() => {
          this.buscandoPasajero = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (pasajero) => {
          this.pasajeroEncontrado = pasajero;
          this.mensajeExito = pasajero.mensaje || 'Pasajero encontrado';
          this.pasoActual = 3;
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  abordarPasajero(): void {
    if (!this.vueloSeleccionado) {
      this.mensajeError = 'Debe seleccionar un vuelo';
      return;
    }

    if (!this.pasajeroEncontrado) {
      this.mensajeError = 'Debe buscar un pasajero antes de abordar';
      return;
    }

    if (this.formAbordaje.invalid) {
      this.formAbordaje.markAllAsTouched();
      this.mensajeError = 'Debe ingresar la cantidad de maletas presentadas';
      return;
    }

    const cantidadMaletasPresentadas = Number(
      this.formAbordaje.value.cantidadMaletasPresentadas
    );

    const pesosMaletas = this.pesosMaletas.value.map((peso: any) => Number(peso));

    this.abordandoPasajero = true;
    this.limpiarMensajes();

    this.abordajeService.abordarPasajero({
      idVuelo: this.vueloSeleccionado.idVuelo,
      noPasaporte: this.pasajeroEncontrado.noPasaporte,
      cantidadMaletasPresentadas,
      pesosMaletas
    })
      .pipe(
        finalize(() => {
          this.abordandoPasajero = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (response) => {
          const nombrePasajero = response.nombrePasajero;

          this.formBusqueda.reset(
            { noPasaporte: '' },
            { emitEvent: false }
          );

          this.formAbordaje.reset(
            { cantidadMaletasPresentadas: null },
            { emitEvent: false }
          );

          this.pesosMaletas.clear();

          this.pasajeroEncontrado = null;
          this.pasoActual = 2;

          this.mensajeExito = `Pasajero ${nombrePasajero} abordado correctamente.`;

          this.refrescarVista();
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  finalizarAbordaje(): void {
    if (!this.vueloSeleccionado) {
      this.mensajeError = 'Debe seleccionar un vuelo';
      return;
    }

    const confirmar = window.confirm(
      `¿Está seguro de finalizar el abordaje del vuelo ${this.vueloSeleccionado.codigoVuelo}?`
    );

    if (!confirmar) {
      return;
    }

    this.finalizandoAbordaje = true;
    this.limpiarMensajes();

    this.abordajeService.finalizarAbordaje(this.vueloSeleccionado.idVuelo)
      .pipe(
        tap((response) => {
          this.mensajeExito = `${response.mensaje}. Boletos cancelados: ${response.boletosCancelados}`;
          this.vueloSeleccionado = null;
          this.pasajeroEncontrado = null;
          this.formBusqueda.reset();
          this.formAbordaje.reset();
          this.pesosMaletas.clear();
          this.pasoActual = 1;
        }),
        switchMap(() => this.cargarVuelosAbordaje()),
        finalize(() => {
          this.finalizandoAbordaje = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ programados, vuelos }) => {
          this.vuelosProgramados = [...programados];
          this.vuelos = [...vuelos];
        },
        error: (error) => {
          this.vuelosProgramados = [];
          this.vuelos = [];
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  obtenerRutaCompleta(vuelo: AbordajeVueloResponse | null): string {
    if (!vuelo) {
      return '';
    }

    if (vuelo.rutaCompleta && vuelo.rutaCompleta.trim().length > 0) {
      return vuelo.rutaCompleta;
    }

    return `${vuelo.origen} → ${vuelo.destino}`;
  }

  obtenerTextoEscalas(vuelo: AbordajeVueloResponse | null): string {
    if (!vuelo || !vuelo.cantidadEscalas || vuelo.cantidadEscalas === 0) {
      return 'Directo';
    }

    return `${vuelo.cantidadEscalas} escala(s)`;
  }

  obtenerEscalas(vuelo: AbordajeVueloResponse | null): VueloEscalaResponse[] {
    if (!vuelo || !vuelo.escalas) {
      return [];
    }

    return [...vuelo.escalas].sort((a, b) => a.orden - b.orden);
  }

  tieneEscalas(vuelo: AbordajeVueloResponse | null): boolean {
    return this.obtenerEscalas(vuelo).length > 0;
  }

  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  obtenerMensajeError(error: any): string {
    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.message) {
      return error.message;
    }

    return 'Ocurrió un error al procesar la solicitud';
  }

  private cargarVuelosAbordaje() {
    return forkJoin({
      programados: this.abordajeService.listarVuelosProgramadosProximos().pipe(
        catchError(() => of([] as AbordajeVueloResponse[]))
      ),
      vuelos: this.abordajeService.listarVuelosParaAbordaje().pipe(
        catchError(() => of([] as AbordajeVueloResponse[]))
      )
    });
  }

  refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

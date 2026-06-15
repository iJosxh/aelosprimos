import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { forkJoin } from 'rxjs';
import { distinctUntilChanged, filter, finalize, switchMap, tap } from 'rxjs/operators';

import {
  AerolineaResponse,
  AeropuertoResponse,
  AvionDisponibleResponse,
  TripulacionDisponibleResponse,
  VueloEscalaRequest,
  VueloRequest,
  VueloResponse,
  VueloService
} from '../../services/vuelo/vuelo.service';

@Component({
  selector: 'app-crear-vuelo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './crear-vuelo.component.html',
  styleUrls: ['./crear-vuelo.component.css']
})
export class CrearVueloComponent implements OnInit {

  pasoActual = 1;

  formVuelo!: FormGroup;

  aerolineas: AerolineaResponse[] = [];
  aeropuertos: AeropuertoResponse[] = [];
  avionesDisponibles: AvionDisponibleResponse[] = [];
  tripulacionesDisponibles: TripulacionDisponibleResponse[] = [];
  vuelos: VueloResponse[] = [];

  avisoAvionesActivos = '';
  validandoAvionesActivos = false;
  aerolineaSinAvionesActivos = false;

  avisoAeropuertosAutorizados = '';
  validandoAeropuertosAutorizados = false;
  aerolineaSinAeropuertosAutorizados = false;

  avisoFechaSalida = '';
  fechaSalidaInvalidaPorTiempo = false;

  mensajeError = '';
  mensajeExito = '';
  cargando = false;

  constructor(
    private fb: FormBuilder,
    private vueloService: VueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.crearFormulario();
    this.cargarPantallaInicial();
    this.escucharCambioAerolinea();
    this.escucharCambioFechaSalida();
  }

  get escalasFormArray(): FormArray {
    return this.formVuelo.get('escalas') as FormArray;
  }

  crearFormulario(): void {
    this.formVuelo = this.fb.group({
      aerolineaId: [null, Validators.required],
      aeropuertoOrigenId: [null, Validators.required],
      aeropuertoDestinoId: [null, Validators.required],
      fechaSalida: ['', Validators.required],
      fechaLlegada: ['', Validators.required],

      escalas: this.fb.array([]),

      avionId: [null, Validators.required],
      tripulacionId: [null, Validators.required],
      precioEconomica: [null, [Validators.required, Validators.min(1)]],
      precioEjecutiva: [null, [Validators.required, Validators.min(1)]]
    });
  }

  crearGrupoEscala(orden: number): FormGroup {
    return this.fb.group({
      aeropuertoId: [null, Validators.required],
      orden: [orden, Validators.required],
      fechaLlegada: ['', Validators.required],
      fechaSalida: ['', Validators.required]
    });
  }

  agregarEscala(): void {
    this.limpiarMensajes();

    if (this.escalasFormArray.length >= 2) {
      this.mensajeError = 'Los vuelos no pueden tener más de 2 escalas.';
      this.refrescarVista();
      return;
    }

    if (!this.formVuelo.get('aerolineaId')?.value) {
      this.mensajeError = 'Debe seleccionar una aerolínea antes de agregar escalas.';
      this.refrescarVista();
      return;
    }

    this.escalasFormArray.push(this.crearGrupoEscala(this.escalasFormArray.length + 1));
    this.refrescarVista();
  }

  quitarEscala(index: number): void {
    this.escalasFormArray.removeAt(index);
    this.reordenarEscalas();
    this.limpiarMensajes();
    this.refrescarVista();
  }

  limpiarEscalas(): void {
    while (this.escalasFormArray.length > 0) {
      this.escalasFormArray.removeAt(0);
    }

    this.refrescarVista();
  }

  reordenarEscalas(): void {
    this.escalasFormArray.controls.forEach((control, index) => {
      control.get('orden')?.setValue(index + 1);
    });
  }

  cargarPantallaInicial(): void {
    this.cargando = true;

    forkJoin({
      aerolineas: this.vueloService.listarAerolineas(),
      aeropuertos: this.vueloService.listarAeropuertos(),
      vuelos: this.vueloService.listarVuelos()
    })
      .pipe(
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ aerolineas, aeropuertos, vuelos }) => {
          this.aerolineas = [...aerolineas];
          this.aeropuertos = [...aeropuertos];
          this.vuelos = [...vuelos];

          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  validarTiempoMinimoSalida(fechaSalida: string): boolean {
    this.avisoFechaSalida = '';
    this.fechaSalidaInvalidaPorTiempo = false;

    if (!fechaSalida) {
      return true;
    }

    const salida = new Date(fechaSalida);

    const fechaMinima = new Date();
    fechaMinima.setHours(fechaMinima.getHours() + 5);

    if (salida.getTime() < fechaMinima.getTime()) {
      this.fechaSalidaInvalidaPorTiempo = true;
      this.avisoFechaSalida = 'Tiempo mínimo para la preparación 5 horas a partir de la hora actual.';
      this.refrescarVista();
      return false;
    }

    this.refrescarVista();
    return true;
  }

  escucharCambioFechaSalida(): void {
    this.formVuelo.get('fechaSalida')?.valueChanges.subscribe(fechaSalida => {
      this.avisoFechaSalida = '';
      this.fechaSalidaInvalidaPorTiempo = false;

      if (fechaSalida) {
        this.validarTiempoMinimoSalida(fechaSalida);
      }

      this.refrescarVista();
    });
  }

  escucharCambioAerolinea(): void {
    this.formVuelo.get('aerolineaId')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.avisoAvionesActivos = '';
          this.avisoAeropuertosAutorizados = '';

          this.aerolineaSinAvionesActivos = false;
          this.aerolineaSinAeropuertosAutorizados = false;

          this.formVuelo.patchValue({
            aeropuertoOrigenId: null,
            aeropuertoDestinoId: null,
            avionId: null,
            tripulacionId: null
          });

          this.limpiarEscalas();

          this.aeropuertos = [];
          this.avionesDisponibles = [];
          this.tripulacionesDisponibles = [];

          this.refrescarVista();
        }),
        filter(aerolineaId => !!aerolineaId),
        tap(() => {
          this.validandoAvionesActivos = true;
          this.validandoAeropuertosAutorizados = true;
          this.refrescarVista();
        }),
        switchMap(aerolineaId =>
          forkJoin({
            aviones: this.vueloService.validarAvionesActivos(Number(aerolineaId)),
            aeropuertosValidacion: this.vueloService.validarAeropuertosAutorizados(Number(aerolineaId)),
            aeropuertos: this.vueloService.listarAeropuertosAutorizados(Number(aerolineaId))
          }).pipe(
            finalize(() => {
              this.validandoAvionesActivos = false;
              this.validandoAeropuertosAutorizados = false;
              this.refrescarVista();
            })
          )
        )
      )
      .subscribe({
        next: ({ aviones, aeropuertosValidacion, aeropuertos }) => {
          this.aerolineaSinAvionesActivos = !aviones.tieneAvionesActivos;
          this.avisoAvionesActivos = this.aerolineaSinAvionesActivos
            ? 'No se puede crear un vuelo porque la aerolínea no cuenta con aviones disponibles.'
            : '';

          this.aerolineaSinAeropuertosAutorizados = !aeropuertosValidacion.tieneAeropuertosAutorizados;
          this.avisoAeropuertosAutorizados = this.aerolineaSinAeropuertosAutorizados
            ? 'No se encontraron aeropuertos autorizados para la aerolínea.'
            : '';

          this.aeropuertos = [...aeropuertos];

          this.refrescarVista();
        },
        error: err => {
          this.aerolineaSinAeropuertosAutorizados = true;
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  siguientePaso(): void {
    this.limpiarMensajes();

    const aerolineaId = this.formVuelo.get('aerolineaId')?.value;
    const aeropuertoOrigenId = this.formVuelo.get('aeropuertoOrigenId')?.value;
    const aeropuertoDestinoId = this.formVuelo.get('aeropuertoDestinoId')?.value;
    const fechaSalida = this.formVuelo.get('fechaSalida')?.value;
    const fechaLlegada = this.formVuelo.get('fechaLlegada')?.value;

    if (!aerolineaId || !aeropuertoOrigenId || !aeropuertoDestinoId || !fechaSalida || !fechaLlegada) {
      this.mensajeError = 'Debe ingresar los campos obligatorios.';
      this.refrescarVista();
      return;
    }

    if (!this.validarTiempoMinimoSalida(fechaSalida)) {
      return;
    }

    if (this.aerolineaSinAvionesActivos) {
      this.avisoAvionesActivos = 'No se puede crear un vuelo porque la aerolínea no cuenta con aviones disponibles.';
      this.refrescarVista();
      return;
    }

    if (this.aerolineaSinAeropuertosAutorizados) {
      this.avisoAeropuertosAutorizados = 'No se encontraron aeropuertos autorizados para la aerolínea.';
      this.refrescarVista();
      return;
    }

    if (Number(aeropuertoOrigenId) === Number(aeropuertoDestinoId)) {
      this.mensajeError = 'No se puede seleccionar el mismo aeropuerto de salida y llegada.';
      this.refrescarVista();
      return;
    }

    if (new Date(fechaLlegada) <= new Date(fechaSalida)) {
      this.mensajeError = 'La fecha y hora de llegada debe ser mayor a la fecha y hora de salida.';
      this.refrescarVista();
      return;
    }

    if (!this.validarEscalas()) {
      return;
    }

    this.cargando = true;
    this.refrescarVista();

    forkJoin({
      aviones: this.vueloService.obtenerAvionesDisponibles(
        Number(aerolineaId),
        fechaSalida,
        fechaLlegada
      ),
      tripulaciones: this.vueloService.obtenerTripulacionesDisponibles(
        Number(aerolineaId),
        fechaSalida,
        fechaLlegada
      )
    })
      .pipe(
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ aviones, tripulaciones }) => {
          this.avionesDisponibles = [...aviones];
          this.tripulacionesDisponibles = [...tripulaciones];

          this.formVuelo.patchValue({
            avionId: null,
            tripulacionId: null,
            precioEconomica: null,
            precioEjecutiva: null
          });

          if (this.avionesDisponibles.length === 0) {
            this.mensajeError = 'No hay aviones disponibles para el vuelo seleccionado.';
            this.refrescarVista();
            return;
          }

          if (this.tripulacionesDisponibles.length === 0) {
            this.mensajeError = 'No hay tripulaciones disponibles para el vuelo seleccionado.';
            this.refrescarVista();
            return;
          }

          this.pasoActual = 2;
          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  validarEscalas(): boolean {
    const aeropuertoOrigenId = Number(this.formVuelo.get('aeropuertoOrigenId')?.value);
    const aeropuertoDestinoId = Number(this.formVuelo.get('aeropuertoDestinoId')?.value);
    const fechaSalidaVuelo = this.formVuelo.get('fechaSalida')?.value;
    const fechaLlegadaVuelo = this.formVuelo.get('fechaLlegada')?.value;

    if (this.escalasFormArray.length > 2) {
      this.mensajeError = 'Los vuelos no pueden tener más de 2 escalas.';
      this.refrescarVista();
      return false;
    }

    if (this.escalasFormArray.length === 0) {
      return true;
    }

    const aeropuertosEscala = new Set<number>();
    let fechaAnterior = new Date(fechaSalidaVuelo);

    for (let i = 0; i < this.escalasFormArray.length; i++) {
      const escala = this.escalasFormArray.at(i) as FormGroup;

      const aeropuertoId = escala.get('aeropuertoId')?.value;
      const fechaLlegada = escala.get('fechaLlegada')?.value;
      const fechaSalida = escala.get('fechaSalida')?.value;

      if (!aeropuertoId || !fechaLlegada || !fechaSalida) {
        this.mensajeError = 'Debe ingresar los campos obligatorios.';
        this.refrescarVista();
        return false;
      }

      const aeropuertoEscalaId = Number(aeropuertoId);

      if (aeropuertoEscalaId === aeropuertoOrigenId) {
        this.mensajeError = 'La escala no puede ser igual al aeropuerto de salida.';
        this.refrescarVista();
        return false;
      }

      if (aeropuertoEscalaId === aeropuertoDestinoId) {
        this.mensajeError = 'La escala no puede ser igual al aeropuerto de llegada.';
        this.refrescarVista();
        return false;
      }

      if (aeropuertosEscala.has(aeropuertoEscalaId)) {
        this.mensajeError = 'No puede repetir el mismo aeropuerto de escala.';
        this.refrescarVista();
        return false;
      }

      aeropuertosEscala.add(aeropuertoEscalaId);

      const llegadaEscala = new Date(fechaLlegada);
      const salidaEscala = new Date(fechaSalida);

      if (llegadaEscala <= fechaAnterior) {
        this.mensajeError = 'La fecha y hora de llegada de la escala debe ser mayor a la fecha y hora anterior.';
        this.refrescarVista();
        return false;
      }

      if (salidaEscala <= llegadaEscala) {
        this.mensajeError = 'La fecha y hora de salida de la escala debe ser mayor a la fecha y hora de llegada de la escala.';
        this.refrescarVista();
        return false;
      }

      fechaAnterior = salidaEscala;
    }

    if (new Date(fechaLlegadaVuelo) <= fechaAnterior) {
      this.mensajeError = 'La fecha y hora de llegada debe ser mayor a la fecha y hora de la última escala.';
      this.refrescarVista();
      return false;
    }

    return true;
  }

  volverPasoUno(): void {
    this.pasoActual = 1;

    this.formVuelo.patchValue({
      avionId: null,
      tripulacionId: null,
      precioEconomica: null,
      precioEjecutiva: null
    });

    this.avionesDisponibles = [];
    this.tripulacionesDisponibles = [];
    this.limpiarMensajes();

    this.refrescarVista();
  }

  guardarVuelo(): void {
    this.limpiarMensajes();

    if (this.formVuelo.invalid) {
      this.mensajeError = 'Debe ingresar los campos obligatorios.';
      this.formVuelo.markAllAsTouched();
      this.refrescarVista();
      return;
    }

    if (!this.validarEscalas()) {
      return;
    }

    const request: VueloRequest = {
      aerolineaId: Number(this.formVuelo.value.aerolineaId),
      avionId: Number(this.formVuelo.value.avionId),
      tripulacionId: Number(this.formVuelo.value.tripulacionId),
      aeropuertoOrigenId: Number(this.formVuelo.value.aeropuertoOrigenId),
      aeropuertoDestinoId: Number(this.formVuelo.value.aeropuertoDestinoId),
      fechaSalida: this.formVuelo.value.fechaSalida,
      fechaLlegada: this.formVuelo.value.fechaLlegada,
      precioEconomica: Number(this.formVuelo.value.precioEconomica),
      precioEjecutiva: Number(this.formVuelo.value.precioEjecutiva),
      escalas: this.construirEscalasRequest()
    };

    this.cargando = true;
    this.refrescarVista();

    this.vueloService.crearVuelo(request)
      .pipe(
        switchMap((response: VueloResponse) => {
          this.mensajeExito = `Vuelo ${response.codigoVuelo} creado correctamente.`;

          this.formVuelo.reset();
          this.limpiarEscalas();

          this.avisoFechaSalida = '';
          this.fechaSalidaInvalidaPorTiempo = false;
          this.avisoAvionesActivos = '';
          this.avisoAeropuertosAutorizados = '';
          this.aerolineaSinAvionesActivos = false;
          this.aerolineaSinAeropuertosAutorizados = false;

          this.pasoActual = 1;
          this.avionesDisponibles = [];
          this.tripulacionesDisponibles = [];
          this.aeropuertos = [];

          return this.vueloService.listarVuelos();
        }),
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: vuelosActualizados => {
          this.vuelos = [...vuelosActualizados];
          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  construirEscalasRequest(): VueloEscalaRequest[] {
    return this.escalasFormArray.controls.map((control, index) => {
      const escala = control as FormGroup;

      return {
        aeropuertoId: Number(escala.get('aeropuertoId')?.value),
        orden: index + 1,
        fechaLlegada: escala.get('fechaLlegada')?.value,
        fechaSalida: escala.get('fechaSalida')?.value
      };
    });
  }

  limpiarFormulario(): void {
    this.formVuelo.reset();
    this.limpiarEscalas();

    this.pasoActual = 1;
    this.avionesDisponibles = [];
    this.tripulacionesDisponibles = [];
    this.aeropuertos = [];

    this.avisoFechaSalida = '';
    this.fechaSalidaInvalidaPorTiempo = false;
    this.avisoAvionesActivos = '';
    this.avisoAeropuertosAutorizados = '';
    this.aerolineaSinAvionesActivos = false;
    this.aerolineaSinAeropuertosAutorizados = false;

    this.limpiarMensajes();

    this.refrescarVista();
  }

  listarVuelos(): void {
    this.vueloService.listarVuelos().subscribe({
      next: data => {
        this.vuelos = [...data];
        this.refrescarVista();
      },
      error: err => {
        this.mensajeError = this.obtenerMensajeError(err);
        this.refrescarVista();
      }
    });
  }

  obtenerNombreAeropuerto(idAeropuerto: number | null | undefined): string {
    if (!idAeropuerto) {
      return 'No seleccionado';
    }

    const aeropuerto = this.aeropuertos.find(a => Number(a.idAeropuerto) === Number(idAeropuerto));

    if (!aeropuerto) {
      return 'No seleccionado';
    }

    return `${aeropuerto.nombre} - ${aeropuerto.ciudad}, ${aeropuerto.pais}`;
  }

  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  obtenerMensajeError(err: any): string {
    if (typeof err.error === 'string') {
      return err.error;
    }

    if (err.error?.message) {
      return err.error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  private refrescarVista(): void {
    setTimeout(() => {
      this.cdr.detectChanges();
    });
  }
}

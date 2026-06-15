import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { forkJoin } from 'rxjs';
import { distinctUntilChanged, finalize, switchMap, tap } from 'rxjs/operators';

import {
  AeropuertoReservaResponse,
  AsientoDisponibleResponse,
  ClaseVueloResponse,
  PaseAbordarResponse,
  ReservaVueloRequest,
  ReservaVueloResponse,
  ReservaVueloService,
  VueloDetalleReservaResponse,
  VueloDisponibleReservaResponse,
  VueloEscalaResponse
} from '../../services/reserva-vuelo/reserva-vuelo.service';

@Component({
  selector: 'app-reservar-vuelo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reservar-vuelo.component.html',
  styleUrls: ['./reservar-vuelo.component.css']
})
export class ReservarVueloComponent implements OnInit {

  pasoActual = 1;

  formBusqueda!: FormGroup;
  formReserva!: FormGroup;

  aeropuertos: AeropuertoReservaResponse[] = [];
  clasesVuelo: ClaseVueloResponse[] = [];
  vuelosDisponibles: VueloDisponibleReservaResponse[] = [];
  asientosDisponibles: AsientoDisponibleResponse[] = [];

  vueloSeleccionado: VueloDisponibleReservaResponse | null = null;
  detalleVuelo: VueloDetalleReservaResponse | null = null;
  reservaCreada: ReservaVueloResponse | null = null;
  paseAbordar: PaseAbordarResponse | null = null;

  mensajeError = '';
  mensajeExito = '';

  cargando = false;
  buscandoVuelos = false;
  cargandoDetalle = false;
  reservando = false;

  fechaMinima = this.obtenerFechaActual();

  constructor(
    private fb: FormBuilder,
    private reservaVueloService: ReservaVueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.crearFormularios();
    this.cargarPantallaInicial();
    this.escucharCambioClaseVuelo();
  }

  crearFormularios(): void {
    this.formBusqueda = this.fb.group({
      aeropuertoOrigenId: [null, Validators.required],
      aeropuertoDestinoId: [null, Validators.required],
      fechaSalida: ['', Validators.required]
    });

    this.formReserva = this.fb.group({
      idVuelo: [null, Validators.required],
      idAsiento: [null, Validators.required],
      codigoClaseVuelo: [null, Validators.required],
      cantidadMaletas: [0, [Validators.required, Validators.min(0)]],
      precioSeleccionado: [{ value: null, disabled: true }]
    });
  }

  cargarPantallaInicial(): void {
    this.cargando = true;
    this.limpiarMensajes();

    forkJoin({
      aeropuertos: this.reservaVueloService.listarAeropuertos(),
      clases: this.reservaVueloService.listarClasesVuelo()
    })
      .pipe(
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ aeropuertos, clases }) => {
          this.aeropuertos = [...aeropuertos];
          this.clasesVuelo = [...clases];

          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  escucharCambioClaseVuelo(): void {
    this.formReserva.get('codigoClaseVuelo')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.actualizarPrecioSeleccionado();
          this.refrescarVista();
        })
      )
      .subscribe();
  }

  buscarVuelos(): void {
    this.limpiarMensajes();

    const aeropuertoOrigenId = this.formBusqueda.get('aeropuertoOrigenId')?.value;
    const aeropuertoDestinoId = this.formBusqueda.get('aeropuertoDestinoId')?.value;
    const fechaSalida = this.formBusqueda.get('fechaSalida')?.value;

    if (!aeropuertoOrigenId || !aeropuertoDestinoId || !fechaSalida) {
      this.mensajeError = 'Debe ingresar los campos obligatorios.';
      this.formBusqueda.markAllAsTouched();
      this.refrescarVista();
      return;
    }

    if (fechaSalida < this.fechaMinima) {
      this.vuelosDisponibles = [];
      this.vueloSeleccionado = null;
      this.detalleVuelo = null;
      this.asientosDisponibles = [];
      this.reservaCreada = null;
      this.paseAbordar = null;

      this.mensajeError = 'No se encontraron vuelos según los parámetros ingresados.';
      this.refrescarVista();
      return;
    }

    if (Number(aeropuertoOrigenId) === Number(aeropuertoDestinoId)) {
      this.mensajeError = 'No se puede seleccionar el mismo aeropuerto de salida y llegada.';
      this.refrescarVista();
      return;
    }

    this.buscandoVuelos = true;

    this.vuelosDisponibles = [];
    this.vueloSeleccionado = null;
    this.detalleVuelo = null;
    this.asientosDisponibles = [];
    this.reservaCreada = null;
    this.paseAbordar = null;

    this.formReserva.reset({
      idVuelo: null,
      idAsiento: null,
      codigoClaseVuelo: null,
      cantidadMaletas: 0,
      precioSeleccionado: null
    });

    this.reservaVueloService.buscarVuelosDisponibles(
      Number(aeropuertoOrigenId),
      Number(aeropuertoDestinoId),
      fechaSalida
    )
      .pipe(
        finalize(() => {
          this.buscandoVuelos = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: vuelos => {
          this.vuelosDisponibles = [...vuelos];

          if (this.vuelosDisponibles.length === 0) {
            this.mensajeError = 'No se encontraron vuelos según los parámetros ingresados.';
          }

          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  verDetalleVuelo(vuelo: VueloDisponibleReservaResponse): void {
    this.limpiarMensajes();
    this.cargandoDetalle = true;

    this.reservaVueloService.obtenerDetalleVuelo(vuelo.idVuelo)
      .pipe(
        finalize(() => {
          this.cargandoDetalle = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: detalle => {
          this.detalleVuelo = detalle;
          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  seleccionarVuelo(vuelo: VueloDisponibleReservaResponse): void {
    this.limpiarMensajes();

    this.vueloSeleccionado = vuelo;
    this.detalleVuelo = null;
    this.asientosDisponibles = [];
    this.reservaCreada = null;
    this.paseAbordar = null;

    this.formReserva.patchValue({
      idVuelo: vuelo.idVuelo,
      idAsiento: null,
      codigoClaseVuelo: null,
      cantidadMaletas: 0,
      precioSeleccionado: null
    });

    this.cargarDetalleYAsientos(vuelo.idVuelo);
  }

  cargarDetalleYAsientos(idVuelo: number): void {
    this.cargandoDetalle = true;

    forkJoin({
      detalle: this.reservaVueloService.obtenerDetalleVuelo(idVuelo),
      asientos: this.reservaVueloService.listarAsientosDisponibles(idVuelo)
    })
      .pipe(
        finalize(() => {
          this.cargandoDetalle = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ detalle, asientos }) => {
          this.detalleVuelo = detalle;
          this.asientosDisponibles = [...asientos];

          if (this.asientosDisponibles.length === 0) {
            this.mensajeError = 'No hay asientos disponibles para este vuelo.';
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

  reservarVuelo(): void {
    this.limpiarMensajes();

    if (this.formReserva.invalid || !this.vueloSeleccionado) {
      this.mensajeError = 'Debe ingresar los campos obligatorios.';
      this.formReserva.markAllAsTouched();
      this.refrescarVista();
      return;
    }

    const confirmado = confirm('¿Está seguro de continuar?');

    if (!confirmado) {
      this.mensajeExito = 'Se ha cancelado el registro satisfactoriamente.';
      this.refrescarVista();
      return;
    }

    const request: ReservaVueloRequest = {
      idVuelo: Number(this.formReserva.get('idVuelo')?.value),
      idAsiento: Number(this.formReserva.get('idAsiento')?.value),
      codigoClaseVuelo: String(this.formReserva.get('codigoClaseVuelo')?.value),
      cantidadMaletas: Number(this.formReserva.get('cantidadMaletas')?.value)
    };

    this.reservando = true;
    this.refrescarVista();

    this.reservaVueloService.reservarVuelo(request)
      .pipe(
        tap((reserva: ReservaVueloResponse) => {
          this.reservaCreada = reserva;
          this.mensajeExito = 'Se ha creado con éxito la reserva.';
        }),
        switchMap((reserva: ReservaVueloResponse) =>
          this.reservaVueloService.obtenerPaseAbordar(reserva.idReserva)
        ),
        finalize(() => {
          this.reservando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: pase => {
          this.paseAbordar = pase;
          this.pasoActual = 3;
          this.refrescarVista();
        },
        error: err => {
          this.mensajeError = this.obtenerMensajeError(err);
          this.refrescarVista();
        }
      });
  }

  volverABusqueda(): void {
    this.pasoActual = 1;

    this.vueloSeleccionado = null;
    this.detalleVuelo = null;
    this.asientosDisponibles = [];
    this.reservaCreada = null;
    this.paseAbordar = null;

    this.formReserva.reset({
      idVuelo: null,
      idAsiento: null,
      codigoClaseVuelo: null,
      cantidadMaletas: 0,
      precioSeleccionado: null
    });

    this.limpiarMensajes();
    this.refrescarVista();
  }

  nuevaReserva(): void {
    this.pasoActual = 1;

    this.formBusqueda.reset({
      aeropuertoOrigenId: null,
      aeropuertoDestinoId: null,
      fechaSalida: ''
    });

    this.formReserva.reset({
      idVuelo: null,
      idAsiento: null,
      codigoClaseVuelo: null,
      cantidadMaletas: 0,
      precioSeleccionado: null
    });

    this.vuelosDisponibles = [];
    this.asientosDisponibles = [];
    this.vueloSeleccionado = null;
    this.detalleVuelo = null;
    this.reservaCreada = null;
    this.paseAbordar = null;

    this.limpiarMensajes();
    this.refrescarVista();
  }

  limpiarBusqueda(): void {
    this.formBusqueda.reset({
      aeropuertoOrigenId: null,
      aeropuertoDestinoId: null,
      fechaSalida: ''
    });

    this.formReserva.reset({
      idVuelo: null,
      idAsiento: null,
      codigoClaseVuelo: null,
      cantidadMaletas: 0,
      precioSeleccionado: null
    });

    this.vuelosDisponibles = [];
    this.asientosDisponibles = [];
    this.vueloSeleccionado = null;
    this.detalleVuelo = null;
    this.reservaCreada = null;
    this.paseAbordar = null;

    this.limpiarMensajes();
    this.refrescarVista();
  }

  actualizarPrecioSeleccionado(): void {
    if (!this.detalleVuelo) {
      this.formReserva.patchValue(
        { precioSeleccionado: null },
        { emitEvent: false }
      );
      return;
    }

    const codigoClaseVuelo = this.formReserva.get('codigoClaseVuelo')?.value;

    let precioSeleccionado: number | null = null;

    if (codigoClaseVuelo === 'ECONOMICA') {
      precioSeleccionado = this.detalleVuelo.precioEconomica;
    }

    if (codigoClaseVuelo === 'EJECUTIVA') {
      precioSeleccionado = this.detalleVuelo.precioEjecutiva;
    }

    this.formReserva.patchValue(
      { precioSeleccionado },
      { emitEvent: false }
    );
  }

  obtenerRutaCompleta(
    vuelo: VueloDisponibleReservaResponse | VueloDetalleReservaResponse | null
  ): string {
    if (!vuelo) {
      return '';
    }

    if (vuelo.rutaCompleta && vuelo.rutaCompleta.trim().length > 0) {
      return vuelo.rutaCompleta;
    }

    return `${vuelo.aeropuertoOrigen} → ${vuelo.aeropuertoDestino}`;
  }

  obtenerTextoEscalas(
    vuelo: VueloDisponibleReservaResponse | VueloDetalleReservaResponse | null
  ): string {
    if (!vuelo || !vuelo.cantidadEscalas || vuelo.cantidadEscalas === 0) {
      return 'Directo';
    }

    return `${vuelo.cantidadEscalas} escala(s)`;
  }

  obtenerEscalas(
    vuelo: VueloDisponibleReservaResponse | VueloDetalleReservaResponse | null
  ): VueloEscalaResponse[] {
    if (!vuelo || !vuelo.escalas) {
      return [];
    }

    return [...vuelo.escalas].sort((a, b) => a.orden - b.orden);
  }

  tieneEscalas(
    vuelo: VueloDisponibleReservaResponse | VueloDetalleReservaResponse | null
  ): boolean {
    return this.obtenerEscalas(vuelo).length > 0;
  }

  campoInvalido(formulario: FormGroup, campo: string): boolean {
    const control = formulario.get(campo);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  obtenerMensajeError(err: any): string {
    if (typeof err.error === 'string') {
      return err.error;
    }

    if (err.error?.mensaje) {
      return err.error.mensaje;
    }

    if (err.error?.message) {
      return err.error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  private obtenerFechaActual(): string {
    const hoy = new Date();
    const anio = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const dia = String(hoy.getDate()).padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }

  private refrescarVista(): void {
    setTimeout(() => {
      this.cdr.detectChanges();
    });
  }
}

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';

import {
  AerolineaResponse,
  CrearTripulacionRequest,
  TripulacionResponse,
  TripulacionService,
  TripulanteResponse
} from '../../services/tripulacion/tripulacion.service';

@Component({
  selector: 'app-registrar-tripulacion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-tripulacion.component.html',
  styleUrl: './registrar-tripulacion.component.css'
})
export class RegistrarTripulacionComponent implements OnInit {

  aerolineas: AerolineaResponse[] = [];

  idAerolinea: number | null = null;
  nombreEquipo = '';

  idPiloto: number | null = null;
  idCopiloto: number | null = null;
  idIngenieroVuelo: number | null = null;
  idTripulantesCabina: number[] = [];

  pilotos: TripulanteResponse[] = [];
  copilotos: TripulanteResponse[] = [];
  ingenieros: TripulanteResponse[] = [];
  cabina: TripulanteResponse[] = [];

  tripulaciones: TripulacionResponse[] = [];

  mensaje = '';
  error = '';
  cargando = false;
  cargandoTripulantes = false;

  constructor(
    private tripulacionService: TripulacionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.listarAerolineas();
    this.listarTripulaciones();
  }

  listarAerolineas(): void {
    this.tripulacionService.listarAerolineas().subscribe({
      next: data => {
        this.aerolineas = data;
        this.cdr.detectChanges();
      },
      error: () => this.error = 'No se pudieron cargar las aerolíneas.'
    });
  }

  cargarTripulantes(): void {
    this.limpiarSeleccionTripulantes();

    if (!this.idAerolinea) {
      this.error = 'Debe seleccionar una aerolínea.';
      return;
    }

    this.error = '';
    this.mensaje = '';
    this.cargandoTripulantes = true;

    forkJoin({
      pilotos: this.tripulacionService.listarDisponibles(this.idAerolinea, 'PILOTO'),
      copilotos: this.tripulacionService.listarDisponibles(this.idAerolinea, 'COPILOTO'),
      ingenieros: this.tripulacionService.listarDisponibles(this.idAerolinea, 'INGENIERO_VUELO'),
      cabina: this.tripulacionService.listarDisponibles(this.idAerolinea, 'TRIPULANTE_CABINA')
    })
      .pipe(finalize(() => {
        this.cargandoTripulantes = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: data => {
          this.pilotos = data.pilotos;
          this.copilotos = data.copilotos;
          this.ingenieros = data.ingenieros;
          this.cabina = data.cabina;
        },
        error: () => {
          this.error = 'No se pudieron cargar los tripulantes.';
        }
      });
  }

  toggleCabina(idTripulante: number, checked: boolean): void {
    if (checked) {
      if (this.idTripulantesCabina.length >= 3) {
        this.error = 'Solo puede seleccionar tres tripulantes de cabina.';
        return;
      }

      this.idTripulantesCabina.push(idTripulante);
      this.error = '';
      return;
    }

    this.idTripulantesCabina = this.idTripulantesCabina.filter(id => id !== idTripulante);
  }

  guardar(): void {
    this.mensaje = '';
    this.error = '';

    if (!this.idAerolinea || !this.idPiloto || !this.idCopiloto || !this.idIngenieroVuelo) {
      this.error = 'Debe ingresar los campos obligatorios.';
      return;
    }

    if (this.idTripulantesCabina.length !== 3) {
      this.error = 'Debe seleccionar exactamente tres tripulantes de cabina.';
      return;
    }

    const request: CrearTripulacionRequest = {
      idAerolinea: Number(this.idAerolinea),
      nombreEquipo: this.nombreEquipo || 'Equipo de tripulación',
      idPiloto: Number(this.idPiloto),
      idCopiloto: Number(this.idCopiloto),
      idIngenieroVuelo: Number(this.idIngenieroVuelo),
      idTripulantesCabina: this.idTripulantesCabina.map(id => Number(id))
    };

    this.cargando = true;

    this.tripulacionService.crearTripulacion(request)
      .pipe(finalize(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.mensaje = 'Se creó con éxito la tripulación.';
          this.limpiarFormulario();
          this.listarTripulaciones();
        },
        error: (err) => {
          this.error = err.error?.message || 'No se pudo crear la tripulación.';
        }
      });
  }

  listarTripulaciones(): void {
    this.tripulacionService.listarTripulaciones().subscribe({
      next: data => {
        this.tripulaciones = data;
        this.cdr.detectChanges();
      },
      error: () => this.error = 'No se pudieron cargar las tripulaciones.'
    });
  }

  limpiarFormulario(): void {
    this.nombreEquipo = '';
    this.idPiloto = null;
    this.idCopiloto = null;
    this.idIngenieroVuelo = null;
    this.idTripulantesCabina = [];

    this.pilotos = [];
    this.copilotos = [];
    this.ingenieros = [];
    this.cabina = [];

    this.cargarTripulantes();
  }

  limpiarSeleccionTripulantes(): void {
    this.idPiloto = null;
    this.idCopiloto = null;
    this.idIngenieroVuelo = null;
    this.idTripulantesCabina = [];
  }
}

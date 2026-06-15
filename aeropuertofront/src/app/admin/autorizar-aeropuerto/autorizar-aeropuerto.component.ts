import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Aerolinea,
  AerolineaAeropuertoResponse,
  AerolineaAeropuertoService,
  Aeropuerto
} from '../../services/aerolinea-aeropuerto/aerolinea-aeropuerto.service';

@Component({
  selector: 'app-autorizar-aeropuerto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './autorizar-aeropuerto.component.html',
  styleUrl: './autorizar-aeropuerto.component.css'
})
export class AutorizarAeropuertoComponent implements OnInit {

  aerolineas: Aerolinea[] = [];
  aeropuertos: Aeropuerto[] = [];
  autorizaciones: AerolineaAeropuertoResponse[] = [];

  idAerolineaSeleccionada: number = 0;
  idsAeropuertosSeleccionados: number[] = [];

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private service: AerolineaAeropuertoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAerolineas();
    this.cargarAeropuertos();
    this.cargarAutorizaciones();
  }

  cargarAerolineas(): void {
    this.service.listarAerolineas().subscribe({
      next: (data) => {
        this.aerolineas = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar aerolíneas:', error);
        this.mensajeError = 'No se pudieron cargar las aerolíneas.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarAeropuertos(): void {
    this.service.listarAeropuertosActivos().subscribe({
      next: (data) => {
        this.aeropuertos = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar aeropuertos:', error);
        this.mensajeError = 'No se pudieron cargar los aeropuertos.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarAutorizaciones(): void {
    this.service.listarAutorizaciones().subscribe({
      next: (data) => {
        this.autorizaciones = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar autorizaciones:', error);
        this.mensajeError = 'No se pudieron cargar las autorizaciones.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarAutorizacionesPorAerolinea(): void {
    this.mensajeError = '';
    this.mensajeExito = '';

    if (!this.idAerolineaSeleccionada) {
      this.cargarAutorizaciones();
      return;
    }

    this.service.listarPorAerolinea(this.idAerolineaSeleccionada).subscribe({
      next: (data) => {
        this.autorizaciones = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar autorizaciones por aerolínea:', error);
        this.mensajeError = 'No se pudieron cargar los aeropuertos autorizados de la aerolínea.';
        this.cdr.detectChanges();
      }
    });
  }

  toggleAeropuerto(idAeropuerto: number, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {
      if (!this.idsAeropuertosSeleccionados.includes(idAeropuerto)) {
        this.idsAeropuertosSeleccionados.push(idAeropuerto);
      }
    } else {
      this.idsAeropuertosSeleccionados = this.idsAeropuertosSeleccionados.filter(
        id => id !== idAeropuerto
      );
    }
  }

  estaSeleccionado(idAeropuerto: number): boolean {
    return this.idsAeropuertosSeleccionados.includes(idAeropuerto);
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.idAerolineaSeleccionada) {
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      this.cdr.detectChanges();
      return;
    }

    if (this.idsAeropuertosSeleccionados.length === 0) {
      this.mensajeError = 'Debe seleccionar al menos un aeropuerto.';
      this.cdr.detectChanges();
      return;
    }

    this.service.autorizar({
      idAerolinea: this.idAerolineaSeleccionada,
      idsAeropuertos: this.idsAeropuertosSeleccionados
    }).subscribe({
      next: () => {
        this.mensajeError = '';
        this.mensajeExito = 'Aeropuertos autorizados correctamente.';

        this.idsAeropuertosSeleccionados = [];
        this.cargarAutorizacionesPorAerolinea();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al autorizar aeropuertos:', error);

        this.mensajeExito = '';

        if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudieron autorizar los aeropuertos.';
        }

        this.cdr.detectChanges();
      }
    });
  }

  limpiar(): void {
    this.idAerolineaSeleccionada = 0;
    this.idsAeropuertosSeleccionados = [];
    this.mensajeError = '';
    this.mensajeExito = '';
    this.cargarAutorizaciones();
    this.cdr.detectChanges();
  }
}

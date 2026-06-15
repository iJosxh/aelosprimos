import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Aerolinea,
  CargoTripulante,
  TripulanteRequest,
  TripulanteService
} from '../../services/tripulante/tripulante.service';

@Component({
  selector: 'app-registrar-tripulante',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-tripulante.component.html',
  styleUrl: './registrar-tripulante.component.css'
})
export class RegistrarTripulanteComponent implements OnInit {

  cargos: CargoTripulante[] = [];
  aerolineas: Aerolinea[] = [];

  tripulante: TripulanteRequest = {
    nombre: '',
    apellido: '',
    licencia: '',
    idAerolinea: 0,
    idCargoTripulante: 0
  };

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private tripulanteService: TripulanteService,
    private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargarCargos();
    this.cargarAerolineas();
  }

  cargarCargos(): void {
    this.tripulanteService.listarCargos().subscribe({
      next: (data) => {
        this.cargos = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensajeError = 'No se pudieron cargar los cargos de tripulante.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarAerolineas(): void {
    this.tripulanteService.listarAerolineas().subscribe({
      next: (data) => {
        this.aerolineas = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensajeError = 'No se pudieron cargar las aerolíneas.';
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.tripulante.nombre.trim()) {
      this.mensajeError = 'Debe ingresar el nombre.';
      return;
    }

    if (!this.tripulante.apellido.trim()) {
      this.mensajeError = 'Debe ingresar el apellido.';
      return;
    }

    if (!this.tripulante.idAerolinea) {
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      return;
    }

    if (!this.tripulante.idCargoTripulante) {
      this.mensajeError = 'Debe seleccionar un cargo.';
      return;
    }

    this.tripulanteService.registrarTripulante(this.tripulante).subscribe({
      next: () => {
        this.mensajeError = '';
        this.mensajeExito = 'Tripulante registrado correctamente.';

        this.limpiarFormulario();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error completo al registrar tripulante:', error);

        this.mensajeExito = '';

        if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudo registrar el tripulante.';
        }

        console.log('Mensaje mostrado:', this.mensajeError);

        this.cdr.detectChanges();
      }
    });
  }

  limpiarFormulario(): void {
    this.tripulante = {
      nombre: '',
      apellido: '',
      licencia: '',
      idAerolinea: 0,
      idCargoTripulante: 0
    };
  }
}

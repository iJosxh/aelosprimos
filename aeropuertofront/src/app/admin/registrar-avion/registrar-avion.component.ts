import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Aerolinea,
  AvionRequest,
  AvionResponse,
  AvionService
} from '../../services/avion/avion.service';

@Component({
  selector: 'app-registrar-avion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-avion.component.html',
  styleUrl: './registrar-avion.component.css'
})
export class RegistrarAvionComponent implements OnInit {

  aerolineas: Aerolinea[] = [];
  aviones: AvionResponse[] = [];

  avion: AvionRequest = {
    idAerolinea: 0,
    modelo: '',
    marca: '',
    anio: new Date().getFullYear(),
    capacidad: 0
  };

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private avionService: AvionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAerolineas();
    this.cargarAviones();
  }

  cargarAerolineas(): void {
    this.avionService.listarAerolineas().subscribe({
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

  cargarAviones(): void {
    this.avionService.listarAviones().subscribe({
      next: (data) => {
        this.aviones = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar aviones:', error);
        this.mensajeError = 'No se pudieron cargar los aviones.';
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.avion.idAerolinea) {
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.avion.modelo.trim()) {
      this.mensajeError = 'Debe ingresar el modelo del avión.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.avion.marca.trim()) {
      this.mensajeError = 'Debe ingresar la marca del avión.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.avion.anio || this.avion.anio < 1950) {
      this.mensajeError = 'Debe ingresar un año válido.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.avion.capacidad || this.avion.capacidad <= 0) {
      this.mensajeError = 'La capacidad debe ser mayor a cero.';
      this.cdr.detectChanges();
      return;
    }

    this.avionService.registrarAvion(this.avion).subscribe({
      next: (response) => {
        this.mensajeError = '';
        this.mensajeExito = `Avión registrado correctamente. Se generaron ${response.totalAsientosGenerados} asientos.`;

        this.limpiarFormulario();
        this.cargarAviones();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al registrar avión:', error);

        this.mensajeExito = '';

        if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudo registrar el avión.';
        }

        this.cdr.detectChanges();
      }
    });
  }

  limpiarFormulario(): void {
    this.avion = {
      idAerolinea: 0,
      modelo: '',
      marca: '',
      anio: new Date().getFullYear(),
      capacidad: 0
    };
  }
}

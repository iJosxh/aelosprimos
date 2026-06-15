import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AeropuertoRequest,
  AeropuertoResponse,
  AeropuertoService
} from '../../services/aeropuerto/aeropuerto.service';

@Component({
  selector: 'app-registrar-aeropuerto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-aeropuerto.component.html',
  styleUrl: './registrar-aeropuerto.component.css'
})
export class RegistrarAeropuertoComponent implements OnInit {

  aeropuerto: AeropuertoRequest = {
    nombre: '',
    ciudad: '',
    pais: ''
  };

  aeropuertos: AeropuertoResponse[] = [];

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private aeropuertoService: AeropuertoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAeropuertos();
  }

  cargarAeropuertos(): void {
    this.aeropuertoService.listarAeropuertos().subscribe({
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

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.aeropuerto.nombre.trim()) {
      this.mensajeError = 'Debe ingresar el nombre del aeropuerto.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.aeropuerto.ciudad.trim()) {
      this.mensajeError = 'Debe ingresar la ciudad del aeropuerto.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.aeropuerto.pais.trim()) {
      this.mensajeError = 'Debe ingresar el país del aeropuerto.';
      this.cdr.detectChanges();
      return;
    }

    this.aeropuertoService.registrarAeropuerto(this.aeropuerto).subscribe({
      next: () => {
        this.mensajeError = '';
        this.mensajeExito = 'Aeropuerto registrado correctamente.';

        this.limpiarFormulario();
        this.cargarAeropuertos();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al registrar aeropuerto:', error);

        this.mensajeExito = '';

        if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudo registrar el aeropuerto.';
        }

        this.cdr.detectChanges();
      }
    });
  }

  limpiarFormulario(): void {
    this.aeropuerto = {
      nombre: '',
      ciudad: '',
      pais: ''
    };
  }
}

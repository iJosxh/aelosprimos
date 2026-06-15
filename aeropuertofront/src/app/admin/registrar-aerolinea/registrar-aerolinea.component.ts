import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AerolineaRequest,
  AerolineaResponse,
  AerolineaService
} from '../../services/aerolinea/aerolinea.service';

@Component({
  selector: 'app-registrar-aerolinea',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-aerolinea.component.html',
  styleUrl: './registrar-aerolinea.component.css'
})
export class RegistrarAerolineaComponent implements OnInit {

  aerolinea: AerolineaRequest = {
    nombre: ''
  };

  aerolineas: AerolineaResponse[] = [];

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private aerolineaService: AerolineaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAerolineas();
  }

  cargarAerolineas(): void {
    this.aerolineaService.listarAerolineas().subscribe({
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

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.aerolinea.nombre.trim()) {
      this.mensajeError = 'Debe ingresar el nombre de la aerolínea.';
      this.cdr.detectChanges();
      return;
    }

    this.aerolineaService.registrarAerolinea(this.aerolinea).subscribe({
      next: () => {
        this.mensajeError = '';
        this.mensajeExito = 'Aerolínea registrada correctamente.';

        this.limpiarFormulario();
        this.cargarAerolineas();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al registrar aerolínea:', error);

        this.mensajeExito = '';

        if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudo registrar la aerolínea.';
        }

        this.cdr.detectChanges();
      }
    });
  }

  limpiarFormulario(): void {
    this.aerolinea = {
      nombre: ''
    };
  }
}

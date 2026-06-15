import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, tap, distinctUntilChanged } from 'rxjs';

import {
  ReporteEquipajeVueloResponse,
  ReporteEquipajeVueloService
} from '../../services/reportes/reporte-equipaje-vuelo.service';

@Component({
  selector: 'app-reporte-equipaje-vuelo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-equipaje-vuelo.component.html',
  styleUrls: ['./reporte-equipaje-vuelo.component.css']
})
export class ReporteEquipajeVueloComponent implements OnInit {

  formulario!: FormGroup;

  equipajes: ReporteEquipajeVueloResponse[] = [];

  cargando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  busquedaRealizada = false;

  mensajeError = '';
  mensajeExito = '';

  constructor(
    private fb: FormBuilder,
    private reporteEquipajeVueloService: ReporteEquipajeVueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.escucharCambiosFormulario();
  }

  private inicializarFormulario(): void {
    this.formulario = this.fb.group({
      numeroVuelo: ['', [Validators.required]]
    });
  }

  private escucharCambiosFormulario(): void {
    this.formulario.get('numeroVuelo')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.equipajes = [];
          this.busquedaRealizada = false;
          this.mensajeError = '';
          this.mensajeExito = '';
          this.refrescarVista();
        })
      )
      .subscribe();
  }

  buscar(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
    this.equipajes = [];
    this.busquedaRealizada = false;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe ingresar los campos obligatorios';
      this.refrescarVista();
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.cargando = true;

    this.reporteEquipajeVueloService.buscar(numeroVuelo)
      .pipe(
        tap((response) => {
          this.equipajes = response;
          this.busquedaRealizada = true;

          if (this.equipajes.length > 0) {
            this.mensajeExito = 'Consulta realizada correctamente.';
          }
        }),
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        error: (error) => {
          this.equipajes = [];
          this.busquedaRealizada = true;
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  descargarPdf(): void {
    if (!this.puedeExportar()) {
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoPdf = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.reporteEquipajeVueloService.descargarPdf(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, `reporte-equipaje-vuelo-${numeroVuelo}.pdf`);
          this.mensajeExito = 'PDF generado correctamente.';
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  descargarExcel(): void {
    if (!this.puedeExportar()) {
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoExcel = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.reporteEquipajeVueloService.descargarExcel(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, `reporte-equipaje-vuelo-${numeroVuelo}.xlsx`);
          this.mensajeExito = 'Excel generado correctamente.';
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  limpiar(): void {
    this.formulario.reset({
      numeroVuelo: ''
    });

    this.equipajes = [];
    this.busquedaRealizada = false;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  campoInvalido(campo: string): boolean {
    const control = this.formulario.get(campo);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  puedeExportar(): boolean {
    if (this.equipajes.length === 0) {
      this.mensajeError = 'Debe realizar una búsqueda con resultados antes de exportar.';
      this.refrescarVista();
      return false;
    }

    return true;
  }

  obtenerTotalMaletas(): number {
    return this.equipajes.length;
  }

  obtenerPesoTotal(): number {
    return this.equipajes.reduce((total, item) => total + Number(item.peso || 0), 0);
  }

  private obtenerNumeroVuelo(): string {
    return String(this.formulario.get('numeroVuelo')?.value || '').trim();
  }

  private descargarArchivo(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = url;
    link.download = nombreArchivo;
    link.click();

    window.URL.revokeObjectURL(url);
  }

  private obtenerMensajeError(error: any): string {
    if (error?.error instanceof Blob) {
      return 'No se pudo generar el archivo solicitado.';
    }

    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error?.message) {
      return error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  private refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

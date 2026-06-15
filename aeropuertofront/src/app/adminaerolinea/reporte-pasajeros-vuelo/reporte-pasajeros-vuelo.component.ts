import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, of, switchMap, tap, distinctUntilChanged } from 'rxjs';

import {
  ReportePasajerosVueloResponse,
  ReportePasajerosVueloService
} from '../../services/reportes/reporte-pasajeros-vuelo.service';

@Component({
  selector: 'app-reporte-pasajeros-vuelo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-pasajeros-vuelo.component.html',
  styleUrl: './reporte-pasajeros-vuelo.component.css'
})
export class ReportePasajerosVueloComponent implements OnInit {

  formulario!: FormGroup;

  pasajeros: ReportePasajerosVueloResponse[] = [];

  cargando = false;
  descargandoPdf = false;
  descargandoExcel = false;
  busquedaRealizada = false;

  mensajeError = '';
  mensajeExito = '';

  constructor(
    private fb: FormBuilder,
    private reportePasajerosVueloService: ReportePasajerosVueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.escucharCambiosFormulario();
  }

  private inicializarFormulario(): void {
    this.formulario = this.fb.group({
      numeroVuelo: ['', [Validators.required, Validators.maxLength(30)]]
    });
  }

  private escucharCambiosFormulario(): void {
    this.formulario.get('numeroVuelo')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          if (this.busquedaRealizada) {
            this.pasajeros = [];
            this.busquedaRealizada = false;
            this.mensajeError = '';
            this.mensajeExito = '';
            this.refrescarVista();
          }
        })
      )
      .subscribe();
  }

  buscar(): void {
    this.mensajeError = '';
    this.mensajeExito = '';

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe ingresar el número de vuelo';
      this.refrescarVista();
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.cargando = true;
    this.pasajeros = [];
    this.busquedaRealizada = false;

    of(numeroVuelo)
      .pipe(
        tap(() => {
          this.mensajeError = '';
          this.mensajeExito = '';
        }),
        switchMap((vuelo) => this.reportePasajerosVueloService.buscar(vuelo)),
        finalize(() => {
          this.cargando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (response) => {
          this.pasajeros = response;
          this.busquedaRealizada = true;

          if (this.pasajeros.length === 0) {
            this.mensajeError = 'El vuelo consultado no tiene pasajeros registrados.';
            return;
          }

          this.mensajeExito = 'Consulta realizada correctamente.';
        },
        error: (error) => {
          this.pasajeros = [];
          this.busquedaRealizada = true;
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  descargarPdf(): void {
    if (!this.validarConsultaParaDescarga()) {
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoPdf = true;
    this.mensajeError = '';

    this.reportePasajerosVueloService.descargarPdf(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, `reporte-pasajeros-vuelo-${numeroVuelo}.pdf`);
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  descargarExcel(): void {
    if (!this.validarConsultaParaDescarga()) {
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoExcel = true;
    this.mensajeError = '';

    this.reportePasajerosVueloService.descargarExcel(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, `reporte-pasajeros-vuelo-${numeroVuelo}.xlsx`);
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

    this.pasajeros = [];
    this.busquedaRealizada = false;
    this.mensajeError = '';
    this.mensajeExito = '';
    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  private validarConsultaParaDescarga(): boolean {
    this.mensajeError = '';

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe ingresar el número de vuelo';
      this.refrescarVista();
      return false;
    }

    if (!this.busquedaRealizada || this.pasajeros.length === 0) {
      this.mensajeError = 'Primero debe realizar una búsqueda con resultados.';
      this.refrescarVista();
      return false;
    }

    return true;
  }

  private obtenerNumeroVuelo(): string {
    return String(this.formulario.get('numeroVuelo')?.value || '').trim();
  }

  private descargarArchivo(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const enlace = document.createElement('a');

    enlace.href = url;
    enlace.download = nombreArchivo;
    enlace.click();

    window.URL.revokeObjectURL(url);
  }

  private obtenerMensajeError(error: any): string {
    if (error?.error instanceof Blob) {
      return 'No se pudo generar el archivo solicitado.';
    }

    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (error?.message) {
      return error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { distinctUntilChanged, finalize, tap } from 'rxjs';

import {
  ReporteBoletosReservadosDiaResponse,
  ReporteBoletosReservadosDiaService
} from '../../services/reportes/reporte-boletos-reservados-dia.service';

@Component({
  selector: 'app-reporte-boletos-reservados-dia',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-boletos-reservados-dia.component.html',
  styleUrls: ['./reporte-boletos-reservados-dia.component.css']
})
export class ReporteBoletosReservadosDiaComponent implements OnInit {

  formulario!: FormGroup;

  boletos: ReporteBoletosReservadosDiaResponse[] = [];

  cargando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  busquedaRealizada = false;

  mensajeError = '';
  mensajeExito = '';

  constructor(
    private fb: FormBuilder,
    private reporteBoletosReservadosDiaService: ReporteBoletosReservadosDiaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.escucharCambiosFormulario();
  }

  private inicializarFormulario(): void {
    this.formulario = this.fb.group({
      fecha: ['', [Validators.required]]
    });
  }

  private escucharCambiosFormulario(): void {
    this.formulario.get('fecha')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.boletos = [];
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
    this.boletos = [];
    this.busquedaRealizada = false;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe ingresar la fecha de consulta.';
      this.refrescarVista();
      return;
    }

    const fecha = this.obtenerFecha();

    this.cargando = true;

    this.reporteBoletosReservadosDiaService.buscar(fecha)
      .pipe(
        tap((response) => {
          this.boletos = response;
          this.busquedaRealizada = true;

          if (this.boletos.length > 0) {
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
          this.boletos = [];
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

    const fecha = this.obtenerFecha();

    this.descargandoPdf = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.reporteBoletosReservadosDiaService.descargarPdf(fecha)
      .pipe(
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-boletos-reservados-dia.pdf');
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

    const fecha = this.obtenerFecha();

    this.descargandoExcel = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.reporteBoletosReservadosDiaService.descargarExcel(fecha)
      .pipe(
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-boletos-reservados-dia.xlsx');
          this.mensajeExito = 'Excel generado correctamente.';
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  limpiar(): void {
    this.formulario.reset({
      fecha: ''
    });

    this.boletos = [];
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
    if (this.boletos.length === 0) {
      this.mensajeError = 'Debe realizar una busqueda con resultados antes de exportar.';
      this.refrescarVista();
      return false;
    }

    return true;
  }

  obtenerTotalBoletos(): number {
    return this.boletos.length;
  }

  obtenerMontoTotal(): number {
    return this.boletos.reduce((total, item) => total + Number(item.monto || 0), 0);
  }

  private obtenerFecha(): string {
    return String(this.formulario.get('fecha')?.value || '').trim();
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
    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error?.error instanceof Blob) {
      return 'No se pudo generar el archivo solicitado.';
    }

    if (error?.message) {
      return error.message;
    }

    return 'Ocurrio un error al procesar la solicitud.';
  }

  private refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

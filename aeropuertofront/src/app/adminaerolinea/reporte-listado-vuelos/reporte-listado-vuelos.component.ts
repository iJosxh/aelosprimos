import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule
} from '@angular/forms';
import { finalize } from 'rxjs';

import {
  ReporteListadoVuelosFiltro,
  ReporteListadoVuelosResponse,
  ReporteListadoVuelosService
} from '../../services/reportes/reporte-listado-vuelos.service';

@Component({
  selector: 'app-reporte-listado-vuelos',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-listado-vuelos.component.html',
  styleUrl: './reporte-listado-vuelos.component.css'
})
export class ReporteListadoVuelosComponent implements OnInit {

  formulario!: FormGroup;

  vuelos: ReporteListadoVuelosResponse[] = [];

  cargando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  mensajeExito = '';
  mensajeError = '';

  busquedaRealizada = false;

  constructor(
    private fb: FormBuilder,
    private reporteListadoVuelosService: ReporteListadoVuelosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
  }

  private inicializarFormulario(): void {
    this.formulario = this.fb.group({
      fechaDesde: [null],
      horaDesde: [null],
      fechaHasta: [null],
      horaHasta: [null]
    });
  }

  buscar(): void {
    this.limpiarMensajes();

    if (!this.validarFiltroFechas()) {
      return;
    }

    this.cargando = true;
    this.busquedaRealizada = true;
    this.vuelos = [];

    const filtro = this.obtenerFiltro();

    this.reporteListadoVuelosService.buscar(filtro)
      .pipe(finalize(() => {
        this.cargando = false;
        this.refrescarVista();
      }))
      .subscribe({
        next: (response) => {
          this.vuelos = response;
          this.mensajeExito = `Se encontraron ${response.length} vuelo(s).`;
          this.refrescarVista();
        },
        error: (error) => {
          this.vuelos = [];
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  descargarPdf(): void {
    this.limpiarMensajes();

    if (!this.validarFiltroFechas()) {
      return;
    }

    if (this.vuelos.length === 0) {
      this.mensajeError = 'Debe realizar una búsqueda antes de generar el PDF.';
      return;
    }

    this.descargandoPdf = true;

    this.reporteListadoVuelosService.descargarPdf(this.obtenerFiltro())
      .pipe(finalize(() => {
        this.descargandoPdf = false;
        this.refrescarVista();
      }))
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-listado-vuelos.pdf');
          this.mensajeExito = 'PDF generado correctamente.';
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  descargarExcel(): void {
    this.limpiarMensajes();

    if (!this.validarFiltroFechas()) {
      return;
    }

    if (this.vuelos.length === 0) {
      this.mensajeError = 'Debe realizar una búsqueda antes de generar el Excel.';
      return;
    }

    this.descargandoExcel = true;

    this.reporteListadoVuelosService.descargarExcel(this.obtenerFiltro())
      .pipe(finalize(() => {
        this.descargandoExcel = false;
        this.refrescarVista();
      }))
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-listado-vuelos.xlsx');
          this.mensajeExito = 'Excel generado correctamente.';
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error);
        }
      });
  }

  limpiar(): void {
    this.formulario.reset();
    this.vuelos = [];
    this.busquedaRealizada = false;
    this.limpiarMensajes();
    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  private obtenerFiltro(): ReporteListadoVuelosFiltro {
    const valores = this.formulario.value;

    return {
      fechaDesde: valores.fechaDesde || null,
      horaDesde: valores.horaDesde || null,
      fechaHasta: valores.fechaHasta || null,
      horaHasta: valores.horaHasta || null
    };
  }

  private validarFiltroFechas(): boolean {
    const valores = this.formulario.value;

    const fechaDesde = valores.fechaDesde;
    const horaDesde = valores.horaDesde;
    const fechaHasta = valores.fechaHasta;
    const horaHasta = valores.horaHasta;

    const tieneAlgunFiltro = fechaDesde || horaDesde || fechaHasta || horaHasta;

    if (!tieneAlgunFiltro) {
      return true;
    }

    if (!fechaDesde || !horaDesde || !fechaHasta || !horaHasta) {
      this.mensajeError = 'Debe ingresar fecha desde, hora desde, fecha hasta y hora hasta.';
      return false;
    }

    const desde = new Date(`${fechaDesde}T${horaDesde}`);
    const hasta = new Date(`${fechaHasta}T${horaHasta}`);

    if (hasta < desde) {
      this.mensajeError = 'La fecha y hora hasta debe ser mayor a la fecha y hora desde.';
      return false;
    }

    const milisegundosPorDia = 1000 * 60 * 60 * 24;
    const diferenciaDias = (hasta.getTime() - desde.getTime()) / milisegundosPorDia;

    if (diferenciaDias > 30) {
      this.mensajeError = 'El rango máximo de consulta es de 30 días.';
      return false;
    }

    return true;
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

    if (error?.message) {
      return error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  private limpiarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
  }

  refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

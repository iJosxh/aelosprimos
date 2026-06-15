import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { forkJoin, of } from 'rxjs';

import {
  distinctUntilChanged,
  finalize,
  switchMap,
  tap
} from 'rxjs/operators';

import {
  AerolineaResponse,
  AerolineaService
} from '../../services/aerolinea/aerolinea.service';

import {
  ReporteDestinosAutorizadosFiltro,
  ReporteDestinosAutorizadosResponse,
  ReporteDestinosAutorizadosService
} from '../../services/reportes/reporte-destinos-autorizados.service';

@Component({
  selector: 'app-reporte-destinos-autorizados',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-destinos-autorizados.component.html',
  styleUrls: ['./reporte-destinos-autorizados.component.css']
})
export class ReporteDestinosAutorizadosComponent implements OnInit {

  formFiltros!: FormGroup;

  aerolineas: AerolineaResponse[] = [];
  destinos: ReporteDestinosAutorizadosResponse[] = [];

  mensajeError = '';
  mensajeExito = '';

  cargandoCatalogos = false;
  buscando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  busquedaRealizada = false;

  constructor(
    private fb: FormBuilder,
    private aerolineaService: AerolineaService,
    private reporteDestinosAutorizadosService: ReporteDestinosAutorizadosService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.crearFormulario();
    this.cargarCatalogos();
    this.escucharCambiosFormulario();
  }

  crearFormulario(): void {
    this.formFiltros = this.fb.group({
      idAerolinea: ['', [Validators.required]]
    });
  }

  cargarCatalogos(): void {
    this.cargandoCatalogos = true;

    forkJoin({
      aerolineas: this.aerolineaService.listarAerolineas()
    })
      .pipe(
        tap(response => {
          this.aerolineas = response.aerolineas || [];
        }),
        finalize(() => {
          this.cargandoCatalogos = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: () => {
          this.refrescarVista();
        },
        error: error => {
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  escucharCambiosFormulario(): void {
    this.formFiltros.get('idAerolinea')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.destinos = [];
          this.busquedaRealizada = false;
          this.limpiarMensajes();
          this.refrescarVista();
        })
      )
      .subscribe();
  }

  buscar(): void {
    this.limpiarMensajes();

    if (this.formFiltros.invalid) {
      this.formFiltros.markAllAsTouched();
      this.mensajeError = 'Debe ingresar los campos obligatorios';
      this.refrescarVista();
      return;
    }

    const filtros = this.obtenerFiltros();

    this.buscando = true;
    this.destinos = [];
    this.busquedaRealizada = false;

    of(filtros)
      .pipe(
        switchMap(params => this.reporteDestinosAutorizadosService.buscar(params)),
        finalize(() => {
          this.buscando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: response => {
          this.destinos = response || [];
          this.busquedaRealizada = true;

          if (this.destinos.length === 0) {
            this.mensajeError = 'La aerolínea consultada no tiene destinos autorizados';
            return;
          }

          this.mensajeExito = 'Consulta realizada correctamente.';
          this.refrescarVista();
        },
        error: error => {
          this.destinos = [];
          this.busquedaRealizada = false;
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  descargarPdf(): void {
    this.limpiarMensajes();

    if (!this.puedeExportar()) {
      this.mensajeError = 'Debe realizar una consulta antes de generar el PDF.';
      this.refrescarVista();
      return;
    }

    const filtros = this.obtenerFiltros();
    this.descargandoPdf = true;

    of(filtros)
      .pipe(
        switchMap(params => this.reporteDestinosAutorizadosService.descargarPdf(params)),
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: archivo => {
          this.descargarArchivo(
            archivo,
            'reporte-destinos-autorizados.pdf'
          );

          this.mensajeExito = 'PDF generado correctamente.';
          this.refrescarVista();
        },
        error: error => {
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  descargarExcel(): void {
    this.limpiarMensajes();

    if (!this.puedeExportar()) {
      this.mensajeError = 'Debe realizar una consulta antes de generar el Excel.';
      this.refrescarVista();
      return;
    }

    const filtros = this.obtenerFiltros();
    this.descargandoExcel = true;

    of(filtros)
      .pipe(
        switchMap(params => this.reporteDestinosAutorizadosService.descargarExcel(params)),
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: archivo => {
          this.descargarArchivo(
            archivo,
            'reporte-destinos-autorizados.xlsx'
          );

          this.mensajeExito = 'Excel generado correctamente.';
          this.refrescarVista();
        },
        error: error => {
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  limpiar(): void {
    this.formFiltros.reset({
      idAerolinea: ''
    });

    this.destinos = [];
    this.busquedaRealizada = false;
    this.limpiarMensajes();
    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  campoInvalido(campo: string): boolean {
    const control = this.formFiltros.get(campo);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  obtenerFiltros(): ReporteDestinosAutorizadosFiltro {
    return {
      idAerolinea: Number(this.formFiltros.get('idAerolinea')?.value)
    };
  }

  puedeExportar(): boolean {
    return this.busquedaRealizada && this.destinos.length > 0;
  }

  obtenerNombreAerolineaSeleccionada(): string {
    const idAerolinea = Number(this.formFiltros.get('idAerolinea')?.value);

    const aerolinea = this.aerolineas.find(item => item.idAerolinea === idAerolinea);

    return aerolinea?.nombre || 'Aerolínea seleccionada';
  }

  descargarArchivo(archivo: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(archivo);

    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = nombreArchivo;
    enlace.click();

    window.URL.revokeObjectURL(url);
  }

  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  obtenerMensajeError(error: any): string {
    if (error.error instanceof Blob) {
      return 'No se pudo generar el archivo solicitado.';
    }

    if (typeof error.error === 'string') {
      return error.error;
    }

    if (error.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error.error?.message) {
      return error.error.message;
    }

    return 'Ocurrió un error al procesar la solicitud.';
  }

  refrescarVista(): void {
    setTimeout(() => {
      this.cdr.detectChanges();
    });
  }
}

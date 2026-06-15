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
  AerolineaReporteResponse,
  ReporteAvionesAerolineaResponse,
  ReporteAvionesAerolineaService
} from '../../services/reportes/reporte-aviones-aerolinea.service';

@Component({
  selector: 'app-reporte-aviones-aerolinea',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-aviones-aerolinea.component.html',
  styleUrl: './reporte-aviones-aerolinea.component.css'
})
export class ReporteAvionesAerolineaComponent implements OnInit {

  formulario!: FormGroup;

  aerolineas: AerolineaReporteResponse[] = [];
  aviones: ReporteAvionesAerolineaResponse[] = [];

  cargandoCatalogos = false;
  cargando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  busquedaRealizada = false;

  mensajeError = '';
  mensajeExito = '';

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteAvionesAerolineaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.crearFormulario();
    this.cargarCatalogos();
    this.escucharCambiosFormulario();
  }

  private crearFormulario(): void {
    this.formulario = this.fb.group({
      idAerolinea: [null, Validators.required]
    });
  }

  private cargarCatalogos(): void {
    this.cargandoCatalogos = true;
    this.mensajeError = '';

    forkJoin({
      aerolineas: this.reporteService.listarAerolineasActivas()
    })
      .pipe(
        tap(({ aerolineas }) => {
          this.aerolineas = aerolineas;
        }),
        finalize(() => {
          this.cargandoCatalogos = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(
            error,
            'No se pudieron cargar las aerolíneas.'
          );
          this.refrescarVista();
        }
      });
  }

  private escucharCambiosFormulario(): void {
    this.formulario.get('idAerolinea')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.aviones = [];
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
    this.aviones = [];
    this.busquedaRealizada = false;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      return;
    }

    const idAerolinea = this.obtenerIdAerolinea();

    this.cargando = true;

    this.reporteService.buscar(idAerolinea)
      .pipe(
        tap((response) => {
          this.aviones = response;
          this.busquedaRealizada = true;

          if (response.length > 0) {
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
          this.aviones = [];
          this.busquedaRealizada = true;
          this.mensajeError = this.obtenerMensajeError(
            error,
            'La aerolínea consultada no tiene aviones'
          );
          this.refrescarVista();
        }
      });
  }

  limpiar(): void {
    this.formulario.reset({
      idAerolinea: null
    });

    this.aviones = [];
    this.busquedaRealizada = false;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  descargarPdf(): void {
    this.mensajeError = '';

    if (!this.validarConsultaParaExportar()) {
      return;
    }

    this.descargandoPdf = true;

    of(this.obtenerIdAerolinea())
      .pipe(
        switchMap((idAerolinea) => this.reporteService.descargarPdf(idAerolinea)),
        tap((blob) => {
          this.guardarArchivo(blob, 'reporte-aviones-aerolinea.pdf');
        }),
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(
            error,
            'No se pudo generar el PDF.'
          );
          this.refrescarVista();
        }
      });
  }

  descargarExcel(): void {
    this.mensajeError = '';

    if (!this.validarConsultaParaExportar()) {
      return;
    }

    this.descargandoExcel = true;

    of(this.obtenerIdAerolinea())
      .pipe(
        switchMap((idAerolinea) => this.reporteService.descargarExcel(idAerolinea)),
        tap((blob) => {
          this.guardarArchivo(blob, 'reporte-aviones-aerolinea.xlsx');
        }),
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(
            error,
            'No se pudo generar el Excel.'
          );
          this.refrescarVista();
        }
      });
  }

  private validarConsultaParaExportar(): boolean {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      return false;
    }

    if (!this.busquedaRealizada || this.aviones.length === 0) {
      this.mensajeError = 'Debe realizar una búsqueda con resultados antes de exportar.';
      return false;
    }

    return true;
  }

  private obtenerIdAerolinea(): number {
    return Number(this.formulario.get('idAerolinea')?.value);
  }

  private guardarArchivo(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const enlace = document.createElement('a');

    enlace.href = url;
    enlace.download = nombreArchivo;
    enlace.click();

    window.URL.revokeObjectURL(url);
  }

  private obtenerMensajeError(error: any, mensajeDefault: string): string {
    if (error?.error instanceof Blob) {
      return mensajeDefault;
    }

    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (typeof error?.error === 'string') {
      return error.error;
    }

    return mensajeDefault;
  }

  campoInvalido(campo: string): boolean {
    const control = this.formulario.get(campo);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  refrescarVista(): void {
    this.cdr.detectChanges();
  }
}

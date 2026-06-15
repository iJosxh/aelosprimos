import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { distinctUntilChanged, finalize, forkJoin, of, switchMap, tap } from 'rxjs';

import {
  AeropuertoReporteResponse,
  ReporteAerolineasAeropuertoResponse,
  ReporteAerolineasAeropuertoService
} from '../../services/reportes/reporte-aerolineas-aeropuerto.service';

@Component({
  selector: 'app-reporte-aerolineas-aeropuerto',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './reporte-aerolineas-aeropuerto.component.html',
  styleUrl: './reporte-aerolineas-aeropuerto.component.css'
})
export class ReporteAerolineasAeropuertoComponent implements OnInit {

  formulario!: FormGroup;

  aeropuertos: AeropuertoReporteResponse[] = [];
  resultados: ReporteAerolineasAeropuertoResponse[] = [];

  cargandoCatalogos = false;
  cargandoBusqueda = false;
  descargandoPdf = false;
  descargandoExcel = false;

  mensajeError = '';
  mensajeExito = '';

  consultaRealizada = false;

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteAerolineasAeropuertoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
    this.cargarCatalogos();
    this.escucharCambiosFormulario();
  }

  private inicializarFormulario(): void {
    this.formulario = this.fb.group({
      idAeropuerto: [null, Validators.required]
    });
  }

  private cargarCatalogos(): void {
    this.cargandoCatalogos = true;
    this.mensajeError = '';

    forkJoin({
      aeropuertos: this.reporteService.listarAeropuertosActivos()
    })
      .pipe(
        finalize(() => {
          this.cargandoCatalogos = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: ({ aeropuertos }) => {
          this.aeropuertos = aeropuertos || [];
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error, 'No se pudieron cargar los aeropuertos.');
        }
      });
  }

  private escucharCambiosFormulario(): void {
    this.formulario.get('idAeropuerto')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.resultados = [];
          this.consultaRealizada = false;
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
    this.resultados = [];
    this.consultaRealizada = false;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mensajeError = 'Debe seleccionar un aeropuerto.';
      return;
    }

    const idAeropuerto = Number(this.formulario.value.idAeropuerto);

    this.cargandoBusqueda = true;

    of(idAeropuerto)
      .pipe(
        tap(() => {
          this.mensajeExito = '';
          this.mensajeError = '';
        }),
        switchMap((id) => this.reporteService.buscar(id)),
        finalize(() => {
          this.cargandoBusqueda = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (response) => {
          this.resultados = response || [];
          this.consultaRealizada = true;

          if (this.resultados.length === 0) {
            this.mensajeError = 'El aeropuerto consultado no tiene aerolíneas';
            return;
          }

          this.mensajeExito = 'Consulta realizada correctamente.';
        },
        error: (error) => {
          this.resultados = [];
          this.consultaRealizada = false;
          this.mensajeError = this.obtenerMensajeError(error, 'No se pudo realizar la consulta.');
        }
      });
  }

  limpiar(): void {
    this.formulario.reset({
      idAeropuerto: null
    });

    this.resultados = [];
    this.consultaRealizada = false;
    this.mensajeError = '';
    this.mensajeExito = '';
    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiar();
  }

  descargarPdf(): void {
    if (!this.puedeDescargar()) {
      this.mensajeError = 'Primero debe realizar una búsqueda.';
      return;
    }

    const idAeropuerto = Number(this.formulario.value.idAeropuerto);

    this.descargandoPdf = true;
    this.mensajeError = '';

    this.reporteService.descargarPdf(idAeropuerto)
      .pipe(
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-aerolineas-aeropuerto.pdf');
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error, 'No se pudo generar el PDF.');
        }
      });
  }

  descargarExcel(): void {
    if (!this.puedeDescargar()) {
      this.mensajeError = 'Primero debe realizar una búsqueda.';
      return;
    }

    const idAeropuerto = Number(this.formulario.value.idAeropuerto);

    this.descargandoExcel = true;
    this.mensajeError = '';

    this.reporteService.descargarExcel(idAeropuerto)
      .pipe(
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: (blob) => {
          this.descargarArchivo(blob, 'reporte-aerolineas-aeropuerto.xlsx');
        },
        error: (error) => {
          this.mensajeError = this.obtenerMensajeError(error, 'No se pudo generar el Excel.');
        }
      });
  }

  puedeDescargar(): boolean {
    return this.consultaRealizada && this.resultados.length > 0 && this.formulario.valid;
  }

  get aeropuertoSeleccionado(): AeropuertoReporteResponse | undefined {
    const idAeropuerto = Number(this.formulario.value.idAeropuerto);
    return this.aeropuertos.find(a => a.idAeropuerto === idAeropuerto);
  }

  campoInvalido(campo: string): boolean {
    const control = this.formulario.get(campo);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  refrescarVista(): void {
    this.cdr.detectChanges();
  }

  private descargarArchivo(blob: Blob, nombreArchivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const enlace = document.createElement('a');

    enlace.href = url;
    enlace.download = nombreArchivo;
    enlace.click();

    window.URL.revokeObjectURL(url);
  }

  private obtenerMensajeError(error: any, mensajeDefault: string): string {
    if (error?.error?.mensaje) {
      return error.error.mensaje;
    }

    if (typeof error?.error === 'string') {
      return error.error;
    }

    return mensajeDefault;
  }
}

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { distinctUntilChanged, finalize, tap } from 'rxjs/operators';

import {
  ConsultaVueloResponse,
  ConsultaVueloService
} from '../../services/consulta-vuelo/consulta-vuelo.service';

@Component({
  selector: 'app-consulta-vuelo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './consulta-vuelo.component.html',
  styleUrls: ['./consulta-vuelo.component.css']
})
export class ConsultaVueloComponent implements OnInit {

  formConsulta!: FormGroup;

  vueloConsultado: ConsultaVueloResponse | null = null;

  mensajeError = '';
  mensajeExito = '';

  buscando = false;
  descargandoPdf = false;
  descargandoExcel = false;

  pasoActual = 1;

  constructor(
    private fb: FormBuilder,
    private consultaVueloService: ConsultaVueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.crearFormulario();
    this.escucharCambiosFormulario();
  }

  crearFormulario(): void {
    this.formConsulta = this.fb.group({
      numeroVuelo: ['', [Validators.required]]
    });
  }

  escucharCambiosFormulario(): void {
    this.formConsulta.get('numeroVuelo')?.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap(() => {
          this.vueloConsultado = null;
          this.pasoActual = 1;
          this.limpiarMensajes();
          this.refrescarVista();
        })
      )
      .subscribe();
  }

  buscarVuelo(): void {
    this.limpiarMensajes();

    if (this.formConsulta.invalid) {
      this.formConsulta.markAllAsTouched();
      this.mensajeError = 'Debe ingresar el número de vuelo.';
      this.refrescarVista();
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.buscando = true;
    this.vueloConsultado = null;

    this.consultaVueloService.buscarVuelo(numeroVuelo)
      .pipe(
        finalize(() => {
          this.buscando = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: response => {
          this.vueloConsultado = response;
          this.pasoActual = 2;
          this.mensajeExito = 'Vuelo encontrado correctamente.';
          this.refrescarVista();
        },
        error: error => {
          this.vueloConsultado = null;
          this.pasoActual = 1;
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  descargarPdf(): void {
    this.limpiarMensajes();

    if (!this.vueloConsultado) {
      this.mensajeError = 'Debe realizar una consulta antes de generar el PDF.';
      this.refrescarVista();
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoPdf = true;

    this.consultaVueloService.descargarPdf(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoPdf = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: archivo => {
          this.descargarArchivo(
            archivo,
            `consulta-vuelo-${numeroVuelo}.pdf`
          );

          this.pasoActual = 3;
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

    if (!this.vueloConsultado) {
      this.mensajeError = 'Debe realizar una consulta antes de generar el Excel.';
      this.refrescarVista();
      return;
    }

    const numeroVuelo = this.obtenerNumeroVuelo();

    this.descargandoExcel = true;

    this.consultaVueloService.descargarExcel(numeroVuelo)
      .pipe(
        finalize(() => {
          this.descargandoExcel = false;
          this.refrescarVista();
        })
      )
      .subscribe({
        next: archivo => {
          this.descargarArchivo(
            archivo,
            `consulta-vuelo-${numeroVuelo}.xlsx`
          );

          this.pasoActual = 3;
          this.mensajeExito = 'Excel generado correctamente.';
          this.refrescarVista();
        },
        error: error => {
          this.mensajeError = this.obtenerMensajeError(error);
          this.refrescarVista();
        }
      });
  }

  limpiarFiltros(): void {
    this.formConsulta.reset({
      numeroVuelo: ''
    });

    this.vueloConsultado = null;
    this.pasoActual = 1;
    this.limpiarMensajes();
    this.refrescarVista();
  }

  nuevaConsulta(): void {
    this.limpiarFiltros();
  }

  campoInvalido(campo: string): boolean {
    const control = this.formConsulta.get(campo);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  obtenerNumeroVuelo(): string {
    return String(this.formConsulta.get('numeroVuelo')?.value || '').trim();
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

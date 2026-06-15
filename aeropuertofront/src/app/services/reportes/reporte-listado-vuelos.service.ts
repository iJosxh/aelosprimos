import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ReporteListadoVuelosFiltro {
  fechaDesde?: string | null;
  horaDesde?: string | null;
  fechaHasta?: string | null;
  horaHasta?: string | null;
}

export interface ReporteListadoVuelosResponse {
  idVuelo: number;
  numeroVuelo: string;
  modeloAvion: string;
  aerolinea: string;
  origen: string;
  destino: string;
  fechaSalida: string;
  horaSalida: string;
  fechaLlegada: string;
  horaLlegada: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteListadoVuelosService {

  private readonly apiUrl = `${environment.apiUrl}/reportes/listado-vuelos`;

  constructor(private http: HttpClient) {}

  buscar(filtro: ReporteListadoVuelosFiltro): Observable<ReporteListadoVuelosResponse[]> {
    return this.http.get<ReporteListadoVuelosResponse[]>(
      `${this.apiUrl}/buscar`,
      { params: this.construirParams(filtro) }
    );
  }

  descargarPdf(filtro: ReporteListadoVuelosFiltro): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/pdf`, {
      params: this.construirParams(filtro),
      responseType: 'blob'
    });
  }

  descargarExcel(filtro: ReporteListadoVuelosFiltro): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/excel`, {
      params: this.construirParams(filtro),
      responseType: 'blob'
    });
  }

  private construirParams(filtro: ReporteListadoVuelosFiltro): HttpParams {
    let params = new HttpParams();

    if (filtro.fechaDesde) {
      params = params.set('fechaDesde', filtro.fechaDesde);
    }

    if (filtro.horaDesde) {
      params = params.set('horaDesde', filtro.horaDesde);
    }

    if (filtro.fechaHasta) {
      params = params.set('fechaHasta', filtro.fechaHasta);
    }

    if (filtro.horaHasta) {
      params = params.set('horaHasta', filtro.horaHasta);
    }

    return params;
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ReporteDestinosAutorizadosFiltro {
  idAerolinea: number;
}

export interface ReporteDestinosAutorizadosResponse {
  idAerolinea: number;
  nombreAerolinea: string;
  idAeropuerto: number;
  nombreAeropuerto: string;
  paisAeropuerto: string;
  ciudadAeropuerto: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteDestinosAutorizadosService {

  private apiUrl = `${environment.apiUrl}/reportes/destinos-autorizados`;

  constructor(private http: HttpClient) {}

  buscar(filtros: ReporteDestinosAutorizadosFiltro): Observable<ReporteDestinosAutorizadosResponse[]> {
    const params = new HttpParams()
      .set('idAerolinea', filtros.idAerolinea);

    return this.http.get<ReporteDestinosAutorizadosResponse[]>(
      `${this.apiUrl}/buscar`,
      { params }
    );
  }

  descargarPdf(filtros: ReporteDestinosAutorizadosFiltro): Observable<Blob> {
    const params = new HttpParams()
      .set('idAerolinea', filtros.idAerolinea);

    return this.http.get(
      `${this.apiUrl}/pdf`,
      {
        params,
        responseType: 'blob'
      }
    );
  }

  descargarExcel(filtros: ReporteDestinosAutorizadosFiltro): Observable<Blob> {
    const params = new HttpParams()
      .set('idAerolinea', filtros.idAerolinea);

    return this.http.get(
      `${this.apiUrl}/excel`,
      {
        params,
        responseType: 'blob'
      }
    );
  }
}

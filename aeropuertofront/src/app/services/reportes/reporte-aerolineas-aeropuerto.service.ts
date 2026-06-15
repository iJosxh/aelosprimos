import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface AeropuertoReporteResponse {
  idAeropuerto: number;
  nombre: string;
  ciudad: string;
  pais: string;
  idEstado?: number;
  codigoEstado?: string;
  estado?: string;
}

export interface ReporteAerolineasAeropuertoResponse {
  idAerolinea: number;
  nombreAerolinea: string;
  cantidadAviones: number;
  destinosAutorizados: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteAerolineasAeropuertoService {

  private readonly reporteUrl = `${environment.apiUrl}/reportes/aerolineas-aeropuerto`;
  private readonly aeropuertosUrl = `${environment.apiUrl}/aeropuertos`;

  constructor(private http: HttpClient) {}

  listarAeropuertosActivos(): Observable<AeropuertoReporteResponse[]> {
    return this.http.get<AeropuertoReporteResponse[]>(`${this.aeropuertosUrl}/activos`);
  }

  buscar(idAeropuerto: number): Observable<ReporteAerolineasAeropuertoResponse[]> {
    const params = new HttpParams()
      .set('idAeropuerto', idAeropuerto);

    return this.http.get<ReporteAerolineasAeropuertoResponse[]>(
      `${this.reporteUrl}/buscar`,
      { params }
    );
  }

  descargarPdf(idAeropuerto: number): Observable<Blob> {
    const params = new HttpParams()
      .set('idAeropuerto', idAeropuerto);

    return this.http.get(`${this.reporteUrl}/pdf`, {
      params,
      responseType: 'blob'
    });
  }

  descargarExcel(idAeropuerto: number): Observable<Blob> {
    const params = new HttpParams()
      .set('idAeropuerto', idAeropuerto);

    return this.http.get(`${this.reporteUrl}/excel`, {
      params,
      responseType: 'blob'
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface AerolineaReporteResponse {
  idAerolinea: number;
  nombre: string;
}

export interface ReporteAvionesAerolineaResponse {
  idAvion: number;
  modelo: string;
  marca: string;
  anio: number;
  cantidadPasajeros: number;
  cantidadVuelos: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteAvionesAerolineaService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarAerolineasActivas(): Observable<AerolineaReporteResponse[]> {
    return this.http.get<AerolineaReporteResponse[]>(
      `${this.apiUrl}/aerolineas`
    );
  }

  buscar(idAerolinea: number): Observable<ReporteAvionesAerolineaResponse[]> {
    const params = new HttpParams()
      .set('idAerolinea', idAerolinea);

    return this.http.get<ReporteAvionesAerolineaResponse[]>(
      `${this.apiUrl}/reportes/aviones-aerolinea/buscar`,
      { params }
    );
  }

  descargarPdf(idAerolinea: number): Observable<Blob> {
    const params = new HttpParams()
      .set('idAerolinea', idAerolinea);

    return this.http.get(
      `${this.apiUrl}/reportes/aviones-aerolinea/pdf`,
      {
        params,
        responseType: 'blob'
      }
    );
  }

  descargarExcel(idAerolinea: number): Observable<Blob> {
    const params = new HttpParams()
      .set('idAerolinea', idAerolinea);

    return this.http.get(
      `${this.apiUrl}/reportes/aviones-aerolinea/excel`,
      {
        params,
        responseType: 'blob'
      }
    );
  }
}

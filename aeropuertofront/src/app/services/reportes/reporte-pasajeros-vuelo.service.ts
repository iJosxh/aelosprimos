import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ReportePasajerosVueloResponse {
  idVueloPasajero: number;
  idVuelo: number;
  numeroVuelo: string;
  aerolinea: string;
  nombrePasajero: string;
  numeroPasaporte: string;
  nacionalidad: string;
  edad: number;
  telefono: string;
  correoElectronico: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportePasajerosVueloService {

  private readonly apiUrl = `${environment.apiUrl}/reportes/pasajeros-vuelo`;

  constructor(private http: HttpClient) {}

  buscar(numeroVuelo: string): Observable<ReportePasajerosVueloResponse[]> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get<ReportePasajerosVueloResponse[]>(
      `${this.apiUrl}/buscar`,
      { params }
    );
  }

  descargarPdf(numeroVuelo: string): Observable<Blob> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get(`${this.apiUrl}/pdf`, {
      params,
      responseType: 'blob'
    });
  }

  descargarExcel(numeroVuelo: string): Observable<Blob> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get(`${this.apiUrl}/excel`, {
      params,
      responseType: 'blob'
    });
  }
}

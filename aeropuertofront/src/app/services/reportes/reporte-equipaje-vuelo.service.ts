import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ReporteEquipajeVueloResponse {
  idEquipaje: number;
  numeroVuelo: string;
  nombrePasajero: string;
  maleta: string;
  peso: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteEquipajeVueloService {

  private readonly apiUrl = `${environment.apiUrl}/reportes/equipaje-vuelo`;

  constructor(private http: HttpClient) {}

  buscar(numeroVuelo: string): Observable<ReporteEquipajeVueloResponse[]> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get<ReporteEquipajeVueloResponse[]>(`${this.apiUrl}/buscar`, { params });
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

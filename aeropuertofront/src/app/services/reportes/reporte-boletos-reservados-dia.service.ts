import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ReporteBoletosReservadosDiaResponse {
  idBoleto: number;
  numeroBoleto: string;
  monto: number;
  fechaReserva: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteBoletosReservadosDiaService {

  private readonly apiUrl = `${environment.apiUrl}/reportes/boletos-reservados-dia`;

  constructor(private http: HttpClient) {}

  buscar(fecha: string): Observable<ReporteBoletosReservadosDiaResponse[]> {
    const params = new HttpParams()
      .set('fecha', fecha);

    return this.http.get<ReporteBoletosReservadosDiaResponse[]>(`${this.apiUrl}/buscar`, { params });
  }

  descargarPdf(fecha: string): Observable<Blob> {
    const params = new HttpParams()
      .set('fecha', fecha);

    return this.http.get(`${this.apiUrl}/pdf`, {
      params,
      responseType: 'blob'
    });
  }

  descargarExcel(fecha: string): Observable<Blob> {
    const params = new HttpParams()
      .set('fecha', fecha);

    return this.http.get(`${this.apiUrl}/excel`, {
      params,
      responseType: 'blob'
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface ConsultaVueloResponse {
  idVuelo: number;
  codigoVuelo: string;

  modeloAvion: string;
  marcaAvion: string;

  aerolinea: string;

  origen: string;
  ciudadOrigen: string;
  paisOrigen: string;

  destino: string;
  ciudadDestino: string;
  paisDestino: string;

  fechaHoraSalida: string;
  fechaHoraLlegada: string;

  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConsultaVueloService {

  private apiUrl = `${environment.apiUrl}/consulta-vuelos`;

  constructor(private http: HttpClient) {}

  buscarVuelo(numeroVuelo: string): Observable<ConsultaVueloResponse> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get<ConsultaVueloResponse>(
      `${this.apiUrl}/buscar`,
      { params }
    );
  }

  descargarPdf(numeroVuelo: string): Observable<Blob> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get(
      `${this.apiUrl}/pdf`,
      {
        params,
        responseType: 'blob'
      }
    );
  }

  descargarExcel(numeroVuelo: string): Observable<Blob> {
    const params = new HttpParams()
      .set('numeroVuelo', numeroVuelo);

    return this.http.get(
      `${this.apiUrl}/excel`,
      {
        params,
        responseType: 'blob'
      }
    );
  }
}

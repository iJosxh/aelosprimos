import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface TripulanteResponse {
  idTripulante: number;
  nombreCompleto: string;
  codigoCargo: string;
  cargo: string;
  idAerolinea: number;
  nombreAerolinea: string;
}

export interface CrearTripulacionRequest {
  idAerolinea: number;
  nombreEquipo: string;
  idPiloto: number;
  idCopiloto: number;
  idIngenieroVuelo: number;
  idTripulantesCabina: number[];
}

export interface TripulacionResponse {
  idTripulacion: number;
  nombreEquipo: string;
  idAerolinea: number;
  nombreAerolinea: string;
  estado: string;
  integrantes: any[];
}

export interface AerolineaResponse {
  idAerolinea: number;
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class TripulacionService {

  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  listarDisponibles(idAerolinea: number, cargo: string): Observable<TripulanteResponse[]> {
    const params = new HttpParams()
      .set('idAerolinea', idAerolinea)
      .set('cargo', cargo);

    return this.http.get<TripulanteResponse[]>(`${this.apiUrl}/tripulantes/disponibles`, { params });
  }

  crearTripulacion(request: CrearTripulacionRequest): Observable<TripulacionResponse> {
    return this.http.post<TripulacionResponse>(`${this.apiUrl}/tripulaciones`, request);
  }

  listarTripulaciones(): Observable<TripulacionResponse[]> {
    return this.http.get<TripulacionResponse[]>(`${this.apiUrl}/tripulaciones`);
  }

  listarAerolineas(): Observable<AerolineaResponse[]> {
    return this.http.get<AerolineaResponse[]>(`${this.apiUrl}/aerolineas`);
  }
}

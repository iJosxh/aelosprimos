import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Aerolinea {
  idAerolinea: number;
  nombre: string;
}

export interface Aeropuerto {
  idAeropuerto: number;
  nombre: string;
  ciudad: string;
  pais: string;
}

export interface AerolineaAeropuertoRequest {
  idAerolinea: number;
  idsAeropuertos: number[];
}

export interface AerolineaAeropuertoResponse {
  idAerolineaAeropuerto: number;

  idAerolinea: number;
  nombreAerolinea: string;

  idAeropuerto: number;
  nombreAeropuerto: string;
  ciudad: string;
  pais: string;

  idEstado?: number;
  codigoEstado?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AerolineaAeropuertoService {

  private aerolineasUrl = `${environment.apiUrl}/aerolineas`;
  private aeropuertosUrl = `${environment.apiUrl}/aeropuertos`;
  private autorizacionesUrl = `${environment.apiUrl}/aerolineas-aeropuertos`;

  constructor(private http: HttpClient) {}

  listarAerolineas(): Observable<Aerolinea[]> {
    return this.http.get<Aerolinea[]>(this.aerolineasUrl);
  }

  listarAeropuertosActivos(): Observable<Aeropuerto[]> {
    return this.http.get<Aeropuerto[]>(`${this.aeropuertosUrl}/activos`);
  }

  listarAutorizaciones(): Observable<AerolineaAeropuertoResponse[]> {
    return this.http.get<AerolineaAeropuertoResponse[]>(this.autorizacionesUrl);
  }

  listarPorAerolinea(idAerolinea: number): Observable<AerolineaAeropuertoResponse[]> {
    return this.http.get<AerolineaAeropuertoResponse[]>(
      `${this.autorizacionesUrl}/aerolinea/${idAerolinea}`
    );
  }

  autorizar(data: AerolineaAeropuertoRequest): Observable<AerolineaAeropuertoResponse[]> {
    return this.http.post<AerolineaAeropuertoResponse[]>(this.autorizacionesUrl, data);
  }
}

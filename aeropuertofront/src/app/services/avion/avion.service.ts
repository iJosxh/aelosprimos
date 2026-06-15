import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Aerolinea {
  idAerolinea: number;
  nombre: string;
}

export interface AvionRequest {
  idAerolinea: number;
  modelo: string;
  marca: string;
  anio: number;
  capacidad: number;
}

export interface AvionResponse {
  idAvion: number;
  idAerolinea: number;
  nombreAerolinea: string;
  modelo: string;
  marca: string;
  anio: number;
  capacidad: number;
  cantidadVuelos: number;
  estado: string;
  codigoEstado: string;
  totalAsientosGenerados: number;
}

@Injectable({
  providedIn: 'root'
})
export class AvionService {

  private avionesUrl = `${environment.apiUrl}/aviones`;
  private aerolineasUrl = `${environment.apiUrl}/aerolineas`;

  constructor(private http: HttpClient) {}

  listarAerolineas(): Observable<Aerolinea[]> {
    return this.http.get<Aerolinea[]>(this.aerolineasUrl);
  }

  listarAviones(): Observable<AvionResponse[]> {
    return this.http.get<AvionResponse[]>(this.avionesUrl);
  }

  registrarAvion(data: AvionRequest): Observable<AvionResponse> {
    return this.http.post<AvionResponse>(this.avionesUrl, data);
  }
}

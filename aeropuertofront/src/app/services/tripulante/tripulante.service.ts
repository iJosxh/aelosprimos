import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface CargoTripulante {
  id: number;
  codigo: string;
  valor: string;
}

export interface Aerolinea {
  idAerolinea: number;
  nombre: string;
}

export interface TripulanteRequest {
  nombre: string;
  apellido: string;
  licencia: string;
  idAerolinea: number;
  idCargoTripulante: number;
}

@Injectable({
  providedIn: 'root'
})
export class TripulanteService {

  private apiUrl = `${environment.apiUrl}/tripulantes`;
  private aerolineasUrl = `${environment.apiUrl}/aerolineas`;

  constructor(private http: HttpClient) {}

  listarCargos(): Observable<CargoTripulante[]> {
    return this.http.get<CargoTripulante[]>(`${this.apiUrl}/cargos`);
  }

  listarAerolineas(): Observable<Aerolinea[]> {
    return this.http.get<Aerolinea[]>(this.aerolineasUrl);
  }

  registrarTripulante(data: TripulanteRequest): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }
}

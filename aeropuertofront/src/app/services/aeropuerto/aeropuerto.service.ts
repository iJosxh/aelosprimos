import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AeropuertoRequest {
  nombre: string;
  ciudad: string;
  pais: string;
}

export interface AeropuertoResponse {
  idAeropuerto: number;
  nombre: string;
  ciudad: string;
  pais: string;
  idEstado?: number;
  codigoEstado?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AeropuertoService {

  private apiUrl = `${environment.apiUrl}/aeropuertos`;

  constructor(private http: HttpClient) {}

  listarAeropuertos(): Observable<AeropuertoResponse[]> {
    return this.http.get<AeropuertoResponse[]>(this.apiUrl);
  }

  listarAeropuertosActivos(): Observable<AeropuertoResponse[]> {
    return this.http.get<AeropuertoResponse[]>(`${this.apiUrl}/activos`);
  }

  registrarAeropuerto(data: AeropuertoRequest): Observable<AeropuertoResponse> {
    return this.http.post<AeropuertoResponse>(this.apiUrl, data);
  }
}

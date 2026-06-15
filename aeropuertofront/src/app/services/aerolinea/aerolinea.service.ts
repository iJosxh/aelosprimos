import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AerolineaRequest {
  nombre: string;
}

export interface AerolineaResponse {
  idAerolinea: number;
  nombre: string;
  idEstado?: number;
  codigoEstado?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AerolineaService {

  private apiUrl = `${environment.apiUrl}/aerolineas`;

  constructor(private http: HttpClient) {}

  listarAerolineas(): Observable<AerolineaResponse[]> {
    return this.http.get<AerolineaResponse[]>(this.apiUrl);
  }

  registrarAerolinea(data: AerolineaRequest): Observable<AerolineaResponse> {
    return this.http.post<AerolineaResponse>(this.apiUrl, data);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface RolAdministrativo {
  id: number;
  codigo: string;
  valor: string;
}

export interface UsuarioAdminRequest {
  username: string;
  password: string;
  idRol: number;
  idAerolinea: number;
}

export interface UsuarioAdminResponse {
  idUsuario: number;
  username: string;
  idRol: number;
  codigoRol: string;
  rol: string;
  idEstado: number;
  codigoEstado: string;
  estado: string;
  idAerolinea: number;
  aerolinea: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioAdminService {

  private apiUrl = `${environment.apiUrl}/usuarios-administrativos`;

  constructor(private http: HttpClient) {}

  listarRoles(): Observable<RolAdministrativo[]> {
    return this.http.get<RolAdministrativo[]>(`${this.apiUrl}/roles`);
  }

  registrarUsuario(data: UsuarioAdminRequest): Observable<UsuarioAdminResponse> {
    return this.http.post<UsuarioAdminResponse>(this.apiUrl, data);
  }

  listarUsuarios(): Observable<UsuarioAdminResponse[]> {
    return this.http.get<UsuarioAdminResponse[]>(this.apiUrl);
  }
}

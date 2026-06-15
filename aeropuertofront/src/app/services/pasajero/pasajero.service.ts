import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PasajeroService {

  private API = `${environment.apiUrl}/pasajeros`;

  constructor(private http: HttpClient) {}

  // 🔍 VALIDAR PASAPORTE
  existePasaporte(pasaporte: string) {
    return this.http.get<boolean>(`${this.API}/existe-pasaporte/${pasaporte}`);
  }

  existeCorreo(correo: string) {
      return this.http.get<boolean>(`${this.API}/existe-correo/${correo}`);
    }

  // 💾 REGISTRAR PASAJERO
  registrar(data: any) {
    return this.http.post(`${this.API}`, data);
  }

}

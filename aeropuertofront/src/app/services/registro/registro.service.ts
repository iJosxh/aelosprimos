import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegistroService {

  private API = `${environment.apiUrl}/registro`;

  constructor(private http: HttpClient) {}

  registrarCompleto(data: any) {
    return this.http.post(this.API, data);
  }
}

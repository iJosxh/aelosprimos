import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API = `${environment.apiUrl}/auth/login`;

  constructor(private http: HttpClient) {}

  login(data: any) {
    return this.http.post<any>(this.API, data).pipe(
      tap(res => {
        localStorage.setItem('token', res.token); // 🔐 guardamos JWT
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isLogged() {
    return !!this.getToken();
  }
}

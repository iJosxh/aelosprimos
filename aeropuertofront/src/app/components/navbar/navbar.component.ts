import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  username: string = '';
  inicialUsuario: string = 'U';

  @Output() abrirMenu = new EventEmitter<void>();

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.cargarUsuarioDesdeToken();
  }

  cargarUsuarioDesdeToken(): void {
    const token = localStorage.getItem('token');

    if (!token) {
      return;
    }

    const helper = new JwtHelperService();
    const decodedToken = helper.decodeToken(token);

    this.username = decodedToken.sub || 'Usuario';

    this.inicialUsuario = this.username
      ? this.username.charAt(0).toUpperCase()
      : 'U';
  }

  onAbrirMenu(): void {
    this.abrirMenu.emit();
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

}

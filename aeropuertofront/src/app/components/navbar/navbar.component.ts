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
  tituloNavbar: string = 'Panel principal';
  descripcionNavbar: string = 'Sistema Aeropuerto Los Primos';

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

  private configurarNavbarPorRol(rol: string): void {
    switch (rol) {
      case 'ADMIN':
        this.tituloNavbar = 'Panel de Administración';
        this.descripcionNavbar = 'Gestión general del sistema aeroportuario';
        break;

      case 'ADMIN_AEROLINEA':
        this.tituloNavbar = 'Panel de Aerolínea';
        this.descripcionNavbar = 'Gestión de vuelos, tripulación y reportes';
        break;

      case 'ADMIN_ABORDAJE':
        this.tituloNavbar = 'Panel de Abordaje';
        this.descripcionNavbar = 'Control de pasajeros y cierre de abordaje';
        break;

      case 'PAS':
        this.tituloNavbar = 'Portal del Pasajero';
        this.descripcionNavbar = 'Consulta y reserva de vuelos disponibles';
        break;

      default:
        this.tituloNavbar = 'Aeropuerto Los Primos';
        this.descripcionNavbar = 'Sistema de gestión aeroportuaria';
        break;
    }
  }

  onAbrirMenu(): void {
    this.abrirMenu.emit();
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

}

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  RolAdministrativo,
  UsuarioAdminRequest,
  UsuarioAdminResponse,
  UsuarioAdminService
} from '../../services/usuario-admin/usuario-admin.service';

import {
  AerolineaResponse,
  AerolineaService
} from '../../services/aerolinea/aerolinea.service';

@Component({
  selector: 'app-registrar-usuario-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registrar-usuario-admin.component.html',
  styleUrl: './registrar-usuario-admin.component.css'
})
export class RegistrarUsuarioAdminComponent implements OnInit {

  roles: RolAdministrativo[] = [];
  usuarios: UsuarioAdminResponse[] = [];
  aerolineas: AerolineaResponse[] = [];

  usuario: UsuarioAdminRequest = {
    username: '',
    password: '',
    idRol: 0,
    idAerolinea: 0
  };

  mensajeExito = '';
  mensajeError = '';

  constructor(
    private usuarioAdminService: UsuarioAdminService,
    private aerolineaService: AerolineaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarRoles();
    this.cargarUsuarios();
    this.cargarAerolineas();
  }

  cargarRoles(): void {
    this.usuarioAdminService.listarRoles().subscribe({
      next: (data) => {
        this.roles = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar roles:', error);
        this.mensajeError = 'No se pudieron cargar los roles administrativos.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarUsuarios(): void {
    this.usuarioAdminService.listarUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar usuarios:', error);
        this.mensajeError = 'No se pudieron cargar los usuarios administrativos.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarAerolineas(): void {
    this.aerolineaService.listarAerolineas().subscribe({
      next: (data) => {
        this.aerolineas = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar aerolíneas:', error);
        this.mensajeError = 'No se pudieron cargar las aerolíneas.';
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.usuario.username.trim()) {
      this.mensajeError = 'Debe ingresar el nombre de usuario.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.usuario.password.trim()) {
      this.mensajeError = 'Debe ingresar la contraseña.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.usuario.idRol) {
      this.mensajeError = 'Debe seleccionar un rol.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.usuario.idAerolinea) {
      this.mensajeError = 'Debe seleccionar una aerolínea.';
      this.cdr.detectChanges();
      return;
    }

    const request: UsuarioAdminRequest = {
      username: this.usuario.username.trim(),
      password: this.usuario.password.trim(),
      idRol: Number(this.usuario.idRol),
      idAerolinea: Number(this.usuario.idAerolinea)
    };

    this.usuarioAdminService.registrarUsuario(request).subscribe({
      next: () => {
        this.mensajeError = '';
        this.mensajeExito = 'Usuario administrativo registrado correctamente.';

        this.limpiarFormulario();
        this.cargarUsuarios();

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al registrar usuario administrativo:', error);

        this.mensajeExito = '';

        if (error.error?.mensaje) {
          this.mensajeError = error.error.mensaje;
        } else if (error.error?.message) {
          this.mensajeError = error.error.message;
        } else if (typeof error.error === 'string') {
          this.mensajeError = error.error;
        } else {
          this.mensajeError = 'No se pudo registrar el usuario administrativo.';
        }

        this.cdr.detectChanges();
      }
    });
  }

  limpiarFormulario(): void {
    this.usuario = {
      username: '',
      password: '',
      idRol: 0,
      idAerolinea: 0
    };
  }
}

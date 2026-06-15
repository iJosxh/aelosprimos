import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {

  @Input() rol: string = '';

  mostrarMenuAerolinea: boolean = false;
  mostrarMenuConsultas: boolean = false;

  toggleMenuAerolinea(): void {
    this.mostrarMenuAerolinea = !this.mostrarMenuAerolinea;

    // Opcional: cierra Consultas cuando abres Aerolínea
    if (this.mostrarMenuAerolinea) {
      this.mostrarMenuConsultas = false;
    }
  }

  toggleMenuConsultas(): void {
    this.mostrarMenuConsultas = !this.mostrarMenuConsultas;

    // Opcional: cierra Aerolínea cuando abres Consultas
    if (this.mostrarMenuConsultas) {
      this.mostrarMenuAerolinea = false;
    }
  }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JwtHelperService } from '@auth0/angular-jwt';

import { AdminPanelComponent } from '../panels/admin-panel/admin-panel.component';
import { PasajeroPanelComponent } from '../panels/pasajero-panel/pasajero-panel.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    AdminPanelComponent,
    PasajeroPanelComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  rol: string = '';

  ngOnInit(): void {
    const token = localStorage.getItem('token');

    if (token) {
      const helper = new JwtHelperService();
      const decodedToken = helper.decodeToken(token);

      this.rol = decodedToken?.rol || '';
    }
  }
}

import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { RegistroComponent } from './registro/registro.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ConsultaVueloComponent } from './consulta/consulta-vuelo/consulta-vuelo.component';
import { RegistrarTripulacionComponent } from './adminaerolinea/registrar-tripulacion/registrar-tripulacion.component';
import { CrearVueloComponent } from './adminaerolinea/crear-vuelo/crear-vuelo.component';
import { ReporteListadoVuelosComponent } from './adminaerolinea/reporte-listado-vuelos/reporte-listado-vuelos.component';
import { ReporteAerolineasAeropuertoComponent } from './adminaerolinea/reporte-aerolineas-aeropuerto/reporte-aerolineas-aeropuerto.component';
import { ReporteAvionesAerolineaComponent } from './adminaerolinea/reporte-aviones-aerolinea/reporte-aviones-aerolinea.component';
import { ReporteDestinosAutorizadosComponent } from './adminaerolinea/reporte-destinos-autorizados/reporte-destinos-autorizados.component';
import { ReportePasajerosVueloComponent } from './adminaerolinea/reporte-pasajeros-vuelo/reporte-pasajeros-vuelo.component';
import { ReporteEquipajeVueloComponent } from './adminaerolinea/reporte-equipaje-vuelo/reporte-equipaje-vuelo.component';
import { ReporteBoletosReservadosDiaComponent } from './adminaerolinea/reporte-boletos-reservados-dia/reporte-boletos-reservados-dia.component';
import { AbordajeComponent } from './adminabordaje/abordaje/abordaje.component';
import { RegistrarTripulanteComponent } from './admin/registrar-tripulante/registrar-tripulante.component';
import { RegistrarAerolineaComponent } from './admin/registrar-aerolinea/registrar-aerolinea.component';
import { RegistrarUsuarioAdminComponent } from './admin/registrar-usuario-admin/registrar-usuario-admin.component';
import { RegistrarAvionComponent } from './admin/registrar-avion/registrar-avion.component';
import { RegistrarAeropuertoComponent } from './admin/registrar-aeropuerto/registrar-aeropuerto.component';
import { AutorizarAeropuertoComponent } from './admin/autorizar-aeropuerto/autorizar-aeropuerto.component';
import { ReservarVueloComponent } from './pasajero/reservar-vuelo/reservar-vuelo.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'consulta-vuelo', component: ConsultaVueloComponent },
  {
      path: 'dashboard',
      component: MainLayoutComponent,
      children: [
        {
          path: '',
          component: DashboardComponent
        },
        {
          path: 'adminaerolinea/registrar-tripulacion',
          component: RegistrarTripulacionComponent
        },
        {
          path: 'adminaerolinea/crear-vuelo',
          component: CrearVueloComponent
        },
        {
          path: 'adminaerolinea/reporte-listado-vuelos',
          component: ReporteListadoVuelosComponent
        },
        {
          path: 'adminaerolinea/reporte-aerolineas-aeropuerto',
          component: ReporteAerolineasAeropuertoComponent
        },
        {
          path: 'adminaerolinea/reporte-aviones-aerolinea',
          component: ReporteAvionesAerolineaComponent
        },
        {
          path: 'adminaerolinea/reporte-destinos-autorizados',
          component: ReporteDestinosAutorizadosComponent
        },
        {
          path: 'adminaerolinea/reporte-pasajeros-vuelo',
          component: ReportePasajerosVueloComponent
        },
        {
          path: 'adminaerolinea/reporte-equipaje-vuelo',
          component: ReporteEquipajeVueloComponent
        },
        {
          path: 'adminaerolinea/reporte-boletos-reservados-dia',
          component: ReporteBoletosReservadosDiaComponent
        },
        {
          path: 'adminabordaje/abordaje',
          component: AbordajeComponent
        },
        {
          path: 'admin/registrar-tripulante',
          component: RegistrarTripulanteComponent
        },
        {
          path: 'admin/registrar-aerolinea',
          component: RegistrarAerolineaComponent
        },
        {
          path: 'admin/registrar-usuario',
          component: RegistrarUsuarioAdminComponent
        },
        {
          path: 'admin/registrar-avion',
          component: RegistrarAvionComponent
        },
        {
          path: 'admin/registrar-aeropuerto',
          component: RegistrarAeropuertoComponent
        },
        {
          path: 'admin/autorizar-aeropuerto',
          component: AutorizarAeropuertoComponent
        },
        {
          path: 'pasajero/reservar-vuelo',
          component: ReservarVueloComponent
        }
      ]
    }
];

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface AeropuertoResponse {
  idAeropuerto: number;
  nombre: string;
  ciudad: string;
  pais: string;
}

export interface ValidacionAeropuertosAutorizadosResponse {
  tieneAeropuertosAutorizados: boolean;
  cantidad: number;
}

export interface ValidacionAvionesActivosResponse {
  tieneAvionesActivos: boolean;
  cantidad: number;
}

export interface AerolineaResponse {
  idAerolinea: number;
  nombre: string;
}

export interface AvionDisponibleResponse {
  idAvion: number;
  modelo: string;
  marca: string;
  anio: number;
  capacidad: number;
}

export interface TripulacionDisponibleResponse {
  idTripulacion: number;
  nombreEquipo: string;
}

export interface VueloEscalaRequest {
  aeropuertoId: number;
  orden: number;
  fechaLlegada: string;
  fechaSalida: string;
}

export interface VueloEscalaResponse {
  idVueloEscala: number;
  aeropuertoId: number;
  aeropuertoNombre: string;
  ciudad: string;
  pais: string;
  orden: number;
  fechaLlegada: string;
  fechaSalida: string;
}

export interface VueloRequest {
  aerolineaId: number;
  avionId: number;
  tripulacionId: number;
  aeropuertoOrigenId: number;
  aeropuertoDestinoId: number;
  fechaSalida: string;
  fechaLlegada: string;
  precioEconomica: number;
  precioEjecutiva: number;
  escalas: VueloEscalaRequest[];
}

export interface VueloResponse {
  id: number;
  codigoVuelo: string;
  aerolineaId: number;
  aerolineaNombre: string;
  avionId: number;
  avionModelo: string;
  tripulacionId: number;
  tripulacionNombre: string;
  aeropuertoOrigen: string;
  aeropuertoDestino: string;
  fechaSalida: string;
  fechaLlegada: string;
  precioEconomica: number;
  precioEjecutiva: number;
  estado: string;

  cantidadEscalas: number;
  rutaCompleta: string;
  escalas: VueloEscalaResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class VueloService {

  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  validarAvionesActivos(aerolineaId: number): Observable<ValidacionAvionesActivosResponse> {
    const params = new HttpParams()
      .set('aerolineaId', aerolineaId);

    return this.http.get<ValidacionAvionesActivosResponse>(
      `${this.apiUrl}/vuelos/validar-aviones-activos`,
      { params }
    );
  }

  validarAeropuertosAutorizados(aerolineaId: number): Observable<ValidacionAeropuertosAutorizadosResponse> {
    const params = new HttpParams().set('aerolineaId', aerolineaId);

    return this.http.get<ValidacionAeropuertosAutorizadosResponse>(
      `${this.apiUrl}/vuelos/validar-aeropuertos-autorizados`,
      { params }
    );
  }

  listarAeropuertosAutorizados(aerolineaId: number): Observable<AeropuertoResponse[]> {
    const params = new HttpParams().set('aerolineaId', aerolineaId);

    return this.http.get<AeropuertoResponse[]>(
      `${this.apiUrl}/vuelos/aeropuertos-autorizados`,
      { params }
    );
  }

  listarAeropuertos(): Observable<AeropuertoResponse[]> {
    return this.http.get<AeropuertoResponse[]>(`${this.apiUrl}/aeropuertos`);
  }

  listarAerolineas(): Observable<AerolineaResponse[]> {
    return this.http.get<AerolineaResponse[]>(`${this.apiUrl}/aerolineas`);
  }

  listarVuelos(): Observable<VueloResponse[]> {
    return this.http.get<VueloResponse[]>(`${this.apiUrl}/vuelos`);
  }

  obtenerVueloPorId(id: number): Observable<VueloResponse> {
    return this.http.get<VueloResponse>(`${this.apiUrl}/vuelos/detalle/${id}`);
  }

  obtenerAvionesDisponibles(
    aerolineaId: number,
    fechaSalida: string,
    fechaLlegada: string
  ): Observable<AvionDisponibleResponse[]> {

    const params = new HttpParams()
      .set('aerolineaId', aerolineaId)
      .set('fechaSalida', fechaSalida)
      .set('fechaLlegada', fechaLlegada);

    return this.http.get<AvionDisponibleResponse[]>(
      `${this.apiUrl}/vuelos/aviones-disponibles`,
      { params }
    );
  }

  obtenerTripulacionesDisponibles(
    aerolineaId: number,
    fechaSalida: string,
    fechaLlegada: string
  ): Observable<TripulacionDisponibleResponse[]> {

    const params = new HttpParams()
      .set('aerolineaId', aerolineaId)
      .set('fechaSalida', fechaSalida)
      .set('fechaLlegada', fechaLlegada);

    return this.http.get<TripulacionDisponibleResponse[]>(
      `${this.apiUrl}/vuelos/tripulaciones-disponibles`,
      { params }
    );
  }

  crearVuelo(request: VueloRequest): Observable<VueloResponse> {
    return this.http.post<VueloResponse>(`${this.apiUrl}/vuelos`, request);
  }
}

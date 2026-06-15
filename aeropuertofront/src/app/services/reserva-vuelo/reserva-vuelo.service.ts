import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface AeropuertoReservaResponse {
  idAeropuerto: number;
  nombre: string;
  ciudad: string;
  pais: string;
  idEstado?: number;
  codigoEstado?: string;
  estado?: string;
}

export interface ClaseVueloResponse {
  idClaseVuelo: number;
  codigo: string;
  valor: string;
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

export interface VueloDisponibleReservaResponse {
  idVuelo: number;
  codigoVuelo: string;

  aerolineaNombre: string;

  aeropuertoOrigenId: number;
  aeropuertoOrigen: string;

  aeropuertoDestinoId: number;
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

export interface VueloDetalleReservaResponse {
  idVuelo: number;
  codigoVuelo: string;

  aerolineaNombre: string;

  avionId: number;
  avionMarca: string;
  avionModelo: string;
  avionAnio: number;
  avionCapacidad: number;

  aeropuertoOrigenId: number;
  aeropuertoOrigen: string;
  ciudadOrigen: string;
  paisOrigen: string;

  aeropuertoDestinoId: number;
  aeropuertoDestino: string;
  ciudadDestino: string;
  paisDestino: string;

  fechaSalida: string;
  fechaLlegada: string;

  precioEconomica: number;
  precioEjecutiva: number;

  estado: string;

  cantidadEscalas: number;
  rutaCompleta: string;
  escalas: VueloEscalaResponse[];
}

export interface AsientoDisponibleResponse {
  idAsiento: number;
  fila: number;
  letra: string;
  numeroAsiento: string;
  tipoAsiento: string;
}

export interface ReservaVueloRequest {
  idVuelo: number;
  idAsiento: number;
  codigoClaseVuelo: string;
  cantidadMaletas: number;
}

export interface ReservaVueloResponse {
  idReserva: number;
  codigoPaseAbordar: string;

  codigoVuelo: string;
  nombrePasajero: string;
  numeroPasaporte: string;

  claseVuelo: string;
  asiento: string;

  cantidadMaletas: number;
  precioPagado: number;

  estado: string;
  fechaReserva: string;
}

export interface PaseAbordarResponse {
  idReserva: number;
  codigoPaseAbordar: string;

  codigoVuelo: string;

  nombrePasajero: string;
  numeroPasaporte: string;
  nacionalidad: string;

  aerolineaNombre: string;

  aeropuertoOrigen: string;
  ciudadOrigen: string;
  paisOrigen: string;

  aeropuertoDestino: string;
  ciudadDestino: string;
  paisDestino: string;

  fechaSalida: string;
  fechaLlegada: string;

  claseVuelo: string;
  asiento: string;

  cantidadMaletas: number;
  precioPagado: number;

  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReservaVueloService {

  private apiUrl = `${environment.apiUrl}/reservas`;

  constructor(private http: HttpClient) {}

  listarAeropuertos(): Observable<AeropuertoReservaResponse[]> {
    return this.http.get<AeropuertoReservaResponse[]>(`${this.apiUrl}/aeropuertos`);
  }

  listarClasesVuelo(): Observable<ClaseVueloResponse[]> {
    return this.http.get<ClaseVueloResponse[]>(`${this.apiUrl}/clases`);
  }

  buscarVuelosDisponibles(
    origenId: number,
    destinoId: number,
    fechaSalida: string
  ): Observable<VueloDisponibleReservaResponse[]> {

    const params = new HttpParams()
      .set('origenId', origenId)
      .set('destinoId', destinoId)
      .set('fechaSalida', fechaSalida);

    return this.http.get<VueloDisponibleReservaResponse[]>(
      `${this.apiUrl}/vuelos-disponibles`,
      { params }
    );
  }

  obtenerDetalleVuelo(idVuelo: number): Observable<VueloDetalleReservaResponse> {
    return this.http.get<VueloDetalleReservaResponse>(
      `${this.apiUrl}/vuelos/${idVuelo}/detalle`
    );
  }

  listarAsientosDisponibles(idVuelo: number): Observable<AsientoDisponibleResponse[]> {
    return this.http.get<AsientoDisponibleResponse[]>(
      `${this.apiUrl}/vuelos/${idVuelo}/asientos-disponibles`
    );
  }

  reservarVuelo(request: ReservaVueloRequest): Observable<ReservaVueloResponse> {
    return this.http.post<ReservaVueloResponse>(
      `${this.apiUrl}`,
      request
    );
  }

  obtenerPaseAbordar(idReserva: number): Observable<PaseAbordarResponse> {
    return this.http.get<PaseAbordarResponse>(
      `${this.apiUrl}/pase-abordar/${idReserva}`
    );
  }
}

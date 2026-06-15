import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

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

export interface AbordajeVueloResponse {
  idVuelo: number;
  codigoVuelo: string;
  aerolineaNombre: string;
  origen: string;
  destino: string;
  fechaSalida: string;
  fechaLlegada: string;
  estado: string;

  cantidadEscalas: number;
  rutaCompleta: string;
  escalas: VueloEscalaResponse[];
}

export interface AbordajePasajeroResponse {
  idReserva: number;
  codigoPaseAbordar: string;
  codigoVuelo: string;
  nombrePasajero: string;
  noPasaporte: string;
  claseVuelo: string;
  asiento: string;
  cantidadMaletasReservadas: number;
  cantidadMaletasPresentadas: number;
  maletasExtra: number;
  recargoEquipaje: number;
  estado: string;
  fechaAbordaje: string | null;
  mensaje: string;
}

export interface AbordarPasajeroRequest {
  idVuelo: number;
  noPasaporte: string;
  cantidadMaletasPresentadas: number;
  pesosMaletas: number[];
}

export interface FinalizarAbordajeResponse {
  idVuelo: number;
  codigoVuelo: string;
  boletosCancelados: number;
  estadoVuelo: string;
  mensaje: string;
}

@Injectable({
  providedIn: 'root'
})
export class AbordajeService {

  private readonly apiUrl = `${environment.apiUrl}/abordaje`;

  constructor(private http: HttpClient) {}

  listarVuelosProgramadosProximos(): Observable<AbordajeVueloResponse[]> {
    return this.http.get<AbordajeVueloResponse[]>(
      `${this.apiUrl}/vuelos/programados-proximos`
    );
  }

  listarVuelosParaAbordaje(): Observable<AbordajeVueloResponse[]> {
    return this.http.get<AbordajeVueloResponse[]>(`${this.apiUrl}/vuelos`);
  }

  iniciarAbordaje(idVuelo: number): Observable<AbordajeVueloResponse> {
    return this.http.put<AbordajeVueloResponse>(
      `${this.apiUrl}/vuelos/${idVuelo}/iniciar`,
      {}
    );
  }

  buscarPasajero(idVuelo: number, noPasaporte: string): Observable<AbordajePasajeroResponse> {
    const params = new HttpParams().set('noPasaporte', noPasaporte);

    return this.http.get<AbordajePasajeroResponse>(
      `${this.apiUrl}/vuelos/${idVuelo}/pasajero`,
      { params }
    );
  }

  abordarPasajero(request: AbordarPasajeroRequest): Observable<AbordajePasajeroResponse> {
    return this.http.post<AbordajePasajeroResponse>(
      `${this.apiUrl}/abordar`,
      request
    );
  }

  finalizarAbordaje(idVuelo: number): Observable<FinalizarAbordajeResponse> {
    return this.http.put<FinalizarAbordajeResponse>(
      `${this.apiUrl}/vuelos/${idVuelo}/finalizar`,
      {}
    );
  }
}

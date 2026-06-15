package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDate;

public class ReporteBoletosReservadosDiaFiltroRequest {

    private LocalDate fecha;

    public ReporteBoletosReservadosDiaFiltroRequest() {
    }

    public ReporteBoletosReservadosDiaFiltroRequest(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}

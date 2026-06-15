package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReporteListadoVuelosFiltroRequest {

    private LocalDate fechaDesde;
    private LocalTime horaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaHasta;

    public ReporteListadoVuelosFiltroRequest() {
    }

    public ReporteListadoVuelosFiltroRequest(
            LocalDate fechaDesde,
            LocalTime horaDesde,
            LocalDate fechaHasta,
            LocalTime horaHasta
    ) {
        this.fechaDesde = fechaDesde;
        this.horaDesde = horaDesde;
        this.fechaHasta = fechaHasta;
        this.horaHasta = horaHasta;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalTime getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public LocalTime getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(LocalTime horaHasta) {
        this.horaHasta = horaHasta;
    }
}
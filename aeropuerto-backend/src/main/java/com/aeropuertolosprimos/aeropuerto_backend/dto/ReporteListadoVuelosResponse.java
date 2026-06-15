package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReporteListadoVuelosResponse {

    private Long idVuelo;
    private String numeroVuelo;
    private String modeloAvion;
    private String aerolinea;
    private String origen;
    private String destino;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private LocalDate fechaLlegada;
    private LocalTime horaLlegada;

    public ReporteListadoVuelosResponse() {
    }

    public ReporteListadoVuelosResponse(
            Long idVuelo,
            String numeroVuelo,
            String modeloAvion,
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    ) {
        this.idVuelo = idVuelo;
        this.numeroVuelo = numeroVuelo;
        this.modeloAvion = modeloAvion;
        this.aerolinea = aerolinea;
        this.origen = origen;
        this.destino = destino;

        if (fechaSalida != null) {
            this.fechaSalida = fechaSalida.toLocalDate();
            this.horaSalida = fechaSalida.toLocalTime();
        }

        if (fechaLlegada != null) {
            this.fechaLlegada = fechaLlegada.toLocalDate();
            this.horaLlegada = fechaLlegada.toLocalTime();
        }
    }

    public Long getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(Long idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }

    public String getModeloAvion() {
        return modeloAvion;
    }

    public void setModeloAvion(String modeloAvion) {
        this.modeloAvion = modeloAvion;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDate fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDateTime;

public class VueloEscalaRequest {

    private Long aeropuertoId;
    private Integer orden;
    private LocalDateTime fechaLlegada;
    private LocalDateTime fechaSalida;

    public Long getAeropuertoId() {
        return aeropuertoId;
    }

    public void setAeropuertoId(Long aeropuertoId) {
        this.aeropuertoId = aeropuertoId;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
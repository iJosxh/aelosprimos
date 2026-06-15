package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VueloRequest {

    private Long aerolineaId;
    private Long avionId;
    private Long tripulacionId;
    private Long aeropuertoOrigenId;
    private Long aeropuertoDestinoId;

    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;

    private BigDecimal precioEconomica;
    private BigDecimal precioEjecutiva;

    private List<VueloEscalaRequest> escalas = new ArrayList<>();

    public Long getAerolineaId() {
        return aerolineaId;
    }

    public void setAerolineaId(Long aerolineaId) {
        this.aerolineaId = aerolineaId;
    }

    public Long getAvionId() {
        return avionId;
    }

    public void setAvionId(Long avionId) {
        this.avionId = avionId;
    }

    public Long getTripulacionId() {
        return tripulacionId;
    }

    public void setTripulacionId(Long tripulacionId) {
        this.tripulacionId = tripulacionId;
    }

    public Long getAeropuertoOrigenId() {
        return aeropuertoOrigenId;
    }

    public void setAeropuertoOrigenId(Long aeropuertoOrigenId) {
        this.aeropuertoOrigenId = aeropuertoOrigenId;
    }

    public Long getAeropuertoDestinoId() {
        return aeropuertoDestinoId;
    }

    public void setAeropuertoDestinoId(Long aeropuertoDestinoId) {
        this.aeropuertoDestinoId = aeropuertoDestinoId;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public BigDecimal getPrecioEconomica() {
        return precioEconomica;
    }

    public void setPrecioEconomica(BigDecimal precioEconomica) {
        this.precioEconomica = precioEconomica;
    }

    public BigDecimal getPrecioEjecutiva() {
        return precioEjecutiva;
    }

    public void setPrecioEjecutiva(BigDecimal precioEjecutiva) {
        this.precioEjecutiva = precioEjecutiva;
    }

    public List<VueloEscalaRequest> getEscalas() {
        return escalas;
    }

    public void setEscalas(List<VueloEscalaRequest> escalas) {
        this.escalas = escalas;
    }
}
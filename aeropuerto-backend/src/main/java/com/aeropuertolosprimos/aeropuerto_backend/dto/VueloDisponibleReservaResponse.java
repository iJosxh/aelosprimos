package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VueloDisponibleReservaResponse {

    private Long idVuelo;
    private String codigoVuelo;

    private String aerolineaNombre;

    private Long aeropuertoOrigenId;
    private String aeropuertoOrigen;

    private Long aeropuertoDestinoId;
    private String aeropuertoDestino;

    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;

    private BigDecimal precioEconomica;
    private BigDecimal precioEjecutiva;

    private String estado;

    private Integer cantidadEscalas;
    private String rutaCompleta;
    private List<VueloEscalaResponse> escalas = new ArrayList<>();

    public Long getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(Long idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public void setCodigoVuelo(String codigoVuelo) {
        this.codigoVuelo = codigoVuelo;
    }

    public String getAerolineaNombre() {
        return aerolineaNombre;
    }

    public void setAerolineaNombre(String aerolineaNombre) {
        this.aerolineaNombre = aerolineaNombre;
    }

    public Long getAeropuertoOrigenId() {
        return aeropuertoOrigenId;
    }

    public void setAeropuertoOrigenId(Long aeropuertoOrigenId) {
        this.aeropuertoOrigenId = aeropuertoOrigenId;
    }

    public String getAeropuertoOrigen() {
        return aeropuertoOrigen;
    }

    public void setAeropuertoOrigen(String aeropuertoOrigen) {
        this.aeropuertoOrigen = aeropuertoOrigen;
    }

    public Long getAeropuertoDestinoId() {
        return aeropuertoDestinoId;
    }

    public void setAeropuertoDestinoId(Long aeropuertoDestinoId) {
        this.aeropuertoDestinoId = aeropuertoDestinoId;
    }

    public String getAeropuertoDestino() {
        return aeropuertoDestino;
    }

    public void setAeropuertoDestino(String aeropuertoDestino) {
        this.aeropuertoDestino = aeropuertoDestino;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCantidadEscalas() {
        return cantidadEscalas;
    }

    public void setCantidadEscalas(Integer cantidadEscalas) {
        this.cantidadEscalas = cantidadEscalas;
    }

    public String getRutaCompleta() {
        return rutaCompleta;
    }

    public void setRutaCompleta(String rutaCompleta) {
        this.rutaCompleta = rutaCompleta;
    }

    public List<VueloEscalaResponse> getEscalas() {
        return escalas;
    }

    public void setEscalas(List<VueloEscalaResponse> escalas) {
        this.escalas = escalas;
    }
}
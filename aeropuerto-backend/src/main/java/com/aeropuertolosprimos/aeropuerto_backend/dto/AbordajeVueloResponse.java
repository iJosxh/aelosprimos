package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AbordajeVueloResponse {

    private Long idVuelo;
    private String codigoVuelo;
    private String aerolineaNombre;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
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
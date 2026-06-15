package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.time.LocalDateTime;

public class VueloEscalaResponse {

    private Long idVueloEscala;
    private Long aeropuertoId;
    private String aeropuertoNombre;
    private String ciudad;
    private String pais;
    private Integer orden;
    private LocalDateTime fechaLlegada;
    private LocalDateTime fechaSalida;

    public VueloEscalaResponse() {
    }

    public VueloEscalaResponse(
            Long idVueloEscala,
            Long aeropuertoId,
            String aeropuertoNombre,
            String ciudad,
            String pais,
            Integer orden,
            LocalDateTime fechaLlegada,
            LocalDateTime fechaSalida
    ) {
        this.idVueloEscala = idVueloEscala;
        this.aeropuertoId = aeropuertoId;
        this.aeropuertoNombre = aeropuertoNombre;
        this.ciudad = ciudad;
        this.pais = pais;
        this.orden = orden;
        this.fechaLlegada = fechaLlegada;
        this.fechaSalida = fechaSalida;
    }

    public Long getIdVueloEscala() {
        return idVueloEscala;
    }

    public void setIdVueloEscala(Long idVueloEscala) {
        this.idVueloEscala = idVueloEscala;
    }

    public Long getAeropuertoId() {
        return aeropuertoId;
    }

    public void setAeropuertoId(Long aeropuertoId) {
        this.aeropuertoId = aeropuertoId;
    }

    public String getAeropuertoNombre() {
        return aeropuertoNombre;
    }

    public void setAeropuertoNombre(String aeropuertoNombre) {
        this.aeropuertoNombre = aeropuertoNombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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
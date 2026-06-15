package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class AvionDisponibleResponse {

    private Long idAvion;
    private String modelo;
    private String marca;
    private Integer anio;
    private Integer capacidad;

    public AvionDisponibleResponse(Long idAvion, String modelo, String marca, Integer anio, Integer capacidad) {
        this.idAvion = idAvion;
        this.modelo = modelo;
        this.marca = marca;
        this.anio = anio;
        this.capacidad = capacidad;
    }

    public Long getIdAvion() {
        return idAvion;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public Integer getAnio() {
        return anio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }
}
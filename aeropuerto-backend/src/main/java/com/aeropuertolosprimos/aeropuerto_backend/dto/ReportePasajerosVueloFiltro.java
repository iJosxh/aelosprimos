package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReportePasajerosVueloFiltro {

    private String numeroVuelo;

    public ReportePasajerosVueloFiltro() {
    }

    public ReportePasajerosVueloFiltro(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }

    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }
}
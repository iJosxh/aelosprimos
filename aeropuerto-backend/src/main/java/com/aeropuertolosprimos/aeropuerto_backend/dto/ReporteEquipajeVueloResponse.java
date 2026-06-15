package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;

public class ReporteEquipajeVueloResponse {

    private Long idEquipaje;
    private String numeroVuelo;
    private String nombrePasajero;
    private String maleta;
    private BigDecimal peso;

    public Long getIdEquipaje() {
        return idEquipaje;
    }

    public void setIdEquipaje(Long idEquipaje) {
        this.idEquipaje = idEquipaje;
    }

    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public String getMaleta() {
        return maleta;
    }

    public void setMaleta(String maleta) {
        this.maleta = maleta;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReporteAerolineasAeropuertoResponse {

    private Long idAerolinea;
    private String nombreAerolinea;
    private Long cantidadAviones;
    private Long destinosAutorizados;

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public String getNombreAerolinea() {
        return nombreAerolinea;
    }

    public void setNombreAerolinea(String nombreAerolinea) {
        this.nombreAerolinea = nombreAerolinea;
    }

    public Long getCantidadAviones() {
        return cantidadAviones;
    }

    public void setCantidadAviones(Long cantidadAviones) {
        this.cantidadAviones = cantidadAviones;
    }

    public Long getDestinosAutorizados() {
        return destinosAutorizados;
    }

    public void setDestinosAutorizados(Long destinosAutorizados) {
        this.destinosAutorizados = destinosAutorizados;
    }
}
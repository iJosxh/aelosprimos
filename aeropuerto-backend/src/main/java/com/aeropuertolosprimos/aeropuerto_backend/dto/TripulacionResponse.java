package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.util.List;

public class TripulacionResponse {

    private Long idTripulacion;

    private String nombreEquipo;

    private Long idAerolinea;
    private String nombreAerolinea;

    private String estado;

    private List<TripulacionDetalleResponse> integrantes;

    public Long getIdTripulacion() {
        return idTripulacion;
    }

    public void setIdTripulacion(Long idTripulacion) {
        this.idTripulacion = idTripulacion;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<TripulacionDetalleResponse> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<TripulacionDetalleResponse> integrantes) {
        this.integrantes = integrantes;
    }
}
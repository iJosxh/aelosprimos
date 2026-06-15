package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class TripulacionDisponibleResponse {

    private Long idTripulacion;
    private String nombreEquipo;

    public TripulacionDisponibleResponse(Long idTripulacion, String nombreEquipo) {
        this.idTripulacion = idTripulacion;
        this.nombreEquipo = nombreEquipo;
    }

    public Long getIdTripulacion() {
        return idTripulacion;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }
}
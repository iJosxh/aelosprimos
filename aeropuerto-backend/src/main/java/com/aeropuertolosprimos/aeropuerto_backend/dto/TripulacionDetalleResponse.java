package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class TripulacionDetalleResponse {

    private Long idTripulante;
    private String nombreCompleto;
    private String cargo;

    public Long getIdTripulante() {
        return idTripulante;
    }

    public void setIdTripulante(Long idTripulante) {
        this.idTripulante = idTripulante;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}

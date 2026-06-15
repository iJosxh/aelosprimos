package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class TripulanteRequest {

    private String nombre;
    private String apellido;
    private String licencia;
    private Long idAerolinea;
    private Long idCargoTripulante;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public Long getIdCargoTripulante() {
        return idCargoTripulante;
    }

    public void setIdCargoTripulante(Long idCargoTripulante) {
        this.idCargoTripulante = idCargoTripulante;
    }
}

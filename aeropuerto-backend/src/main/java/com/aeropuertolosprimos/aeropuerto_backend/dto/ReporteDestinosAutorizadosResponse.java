package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReporteDestinosAutorizadosResponse {

    private Long idAerolinea;
    private String nombreAerolinea;
    private Long idAeropuerto;
    private String nombreAeropuerto;
    private String paisAeropuerto;
    private String ciudadAeropuerto;

    public ReporteDestinosAutorizadosResponse() {
    }

    public ReporteDestinosAutorizadosResponse(
            Long idAerolinea,
            String nombreAerolinea,
            Long idAeropuerto,
            String nombreAeropuerto,
            String paisAeropuerto,
            String ciudadAeropuerto
    ) {
        this.idAerolinea = idAerolinea;
        this.nombreAerolinea = nombreAerolinea;
        this.idAeropuerto = idAeropuerto;
        this.nombreAeropuerto = nombreAeropuerto;
        this.paisAeropuerto = paisAeropuerto;
        this.ciudadAeropuerto = ciudadAeropuerto;
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

    public Long getIdAeropuerto() {
        return idAeropuerto;
    }

    public void setIdAeropuerto(Long idAeropuerto) {
        this.idAeropuerto = idAeropuerto;
    }

    public String getNombreAeropuerto() {
        return nombreAeropuerto;
    }

    public void setNombreAeropuerto(String nombreAeropuerto) {
        this.nombreAeropuerto = nombreAeropuerto;
    }

    public String getPaisAeropuerto() {
        return paisAeropuerto;
    }

    public void setPaisAeropuerto(String paisAeropuerto) {
        this.paisAeropuerto = paisAeropuerto;
    }

    public String getCiudadAeropuerto() {
        return ciudadAeropuerto;
    }

    public void setCiudadAeropuerto(String ciudadAeropuerto) {
        this.ciudadAeropuerto = ciudadAeropuerto;
    }
}
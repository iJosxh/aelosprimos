package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class AerolineaAeropuertoResponse {

    private Long idAerolineaAeropuerto;

    private Long idAerolinea;
    private String nombreAerolinea;

    private Long idAeropuerto;
    private String nombreAeropuerto;
    private String ciudad;
    private String pais;

    private Long idEstado;
    private String codigoEstado;
    private String estado;

    public Long getIdAerolineaAeropuerto() {
        return idAerolineaAeropuerto;
    }

    public void setIdAerolineaAeropuerto(Long idAerolineaAeropuerto) {
        this.idAerolineaAeropuerto = idAerolineaAeropuerto;
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

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public String getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(String codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
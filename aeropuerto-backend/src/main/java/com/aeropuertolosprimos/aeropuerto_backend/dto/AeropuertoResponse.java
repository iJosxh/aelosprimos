package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class AeropuertoResponse {

    public AeropuertoResponse() {
    }

    public AeropuertoResponse(Long idAeropuerto, String nombre, String ciudad, String pais) {
        this.idAeropuerto = idAeropuerto;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    private Long idAeropuerto;
    private String nombre;
    private String ciudad;
    private String pais;

    private Long idEstado;
    private String codigoEstado;
    private String estado;

    public Long getIdAeropuerto() {
        return idAeropuerto;
    }

    public void setIdAeropuerto(Long idAeropuerto) {
        this.idAeropuerto = idAeropuerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
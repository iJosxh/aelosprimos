package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class TripulanteResponse {

    private Long idTripulante;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String licencia;

    private Long idAerolinea;
    private String nombreAerolinea;

    private Long idCargoTripulante;
    private String codigoCargo;
    private String cargo;

    private Long idEstado;
    private String codigoEstado;
    private String estado;

    public Long getIdTripulante() {
        return idTripulante;
    }

    public void setIdTripulante(Long idTripulante) {
        this.idTripulante = idTripulante;
    }

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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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

    public String getNombreAerolinea() {
        return nombreAerolinea;
    }

    public void setNombreAerolinea(String nombreAerolinea) {
        this.nombreAerolinea = nombreAerolinea;
    }

    public Long getIdCargoTripulante() {
        return idCargoTripulante;
    }

    public void setIdCargoTripulante(Long idCargoTripulante) {
        this.idCargoTripulante = idCargoTripulante;
    }

    public String getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(String codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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
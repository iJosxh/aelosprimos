package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class AvionResponse {

    private Long idAvion;
    private Long idAerolinea;
    private String nombreAerolinea;

    private String modelo;
    private String marca;
    private Integer anio;
    private Integer capacidad;
    private Integer cantidadVuelos;

    private Long idEstado;
    private String codigoEstado;
    private String estado;

    private Integer totalAsientosGenerados;

    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
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

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getCantidadVuelos() {
        return cantidadVuelos;
    }

    public void setCantidadVuelos(Integer cantidadVuelos) {
        this.cantidadVuelos = cantidadVuelos;
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

    public Integer getTotalAsientosGenerados() {
        return totalAsientosGenerados;
    }

    public void setTotalAsientosGenerados(Integer totalAsientosGenerados) {
        this.totalAsientosGenerados = totalAsientosGenerados;
    }
}
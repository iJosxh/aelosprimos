package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReporteAvionesAerolineaResponse {

    private Long idAvion;
    private String modelo;
    private String marca;
    private Integer anio;
    private Integer cantidadPasajeros;
    private Long cantidadVuelos;

    public ReporteAvionesAerolineaResponse() {
    }

    public ReporteAvionesAerolineaResponse(
            Long idAvion,
            String modelo,
            String marca,
            Integer anio,
            Integer cantidadPasajeros,
            Long cantidadVuelos
    ) {
        this.idAvion = idAvion;
        this.modelo = modelo;
        this.marca = marca;
        this.anio = anio;
        this.cantidadPasajeros = cantidadPasajeros;
        this.cantidadVuelos = cantidadVuelos;
    }

    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
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

    public Integer getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public void setCantidadPasajeros(Integer cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public Long getCantidadVuelos() {
        return cantidadVuelos;
    }

    public void setCantidadVuelos(Long cantidadVuelos) {
        this.cantidadVuelos = cantidadVuelos;
    }
}
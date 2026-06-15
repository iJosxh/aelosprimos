package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReservaVueloRequest {

    private Long idVuelo;
    private Long idAsiento;
    private String codigoClaseVuelo;
    private Integer cantidadMaletas;

    public Long getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(Long idVuelo) {
        this.idVuelo = idVuelo;
    }

    public Long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Long idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getCodigoClaseVuelo() {
        return codigoClaseVuelo;
    }

    public void setCodigoClaseVuelo(String codigoClaseVuelo) {
        this.codigoClaseVuelo = codigoClaseVuelo;
    }

    public Integer getCantidadMaletas() {
        return cantidadMaletas;
    }

    public void setCantidadMaletas(Integer cantidadMaletas) {
        this.cantidadMaletas = cantidadMaletas;
    }
}
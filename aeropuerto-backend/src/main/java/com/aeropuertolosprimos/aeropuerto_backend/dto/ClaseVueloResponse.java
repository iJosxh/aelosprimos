package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ClaseVueloResponse {

    private Long idClaseVuelo;
    private String codigo;
    private String valor;

    public Long getIdClaseVuelo() {
        return idClaseVuelo;
    }

    public void setIdClaseVuelo(Long idClaseVuelo) {
        this.idClaseVuelo = idClaseVuelo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
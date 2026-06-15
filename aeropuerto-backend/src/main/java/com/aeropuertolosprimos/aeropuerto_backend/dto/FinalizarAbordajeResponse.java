package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class FinalizarAbordajeResponse {

    private Long idVuelo;
    private String codigoVuelo;
    private Integer boletosCancelados;
    private String estadoVuelo;
    private String mensaje;

    public Long getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(Long idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public void setCodigoVuelo(String codigoVuelo) {
        this.codigoVuelo = codigoVuelo;
    }

    public Integer getBoletosCancelados() {
        return boletosCancelados;
    }

    public void setBoletosCancelados(Integer boletosCancelados) {
        this.boletosCancelados = boletosCancelados;
    }

    public String getEstadoVuelo() {
        return estadoVuelo;
    }

    public void setEstadoVuelo(String estadoVuelo) {
        this.estadoVuelo = estadoVuelo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
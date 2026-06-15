package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservaVueloResponse {

    private Long idReserva;
    private String codigoPaseAbordar;

    private String codigoVuelo;
    private String nombrePasajero;
    private String numeroPasaporte;

    private String claseVuelo;
    private String asiento;

    private Integer cantidadMaletas;
    private BigDecimal precioPagado;

    private String estado;
    private LocalDateTime fechaReserva;

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodigoPaseAbordar() {
        return codigoPaseAbordar;
    }

    public void setCodigoPaseAbordar(String codigoPaseAbordar) {
        this.codigoPaseAbordar = codigoPaseAbordar;
    }

    public String getCodigoVuelo() {
        return codigoVuelo;
    }

    public void setCodigoVuelo(String codigoVuelo) {
        this.codigoVuelo = codigoVuelo;
    }

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public String getNumeroPasaporte() {
        return numeroPasaporte;
    }

    public void setNumeroPasaporte(String numeroPasaporte) {
        this.numeroPasaporte = numeroPasaporte;
    }

    public String getClaseVuelo() {
        return claseVuelo;
    }

    public void setClaseVuelo(String claseVuelo) {
        this.claseVuelo = claseVuelo;
    }

    public String getAsiento() {
        return asiento;
    }

    public void setAsiento(String asiento) {
        this.asiento = asiento;
    }

    public Integer getCantidadMaletas() {
        return cantidadMaletas;
    }

    public void setCantidadMaletas(Integer cantidadMaletas) {
        this.cantidadMaletas = cantidadMaletas;
    }

    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AbordajePasajeroResponse {

    private Long idReserva;
    private String codigoPaseAbordar;
    private String codigoVuelo;
    private String nombrePasajero;
    private String noPasaporte;
    private String claseVuelo;
    private String asiento;
    private Integer cantidadMaletasReservadas;
    private Integer cantidadMaletasPresentadas;
    private Integer maletasExtra;
    private BigDecimal recargoEquipaje;
    private String estado;
    private LocalDateTime fechaAbordaje;
    private String mensaje;

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

    public String getNoPasaporte() {
        return noPasaporte;
    }

    public void setNoPasaporte(String noPasaporte) {
        this.noPasaporte = noPasaporte;
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

    public Integer getCantidadMaletasReservadas() {
        return cantidadMaletasReservadas;
    }

    public void setCantidadMaletasReservadas(Integer cantidadMaletasReservadas) {
        this.cantidadMaletasReservadas = cantidadMaletasReservadas;
    }

    public Integer getCantidadMaletasPresentadas() {
        return cantidadMaletasPresentadas;
    }

    public void setCantidadMaletasPresentadas(Integer cantidadMaletasPresentadas) {
        this.cantidadMaletasPresentadas = cantidadMaletasPresentadas;
    }

    public Integer getMaletasExtra() {
        return maletasExtra;
    }

    public void setMaletasExtra(Integer maletasExtra) {
        this.maletasExtra = maletasExtra;
    }

    public BigDecimal getRecargoEquipaje() {
        return recargoEquipaje;
    }

    public void setRecargoEquipaje(BigDecimal recargoEquipaje) {
        this.recargoEquipaje = recargoEquipaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaAbordaje() {
        return fechaAbordaje;
    }

    public void setFechaAbordaje(LocalDateTime fechaAbordaje) {
        this.fechaAbordaje = fechaAbordaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
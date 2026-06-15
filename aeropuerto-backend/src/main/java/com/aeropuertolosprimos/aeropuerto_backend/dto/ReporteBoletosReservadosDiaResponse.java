package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReporteBoletosReservadosDiaResponse {

    private Long idBoleto;
    private String numeroBoleto;
    private BigDecimal monto;
    private LocalDateTime fechaReserva;

    public ReporteBoletosReservadosDiaResponse() {
    }

    public ReporteBoletosReservadosDiaResponse(
            Long idBoleto,
            String numeroBoleto,
            BigDecimal monto,
            LocalDateTime fechaReserva
    ) {
        this.idBoleto = idBoleto;
        this.numeroBoleto = numeroBoleto;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
        this.fechaReserva = fechaReserva;
    }

    public Long getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(Long idBoleto) {
        this.idBoleto = idBoleto;
    }

    public String getNumeroBoleto() {
        return numeroBoleto;
    }

    public void setNumeroBoleto(String numeroBoleto) {
        this.numeroBoleto = numeroBoleto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
}

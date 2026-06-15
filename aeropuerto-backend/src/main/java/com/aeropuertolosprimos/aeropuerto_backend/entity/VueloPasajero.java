package com.aeropuertolosprimos.aeropuerto_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vuelo_pasajero")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VueloPasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vuelo_pasajero")
    private Long idVueloPasajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vuelo")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vuelo vuelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasajero")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Pasajero pasajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase_vuelo")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CatalogoDetalle claseVuelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CatalogoDetalle estado;

    @Column(name = "precio_pagado", precision = 10, scale = 2)
    private BigDecimal precioPagado;

    @Column(name = "cantidad_maletas")
    private Integer cantidadMaletas;

    @Column(name = "cantidad_maletas_presentadas")
    private Integer cantidadMaletasPresentadas;

    @Column(name = "maletas_extra")
    private Integer maletasExtra;

    @Column(name = "recargo_equipaje", precision = 10, scale = 2)
    private BigDecimal recargoEquipaje;

    @Column(name = "codigo_pase_abordar", length = 50)
    private String codigoPaseAbordar;

    @Column(name = "fecha_abordaje")
    private LocalDateTime fechaAbordaje;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creacion")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuarioCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_modificacion")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuarioModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }

        if (this.cantidadMaletas == null) {
            this.cantidadMaletas = 0;
        }

        if (this.cantidadMaletasPresentadas == null) {
            this.cantidadMaletasPresentadas = 0;
        }

        if (this.maletasExtra == null) {
            this.maletasExtra = 0;
        }

        if (this.recargoEquipaje == null) {
            this.recargoEquipaje = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public Long getIdVueloPasajero() {
        return idVueloPasajero;
    }

    public void setIdVueloPasajero(Long idVueloPasajero) {
        this.idVueloPasajero = idVueloPasajero;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public CatalogoDetalle getClaseVuelo() {
        return claseVuelo;
    }

    public void setClaseVuelo(CatalogoDetalle claseVuelo) {
        this.claseVuelo = claseVuelo;
    }

    public CatalogoDetalle getEstado() {
        return estado;
    }

    public void setEstado(CatalogoDetalle estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
    }

    public Integer getCantidadMaletas() {
        return cantidadMaletas;
    }

    public void setCantidadMaletas(Integer cantidadMaletas) {
        this.cantidadMaletas = cantidadMaletas;
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

    public String getCodigoPaseAbordar() {
        return codigoPaseAbordar;
    }

    public void setCodigoPaseAbordar(String codigoPaseAbordar) {
        this.codigoPaseAbordar = codigoPaseAbordar;
    }

    public LocalDateTime getFechaAbordaje() {
        return fechaAbordaje;
    }

    public void setFechaAbordaje(LocalDateTime fechaAbordaje) {
        this.fechaAbordaje = fechaAbordaje;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Usuario usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Usuario getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(Usuario usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
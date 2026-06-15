package com.aeropuertolosprimos.aeropuerto_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_asiento")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReservaAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_asiento")
    private Long idReservaAsiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vuelo_pasajero")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private VueloPasajero vueloPasajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asiento")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Asiento asiento;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CatalogoDetalle estado;

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
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public Long getIdReservaAsiento() {
        return idReservaAsiento;
    }

    public void setIdReservaAsiento(Long idReservaAsiento) {
        this.idReservaAsiento = idReservaAsiento;
    }

    public VueloPasajero getVueloPasajero() {
        return vueloPasajero;
    }

    public void setVueloPasajero(VueloPasajero vueloPasajero) {
        this.vueloPasajero = vueloPasajero;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public CatalogoDetalle getEstado() {
        return estado;
    }

    public void setEstado(CatalogoDetalle estado) {
        this.estado = estado;
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
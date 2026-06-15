package com.aeropuertolosprimos.aeropuerto_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tripulacion_detalle")
public class TripulacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tripulacion_detalle")
    private Long idTripulacionDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tripulacion")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tripulacion tripulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tripulante")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tripulante tripulante;

    public Long getIdTripulacionDetalle() {
        return idTripulacionDetalle;
    }

    public void setIdTripulacionDetalle(Long idTripulacionDetalle) {
        this.idTripulacionDetalle = idTripulacionDetalle;
    }

    public Tripulacion getTripulacion() {
        return tripulacion;
    }

    public void setTripulacion(Tripulacion tripulacion) {
        this.tripulacion = tripulacion;
    }

    public Tripulante getTripulante() {
        return tripulante;
    }

    public void setTripulante(Tripulante tripulante) {
        this.tripulante = tripulante;
    }
}
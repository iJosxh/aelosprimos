package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.TripulacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripulacionDetalleRepository extends JpaRepository<TripulacionDetalle, Long> {

    @Query("""
        SELECT td
        FROM TripulacionDetalle td
        LEFT JOIN FETCH td.tripulante t
        LEFT JOIN FETCH t.cargoTripulante
        WHERE td.tripulacion.idTripulacion = :idTripulacion
    """)
    List<TripulacionDetalle> buscarPorTripulacion(
            @Param("idTripulacion") Long idTripulacion
    );
}
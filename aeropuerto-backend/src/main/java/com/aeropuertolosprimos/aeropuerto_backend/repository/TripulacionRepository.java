package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Tripulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripulacionRepository extends JpaRepository<Tripulacion, Long> {

    @Query("""
        SELECT t
        FROM Tripulacion t
        LEFT JOIN FETCH t.aerolinea
        LEFT JOIN FETCH t.estado
        ORDER BY t.idTripulacion DESC
    """)
    List<Tripulacion> listarTripulaciones();

    @Query("""
    SELECT t
    FROM Tripulacion t
    LEFT JOIN FETCH t.aerolinea
    LEFT JOIN FETCH t.estado
    WHERE t.aerolinea.idAerolinea = :aerolineaId
    AND t.estado.codigo = 'ACT'
    ORDER BY t.nombreEquipo ASC
""")
    List<Tripulacion> listarDisponiblesPorAerolinea(Long aerolineaId);
}
package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Tripulante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripulanteRepository extends JpaRepository<Tripulante, Long> {

    boolean existsByLicenciaIgnoreCase(String licencia);

    @Query("""
        SELECT t
        FROM Tripulante t
        LEFT JOIN FETCH t.aerolinea
        LEFT JOIN FETCH t.cargoTripulante
        LEFT JOIN FETCH t.estado
        WHERE (:idAerolinea IS NULL OR t.aerolinea.idAerolinea = :idAerolinea)
        ORDER BY t.nombre ASC, t.apellido ASC
    """)
    List<Tripulante> listarTripulantes(@Param("idAerolinea") Long idAerolinea);

    @Query("""
        SELECT t
        FROM Tripulante t
        LEFT JOIN FETCH t.aerolinea
        LEFT JOIN FETCH t.cargoTripulante
        LEFT JOIN FETCH t.estado
        WHERE t.aerolinea.idAerolinea = :idAerolinea
        AND t.cargoTripulante.codigo = :codigoCargo
        AND t.estado.codigo = 'ACT'
        ORDER BY t.nombre ASC, t.apellido ASC
    """)
    List<Tripulante> listarDisponiblesPorCargo(
            @Param("idAerolinea") Long idAerolinea,
            @Param("codigoCargo") String codigoCargo
    );
}

package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Avion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvionRepository extends JpaRepository<Avion, Long> {

    boolean existsByModeloIgnoreCaseAndMarcaIgnoreCaseAndAnioAndAerolinea_IdAerolinea(
            String modelo,
            String marca,
            Integer anio,
            Long idAerolinea
    );

    @Query("""
        SELECT a
        FROM Avion a
        LEFT JOIN FETCH a.aerolinea
        LEFT JOIN FETCH a.estado
        ORDER BY a.idAvion DESC
    """)
    List<Avion> listarTodos();

    @Query("""
        SELECT a
        FROM Avion a
        LEFT JOIN FETCH a.aerolinea
        LEFT JOIN FETCH a.estado
        WHERE a.estado.codigo = 'ACT'
        ORDER BY a.modelo ASC
    """)
    List<Avion> listarActivos();

    @Query("""
    SELECT a
    FROM Avion a
    LEFT JOIN FETCH a.aerolinea
    LEFT JOIN FETCH a.estado
    WHERE a.aerolinea.idAerolinea = :aerolineaId
    AND a.estado.codigo = 'ACT'
    ORDER BY a.modelo ASC
""")
    List<Avion> listarActivosPorAerolinea(Long aerolineaId);

    @Query("""
    SELECT COUNT(a)
    FROM Avion a
    WHERE a.aerolinea.idAerolinea = :aerolineaId
    AND a.estado.codigo = 'ACT'
""")
    Long contarAvionesActivosPorAerolinea(Long aerolineaId);
}
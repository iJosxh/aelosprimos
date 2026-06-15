package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Avion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReporteAvionesAerolineaRepository extends JpaRepository<Avion, Long> {

    @Query("""
        SELECT a
        FROM Avion a
        LEFT JOIN FETCH a.aerolinea
        LEFT JOIN FETCH a.estado
        WHERE a.aerolinea.idAerolinea = :idAerolinea
        AND a.estado.codigo = 'ACT'
        ORDER BY a.modelo ASC, a.marca ASC
    """)
    List<Avion> buscarAvionesActivosPorAerolinea(Long idAerolinea);

    @Query("""
        SELECT COUNT(v)
        FROM Vuelo v
        WHERE v.avion.idAvion = :idAvion
    """)
    Long contarVuelosPorAvion(Long idAvion);
}
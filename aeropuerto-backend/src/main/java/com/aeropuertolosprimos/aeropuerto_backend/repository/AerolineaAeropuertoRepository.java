package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.AerolineaAeropuerto;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aeropuerto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AerolineaAeropuertoRepository extends JpaRepository<AerolineaAeropuerto, Long> {

    boolean existsByAerolinea_IdAerolineaAndAeropuerto_IdAeropuerto(
            Long idAerolinea,
            Long idAeropuerto
    );

    @Query("""
        SELECT aa
        FROM AerolineaAeropuerto aa
        LEFT JOIN FETCH aa.aerolinea
        LEFT JOIN FETCH aa.aeropuerto
        LEFT JOIN FETCH aa.estado
        ORDER BY aa.aerolinea.nombre ASC, aa.aeropuerto.nombre ASC
    """)
    List<AerolineaAeropuerto> listarTodos();

    @Query("""
        SELECT aa
        FROM AerolineaAeropuerto aa
        LEFT JOIN FETCH aa.aerolinea
        LEFT JOIN FETCH aa.aeropuerto
        LEFT JOIN FETCH aa.estado
        WHERE aa.aerolinea.idAerolinea = :idAerolinea
        AND aa.estado.codigo = 'ACT'
        ORDER BY aa.aeropuerto.nombre ASC
    """)
    List<AerolineaAeropuerto> listarPorAerolinea(Long idAerolinea);

    @Query("""
        SELECT COUNT(aa)
        FROM AerolineaAeropuerto aa
        WHERE aa.aerolinea.idAerolinea = :aerolineaId
        AND aa.estado.codigo = 'ACT'
    """)
    Long contarAeropuertosAutorizados(Long aerolineaId);

    @Query("""
        SELECT aa.aeropuerto
        FROM AerolineaAeropuerto aa
        LEFT JOIN FETCH aa.aeropuerto.estado
        WHERE aa.aerolinea.idAerolinea = :aerolineaId
        AND aa.estado.codigo = 'ACT'
        AND aa.aeropuerto.estado.codigo = 'ACT'
        ORDER BY aa.aeropuerto.nombre ASC
    """)
    List<Aeropuerto> listarAeropuertosAutorizados(Long aerolineaId);

    @Query("""
        SELECT aa
        FROM AerolineaAeropuerto aa
        LEFT JOIN FETCH aa.aerolinea aerolinea
        LEFT JOIN FETCH aerolinea.estado
        LEFT JOIN FETCH aa.aeropuerto aeropuerto
        LEFT JOIN FETCH aa.estado
        WHERE aeropuerto.idAeropuerto = :idAeropuerto
        AND aa.estado.codigo = 'ACT'
        AND aerolinea.estado.codigo = 'ACT'
        ORDER BY aerolinea.nombre ASC
    """)
    List<AerolineaAeropuerto> listarAerolineasActivasPorAeropuerto(Long idAeropuerto);

    @Query("""
    SELECT new com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosResponse(
        al.idAerolinea,
        al.nombre,
        ap.idAeropuerto,
        ap.nombre,
        ap.pais,
        ap.ciudad
    )
    FROM AerolineaAeropuerto aa
    INNER JOIN aa.aerolinea al
    INNER JOIN aa.aeropuerto ap
    INNER JOIN aa.estado estadoAutorizacion
    INNER JOIN al.estado estadoAerolinea
    INNER JOIN ap.estado estadoAeropuerto
    WHERE al.idAerolinea = :idAerolinea
      AND estadoAutorizacion.codigo = 'ACT'
      AND estadoAerolinea.codigo = 'ACT'
      AND estadoAeropuerto.codigo = 'ACT'
    ORDER BY ap.nombre ASC, ap.pais ASC, ap.ciudad ASC
""")
    List<ReporteDestinosAutorizadosResponse> buscarDestinosAutorizadosReporte(
            @Param("idAerolinea") Long idAerolinea
    );

    @Query("""
    SELECT CASE WHEN COUNT(aa) > 0 THEN true ELSE false END
    FROM AerolineaAeropuerto aa
    WHERE aa.aerolinea.idAerolinea = :aerolineaId
    AND aa.aeropuerto.idAeropuerto = :aeropuertoId
    AND aa.estado.codigo = 'ACT'
    AND aa.aeropuerto.estado.codigo = 'ACT'
""")
    boolean existeAeropuertoAutorizadoActivo(Long aerolineaId, Long aeropuertoId);
}
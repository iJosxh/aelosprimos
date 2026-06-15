package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AerolineaRepository extends JpaRepository<Aerolinea, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("""
        SELECT a
        FROM Aerolinea a
        LEFT JOIN FETCH a.estado
        WHERE a.estado.codigo = 'ACT'
        ORDER BY a.nombre ASC
    """)
    List<Aerolinea> listarActivas();

    boolean existsByIdAerolineaAndEstado_Codigo(Long idAerolinea, String codigoEstado);

    @Query("""
        SELECT COUNT(a) > 0
        FROM Aerolinea a
        WHERE a.idAerolinea = :idAerolinea
        AND (
            a.usuarioCreacion.idUsuario = :idUsuario
            OR EXISTS (
                SELECT av.idAvion
                FROM Avion av
                WHERE av.aerolinea.idAerolinea = a.idAerolinea
                AND av.usuarioCreacion.idUsuario = :idUsuario
            )
            OR EXISTS (
                SELECT tr.idTripulacion
                FROM Tripulacion tr
                WHERE tr.aerolinea.idAerolinea = a.idAerolinea
                AND tr.usuarioCreacion.idUsuario = :idUsuario
            )
            OR EXISTS (
                SELECT tp.idTripulante
                FROM Tripulante tp
                WHERE tp.aerolinea.idAerolinea = a.idAerolinea
                AND tp.usuarioCreacion.idUsuario = :idUsuario
            )
        )
    """)
    boolean usuarioPerteneceAAerolinea(Long idUsuario, Long idAerolinea);

    @Query("""
        SELECT a
        FROM Aerolinea a
        LEFT JOIN FETCH a.estado
        WHERE a.idAerolinea = :idAerolinea
        AND a.estado.codigo = 'ACT'
    """)
    Optional<Aerolinea> buscarActivaPorId(Long idAerolinea);
}

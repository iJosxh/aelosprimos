package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Aeropuerto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AeropuertoRepository extends JpaRepository<Aeropuerto, Long> {

    boolean existsByNombreIgnoreCaseAndCiudadIgnoreCaseAndPaisIgnoreCase(
            String nombre,
            String ciudad,
            String pais
    );

    @Query("""
        SELECT a
        FROM Aeropuerto a
        LEFT JOIN FETCH a.estado
        ORDER BY a.nombre ASC
    """)
    List<Aeropuerto> listarTodos();

    @Query("""
        SELECT a
        FROM Aeropuerto a
        LEFT JOIN FETCH a.estado
        WHERE a.estado.codigo = 'ACT'
        ORDER BY a.nombre ASC
    """)
    List<Aeropuerto> listarActivos();

    @Query("""
        SELECT a
        FROM Aeropuerto a
        LEFT JOIN FETCH a.estado
        WHERE a.idAeropuerto = :idAeropuerto
        AND a.estado.codigo = 'ACT'
    """)
    Optional<Aeropuerto> buscarActivoPorId(Long idAeropuerto);
}
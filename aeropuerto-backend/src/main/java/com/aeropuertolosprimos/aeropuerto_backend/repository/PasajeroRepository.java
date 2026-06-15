package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero, Long> {

    boolean existsByNoPasaporte(String noPasaporte);

    boolean existsByCorreo(String correo);

    @Query("""
        SELECT p
        FROM Pasajero p
        LEFT JOIN FETCH p.usuario
        LEFT JOIN FETCH p.estado
        WHERE p.usuario.username = :username
    """)
    Optional<Pasajero> buscarPorUsername(String username);
}
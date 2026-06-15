package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    int countByAvion_IdAvion(Long idAvion);

    @Query("""
        SELECT a
        FROM Asiento a
        LEFT JOIN FETCH a.tipoAsiento
        LEFT JOIN FETCH a.avion
        WHERE a.avion.idAvion = :idAvion
        ORDER BY a.fila ASC, a.letra ASC
    """)
    List<Asiento> listarPorAvion(Long idAvion);

    @Query("""
        SELECT a
        FROM Asiento a
        LEFT JOIN FETCH a.tipoAsiento
        LEFT JOIN FETCH a.avion
        WHERE a.avion.idAvion = (
            SELECT v.avion.idAvion
            FROM Vuelo v
            WHERE v.idVuelo = :idVuelo
        )
        AND NOT EXISTS (
            SELECT 1
            FROM ReservaAsiento ra
            WHERE ra.asiento.idAsiento = a.idAsiento
            AND ra.vueloPasajero.vuelo.idVuelo = :idVuelo
            AND ra.vueloPasajero.estado.codigo <> 'CANC'
        )
        ORDER BY a.fila ASC, a.letra ASC
    """)
    List<Asiento> listarDisponiblesPorVuelo(Long idVuelo);
}
package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.ReservaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservaAsientoRepository extends JpaRepository<ReservaAsiento, Long> {

    @Query("""
        SELECT COUNT(ra) > 0
        FROM ReservaAsiento ra
        WHERE ra.asiento.idAsiento = :idAsiento
        AND ra.vueloPasajero.vuelo.idVuelo = :idVuelo
        AND ra.vueloPasajero.estado.codigo <> 'CANC'
    """)
    boolean asientoOcupadoEnVuelo(Long idVuelo, Long idAsiento);

    @Query("""
        SELECT ra
        FROM ReservaAsiento ra
        LEFT JOIN FETCH ra.asiento a
        LEFT JOIN FETCH a.tipoAsiento
        LEFT JOIN FETCH ra.vueloPasajero vp
        WHERE vp.idVueloPasajero = :idReserva
    """)
    Optional<ReservaAsiento> obtenerPorReserva(Long idReserva);
}
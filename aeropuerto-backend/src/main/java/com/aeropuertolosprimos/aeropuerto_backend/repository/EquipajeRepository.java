package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Equipaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipajeRepository extends JpaRepository<Equipaje, Long> {

    List<Equipaje> findByVueloPasajero_IdVueloPasajero(Long idReserva);

    @Query("""
        SELECT e
        FROM Equipaje e
        LEFT JOIN FETCH e.vueloPasajero vp
        LEFT JOIN FETCH vp.vuelo v
        LEFT JOIN FETCH v.aerolinea
        LEFT JOIN FETCH vp.pasajero
        LEFT JOIN FETCH e.tipoEquipaje
        LEFT JOIN FETCH e.estado
        WHERE v.idVuelo = :idVuelo
        ORDER BY e.idEquipaje ASC
    """)
    List<Equipaje> buscarPorVueloParaReporte(Long idVuelo);
}
package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.VueloPasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VueloPasajeroRepository extends JpaRepository<VueloPasajero, Long> {

    @Query("""
        SELECT vp
        FROM VueloPasajero vp
        LEFT JOIN FETCH vp.vuelo v
        LEFT JOIN FETCH v.aerolinea
        LEFT JOIN FETCH v.avion
        LEFT JOIN FETCH v.aeropuertoOrigen
        LEFT JOIN FETCH v.aeropuertoDestino
        LEFT JOIN FETCH vp.pasajero
        LEFT JOIN FETCH vp.claseVuelo
        LEFT JOIN FETCH vp.estado
        WHERE vp.idVueloPasajero = :idReserva
    """)
    Optional<VueloPasajero> obtenerReservaConDetalle(Long idReserva);

    @Query("""
        SELECT COUNT(vp) > 0
        FROM VueloPasajero vp
        WHERE vp.vuelo.idVuelo = :idVuelo
        AND vp.pasajero.idPasajero = :idPasajero
        AND vp.estado.codigo <> 'CANC'
    """)
    boolean existeReservaActivaMismoVuelo(Long idVuelo, Long idPasajero);

    @Query("""
        SELECT COUNT(vp) > 0
        FROM VueloPasajero vp
        WHERE vp.pasajero.idPasajero = :idPasajero
        AND vp.estado.codigo <> 'CANC'
        AND (
            (:fechaSalida BETWEEN vp.vuelo.fechaSalida AND vp.vuelo.fechaLlegada)
            OR
            (:fechaLlegada BETWEEN vp.vuelo.fechaSalida AND vp.vuelo.fechaLlegada)
            OR
            (vp.vuelo.fechaSalida BETWEEN :fechaSalida AND :fechaLlegada)
        )
    """)
    boolean existeChoqueHorarioPasajero(
            Long idPasajero,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    );

    @Query("""
        SELECT vp
        FROM VueloPasajero vp
        LEFT JOIN FETCH vp.vuelo v
        LEFT JOIN FETCH v.aerolinea
        LEFT JOIN FETCH v.avion
        LEFT JOIN FETCH v.aeropuertoOrigen
        LEFT JOIN FETCH v.aeropuertoDestino
        LEFT JOIN FETCH v.estado
        LEFT JOIN FETCH vp.pasajero p
        LEFT JOIN FETCH vp.claseVuelo
        LEFT JOIN FETCH vp.estado
        WHERE v.idVuelo = :idVuelo
        AND LOWER(p.noPasaporte) = LOWER(:noPasaporte)
    """)
    Optional<VueloPasajero> buscarPorVueloYPasaporte(Long idVuelo, String noPasaporte);

    @Query("""
        SELECT vp
        FROM VueloPasajero vp
        LEFT JOIN FETCH vp.estado
        WHERE vp.vuelo.idVuelo = :idVuelo
        AND vp.estado.codigo NOT IN ('ABOR', 'CANC')
    """)
    List<VueloPasajero> listarPendientesDeAbordarPorVuelo(Long idVuelo);

    @Query("""
        SELECT vp
        FROM VueloPasajero vp
        LEFT JOIN FETCH vp.vuelo v
        LEFT JOIN FETCH v.aerolinea
        LEFT JOIN FETCH vp.pasajero p
        LEFT JOIN FETCH vp.claseVuelo
        LEFT JOIN FETCH vp.estado
        WHERE v.idVuelo = :idVuelo
        AND (vp.estado IS NULL OR vp.estado.codigo <> 'CANC')
        ORDER BY vp.idVueloPasajero ASC
    """)
    List<VueloPasajero> listarPasajerosReportePorVuelo(Long idVuelo);

    @Query("""
        SELECT new com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaResponse(
            vp.idVueloPasajero,
            vp.codigoPaseAbordar,
            vp.precioPagado,
            vp.fechaRegistro
        )
        FROM VueloPasajero vp
        JOIN vp.vuelo v
        JOIN v.aerolinea a
        WHERE vp.fechaRegistro >= :inicioDia
        AND vp.fechaRegistro < :finDia
        AND a.idAerolinea = :idAerolinea
        AND (vp.estado IS NULL OR vp.estado.codigo <> 'CANC')
        ORDER BY vp.fechaRegistro ASC, vp.idVueloPasajero ASC
    """)
    List<ReporteBoletosReservadosDiaResponse> listarBoletosReservadosPorDia(
            Long idAerolinea,
            LocalDateTime inicioDia,
            LocalDateTime finDia
    );
}

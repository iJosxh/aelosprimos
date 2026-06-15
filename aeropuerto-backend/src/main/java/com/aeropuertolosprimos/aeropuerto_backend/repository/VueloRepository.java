package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.tripulacion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    ORDER BY v.fechaSalida DESC
""")
    List<Vuelo> listarVuelos();

    @Query("""
        SELECT COUNT(v) > 0
        FROM Vuelo v
        WHERE v.avion.idAvion = :avionId
        AND (
            (:fechaSalida BETWEEN v.fechaSalida AND v.fechaLlegada)
            OR
            (:fechaLlegada BETWEEN v.fechaSalida AND v.fechaLlegada)
            OR
            (v.fechaSalida BETWEEN :fechaSalida AND :fechaLlegada)
        )
    """)
    boolean existeChoqueHorarioAvion(
            Long avionId,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    );

    @Query("""
        SELECT COUNT(v) > 0
        FROM Vuelo v
        WHERE v.tripulacion.idTripulacion = :tripulacionId
        AND (
            (:fechaSalida BETWEEN v.fechaSalida AND v.fechaLlegada)
            OR
            (:fechaLlegada BETWEEN v.fechaSalida AND v.fechaLlegada)
            OR
            (v.fechaSalida BETWEEN :fechaSalida AND :fechaLlegada)
        )
    """)
    boolean existeChoqueHorarioTripulacion(
            Long tripulacionId,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    );

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.tripulacion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.idVuelo = :id
""")
    Vuelo obtenerPorId(Long id);

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.aeropuertoOrigen.idAeropuerto = :origenId
    AND v.aeropuertoDestino.idAeropuerto = :destinoId
    AND v.fechaSalida >= :inicioDia
    AND v.fechaSalida < :finDia
    AND v.fechaSalida >= :fechaActual
    AND v.estado.codigo = 'PROG'
    ORDER BY v.fechaSalida ASC
""")
    List<Vuelo> buscarVuelosDisponiblesParaReserva(
            Long origenId,
            Long destinoId,
            LocalDateTime inicioDia,
            LocalDateTime finDia,
            LocalDateTime fechaActual
    );

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.idVuelo = :idVuelo
""")
    Optional<Vuelo> obtenerDetalleReserva(Long idVuelo);

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.estado.codigo = 'ABOR'
    AND v.fechaSalida >= :fechaMinima
    ORDER BY v.fechaSalida ASC
""")
    List<Vuelo> listarVuelosParaAbordaje(LocalDateTime fechaMinima);

    @Query("""
        SELECT v
        FROM Vuelo v
        LEFT JOIN FETCH v.aerolinea
        LEFT JOIN FETCH v.avion
        LEFT JOIN FETCH v.aeropuertoOrigen
        LEFT JOIN FETCH v.aeropuertoDestino
        LEFT JOIN FETCH v.estado
        WHERE UPPER(TRIM(v.codigoVuelo)) = UPPER(TRIM(:codigoVuelo))
    """)
    Optional<Vuelo> buscarConsultaPorCodigoVuelo(String codigoVuelo);

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.idVuelo = :idVuelo
""")
    Optional<Vuelo> obtenerVueloParaAbordaje(Long idVuelo);

    @Query("""
    SELECT new com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosResponse(
        v.idVuelo,
        v.codigoVuelo,
        av.modelo,
        ae.nombre,
        ao.nombre,
        ad.nombre,
        v.fechaSalida,
        v.fechaLlegada
    )
    FROM Vuelo v
    LEFT JOIN v.avion av
    LEFT JOIN v.aerolinea ae
    LEFT JOIN v.aeropuertoOrigen ao
    LEFT JOIN v.aeropuertoDestino ad
    ORDER BY v.fechaSalida ASC
""")
    List<ReporteListadoVuelosResponse> buscarReporteListadoVuelosTodos();


    @Query("""
    SELECT new com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosResponse(
        v.idVuelo,
        v.codigoVuelo,
        av.modelo,
        ae.nombre,
        ao.nombre,
        ad.nombre,
        v.fechaSalida,
        v.fechaLlegada
    )
    FROM Vuelo v
    LEFT JOIN v.avion av
    LEFT JOIN v.aerolinea ae
    LEFT JOIN v.aeropuertoOrigen ao
    LEFT JOIN v.aeropuertoDestino ad
    WHERE v.fechaSalida >= :fechaDesde
    AND v.fechaSalida <= :fechaHasta
    ORDER BY v.fechaSalida ASC
""")
    List<ReporteListadoVuelosResponse> buscarReporteListadoVuelosPorRango(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    );

    @Query("""
    SELECT DISTINCT v
    FROM Vuelo v
    LEFT JOIN FETCH v.aerolinea
    LEFT JOIN FETCH v.avion
    LEFT JOIN FETCH v.aeropuertoOrigen
    LEFT JOIN FETCH v.aeropuertoDestino
    LEFT JOIN FETCH v.estado
    LEFT JOIN FETCH v.escalas e
    LEFT JOIN FETCH e.aeropuerto
    WHERE v.estado.codigo = 'PROG'
    AND v.fechaSalida >= :fechaDesde
    AND v.fechaSalida <= :fechaHasta
    ORDER BY v.fechaSalida ASC
""")
    List<Vuelo> listarVuelosProgramadosProximosParaAbordaje(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta
    );

}
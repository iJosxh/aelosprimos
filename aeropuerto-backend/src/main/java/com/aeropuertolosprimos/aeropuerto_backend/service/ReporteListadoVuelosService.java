package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteListadoVuelosService {

    private final VueloRepository vueloRepository;
    private final ReporteArchivoService reporteArchivoService;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final List<String> ENCABEZADOS = List.of(
            "Número de vuelo",
            "Modelo de avión",
            "Aerolínea",
            "Origen",
            "Destino",
            "Fecha salida",
            "Hora salida",
            "Fecha llegada",
            "Hora llegada"
    );

    public ReporteListadoVuelosService(
            VueloRepository vueloRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.vueloRepository = vueloRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public List<ReporteListadoVuelosResponse> buscar(ReporteListadoVuelosFiltroRequest filtro) {
        RangoFechas rango = validarFiltros(filtro);

        List<ReporteListadoVuelosResponse> vuelos;

        if (rango.fechaHoraDesde() == null && rango.fechaHoraHasta() == null) {
            vuelos = vueloRepository.buscarReporteListadoVuelosTodos();
        } else {
            vuelos = vueloRepository.buscarReporteListadoVuelosPorRango(
                    rango.fechaHoraDesde(),
                    rango.fechaHoraHasta()
            );
        }

        if (vuelos.isEmpty()) {
            throw new RuntimeException("No se encontraron vuelos según los parámetros ingresados");
        }

        return vuelos;
    }

    public byte[] generarPdf(ReporteListadoVuelosFiltroRequest filtro) {
        List<ReporteListadoVuelosResponse> vuelos = buscar(filtro);

        return reporteArchivoService.generarPdf(
                "Reporte de vuelos por fecha y hora",
                ENCABEZADOS,
                construirFilasReporte(vuelos)
        );
    }

    public byte[] generarExcel(ReporteListadoVuelosFiltroRequest filtro) {
        List<ReporteListadoVuelosResponse> vuelos = buscar(filtro);

        return reporteArchivoService.generarExcel(
                "Reporte vuelos",
                ENCABEZADOS,
                construirFilasReporte(vuelos)
        );
    }

    private RangoFechas validarFiltros(ReporteListadoVuelosFiltroRequest filtro) {
        if (filtro == null) {
            return new RangoFechas(null, null);
        }

        boolean tieneFechaDesde = filtro.getFechaDesde() != null;
        boolean tieneHoraDesde = filtro.getHoraDesde() != null;
        boolean tieneFechaHasta = filtro.getFechaHasta() != null;
        boolean tieneHoraHasta = filtro.getHoraHasta() != null;

        boolean tieneAlgunFiltro = tieneFechaDesde || tieneHoraDesde || tieneFechaHasta || tieneHoraHasta;

        if (!tieneAlgunFiltro) {
            return new RangoFechas(null, null);
        }

        if (!(tieneFechaDesde && tieneHoraDesde && tieneFechaHasta && tieneHoraHasta)) {
            throw new RuntimeException("Debe ingresar fecha desde, hora desde, fecha hasta y hora hasta");
        }

        LocalDateTime fechaHoraDesde = LocalDateTime.of(filtro.getFechaDesde(), filtro.getHoraDesde());
        LocalDateTime fechaHoraHasta = LocalDateTime.of(filtro.getFechaHasta(), filtro.getHoraHasta());

        if (fechaHoraHasta.isBefore(fechaHoraDesde)) {
            throw new RuntimeException("La fecha y hora hasta debe ser mayor a la fecha y hora desde");
        }

        if (fechaHoraDesde.plusDays(30).isBefore(fechaHoraHasta)) {
            throw new RuntimeException("El rango máximo de consulta es de 30 días");
        }

        return new RangoFechas(fechaHoraDesde, fechaHoraHasta);
    }

    private List<List<String>> construirFilasReporte(List<ReporteListadoVuelosResponse> vuelos) {
        return vuelos.stream()
                .map(vuelo -> List.of(
                        valor(vuelo.getNumeroVuelo()),
                        valor(vuelo.getModeloAvion()),
                        valor(vuelo.getAerolinea()),
                        valor(vuelo.getOrigen()),
                        valor(vuelo.getDestino()),
                        vuelo.getFechaSalida() != null ? vuelo.getFechaSalida().format(FORMATO_FECHA) : "",
                        vuelo.getHoraSalida() != null ? vuelo.getHoraSalida().format(FORMATO_HORA) : "",
                        vuelo.getFechaLlegada() != null ? vuelo.getFechaLlegada().format(FORMATO_FECHA) : "",
                        vuelo.getHoraLlegada() != null ? vuelo.getHoraLlegada().format(FORMATO_HORA) : ""
                ))
                .toList();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private record RangoFechas(LocalDateTime fechaHoraDesde, LocalDateTime fechaHoraHasta) {
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteListadoVuelosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteListadoVuelosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/listado-vuelos")
@CrossOrigin(origins = "*")
public class ReporteListadoVuelosController {

    private final ReporteListadoVuelosService reporteListadoVuelosService;

    public ReporteListadoVuelosController(ReporteListadoVuelosService reporteListadoVuelosService) {
        this.reporteListadoVuelosService = reporteListadoVuelosService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaHasta
    ) {
        try {
            ReporteListadoVuelosFiltroRequest filtro = construirFiltro(
                    fechaDesde,
                    horaDesde,
                    fechaHasta,
                    horaHasta
            );

            List<ReporteListadoVuelosResponse> response = reporteListadoVuelosService.buscar(filtro);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaHasta
    ) {
        try {
            ReporteListadoVuelosFiltroRequest filtro = construirFiltro(
                    fechaDesde,
                    horaDesde,
                    fechaHasta,
                    horaHasta
            );

            byte[] archivo = reporteListadoVuelosService.generarPdf(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-listado-vuelos.pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaHasta
    ) {
        try {
            ReporteListadoVuelosFiltroRequest filtro = construirFiltro(
                    fechaDesde,
                    horaDesde,
                    fechaHasta,
                    horaHasta
            );

            byte[] archivo = reporteListadoVuelosService.generarExcel(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-listado-vuelos.xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    private ReporteListadoVuelosFiltroRequest construirFiltro(
            LocalDate fechaDesde,
            LocalTime horaDesde,
            LocalDate fechaHasta,
            LocalTime horaHasta
    ) {
        return new ReporteListadoVuelosFiltroRequest(
                fechaDesde,
                horaDesde,
                fechaHasta,
                horaHasta
        );
    }
}
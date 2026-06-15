package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteBoletosReservadosDiaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/boletos-reservados-dia")
@CrossOrigin(origins = "*")
public class ReporteBoletosReservadosDiaController {

    private final ReporteBoletosReservadosDiaService reporteBoletosReservadosDiaService;

    public ReporteBoletosReservadosDiaController(
            ReporteBoletosReservadosDiaService reporteBoletosReservadosDiaService
    ) {
        this.reporteBoletosReservadosDiaService = reporteBoletosReservadosDiaService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            Authentication authentication
    ) {
        try {
            List<ReporteBoletosReservadosDiaResponse> response =
                    reporteBoletosReservadosDiaService.buscar(
                            new ReporteBoletosReservadosDiaFiltroRequest(fecha),
                            obtenerUsername(authentication)
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            Authentication authentication
    ) {
        try {
            byte[] archivo = reporteBoletosReservadosDiaService.generarPdf(
                    new ReporteBoletosReservadosDiaFiltroRequest(fecha),
                    obtenerUsername(authentication)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-boletos-reservados-dia.pdf")
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
            LocalDate fecha,
            Authentication authentication
    ) {
        try {
            byte[] archivo = reporteBoletosReservadosDiaService.generarExcel(
                    new ReporteBoletosReservadosDiaFiltroRequest(fecha),
                    obtenerUsername(authentication)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-boletos-reservados-dia.xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    private String obtenerUsername(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}

package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAerolineasAeropuertoFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAerolineasAeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteAerolineasAeropuertoService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/aerolineas-aeropuerto")
@CrossOrigin(origins = "*")
public class ReporteAerolineasAeropuertoController {

    private final ReporteAerolineasAeropuertoService reporteAerolineasAeropuertoService;

    public ReporteAerolineasAeropuertoController(
            ReporteAerolineasAeropuertoService reporteAerolineasAeropuertoService
    ) {
        this.reporteAerolineasAeropuertoService = reporteAerolineasAeropuertoService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@ModelAttribute ReporteAerolineasAeropuertoFiltro filtro) {
        try {
            List<ReporteAerolineasAeropuertoResponse> response = reporteAerolineasAeropuertoService.buscar(filtro);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(@ModelAttribute ReporteAerolineasAeropuertoFiltro filtro) {
        try {
            byte[] archivo = reporteAerolineasAeropuertoService.generarPdf(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-aerolineas-aeropuerto.pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(@ModelAttribute ReporteAerolineasAeropuertoFiltro filtro) {
        try {
            byte[] archivo = reporteAerolineasAeropuertoService.generarExcel(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-aerolineas-aeropuerto.xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
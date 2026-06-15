package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReportePasajerosVueloFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReportePasajerosVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReportePasajerosVueloService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/pasajeros-vuelo")
@CrossOrigin(origins = "*")
public class ReportePasajerosVueloController {

    private final ReportePasajerosVueloService reportePasajerosVueloService;

    public ReportePasajerosVueloController(ReportePasajerosVueloService reportePasajerosVueloService) {
        this.reportePasajerosVueloService = reportePasajerosVueloService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam(required = false) String numeroVuelo) {
        try {
            List<ReportePasajerosVueloResponse> response = reportePasajerosVueloService.buscar(
                    new ReportePasajerosVueloFiltro(numeroVuelo)
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(@RequestParam(required = false) String numeroVuelo) {
        try {
            byte[] archivo = reportePasajerosVueloService.generarPdf(
                    new ReportePasajerosVueloFiltro(numeroVuelo)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-pasajeros-vuelo-" + numeroVuelo + ".pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(@RequestParam(required = false) String numeroVuelo) {
        try {
            byte[] archivo = reportePasajerosVueloService.generarExcel(
                    new ReportePasajerosVueloFiltro(numeroVuelo)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-pasajeros-vuelo-" + numeroVuelo + ".xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
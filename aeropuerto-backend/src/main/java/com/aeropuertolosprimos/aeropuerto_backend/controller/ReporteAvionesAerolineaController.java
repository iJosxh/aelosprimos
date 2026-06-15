package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAvionesAerolineaFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAvionesAerolineaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteAvionesAerolineaService;
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
@RequestMapping("/reportes/aviones-aerolinea")
@CrossOrigin(origins = "*")
public class ReporteAvionesAerolineaController {

    private final ReporteAvionesAerolineaService reporteService;

    public ReporteAvionesAerolineaController(ReporteAvionesAerolineaService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam(required = false) Long idAerolinea) {
        try {
            ReporteAvionesAerolineaFiltro filtro = new ReporteAvionesAerolineaFiltro(idAerolinea);
            List<ReporteAvionesAerolineaResponse> response = reporteService.buscar(filtro);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(@RequestParam(required = false) Long idAerolinea) {
        try {
            ReporteAvionesAerolineaFiltro filtro = new ReporteAvionesAerolineaFiltro(idAerolinea);
            byte[] archivo = reporteService.generarPdf(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-aviones-aerolinea.pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(@RequestParam(required = false) Long idAerolinea) {
        try {
            ReporteAvionesAerolineaFiltro filtro = new ReporteAvionesAerolineaFiltro(idAerolinea);
            byte[] archivo = reporteService.generarExcel(filtro);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-aviones-aerolinea.xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
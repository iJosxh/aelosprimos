package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteEquipajeVueloRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteEquipajeVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteEquipajeVueloService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/equipaje-vuelo")
@CrossOrigin(origins = "*")
public class ReporteEquipajeVueloController {

    private final ReporteEquipajeVueloService reporteEquipajeVueloService;

    public ReporteEquipajeVueloController(ReporteEquipajeVueloService reporteEquipajeVueloService) {
        this.reporteEquipajeVueloService = reporteEquipajeVueloService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @ModelAttribute ReporteEquipajeVueloRequest request,
            Authentication authentication
    ) {
        try {
            List<ReporteEquipajeVueloResponse> response = reporteEquipajeVueloService.buscar(
                    request,
                    obtenerUsername(authentication)
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(
            @ModelAttribute ReporteEquipajeVueloRequest request,
            Authentication authentication
    ) {
        try {
            byte[] archivo = reporteEquipajeVueloService.generarPdf(
                    request,
                    obtenerUsername(authentication)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-equipaje-vuelo-" + limpiarNombreArchivo(request.getNumeroVuelo()) + ".pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(
            @ModelAttribute ReporteEquipajeVueloRequest request,
            Authentication authentication
    ) {
        try {
            byte[] archivo = reporteEquipajeVueloService.generarExcel(
                    request,
                    obtenerUsername(authentication)
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("reporte-equipaje-vuelo-" + limpiarNombreArchivo(request.getNumeroVuelo()) + ".xlsx")
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

    private String limpiarNombreArchivo(String texto) {
        if (texto == null || texto.isBlank()) {
            return "sin-numero";
        }

        return texto.trim().replaceAll("[^a-zA-Z0-9-_]", "-");
    }
}
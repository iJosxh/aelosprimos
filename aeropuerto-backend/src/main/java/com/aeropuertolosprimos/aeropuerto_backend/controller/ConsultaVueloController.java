package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ConsultaVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ConsultaVueloService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/consulta-vuelos")
@CrossOrigin(origins = "*")
public class ConsultaVueloController {

    private final ConsultaVueloService consultaVueloService;

    public ConsultaVueloController(ConsultaVueloService consultaVueloService) {
        this.consultaVueloService = consultaVueloService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String numeroVuelo) {
        try {
            ConsultaVueloResponse response = consultaVueloService.consultarPorNumeroVuelo(numeroVuelo);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<?> generarPdf(@RequestParam String numeroVuelo) {
        try {
            byte[] archivo = consultaVueloService.generarPdf(numeroVuelo);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("consulta-vuelo-" + numeroVuelo + ".pdf")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<?> generarExcel(@RequestParam String numeroVuelo) {
        try {
            byte[] archivo = consultaVueloService.generarExcel(numeroVuelo);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename("consulta-vuelo-" + numeroVuelo + ".xlsx")
                            .build()
                            .toString())
                    .body(archivo);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
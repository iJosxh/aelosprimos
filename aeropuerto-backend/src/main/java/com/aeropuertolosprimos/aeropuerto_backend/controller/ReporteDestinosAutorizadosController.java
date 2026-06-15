package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReporteDestinosAutorizadosService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reportes/destinos-autorizados")
@CrossOrigin(origins = "*")
public class ReporteDestinosAutorizadosController {

    private final ReporteDestinosAutorizadosService reporteDestinosAutorizadosService;

    public ReporteDestinosAutorizadosController(
            ReporteDestinosAutorizadosService reporteDestinosAutorizadosService
    ) {
        this.reporteDestinosAutorizadosService = reporteDestinosAutorizadosService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ReporteDestinosAutorizadosResponse>> buscar(
            @ModelAttribute ReporteDestinosAutorizadosFiltroRequest filtros
    ) {
        return ResponseEntity.ok(reporteDestinosAutorizadosService.buscar(filtros));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generarPdf(
            @ModelAttribute ReporteDestinosAutorizadosFiltroRequest filtros
    ) {
        byte[] archivo = reporteDestinosAutorizadosService.generarPdf(filtros);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte-destinos-autorizados.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> generarExcel(
            @ModelAttribute ReporteDestinosAutorizadosFiltroRequest filtros
    ) {
        byte[] archivo = reporteDestinosAutorizadosService.generarExcel(filtros);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte-destinos-autorizados.xlsx"
                )
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(archivo);
    }
}

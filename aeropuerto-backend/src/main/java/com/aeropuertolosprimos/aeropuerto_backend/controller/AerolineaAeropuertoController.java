package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaAeropuertoRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaAeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.AerolineaAeropuertoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aerolineas-aeropuertos")
@CrossOrigin(origins = "http://localhost:4200")
public class AerolineaAeropuertoController {

    private final AerolineaAeropuertoService aerolineaAeropuertoService;

    public AerolineaAeropuertoController(AerolineaAeropuertoService aerolineaAeropuertoService) {
        this.aerolineaAeropuertoService = aerolineaAeropuertoService;
    }

    @PostMapping
    public ResponseEntity<List<AerolineaAeropuertoResponse>> autorizar(
            @RequestBody AerolineaAeropuertoRequest request
    ) {
        return ResponseEntity.ok(aerolineaAeropuertoService.autorizar(request));
    }

    @GetMapping
    public ResponseEntity<List<AerolineaAeropuertoResponse>> listarTodos() {
        return ResponseEntity.ok(aerolineaAeropuertoService.listarTodos());
    }

    @GetMapping("/aerolinea/{idAerolinea}")
    public ResponseEntity<List<AerolineaAeropuertoResponse>> listarPorAerolinea(
            @PathVariable Long idAerolinea
    ) {
        return ResponseEntity.ok(aerolineaAeropuertoService.listarPorAerolinea(idAerolinea));
    }
}
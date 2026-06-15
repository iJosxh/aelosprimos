package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.AerolineaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aerolineas")
@CrossOrigin(origins = "*")
public class AerolineaController {

    private final AerolineaService aerolineaService;

    public AerolineaController(AerolineaService aerolineaService) {
        this.aerolineaService = aerolineaService;
    }

    @PostMapping
    public ResponseEntity<AerolineaResponse> registrar(@RequestBody AerolineaRequest request) {
        return ResponseEntity.ok(aerolineaService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<AerolineaResponse>> listarActivas() {
        return ResponseEntity.ok(aerolineaService.listarActivas());
    }

    @GetMapping("/todas")
    public ResponseEntity<List<AerolineaResponse>> listarTodas() {
        return ResponseEntity.ok(aerolineaService.listarTodas());
    }
}
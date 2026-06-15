package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AeropuertoRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.AeropuertoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aeropuertos")
@CrossOrigin(origins = "*")
public class AeropuertoController {

    private final AeropuertoService aeropuertoService;

    public AeropuertoController(AeropuertoService aeropuertoService) {
        this.aeropuertoService = aeropuertoService;
    }

    @PostMapping
    public ResponseEntity<AeropuertoResponse> registrar(@RequestBody AeropuertoRequest request) {
        return ResponseEntity.ok(aeropuertoService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<AeropuertoResponse>> listarTodos() {
        return ResponseEntity.ok(aeropuertoService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AeropuertoResponse>> listarActivos() {
        return ResponseEntity.ok(aeropuertoService.listarActivos());
    }
}
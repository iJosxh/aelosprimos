package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AvionRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AvionResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.AvionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aviones")
@CrossOrigin(origins = "*")
public class AvionController {

    private final AvionService avionService;

    public AvionController(AvionService avionService) {
        this.avionService = avionService;
    }

    @PostMapping
    public ResponseEntity<AvionResponse> registrar(@RequestBody AvionRequest request) {
        return ResponseEntity.ok(avionService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<AvionResponse>> listarTodos() {
        return ResponseEntity.ok(avionService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AvionResponse>> listarActivos() {
        return ResponseEntity.ok(avionService.listarActivos());
    }
}

package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.CrearTripulacionRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulacionResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.TripulacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tripulaciones")
@CrossOrigin(origins = "*")
public class TripulacionController {

    private final TripulacionService tripulacionService;

    public TripulacionController(TripulacionService tripulacionService) {
        this.tripulacionService = tripulacionService;
    }

    @PostMapping
    public ResponseEntity<TripulacionResponse> crear(
            @RequestBody CrearTripulacionRequest request
    ) {
        return ResponseEntity.ok(tripulacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<TripulacionResponse>> listar() {
        return ResponseEntity.ok(tripulacionService.listar());
    }
}
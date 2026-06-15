package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulanteRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulanteResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.service.TripulanteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tripulantes")
@CrossOrigin(origins = "*")
public class TripulanteController {

    private final TripulanteService tripulanteService;

    public TripulanteController(TripulanteService tripulanteService) {
        this.tripulanteService = tripulanteService;
    }

    @PostMapping
    public ResponseEntity<TripulanteResponse> registrar(@RequestBody TripulanteRequest request) {
        return ResponseEntity.ok(tripulanteService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<TripulanteResponse>> listar(
            @RequestParam(required = false) Long idAerolinea
    ) {
        return ResponseEntity.ok(tripulanteService.listar(idAerolinea));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<TripulanteResponse>> listarDisponibles(
            @RequestParam Long idAerolinea,
            @RequestParam String cargo
    ) {
        return ResponseEntity.ok(tripulanteService.listarDisponibles(idAerolinea, cargo));
    }

    @GetMapping("/cargos")
    public ResponseEntity<List<CatalogoDetalle>> listarCargos() {
        return ResponseEntity.ok(tripulanteService.listarCargosTripulante());
    }
}
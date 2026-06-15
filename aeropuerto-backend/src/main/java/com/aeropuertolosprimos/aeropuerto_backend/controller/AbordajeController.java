package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AbordajePasajeroResponse;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AbordajeVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AbordarPasajeroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.FinalizarAbordajeResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.AbordajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/abordaje")
@CrossOrigin(origins = "*")
public class AbordajeController {

    private final AbordajeService abordajeService;

    public AbordajeController(AbordajeService abordajeService) {
        this.abordajeService = abordajeService;
    }

    @GetMapping("/vuelos")
    public ResponseEntity<?> listarVuelosParaAbordaje() {
        try {
            List<AbordajeVueloResponse> response = abordajeService.listarVuelosParaAbordaje();
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/vuelos/{idVuelo}/pasajero")
    public ResponseEntity<?> buscarPasajero(
            @PathVariable Long idVuelo,
            @RequestParam String noPasaporte
    ) {
        try {
            AbordajePasajeroResponse response = abordajeService.buscarPasajero(idVuelo, noPasaporte);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/abordar")
    public ResponseEntity<?> abordarPasajero(@RequestBody AbordarPasajeroRequest request) {
        try {
            AbordajePasajeroResponse response = abordajeService.abordarPasajero(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/vuelos/programados-proximos")
    public ResponseEntity<?> listarVuelosProgramadosProximos() {
        try {
            return ResponseEntity.ok(abordajeService.listarVuelosProgramadosProximos());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/vuelos/{idVuelo}/iniciar")
    public ResponseEntity<?> iniciarAbordaje(@PathVariable Long idVuelo) {
        try {
            AbordajeVueloResponse response = abordajeService.iniciarAbordaje(idVuelo);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/vuelos/{idVuelo}/finalizar")
    public ResponseEntity<?> finalizarAbordaje(@PathVariable Long idVuelo) {
        try {
            FinalizarAbordajeResponse response = abordajeService.finalizarAbordaje(idVuelo);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
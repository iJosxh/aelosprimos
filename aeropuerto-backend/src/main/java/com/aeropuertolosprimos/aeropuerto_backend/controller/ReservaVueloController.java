package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.*;
import com.aeropuertolosprimos.aeropuerto_backend.service.ReservaVueloService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservaVueloController {

    private final ReservaVueloService reservaVueloService;

    public ReservaVueloController(ReservaVueloService reservaVueloService) {
        this.reservaVueloService = reservaVueloService;
    }

    // =========================================================
    // 1. LISTAR AEROPUERTOS PARA FILTRO
    // =========================================================

    @GetMapping("/aeropuertos")
    public ResponseEntity<?> listarAeropuertos() {
        try {
            List<AeropuertoResponse> response = reservaVueloService.listarAeropuertosActivos();
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 2. LISTAR CLASES DE VUELO
    // =========================================================

    @GetMapping("/clases")
    public ResponseEntity<?> listarClasesVuelo() {
        try {
            List<ClaseVueloResponse> response = reservaVueloService.listarClasesVuelo();
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 3. BUSCAR VUELOS DISPONIBLES
    // Ejemplo:
    // /reservas/vuelos-disponibles?origenId=1&destinoId=2&fechaSalida=2026-05-20
    // =========================================================

    @GetMapping("/vuelos-disponibles")
    public ResponseEntity<?> buscarVuelosDisponibles(
            @RequestParam Long origenId,
            @RequestParam Long destinoId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaSalida
    ) {
        try {
            List<VueloDisponibleReservaResponse> response =
                    reservaVueloService.buscarVuelosDisponibles(
                            origenId,
                            destinoId,
                            fechaSalida
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 4. VER DETALLE DEL VUELO
    // =========================================================

    @GetMapping("/vuelos/{idVuelo}/detalle")
    public ResponseEntity<?> obtenerDetalleVuelo(@PathVariable Long idVuelo) {
        try {
            VueloDetalleReservaResponse response =
                    reservaVueloService.obtenerDetalleVuelo(idVuelo);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 5. LISTAR ASIENTOS DISPONIBLES DEL VUELO
    // =========================================================

    @GetMapping("/vuelos/{idVuelo}/asientos-disponibles")
    public ResponseEntity<?> listarAsientosDisponibles(@PathVariable Long idVuelo) {
        try {
            List<AsientoDisponibleResponse> response =
                    reservaVueloService.listarAsientosDisponibles(idVuelo);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 6. CREAR RESERVA
    // El pasajero se toma desde el usuario autenticado por JWT.
    // =========================================================

    @PostMapping
    public ResponseEntity<?> reservarVuelo(
            @RequestBody ReservaVueloRequest request,
            Authentication authentication
    ) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(
                        Map.of("mensaje", "No se pudo identificar el usuario autenticado")
                );
            }

            String username = authentication.getName();

            ReservaVueloResponse response =
                    reservaVueloService.reservarVuelo(request, username);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // =========================================================
    // 7. OBTENER PASE DE ABORDAR
    // =========================================================

    @GetMapping("/pase-abordar/{idReserva}")
    public ResponseEntity<?> obtenerPaseAbordar(@PathVariable Long idReserva) {
        try {
            PaseAbordarResponse response =
                    reservaVueloService.obtenerPaseAbordar(idReserva);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
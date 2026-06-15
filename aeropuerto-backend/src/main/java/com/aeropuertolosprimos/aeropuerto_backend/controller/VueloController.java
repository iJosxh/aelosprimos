package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.VueloRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.VueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.service.VueloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/vuelos")
@CrossOrigin(origins = "*")
public class VueloController {

    private final VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody VueloRequest request) {

        try {

            VueloResponse response = vueloService.crearVuelo(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<VueloResponse>> listar() {

        return ResponseEntity.ok(vueloService.listar());
    }

    @GetMapping("/validar-aviones-activos")
    public ResponseEntity<?> validarAvionesActivos(@RequestParam Long aerolineaId) {

        Long cantidad = vueloService.contarAvionesActivosPorAerolinea(aerolineaId);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "tieneAvionesActivos", cantidad > 0,
                        "cantidad", cantidad
                )
        );
    }

    @GetMapping("/aviones-disponibles")
    public ResponseEntity<?> avionesDisponibles(

            @RequestParam Long aerolineaId,
            @RequestParam String fechaSalida,
            @RequestParam String fechaLlegada
    ) {

        try {

            return ResponseEntity.ok(
                    vueloService.obtenerAvionesDisponibles(
                            aerolineaId,
                            LocalDateTime.parse(fechaSalida),
                            LocalDateTime.parse(fechaLlegada)
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tripulaciones-disponibles")
    public ResponseEntity<?> tripulacionesDisponibles(

            @RequestParam Long aerolineaId,
            @RequestParam String fechaSalida,
            @RequestParam String fechaLlegada
    ) {

        try {

            return ResponseEntity.ok(
                    vueloService.obtenerTripulacionesDisponibles(
                            aerolineaId,
                            LocalDateTime.parse(fechaSalida),
                            LocalDateTime.parse(fechaLlegada)
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validar-aeropuertos-autorizados")
    public ResponseEntity<?> validarAeropuertosAutorizados(@RequestParam Long aerolineaId) {

        Long cantidad = vueloService.contarAeropuertosAutorizados(aerolineaId);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "tieneAeropuertosAutorizados", cantidad > 0,
                        "cantidad", cantidad
                )
        );
    }

    @GetMapping("/aeropuertos-autorizados")
    public ResponseEntity<?> aeropuertosAutorizados(@RequestParam Long aerolineaId) {

        return ResponseEntity.ok(
                vueloService.listarAeropuertosAutorizados(aerolineaId)
        );
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        try {

            return ResponseEntity.ok(vueloService.obtenerDetalle(id));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
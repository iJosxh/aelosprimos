package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.service.PasajeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pasajeros")
@CrossOrigin(origins = "*")
public class PasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    @GetMapping("/existe-pasaporte/{pasaporte}")
    public ResponseEntity<Boolean> existePasaporte(@PathVariable String pasaporte) {

        boolean existe = pasajeroService.existePasaporte(pasaporte);

        return ResponseEntity.ok(existe);
    }

    @GetMapping("/existe-correo/{correo}")
    public ResponseEntity<Boolean> existeCorreo(@PathVariable String correo) {

        boolean existe = pasajeroService.existeCorreo(correo);

        return ResponseEntity.ok(existe);
    }
}

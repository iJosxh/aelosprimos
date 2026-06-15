package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.RegistroDTO;
import com.aeropuertolosprimos.aeropuerto_backend.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/registro")
@CrossOrigin(origins = "*")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroDTO dto) {

        registroService.registrarCompleto(dto);

        return ResponseEntity.ok(Map.of("message", "Registro exitoso"));
    }
}
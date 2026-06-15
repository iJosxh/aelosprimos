package com.aeropuertolosprimos.aeropuerto_backend.controller;

import com.aeropuertolosprimos.aeropuerto_backend.dto.UsuarioAdminRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.UsuarioAdminResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.service.UsuarioAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios-administrativos")
@CrossOrigin(origins = "*")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @PostMapping
    public ResponseEntity<UsuarioAdminResponse> registrar(@RequestBody UsuarioAdminRequest request) {
        return ResponseEntity.ok(usuarioAdminService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioAdminResponse>> listar() {
        return ResponseEntity.ok(usuarioAdminService.listar());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<CatalogoDetalle>> listarRolesAdministrativos() {
        return ResponseEntity.ok(usuarioAdminService.listarRolesAdministrativos());
    }
}
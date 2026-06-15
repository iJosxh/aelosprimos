package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AeropuertoRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aeropuerto;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AeropuertoRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AeropuertoService {

    private final AeropuertoRepository aeropuertoRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public AeropuertoService(
            AeropuertoRepository aeropuertoRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.aeropuertoRepository = aeropuertoRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public AeropuertoResponse registrar(AeropuertoRequest request) {
        validarRequest(request);

        String nombre = request.getNombre().trim();
        String ciudad = request.getCiudad().trim();
        String pais = request.getPais().trim();

        boolean existe = aeropuertoRepository.existsByNombreIgnoreCaseAndCiudadIgnoreCaseAndPaisIgnoreCase(
                nombre,
                ciudad,
                pais
        );

        if (existe) {
            throw new IllegalArgumentException("El aeropuerto ingresado ya existe.");
        }

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Aeropuerto aeropuerto = new Aeropuerto();
        aeropuerto.setNombre(nombre);
        aeropuerto.setCiudad(ciudad);
        aeropuerto.setPais(pais);
        aeropuerto.setEstado(estadoActivo);
        aeropuerto.setUsuarioCreacion(usuarioActual);

        Aeropuerto guardado = aeropuertoRepository.save(aeropuerto);

        return convertirAResponse(guardado);
    }

    public List<AeropuertoResponse> listarTodos() {
        return aeropuertoRepository.listarTodos()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<AeropuertoResponse> listarActivos() {
        return aeropuertoRepository.listarActivos()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private void validarRequest(AeropuertoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el nombre del aeropuerto.");
        }

        if (request.getCiudad() == null || request.getCiudad().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar la ciudad del aeropuerto.");
        }

        if (request.getPais() == null || request.getPais().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el país del aeropuerto.");
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();

            return usuarioRepository.findByUsername(username)
                    .orElseGet(() -> usuarioRepository.findById(1L)
                            .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario del sistema.")));
        }

        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario del sistema."));
    }

    private AeropuertoResponse convertirAResponse(Aeropuerto aeropuerto) {
        AeropuertoResponse response = new AeropuertoResponse();

        response.setIdAeropuerto(aeropuerto.getIdAeropuerto());
        response.setNombre(aeropuerto.getNombre());
        response.setCiudad(aeropuerto.getCiudad());
        response.setPais(aeropuerto.getPais());

        if (aeropuerto.getEstado() != null) {
            response.setIdEstado(aeropuerto.getEstado().getId());
            response.setCodigoEstado(aeropuerto.getEstado().getCodigo());
            response.setEstado(aeropuerto.getEstado().getValor());
        }

        return response;
    }
}
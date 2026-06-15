package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AerolineaService {

    private final AerolineaRepository aerolineaRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public AerolineaService(
            AerolineaRepository aerolineaRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.aerolineaRepository = aerolineaRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public AerolineaResponse registrar(AerolineaRequest request) {
        validarRequest(request);

        String nombreLimpio = request.getNombre().trim();

        if (aerolineaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new RuntimeException("La aerolínea ingresada ya existe.");
        }

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new RuntimeException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Aerolinea aerolinea = new Aerolinea();
        aerolinea.setNombre(nombreLimpio);
        aerolinea.setEstado(estadoActivo);
        aerolinea.setUsuarioCreacion(usuarioActual);

        Aerolinea guardada = aerolineaRepository.save(aerolinea);

        return convertirAResponse(guardada);
    }

    public List<AerolineaResponse> listarActivas() {
        return aerolineaRepository.listarActivas()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<AerolineaResponse> listarTodas() {
        return aerolineaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private void validarRequest(AerolineaRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("Debe ingresar el nombre de la aerolínea.");
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();

            return usuarioRepository.findByUsername(username)
                    .orElseGet(() -> usuarioRepository.findById(1L)
                            .orElseThrow(() -> new RuntimeException("No se encontró el usuario del sistema.")));
        }

        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario del sistema."));
    }

    private AerolineaResponse convertirAResponse(Aerolinea aerolinea) {
        AerolineaResponse response = new AerolineaResponse();

        response.setIdAerolinea(aerolinea.getIdAerolinea());
        response.setNombre(aerolinea.getNombre());

        if (aerolinea.getEstado() != null) {
            response.setIdEstado(aerolinea.getEstado().getId());
            response.setCodigoEstado(aerolinea.getEstado().getCodigo());
            response.setEstado(aerolinea.getEstado().getValor());
        }

        return response;
    }
}

package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaAeropuertoRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AerolineaAeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.*;
import com.aeropuertolosprimos.aeropuerto_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerolineaAeropuertoService {

    private final AerolineaAeropuertoRepository aerolineaAeropuertoRepository;
    private final AerolineaRepository aerolineaRepository;
    private final AeropuertoRepository aeropuertoRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public AerolineaAeropuertoService(
            AerolineaAeropuertoRepository aerolineaAeropuertoRepository,
            AerolineaRepository aerolineaRepository,
            AeropuertoRepository aeropuertoRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.aerolineaAeropuertoRepository = aerolineaAeropuertoRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.aeropuertoRepository = aeropuertoRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public List<AerolineaAeropuertoResponse> autorizar(AerolineaAeropuertoRequest request) {
        validarRequest(request);

        Aerolinea aerolinea = aerolineaRepository.findById(request.getIdAerolinea())
                .orElseThrow(() -> new IllegalArgumentException("La aerolínea seleccionada no existe."));

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        List<AerolineaAeropuertoResponse> respuestas = new ArrayList<>();

        for (Long idAeropuerto : request.getIdsAeropuertos()) {

            Aeropuerto aeropuerto = aeropuertoRepository.findById(idAeropuerto)
                    .orElseThrow(() -> new IllegalArgumentException("Uno de los aeropuertos seleccionados no existe."));

            boolean yaExiste = aerolineaAeropuertoRepository
                    .existsByAerolinea_IdAerolineaAndAeropuerto_IdAeropuerto(
                            request.getIdAerolinea(),
                            idAeropuerto
                    );

            if (yaExiste) {
                throw new IllegalArgumentException(
                        "El aeropuerto " + aeropuerto.getNombre() + " ya está autorizado para esta aerolínea."
                );
            }

            AerolineaAeropuerto autorizacion = new AerolineaAeropuerto();
            autorizacion.setAerolinea(aerolinea);
            autorizacion.setAeropuerto(aeropuerto);
            autorizacion.setEstado(estadoActivo);
            autorizacion.setUsuarioCreacion(usuarioActual);

            AerolineaAeropuerto guardado = aerolineaAeropuertoRepository.save(autorizacion);

            respuestas.add(convertirAResponse(guardado));
        }

        return respuestas;
    }

    public List<AerolineaAeropuertoResponse> listarTodos() {
        return aerolineaAeropuertoRepository.listarTodos()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<AerolineaAeropuertoResponse> listarPorAerolinea(Long idAerolinea) {
        if (idAerolinea == null) {
            throw new IllegalArgumentException("Debe indicar una aerolínea.");
        }

        return aerolineaAeropuertoRepository.listarPorAerolinea(idAerolinea)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private void validarRequest(AerolineaAeropuertoRequest request) {
        if (request.getIdAerolinea() == null) {
            throw new IllegalArgumentException("Debe seleccionar una aerolínea.");
        }

        if (request.getIdsAeropuertos() == null || request.getIdsAeropuertos().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un aeropuerto.");
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

    private AerolineaAeropuertoResponse convertirAResponse(AerolineaAeropuerto autorizacion) {
        AerolineaAeropuertoResponse response = new AerolineaAeropuertoResponse();

        response.setIdAerolineaAeropuerto(autorizacion.getIdAerolineaAeropuerto());

        if (autorizacion.getAerolinea() != null) {
            response.setIdAerolinea(autorizacion.getAerolinea().getIdAerolinea());
            response.setNombreAerolinea(autorizacion.getAerolinea().getNombre());
        }

        if (autorizacion.getAeropuerto() != null) {
            response.setIdAeropuerto(autorizacion.getAeropuerto().getIdAeropuerto());
            response.setNombreAeropuerto(autorizacion.getAeropuerto().getNombre());
            response.setCiudad(autorizacion.getAeropuerto().getCiudad());
            response.setPais(autorizacion.getAeropuerto().getPais());
        }

        if (autorizacion.getEstado() != null) {
            response.setIdEstado(autorizacion.getEstado().getId());
            response.setCodigoEstado(autorizacion.getEstado().getCodigo());
            response.setEstado(autorizacion.getEstado().getValor());
        }

        return response;
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulanteRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulanteResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Tripulante;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.TripulanteRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripulanteService {

    private final TripulanteRepository tripulanteRepository;
    private final AerolineaRepository aerolineaRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public TripulanteService(
            TripulanteRepository tripulanteRepository,
            AerolineaRepository aerolineaRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.tripulanteRepository = tripulanteRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TripulanteResponse registrar(TripulanteRequest request) {
        validarRequest(request);

        if (request.getLicencia() != null && !request.getLicencia().isBlank()) {
            boolean licenciaExiste = tripulanteRepository.existsByLicenciaIgnoreCase(request.getLicencia().trim());

            if (licenciaExiste) {
                throw new IllegalArgumentException("La licencia ingresada ya está registrada.");
            }
        }

        Aerolinea aerolinea = aerolineaRepository.findById(request.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("La aerolínea seleccionada no existe."));

        CatalogoDetalle cargo = catalogoDetalleRepository.findById(request.getIdCargoTripulante())
                .orElseThrow(() -> new RuntimeException("El cargo seleccionado no existe."));

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new RuntimeException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Tripulante tripulante = new Tripulante();
        tripulante.setNombre(request.getNombre().trim());
        tripulante.setApellido(request.getApellido().trim());
        tripulante.setLicencia(request.getLicencia() != null ? request.getLicencia().trim() : null);

        // Campo temporal para mantener compatibilidad con la columna vieja.
        tripulante.setPuesto(cargo.getCodigo());

        tripulante.setAerolinea(aerolinea);
        tripulante.setCargoTripulante(cargo);
        tripulante.setEstado(estadoActivo);
        tripulante.setUsuarioCreacion(usuarioActual);

        Tripulante guardado = tripulanteRepository.save(tripulante);

        return convertirAResponse(guardado);
    }

    public List<TripulanteResponse> listar(Long idAerolinea) {
        return tripulanteRepository.listarTripulantes(idAerolinea)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<TripulanteResponse> listarDisponibles(Long idAerolinea, String codigoCargo) {
        if (idAerolinea == null) {
            throw new RuntimeException("Debe seleccionar una aerolínea.");
        }

        if (codigoCargo == null || codigoCargo.isBlank()) {
            throw new RuntimeException("Debe indicar el cargo del tripulante.");
        }

        return tripulanteRepository.listarDisponiblesPorCargo(idAerolinea, codigoCargo.trim().toUpperCase())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<CatalogoDetalle> listarCargosTripulante() {
        return catalogoDetalleRepository.listarPorCatalogo("CARGO_TRIPULANTE");
    }

    private void validarRequest(TripulanteRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("Debe ingresar el nombre del tripulante.");
        }

        if (request.getApellido() == null || request.getApellido().isBlank()) {
            throw new RuntimeException("Debe ingresar el apellido del tripulante.");
        }

        if (request.getIdAerolinea() == null) {
            throw new RuntimeException("Debe seleccionar una aerolínea.");
        }

        if (request.getIdCargoTripulante() == null) {
            throw new RuntimeException("Debe seleccionar el cargo del tripulante.");
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

    private TripulanteResponse convertirAResponse(Tripulante tripulante) {
        TripulanteResponse response = new TripulanteResponse();

        response.setIdTripulante(tripulante.getIdTripulante());
        response.setNombre(tripulante.getNombre());
        response.setApellido(tripulante.getApellido());
        response.setNombreCompleto(tripulante.getNombre() + " " + tripulante.getApellido());
        response.setLicencia(tripulante.getLicencia());

        if (tripulante.getAerolinea() != null) {
            response.setIdAerolinea(tripulante.getAerolinea().getIdAerolinea());
            response.setNombreAerolinea(tripulante.getAerolinea().getNombre());
        }

        if (tripulante.getCargoTripulante() != null) {
            response.setIdCargoTripulante(tripulante.getCargoTripulante().getId());
            response.setCodigoCargo(tripulante.getCargoTripulante().getCodigo());
            response.setCargo(tripulante.getCargoTripulante().getCodigo());
        }

        if (tripulante.getEstado() != null) {
            response.setIdEstado(tripulante.getEstado().getId());
            response.setCodigoEstado(tripulante.getEstado().getCodigo());
            response.setEstado(tripulante.getEstado().getCodigo());
        }

        return response;
    }
}
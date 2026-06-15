package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.CrearTripulacionRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulacionDetalleResponse;
import com.aeropuertolosprimos.aeropuerto_backend.dto.TripulacionResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Tripulacion;
import com.aeropuertolosprimos.aeropuerto_backend.entity.TripulacionDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Tripulante;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.TripulacionDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.TripulacionRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.TripulanteRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TripulacionService {

    private final TripulacionRepository tripulacionRepository;
    private final TripulacionDetalleRepository tripulacionDetalleRepository;
    private final TripulanteRepository tripulanteRepository;
    private final AerolineaRepository aerolineaRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public TripulacionService(
            TripulacionRepository tripulacionRepository,
            TripulacionDetalleRepository tripulacionDetalleRepository,
            TripulanteRepository tripulanteRepository,
            AerolineaRepository aerolineaRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.tripulacionRepository = tripulacionRepository;
        this.tripulacionDetalleRepository = tripulacionDetalleRepository;
        this.tripulanteRepository = tripulanteRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TripulacionResponse crear(CrearTripulacionRequest request) {
        validarRequest(request);

        Aerolinea aerolinea = aerolineaRepository.findById(request.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("La aerolínea seleccionada no existe."));

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new RuntimeException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Tripulante piloto = obtenerYValidarTripulante(
                request.getIdPiloto(),
                request.getIdAerolinea(),
                "PILOTO",
                "El piloto seleccionado no es válido."
        );

        Tripulante copiloto = obtenerYValidarTripulante(
                request.getIdCopiloto(),
                request.getIdAerolinea(),
                "COPILOTO",
                "El copiloto seleccionado no es válido."
        );

        Tripulante ingeniero = obtenerYValidarTripulante(
                request.getIdIngenieroVuelo(),
                request.getIdAerolinea(),
                "INGENIERO_VUELO",
                "El ingeniero de vuelo seleccionado no es válido."
        );

        List<Tripulante> cabina = new ArrayList<>();

        for (Long idCabina : request.getIdTripulantesCabina()) {
            Tripulante tripulanteCabina = obtenerYValidarTripulante(
                    idCabina,
                    request.getIdAerolinea(),
                    "TRIPULANTE_CABINA",
                    "Uno de los tripulantes de cabina seleccionados no es válido."
            );

            cabina.add(tripulanteCabina);
        }

        validarNoRepetidos(piloto, copiloto, ingeniero, cabina);

        Tripulacion tripulacion = new Tripulacion();
        tripulacion.setNombreEquipo(
                request.getNombreEquipo() != null && !request.getNombreEquipo().isBlank()
                        ? request.getNombreEquipo().trim()
                        : "Equipo de tripulación"
        );
        tripulacion.setAerolinea(aerolinea);
        tripulacion.setEstado(estadoActivo);
        tripulacion.setUsuarioCreacion(usuarioActual);

        Tripulacion guardada = tripulacionRepository.save(tripulacion);

        guardarDetalle(guardada, piloto);
        guardarDetalle(guardada, copiloto);
        guardarDetalle(guardada, ingeniero);

        for (Tripulante tripulanteCabina : cabina) {
            guardarDetalle(guardada, tripulanteCabina);
        }

        return convertirAResponse(guardada);
    }

    public List<TripulacionResponse> listar() {
        return tripulacionRepository.listarTripulaciones()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private void validarRequest(CrearTripulacionRequest request) {
        if (request.getIdAerolinea() == null) {
            throw new RuntimeException("Debe seleccionar una aerolínea.");
        }

        if (request.getIdPiloto() == null) {
            throw new RuntimeException("Debe seleccionar un piloto.");
        }

        if (request.getIdCopiloto() == null) {
            throw new RuntimeException("Debe seleccionar un copiloto.");
        }

        if (request.getIdIngenieroVuelo() == null) {
            throw new RuntimeException("Debe seleccionar un ingeniero de vuelo.");
        }

        if (request.getIdTripulantesCabina() == null || request.getIdTripulantesCabina().size() != 3) {
            throw new RuntimeException("Debe seleccionar exactamente tres tripulantes de cabina.");
        }
    }

    private Tripulante obtenerYValidarTripulante(
            Long idTripulante,
            Long idAerolinea,
            String codigoCargoEsperado,
            String mensajeError
    ) {
        Tripulante tripulante = tripulanteRepository.findById(idTripulante)
                .orElseThrow(() -> new RuntimeException(mensajeError));

        if (tripulante.getAerolinea() == null ||
                !tripulante.getAerolinea().getIdAerolinea().equals(idAerolinea)) {
            throw new RuntimeException("Todos los tripulantes deben pertenecer a la aerolínea seleccionada.");
        }

        if (tripulante.getCargoTripulante() == null ||
                tripulante.getCargoTripulante().getCodigo() == null ||
                !tripulante.getCargoTripulante().getCodigo().equalsIgnoreCase(codigoCargoEsperado)) {
            throw new RuntimeException(mensajeError);
        }

        if (tripulante.getEstado() == null ||
                tripulante.getEstado().getCodigo() == null ||
                !tripulante.getEstado().getCodigo().equalsIgnoreCase("ACT")) {
            throw new RuntimeException("Todos los tripulantes seleccionados deben estar activos.");
        }

        return tripulante;
    }

    private void validarNoRepetidos(
            Tripulante piloto,
            Tripulante copiloto,
            Tripulante ingeniero,
            List<Tripulante> cabina
    ) {
        Set<Long> ids = new HashSet<>();

        ids.add(piloto.getIdTripulante());
        ids.add(copiloto.getIdTripulante());
        ids.add(ingeniero.getIdTripulante());

        for (Tripulante tripulanteCabina : cabina) {
            ids.add(tripulanteCabina.getIdTripulante());
        }

        if (ids.size() != 6) {
            throw new RuntimeException("No puede seleccionar el mismo tripulante más de una vez.");
        }
    }

    private void guardarDetalle(Tripulacion tripulacion, Tripulante tripulante) {
        TripulacionDetalle detalle = new TripulacionDetalle();
        detalle.setTripulacion(tripulacion);
        detalle.setTripulante(tripulante);

        tripulacionDetalleRepository.save(detalle);
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

    private TripulacionResponse convertirAResponse(Tripulacion tripulacion) {
        TripulacionResponse response = new TripulacionResponse();

        response.setIdTripulacion(tripulacion.getIdTripulacion());
        response.setNombreEquipo(tripulacion.getNombreEquipo());

        if (tripulacion.getAerolinea() != null) {
            response.setIdAerolinea(tripulacion.getAerolinea().getIdAerolinea());
            response.setNombreAerolinea(tripulacion.getAerolinea().getNombre());
        }

        if (tripulacion.getEstado() != null) {
            response.setEstado(tripulacion.getEstado().getCodigo());
        }

        List<TripulacionDetalleResponse> integrantes =
                tripulacionDetalleRepository.buscarPorTripulacion(tripulacion.getIdTripulacion())
                        .stream()
                        .map(this::convertirDetalleAResponse)
                        .toList();

        response.setIntegrantes(integrantes);

        return response;
    }

    private TripulacionDetalleResponse convertirDetalleAResponse(TripulacionDetalle detalle) {
        TripulacionDetalleResponse response = new TripulacionDetalleResponse();

        Tripulante tripulante = detalle.getTripulante();

        response.setIdTripulante(tripulante.getIdTripulante());
        response.setNombreCompleto(tripulante.getNombre() + " " + tripulante.getApellido());

        if (tripulante.getCargoTripulante() != null) {
            response.setCargo(tripulante.getCargoTripulante().getCodigo());
        }

        return response;
    }
}
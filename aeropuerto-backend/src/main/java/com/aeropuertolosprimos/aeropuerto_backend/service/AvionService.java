package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.AvionRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.AvionResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.*;
import com.aeropuertolosprimos.aeropuerto_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvionService {

    private final AvionRepository avionRepository;
    private final AsientoRepository asientoRepository;
    private final AerolineaRepository aerolineaRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public AvionService(
            AvionRepository avionRepository,
            AsientoRepository asientoRepository,
            AerolineaRepository aerolineaRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.avionRepository = avionRepository;
        this.asientoRepository = asientoRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AvionResponse registrar(AvionRequest request) {
        validarRequest(request);

        Aerolinea aerolinea = aerolineaRepository.findById(request.getIdAerolinea())
                .orElseThrow(() -> new IllegalArgumentException("La aerolínea seleccionada no existe."));

        boolean avionExiste = avionRepository.existsByModeloIgnoreCaseAndMarcaIgnoreCaseAndAnioAndAerolinea_IdAerolinea(
                request.getModelo().trim(),
                request.getMarca().trim(),
                request.getAnio(),
                request.getIdAerolinea()
        );

        if (avionExiste) {
            throw new IllegalArgumentException("Ya existe un avión con el mismo modelo, marca, año y aerolínea.");
        }

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el estado ACTIVO en catálogo."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Avion avion = new Avion();
        avion.setAerolinea(aerolinea);
        avion.setModelo(request.getModelo().trim());
        avion.setMarca(request.getMarca().trim());
        avion.setAnio(request.getAnio());
        avion.setCapacidad(request.getCapacidad());
        avion.setCantidadVuelos(0);
        avion.setEstado(estadoActivo);
        avion.setUsuarioCreacion(usuarioActual);

        Avion avionGuardado = avionRepository.save(avion);

        int totalAsientos = generarAsientos(avionGuardado, request.getCapacidad(), usuarioActual);

        AvionResponse response = convertirAResponse(avionGuardado);
        response.setTotalAsientosGenerados(totalAsientos);

        return response;
    }

    public List<AvionResponse> listarTodos() {
        return avionRepository.listarTodos()
                .stream()
                .map(avion -> {
                    AvionResponse response = convertirAResponse(avion);
                    response.setTotalAsientosGenerados(asientoRepository.countByAvion_IdAvion(avion.getIdAvion()));
                    return response;
                })
                .toList();
    }

    public List<AvionResponse> listarActivos() {
        return avionRepository.listarActivos()
                .stream()
                .map(avion -> {
                    AvionResponse response = convertirAResponse(avion);
                    response.setTotalAsientosGenerados(asientoRepository.countByAvion_IdAvion(avion.getIdAvion()));
                    return response;
                })
                .toList();
    }

    private int generarAsientos(Avion avion, Integer capacidad, Usuario usuarioActual) {
        CatalogoDetalle ventana = catalogoDetalleRepository.buscarPorCatalogoYCodigo("TIPO_ASIENTO", "VENT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el tipo de asiento VENTANA en catálogo."));

        CatalogoDetalle centro = catalogoDetalleRepository.buscarPorCatalogoYCodigo("TIPO_ASIENTO", "CENT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el tipo de asiento CENTRO en catálogo."));

        CatalogoDetalle pasillo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("TIPO_ASIENTO", "PAS")
                .orElseThrow(() -> new IllegalArgumentException("No existe el tipo de asiento PASILLO en catálogo."));

        String[] letras = {"A", "B", "C", "D", "E", "F"};

        List<Asiento> asientos = new ArrayList<>();

        for (int i = 0; i < capacidad; i++) {
            int fila = (i / 6) + 1;
            String letra = letras[i % 6];

            Asiento asiento = new Asiento();
            asiento.setAvion(avion);
            asiento.setFila(fila);
            asiento.setLetra(letra);
            asiento.setTipoAsiento(obtenerTipoPorLetra(letra, ventana, centro, pasillo));
            asiento.setUsuarioCreacion(usuarioActual);

            asientos.add(asiento);
        }

        asientoRepository.saveAll(asientos);

        return asientos.size();
    }

    private CatalogoDetalle obtenerTipoPorLetra(
            String letra,
            CatalogoDetalle ventana,
            CatalogoDetalle centro,
            CatalogoDetalle pasillo
    ) {
        return switch (letra) {
            case "A", "F" -> ventana;
            case "B", "E" -> centro;
            case "C", "D" -> pasillo;
            default -> centro;
        };
    }

    private void validarRequest(AvionRequest request) {
        if (request.getIdAerolinea() == null) {
            throw new IllegalArgumentException("Debe seleccionar una aerolínea.");
        }

        if (request.getModelo() == null || request.getModelo().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el modelo del avión.");
        }

        if (request.getMarca() == null || request.getMarca().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar la marca del avión.");
        }

        if (request.getAnio() == null) {
            throw new IllegalArgumentException("Debe ingresar el año del avión.");
        }

        int anioActual = Year.now().getValue();

        if (request.getAnio() < 1950 || request.getAnio() > anioActual + 1) {
            throw new IllegalArgumentException("El año del avión no es válido.");
        }

        if (request.getCapacidad() == null || request.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad del avión debe ser mayor a cero.");
        }

        if (request.getCapacidad() > 500) {
            throw new IllegalArgumentException("La capacidad del avión no puede ser mayor a 500 pasajeros.");
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

    private AvionResponse convertirAResponse(Avion avion) {
        AvionResponse response = new AvionResponse();

        response.setIdAvion(avion.getIdAvion());
        response.setModelo(avion.getModelo());
        response.setMarca(avion.getMarca());
        response.setAnio(avion.getAnio());
        response.setCapacidad(avion.getCapacidad());
        response.setCantidadVuelos(avion.getCantidadVuelos());

        if (avion.getAerolinea() != null) {
            response.setIdAerolinea(avion.getAerolinea().getIdAerolinea());
            response.setNombreAerolinea(avion.getAerolinea().getNombre());
        }

        if (avion.getEstado() != null) {
            response.setIdEstado(avion.getEstado().getId());
            response.setCodigoEstado(avion.getEstado().getCodigo());
            response.setEstado(avion.getEstado().getValor());
        }

        return response;
    }
}
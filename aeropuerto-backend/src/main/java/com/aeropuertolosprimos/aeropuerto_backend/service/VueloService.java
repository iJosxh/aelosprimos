package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.*;
import com.aeropuertolosprimos.aeropuerto_backend.entity.*;
import com.aeropuertolosprimos.aeropuerto_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VueloService {

    private static final int MAXIMO_ESCALAS = 2;

    private final VueloRepository vueloRepository;
    private final AerolineaRepository aerolineaRepository;
    private final AvionRepository avionRepository;
    private final TripulacionRepository tripulacionRepository;
    private final AeropuertoRepository aeropuertoRepository;
    private final AerolineaAeropuertoRepository aerolineaAeropuertoRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    public VueloService(
            VueloRepository vueloRepository,
            AerolineaRepository aerolineaRepository,
            AvionRepository avionRepository,
            TripulacionRepository tripulacionRepository,
            AeropuertoRepository aeropuertoRepository,
            AerolineaAeropuertoRepository aerolineaAeropuertoRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.vueloRepository = vueloRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.avionRepository = avionRepository;
        this.tripulacionRepository = tripulacionRepository;
        this.aeropuertoRepository = aeropuertoRepository;
        this.aerolineaAeropuertoRepository = aerolineaAeropuertoRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AvionDisponibleResponse> obtenerAvionesDisponibles(
            Long aerolineaId,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    ) {
        List<Avion> aviones = avionRepository.listarActivosPorAerolinea(aerolineaId);

        return aviones.stream()
                .filter(avion -> !vueloRepository.existeChoqueHorarioAvion(
                        avion.getIdAvion(),
                        fechaSalida,
                        fechaLlegada
                ))
                .map(avion -> new AvionDisponibleResponse(
                        avion.getIdAvion(),
                        avion.getModelo(),
                        avion.getMarca(),
                        avion.getAnio(),
                        avion.getCapacidad()
                ))
                .collect(Collectors.toList());
    }

    public List<TripulacionDisponibleResponse> obtenerTripulacionesDisponibles(
            Long aerolineaId,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    ) {

        List<Tripulacion> tripulaciones = tripulacionRepository
                .listarDisponiblesPorAerolinea(aerolineaId);

        return tripulaciones.stream()
                .filter(tripulacion -> !vueloRepository.existeChoqueHorarioTripulacion(
                        tripulacion.getIdTripulacion(),
                        fechaSalida,
                        fechaLlegada
                ))
                .map(tripulacion -> new TripulacionDisponibleResponse(
                        tripulacion.getIdTripulacion(),
                        tripulacion.getNombreEquipo()
                ))
                .collect(Collectors.toList());
    }

    public Long contarAeropuertosAutorizados(Long aerolineaId) {
        return aerolineaAeropuertoRepository.contarAeropuertosAutorizados(aerolineaId);
    }

    public List<AeropuertoResponse> listarAeropuertosAutorizados(Long aerolineaId) {
        return aerolineaAeropuertoRepository.listarAeropuertosAutorizados(aerolineaId)
                .stream()
                .map(a -> new AeropuertoResponse(
                        a.getIdAeropuerto(),
                        a.getNombre(),
                        a.getCiudad(),
                        a.getPais()
                ))
                .toList();
    }

    @Transactional
    public VueloResponse crearVuelo(VueloRequest request) {

        validarDatos(request);

        List<VueloEscalaRequest> escalasRequest = obtenerEscalasOrdenadas(request);

        Aerolinea aerolinea = aerolineaRepository.findById(request.getAerolineaId())
                .orElseThrow(() -> new RuntimeException("Aerolinea no encontrada"));

        Avion avion = avionRepository.findById(request.getAvionId())
                .orElseThrow(() -> new RuntimeException("Avion no encontrado"));

        Tripulacion tripulacion = tripulacionRepository.findById(request.getTripulacionId())
                .orElseThrow(() -> new RuntimeException("Tripulación no encontrada"));

        Aeropuerto origen = aeropuertoRepository.findById(request.getAeropuertoOrigenId())
                .orElseThrow(() -> new RuntimeException("Aeropuerto origen no encontrado"));

        Aeropuerto destino = aeropuertoRepository.findById(request.getAeropuertoDestinoId())
                .orElseThrow(() -> new RuntimeException("Aeropuerto destino no encontrado"));

        validarEscalas(request, escalasRequest, aerolinea.getIdAerolinea());

        boolean avionOcupado = vueloRepository.existeChoqueHorarioAvion(
                avion.getIdAvion(),
                request.getFechaSalida(),
                request.getFechaLlegada()
        );

        if (avionOcupado) {
            throw new RuntimeException("El avión ya tiene un vuelo asignado en ese horario");
        }

        boolean tripulacionOcupada = vueloRepository.existeChoqueHorarioTripulacion(
                tripulacion.getIdTripulacion(),
                request.getFechaSalida(),
                request.getFechaLlegada()
        );

        if (tripulacionOcupada) {
            throw new RuntimeException("La tripulación ya tiene un vuelo asignado en ese horario");
        }

        CatalogoDetalle estadoProgramado = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_VUELO", "PROG")
                .orElseThrow(() -> new RuntimeException("Estado PROGRAMADO no encontrado"));

        Usuario usuarioActual = obtenerUsuarioActual();

        Vuelo vuelo = new Vuelo();

        vuelo.setCodigoVuelo(generarCodigoVuelo());

        vuelo.setAerolinea(aerolinea);
        vuelo.setAvion(avion);
        vuelo.setTripulacion(tripulacion);

        vuelo.setAeropuertoOrigen(origen);
        vuelo.setAeropuertoDestino(destino);

        vuelo.setFechaSalida(request.getFechaSalida());
        vuelo.setFechaLlegada(request.getFechaLlegada());

        vuelo.setPrecioEconomica(request.getPrecioEconomica());
        vuelo.setPrecioEjecutiva(request.getPrecioEjecutiva());

        vuelo.setEstado(estadoProgramado);
        vuelo.setUsuarioCreacion(usuarioActual);

        agregarEscalasAlVuelo(vuelo, escalasRequest, usuarioActual);

        vueloRepository.save(vuelo);

        avion.setCantidadVuelos((avion.getCantidadVuelos() == null ? 0 : avion.getCantidadVuelos()) + 1);
        avion.setUsuarioModificacion(usuarioActual);

        avionRepository.save(avion);

        return convertirResponse(vuelo);
    }

    public List<VueloResponse> listar() {

        return vueloRepository.listarVuelos()
                .stream()
                .map(this::convertirResponse)
                .collect(Collectors.toList());
    }

    public VueloResponse obtenerDetalle(Long id) {

        Vuelo vuelo = vueloRepository.obtenerPorId(id);

        if (vuelo == null) {
            throw new RuntimeException("Vuelo no encontrado");
        }

        return convertirResponse(vuelo);
    }

    public Long contarAvionesActivosPorAerolinea(Long aerolineaId) {
        return avionRepository.contarAvionesActivosPorAerolinea(aerolineaId);
    }

    private void validarDatos(VueloRequest request) {

        if (
                request.getAerolineaId() == null ||
                        request.getAvionId() == null ||
                        request.getTripulacionId() == null ||
                        request.getAeropuertoOrigenId() == null ||
                        request.getAeropuertoDestinoId() == null ||
                        request.getFechaSalida() == null ||
                        request.getFechaLlegada() == null ||
                        request.getPrecioEconomica() == null ||
                        request.getPrecioEjecutiva() == null
        ) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }

        if (request.getAeropuertoOrigenId().equals(request.getAeropuertoDestinoId())) {
            throw new RuntimeException("No se puede seleccionar el mismo aeropuerto de salida y llegada.");
        }

        if (!request.getFechaLlegada().isAfter(request.getFechaSalida())) {
            throw new RuntimeException("La fecha y hora de llegada debe ser mayor a la fecha y hora de salida.");
        }

        if (request.getFechaSalida().isBefore(LocalDateTime.now().plusHours(5))) {
            throw new RuntimeException("Tiempo mínimo para la preparación 5 horas a partir de la hora actual.");
        }
    }

    private List<VueloEscalaRequest> obtenerEscalasOrdenadas(VueloRequest request) {

        if (request.getEscalas() == null) {
            return new ArrayList<>();
        }

        if (request.getEscalas().size() > MAXIMO_ESCALAS) {
            throw new RuntimeException("Los vuelos no pueden tener más de 2 escalas");
        }

        return request.getEscalas()
                .stream()
                .sorted(Comparator.comparing(
                        VueloEscalaRequest::getOrden,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
    }

    private void validarEscalas(
            VueloRequest request,
            List<VueloEscalaRequest> escalas,
            Long aerolineaId
    ) {

        if (escalas.isEmpty()) {
            return;
        }

        Set<Long> aeropuertosEscala = new HashSet<>();
        Set<Integer> ordenes = new HashSet<>();

        LocalDateTime fechaAnterior = request.getFechaSalida();

        for (int i = 0; i < escalas.size(); i++) {

            VueloEscalaRequest escala = escalas.get(i);

            if (
                    escala.getAeropuertoId() == null ||
                            escala.getOrden() == null ||
                            escala.getFechaLlegada() == null ||
                            escala.getFechaSalida() == null
            ) {
                throw new RuntimeException("Debe ingresar los campos obligatorios");
            }

            int ordenEsperado = i + 1;

            if (!escala.getOrden().equals(ordenEsperado)) {
                throw new RuntimeException("El orden de las escalas debe iniciar en 1 y ser consecutivo");
            }

            if (!ordenes.add(escala.getOrden())) {
                throw new RuntimeException("No puede repetir el orden de las escalas");
            }

            if (escala.getAeropuertoId().equals(request.getAeropuertoOrigenId())) {
                throw new RuntimeException("La escala no puede ser igual al aeropuerto de salida.");
            }

            if (escala.getAeropuertoId().equals(request.getAeropuertoDestinoId())) {
                throw new RuntimeException("La escala no puede ser igual al aeropuerto de llegada.");
            }

            if (!aeropuertosEscala.add(escala.getAeropuertoId())) {
                throw new RuntimeException("No puede repetir el mismo aeropuerto de escala.");
            }

            boolean escalaAutorizada = aerolineaAeropuertoRepository.existeAeropuertoAutorizadoActivo(
                    aerolineaId,
                    escala.getAeropuertoId()
            );

            if (!escalaAutorizada) {
                throw new RuntimeException("El aeropuerto de escala no está autorizado para la aerolínea.");
            }

            if (!escala.getFechaLlegada().isAfter(fechaAnterior)) {
                throw new RuntimeException("La fecha y hora de llegada de la escala debe ser mayor a la fecha y hora anterior.");
            }

            if (!escala.getFechaSalida().isAfter(escala.getFechaLlegada())) {
                throw new RuntimeException("La fecha y hora de salida de la escala debe ser mayor a la fecha y hora de llegada de la escala.");
            }

            fechaAnterior = escala.getFechaSalida();
        }

        if (!request.getFechaLlegada().isAfter(fechaAnterior)) {
            throw new RuntimeException("La fecha y hora de llegada debe ser mayor a la fecha y hora de la última escala.");
        }
    }

    private void agregarEscalasAlVuelo(
            Vuelo vuelo,
            List<VueloEscalaRequest> escalasRequest,
            Usuario usuarioActual
    ) {

        for (VueloEscalaRequest escalaRequest : escalasRequest) {

            Aeropuerto aeropuertoEscala = aeropuertoRepository.findById(escalaRequest.getAeropuertoId())
                    .orElseThrow(() -> new RuntimeException("Aeropuerto de escala no encontrado"));

            VueloEscala escala = new VueloEscala();
            escala.setAeropuerto(aeropuertoEscala);
            escala.setOrden(escalaRequest.getOrden());
            escala.setFechaLlegada(escalaRequest.getFechaLlegada());
            escala.setFechaSalida(escalaRequest.getFechaSalida());
            escala.setUsuarioCreacion(usuarioActual);

            vuelo.agregarEscala(escala);
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

    private String generarCodigoVuelo() {

        Random random = new Random();

        int numero = 1000 + random.nextInt(9000);

        return "VU-" + numero;
    }

    private VueloResponse convertirResponse(Vuelo vuelo) {

        VueloResponse response = new VueloResponse();

        response.setId(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());

        response.setAerolineaId(vuelo.getAerolinea().getIdAerolinea());
        response.setAerolineaNombre(vuelo.getAerolinea().getNombre());

        response.setAvionId(vuelo.getAvion().getIdAvion());
        response.setAvionModelo(vuelo.getAvion().getModelo());

        response.setTripulacionId(vuelo.getTripulacion().getIdTripulacion());
        response.setTripulacionNombre(vuelo.getTripulacion().getNombreEquipo());

        response.setAeropuertoOrigen(vuelo.getAeropuertoOrigen().getNombre());
        response.setAeropuertoDestino(vuelo.getAeropuertoDestino().getNombre());

        response.setFechaSalida(vuelo.getFechaSalida());
        response.setFechaLlegada(vuelo.getFechaLlegada());

        response.setPrecioEconomica(vuelo.getPrecioEconomica());
        response.setPrecioEjecutiva(vuelo.getPrecioEjecutiva());

        response.setEstado(vuelo.getEstado().getValor());

        List<VueloEscalaResponse> escalas = vuelo.getEscalas() == null
                ? new ArrayList<>()
                : vuelo.getEscalas()
                .stream()
                .sorted(Comparator.comparing(VueloEscala::getOrden))
                .map(this::convertirEscalaResponse)
                .collect(Collectors.toList());

        response.setEscalas(escalas);
        response.setCantidadEscalas(escalas.size());
        response.setRutaCompleta(generarRutaCompleta(vuelo, escalas));

        return response;
    }

    private VueloEscalaResponse convertirEscalaResponse(VueloEscala escala) {

        Aeropuerto aeropuerto = escala.getAeropuerto();

        return new VueloEscalaResponse(
                escala.getIdVueloEscala(),
                aeropuerto.getIdAeropuerto(),
                aeropuerto.getNombre(),
                aeropuerto.getCiudad(),
                aeropuerto.getPais(),
                escala.getOrden(),
                escala.getFechaLlegada(),
                escala.getFechaSalida()
        );
    }

    private String generarRutaCompleta(Vuelo vuelo, List<VueloEscalaResponse> escalas) {

        List<String> ruta = new ArrayList<>();

        ruta.add(vuelo.getAeropuertoOrigen().getNombre());

        for (VueloEscalaResponse escala : escalas) {
            ruta.add(escala.getAeropuertoNombre());
        }

        ruta.add(vuelo.getAeropuertoDestino().getNombre());

        return String.join(" → ", ruta);
    }
}
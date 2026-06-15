package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.*;
import com.aeropuertolosprimos.aeropuerto_backend.entity.*;
import com.aeropuertolosprimos.aeropuerto_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class ReservaVueloService {

    private final AeropuertoRepository aeropuertoRepository;
    private final VueloRepository vueloRepository;
    private final AsientoRepository asientoRepository;
    private final PasajeroRepository pasajeroRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final VueloPasajeroRepository vueloPasajeroRepository;
    private final ReservaAsientoRepository reservaAsientoRepository;
    private final EquipajeRepository equipajeRepository;

    public ReservaVueloService(
            AeropuertoRepository aeropuertoRepository,
            VueloRepository vueloRepository,
            AsientoRepository asientoRepository,
            PasajeroRepository pasajeroRepository,
            UsuarioRepository usuarioRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            VueloPasajeroRepository vueloPasajeroRepository,
            ReservaAsientoRepository reservaAsientoRepository,
            EquipajeRepository equipajeRepository
    ) {
        this.aeropuertoRepository = aeropuertoRepository;
        this.vueloRepository = vueloRepository;
        this.asientoRepository = asientoRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.usuarioRepository = usuarioRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.vueloPasajeroRepository = vueloPasajeroRepository;
        this.reservaAsientoRepository = reservaAsientoRepository;
        this.equipajeRepository = equipajeRepository;
    }

    // =========================================================
    // 1. AEROPUERTOS PARA FILTRO
    // =========================================================

    @Transactional(readOnly = true)
    public List<AeropuertoResponse> listarAeropuertosActivos() {
        return aeropuertoRepository.listarActivos()
                .stream()
                .map(this::convertirAeropuertoResponse)
                .toList();
    }

    // =========================================================
    // 2. CLASES DE VUELO
    // =========================================================

    @Transactional(readOnly = true)
    public List<ClaseVueloResponse> listarClasesVuelo() {
        return catalogoDetalleRepository.listarPorCatalogo("CLASE_VUELO")
                .stream()
                .map(this::convertirClaseVueloResponse)
                .toList();
    }

    // =========================================================
    // 3. BUSCAR VUELOS DISPONIBLES
    // =========================================================

    @Transactional(readOnly = true)
    public List<VueloDisponibleReservaResponse> buscarVuelosDisponibles(
            Long origenId,
            Long destinoId,
            LocalDate fechaSalida
    ) {
        if (origenId == null || destinoId == null || fechaSalida == null) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }

        if (origenId.equals(destinoId)) {
            throw new RuntimeException("No se puede seleccionar el mismo aeropuerto de salida y llegada.");
        }

        if (fechaSalida.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se encontraron vuelos según los parámetros ingresados");
        }

        LocalDateTime inicioDia = fechaSalida.atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);
        LocalDateTime fechaActual = LocalDateTime.now();

        List<Vuelo> vuelos = vueloRepository.buscarVuelosDisponiblesParaReserva(
                origenId,
                destinoId,
                inicioDia,
                finDia,
                fechaActual
        );

        if (vuelos.isEmpty()) {
            throw new RuntimeException("No se encontraron vuelos según los parámetros ingresados");
        }

        return vuelos.stream()
                .map(this::convertirVueloDisponibleResponse)
                .toList();
    }

    // =========================================================
    // 4. DETALLE DEL VUELO
    // =========================================================

    @Transactional(readOnly = true)
    public VueloDetalleReservaResponse obtenerDetalleVuelo(Long idVuelo) {
        if (idVuelo == null) {
            throw new RuntimeException("Debe ingresar el vuelo");
        }

        Vuelo vuelo = vueloRepository.obtenerDetalleReserva(idVuelo)
                .orElseThrow(() -> new RuntimeException("El vuelo seleccionado no existe"));

        return convertirVueloDetalleResponse(vuelo);
    }

    // =========================================================
    // 5. ASIENTOS DISPONIBLES
    // =========================================================

    @Transactional(readOnly = true)
    public List<AsientoDisponibleResponse> listarAsientosDisponibles(Long idVuelo) {
        if (idVuelo == null) {
            throw new RuntimeException("Debe ingresar el vuelo");
        }

        Vuelo vuelo = vueloRepository.obtenerDetalleReserva(idVuelo)
                .orElseThrow(() -> new RuntimeException("El vuelo seleccionado no existe"));

        if (vuelo.getAvion() == null) {
            throw new RuntimeException("El vuelo no tiene avión asignado");
        }

        return asientoRepository.listarDisponiblesPorVuelo(idVuelo)
                .stream()
                .map(this::convertirAsientoDisponibleResponse)
                .toList();
    }

    // =========================================================
    // 6. RESERVAR VUELO
    // =========================================================

    @Transactional
    public ReservaVueloResponse reservarVuelo(ReservaVueloRequest request, String username) {

        validarReservaRequest(request);

        if (username == null || username.isBlank()) {
            throw new RuntimeException("No se pudo identificar el usuario autenticado");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Pasajero pasajero = pasajeroRepository.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("El usuario autenticado no tiene pasajero asociado"));

        Vuelo vuelo = vueloRepository.obtenerDetalleReserva(request.getIdVuelo())
                .orElseThrow(() -> new RuntimeException("El vuelo seleccionado no existe"));

        validarVueloReservable(vuelo);

        Asiento asiento = asientoRepository.findById(request.getIdAsiento())
                .orElseThrow(() -> new RuntimeException("El asiento seleccionado no existe"));

        validarAsientoPerteneceAlAvion(vuelo, asiento);

        boolean yaReservoMismoVuelo = vueloPasajeroRepository.existeReservaActivaMismoVuelo(
                vuelo.getIdVuelo(),
                pasajero.getIdPasajero()
        );

        if (yaReservoMismoVuelo) {
            throw new RuntimeException("El pasajero ya tiene una reserva activa para este vuelo");
        }

        boolean tieneChoqueHorario = vueloPasajeroRepository.existeChoqueHorarioPasajero(
                pasajero.getIdPasajero(),
                vuelo.getFechaSalida(),
                vuelo.getFechaLlegada()
        );

        if (tieneChoqueHorario) {
            throw new RuntimeException("No se puede seleccionar el vuelo porque ya tiene vuelos asignados");
        }

        boolean asientoOcupado = reservaAsientoRepository.asientoOcupadoEnVuelo(
                vuelo.getIdVuelo(),
                asiento.getIdAsiento()
        );

        if (asientoOcupado) {
            throw new RuntimeException("El asiento seleccionado ya se encuentra ocupado");
        }

        String codigoClase = request.getCodigoClaseVuelo().trim().toUpperCase();

        CatalogoDetalle claseVuelo = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("CLASE_VUELO", codigoClase)
                .orElseThrow(() -> new RuntimeException("La clase de vuelo seleccionada no existe"));

        BigDecimal precioPagado = obtenerPrecioPorClase(vuelo, codigoClase);

        CatalogoDetalle estadoReserva = obtenerEstadoReservaInicial();

        VueloPasajero reserva = new VueloPasajero();
        reserva.setVuelo(vuelo);
        reserva.setPasajero(pasajero);
        reserva.setClaseVuelo(claseVuelo);
        reserva.setEstado(estadoReserva);
        reserva.setPrecioPagado(precioPagado);
        reserva.setCantidadMaletas(request.getCantidadMaletas());
        reserva.setUsuarioCreacion(usuario);

        VueloPasajero reservaGuardada = vueloPasajeroRepository.save(reserva);

        reservaGuardada.setCodigoPaseAbordar(generarCodigoPaseAbordar(vuelo, reservaGuardada));
        reservaGuardada = vueloPasajeroRepository.save(reservaGuardada);

        ReservaAsiento reservaAsiento = new ReservaAsiento();
        reservaAsiento.setVueloPasajero(reservaGuardada);
        reservaAsiento.setAsiento(asiento);
        reservaAsiento.setEstado(estadoReserva);
        reservaAsiento.setUsuarioCreacion(usuario);

        reservaAsientoRepository.save(reservaAsiento);

        registrarEquipajeBasicoSiAplica(
                reservaGuardada,
                request.getCantidadMaletas(),
                usuario
        );

        return convertirReservaVueloResponse(reservaGuardada, asiento);
    }

    // =========================================================
    // 7. PASE DE ABORDAR
    // =========================================================

    @Transactional(readOnly = true)
    public PaseAbordarResponse obtenerPaseAbordar(Long idReserva) {
        if (idReserva == null) {
            throw new RuntimeException("Debe ingresar la reserva");
        }

        VueloPasajero reserva = vueloPasajeroRepository.obtenerReservaConDetalle(idReserva)
                .orElseThrow(() -> new RuntimeException("La reserva no existe"));

        ReservaAsiento reservaAsiento = reservaAsientoRepository.obtenerPorReserva(idReserva)
                .orElseThrow(() -> new RuntimeException("La reserva no tiene asiento asignado"));

        return convertirPaseAbordarResponse(reserva, reservaAsiento.getAsiento());
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarReservaRequest(ReservaVueloRequest request) {
        if (
                request == null ||
                        request.getIdVuelo() == null ||
                        request.getIdAsiento() == null ||
                        request.getCodigoClaseVuelo() == null ||
                        request.getCodigoClaseVuelo().isBlank() ||
                        request.getCantidadMaletas() == null
        ) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }

        if (request.getCantidadMaletas() < 0) {
            throw new RuntimeException("La cantidad de maletas no puede ser negativa");
        }
    }

    private void validarVueloReservable(Vuelo vuelo) {
        if (vuelo.getEstado() == null || vuelo.getEstado().getCodigo() == null) {
            throw new RuntimeException("El vuelo no tiene estado válido");
        }

        if (!"PROG".equalsIgnoreCase(vuelo.getEstado().getCodigo())) {
            throw new RuntimeException("Solo se pueden reservar vuelos en estado Programado");
        }

        if (vuelo.getFechaSalida() == null || vuelo.getFechaLlegada() == null) {
            throw new RuntimeException("El vuelo no tiene fechas válidas");
        }

        if (vuelo.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar un vuelo que ya inició");
        }
    }

    private void validarAsientoPerteneceAlAvion(Vuelo vuelo, Asiento asiento) {
        if (vuelo.getAvion() == null || asiento.getAvion() == null) {
            throw new RuntimeException("No se pudo validar el avión del asiento");
        }

        Long avionVueloId = vuelo.getAvion().getIdAvion();
        Long avionAsientoId = asiento.getAvion().getIdAvion();

        if (!avionVueloId.equals(avionAsientoId)) {
            throw new RuntimeException("El asiento seleccionado no pertenece al avión del vuelo");
        }
    }

    private BigDecimal obtenerPrecioPorClase(Vuelo vuelo, String codigoClase) {
        if ("ECONOMICA".equalsIgnoreCase(codigoClase)) {
            if (vuelo.getPrecioEconomica() == null) {
                throw new RuntimeException("El vuelo no tiene precio configurado para clase económica");
            }

            return vuelo.getPrecioEconomica();
        }

        if ("EJECUTIVA".equalsIgnoreCase(codigoClase)) {
            if (vuelo.getPrecioEjecutiva() == null) {
                throw new RuntimeException("El vuelo no tiene precio configurado para clase ejecutiva");
            }

            return vuelo.getPrecioEjecutiva();
        }

        throw new RuntimeException("Clase de vuelo no válida");
    }

    private CatalogoDetalle obtenerEstadoReservaInicial() {
        return catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_RESERVA", "CONF")
                .orElseGet(() -> catalogoDetalleRepository
                        .buscarPorCatalogoYCodigo("ESTADO_RESERVA", "RES")
                        .orElseThrow(() -> new RuntimeException("Estado de reserva inicial no encontrado"))
                );
    }

    private void registrarEquipajeBasicoSiAplica(
            VueloPasajero reserva,
            Integer cantidadMaletas,
            Usuario usuario
    ) {
        if (cantidadMaletas == null || cantidadMaletas <= 0) {
            return;
        }

        CatalogoDetalle tipoEquipaje = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("TIPO_EQUIPAJE", "DOC")
                .orElseThrow(() -> new RuntimeException("Tipo de equipaje documentado no encontrado"));

        CatalogoDetalle estadoEquipaje = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_EQUIPAJE", "REG")
                .orElseThrow(() -> new RuntimeException("Estado de equipaje registrado no encontrado"));

        for (int i = 1; i <= cantidadMaletas; i++) {
            Equipaje equipaje = new Equipaje();

            equipaje.setVueloPasajero(reserva);
            equipaje.setTipoEquipaje(tipoEquipaje);
            equipaje.setEstado(estadoEquipaje);
            equipaje.setPeso(BigDecimal.ZERO);
            equipaje.setDescripcion("Maleta registrada " + i);
            equipaje.setUsuarioCreacion(usuario);

            equipajeRepository.save(equipaje);
        }
    }

    // =========================================================
    // CONVERSORES
    // =========================================================

    private AeropuertoResponse convertirAeropuertoResponse(Aeropuerto aeropuerto) {
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

    private ClaseVueloResponse convertirClaseVueloResponse(CatalogoDetalle clase) {
        ClaseVueloResponse response = new ClaseVueloResponse();

        response.setIdClaseVuelo(clase.getId());
        response.setCodigo(clase.getCodigo());
        response.setValor(clase.getValor());

        return response;
    }

    private VueloDisponibleReservaResponse convertirVueloDisponibleResponse(Vuelo vuelo) {
        VueloDisponibleReservaResponse response = new VueloDisponibleReservaResponse();

        response.setIdVuelo(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());

        if (vuelo.getAerolinea() != null) {
            response.setAerolineaNombre(vuelo.getAerolinea().getNombre());
        }

        if (vuelo.getAeropuertoOrigen() != null) {
            response.setAeropuertoOrigenId(vuelo.getAeropuertoOrigen().getIdAeropuerto());
            response.setAeropuertoOrigen(vuelo.getAeropuertoOrigen().getNombre());
        }

        if (vuelo.getAeropuertoDestino() != null) {
            response.setAeropuertoDestinoId(vuelo.getAeropuertoDestino().getIdAeropuerto());
            response.setAeropuertoDestino(vuelo.getAeropuertoDestino().getNombre());
        }

        response.setFechaSalida(vuelo.getFechaSalida());
        response.setFechaLlegada(vuelo.getFechaLlegada());

        response.setPrecioEconomica(vuelo.getPrecioEconomica());
        response.setPrecioEjecutiva(vuelo.getPrecioEjecutiva());

        if (vuelo.getEstado() != null) {
            response.setEstado(vuelo.getEstado().getValor());
        }

        List<VueloEscalaResponse> escalas = construirEscalasResponse(vuelo);

        response.setEscalas(escalas);
        response.setCantidadEscalas(escalas.size());
        response.setRutaCompleta(generarRutaCompleta(vuelo, escalas));

        return response;
    }

    private VueloDetalleReservaResponse convertirVueloDetalleResponse(Vuelo vuelo) {
        VueloDetalleReservaResponse response = new VueloDetalleReservaResponse();

        response.setIdVuelo(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());

        if (vuelo.getAerolinea() != null) {
            response.setAerolineaNombre(vuelo.getAerolinea().getNombre());
        }

        if (vuelo.getAvion() != null) {
            response.setAvionId(vuelo.getAvion().getIdAvion());
            response.setAvionMarca(vuelo.getAvion().getMarca());
            response.setAvionModelo(vuelo.getAvion().getModelo());
            response.setAvionAnio(vuelo.getAvion().getAnio());
            response.setAvionCapacidad(vuelo.getAvion().getCapacidad());
        }

        if (vuelo.getAeropuertoOrigen() != null) {
            response.setAeropuertoOrigenId(vuelo.getAeropuertoOrigen().getIdAeropuerto());
            response.setAeropuertoOrigen(vuelo.getAeropuertoOrigen().getNombre());
            response.setCiudadOrigen(vuelo.getAeropuertoOrigen().getCiudad());
            response.setPaisOrigen(vuelo.getAeropuertoOrigen().getPais());
        }

        if (vuelo.getAeropuertoDestino() != null) {
            response.setAeropuertoDestinoId(vuelo.getAeropuertoDestino().getIdAeropuerto());
            response.setAeropuertoDestino(vuelo.getAeropuertoDestino().getNombre());
            response.setCiudadDestino(vuelo.getAeropuertoDestino().getCiudad());
            response.setPaisDestino(vuelo.getAeropuertoDestino().getPais());
        }

        response.setFechaSalida(vuelo.getFechaSalida());
        response.setFechaLlegada(vuelo.getFechaLlegada());

        response.setPrecioEconomica(vuelo.getPrecioEconomica());
        response.setPrecioEjecutiva(vuelo.getPrecioEjecutiva());

        if (vuelo.getEstado() != null) {
            response.setEstado(vuelo.getEstado().getValor());
        }

        List<VueloEscalaResponse> escalas = construirEscalasResponse(vuelo);

        response.setEscalas(escalas);
        response.setCantidadEscalas(escalas.size());
        response.setRutaCompleta(generarRutaCompleta(vuelo, escalas));

        return response;
    }

    private List<VueloEscalaResponse> construirEscalasResponse(Vuelo vuelo) {

        if (vuelo.getEscalas() == null || vuelo.getEscalas().isEmpty()) {
            return new ArrayList<>();
        }

        return vuelo.getEscalas()
                .stream()
                .sorted(Comparator.comparing(VueloEscala::getOrden))
                .map(this::convertirEscalaResponse)
                .toList();
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

        if (vuelo.getAeropuertoOrigen() != null) {
            ruta.add(vuelo.getAeropuertoOrigen().getNombre());
        }

        for (VueloEscalaResponse escala : escalas) {
            ruta.add(escala.getAeropuertoNombre());
        }

        if (vuelo.getAeropuertoDestino() != null) {
            ruta.add(vuelo.getAeropuertoDestino().getNombre());
        }

        return String.join(" → ", ruta);
    }

    private AsientoDisponibleResponse convertirAsientoDisponibleResponse(Asiento asiento) {
        AsientoDisponibleResponse response = new AsientoDisponibleResponse();

        response.setIdAsiento(asiento.getIdAsiento());
        response.setFila(asiento.getFila());
        response.setLetra(asiento.getLetra());
        response.setNumeroAsiento(construirNumeroAsiento(asiento));

        if (asiento.getTipoAsiento() != null) {
            response.setTipoAsiento(asiento.getTipoAsiento().getValor());
        }

        return response;
    }

    private ReservaVueloResponse convertirReservaVueloResponse(
            VueloPasajero reserva,
            Asiento asiento
    ) {
        ReservaVueloResponse response = new ReservaVueloResponse();

        response.setIdReserva(reserva.getIdVueloPasajero());
        response.setCodigoPaseAbordar(reserva.getCodigoPaseAbordar());

        if (reserva.getVuelo() != null) {
            response.setCodigoVuelo(reserva.getVuelo().getCodigoVuelo());
        }

        if (reserva.getPasajero() != null) {
            response.setNombrePasajero(reserva.getPasajero().getNombreCompleto());
            response.setNumeroPasaporte(reserva.getPasajero().getNoPasaporte());
        }

        if (reserva.getClaseVuelo() != null) {
            response.setClaseVuelo(reserva.getClaseVuelo().getValor());
        }

        response.setAsiento(construirNumeroAsiento(asiento));
        response.setCantidadMaletas(reserva.getCantidadMaletas());
        response.setPrecioPagado(reserva.getPrecioPagado());

        if (reserva.getEstado() != null) {
            response.setEstado(reserva.getEstado().getValor());
        }

        response.setFechaReserva(reserva.getFechaRegistro());

        return response;
    }

    private PaseAbordarResponse convertirPaseAbordarResponse(
            VueloPasajero reserva,
            Asiento asiento
    ) {
        PaseAbordarResponse response = new PaseAbordarResponse();

        response.setIdReserva(reserva.getIdVueloPasajero());
        response.setCodigoPaseAbordar(reserva.getCodigoPaseAbordar());

        Vuelo vuelo = reserva.getVuelo();
        Pasajero pasajero = reserva.getPasajero();

        if (vuelo != null) {
            response.setCodigoVuelo(vuelo.getCodigoVuelo());
            response.setFechaSalida(vuelo.getFechaSalida());
            response.setFechaLlegada(vuelo.getFechaLlegada());

            if (vuelo.getAerolinea() != null) {
                response.setAerolineaNombre(vuelo.getAerolinea().getNombre());
            }

            if (vuelo.getAeropuertoOrigen() != null) {
                response.setAeropuertoOrigen(vuelo.getAeropuertoOrigen().getNombre());
                response.setCiudadOrigen(vuelo.getAeropuertoOrigen().getCiudad());
                response.setPaisOrigen(vuelo.getAeropuertoOrigen().getPais());
            }

            if (vuelo.getAeropuertoDestino() != null) {
                response.setAeropuertoDestino(vuelo.getAeropuertoDestino().getNombre());
                response.setCiudadDestino(vuelo.getAeropuertoDestino().getCiudad());
                response.setPaisDestino(vuelo.getAeropuertoDestino().getPais());
            }
        }

        if (pasajero != null) {
            response.setNombrePasajero(pasajero.getNombreCompleto());
            response.setNumeroPasaporte(pasajero.getNoPasaporte());
            response.setNacionalidad(pasajero.getNacionalidad());
        }

        if (reserva.getClaseVuelo() != null) {
            response.setClaseVuelo(reserva.getClaseVuelo().getValor());
        }

        if (reserva.getEstado() != null) {
            response.setEstado(reserva.getEstado().getValor());
        }

        response.setAsiento(construirNumeroAsiento(asiento));
        response.setCantidadMaletas(reserva.getCantidadMaletas());
        response.setPrecioPagado(reserva.getPrecioPagado());

        return response;
    }

    // =========================================================
    // UTILIDADES
    // =========================================================

    private String construirNumeroAsiento(Asiento asiento) {
        if (asiento == null) {
            return null;
        }

        String fila = asiento.getFila() != null ? asiento.getFila().toString() : "";
        String letra = asiento.getLetra() != null ? asiento.getLetra() : "";

        return fila + letra;
    }

    private String generarCodigoPaseAbordar(Vuelo vuelo, VueloPasajero reserva) {
        int random = ThreadLocalRandom.current().nextInt(100, 999);

        String codigoVuelo = vuelo.getCodigoVuelo() != null
                ? vuelo.getCodigoVuelo().replace("-", "")
                : "VU";

        return "PAB-" + codigoVuelo + "-" + reserva.getIdVueloPasajero() + "-" + random;
    }
}
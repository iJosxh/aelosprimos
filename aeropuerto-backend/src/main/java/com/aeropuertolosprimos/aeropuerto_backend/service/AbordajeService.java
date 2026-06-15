package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.*;
import com.aeropuertolosprimos.aeropuerto_backend.entity.*;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.ReservaAsientoRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloPasajeroRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.EquipajeRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class AbordajeService {

    private static final BigDecimal RECARGO_POR_MALETA_EXTRA = new BigDecimal("50.00");

    private static final int HORAS_ANTICIPACION_ABORDAJE = 2;
    private static final int MINUTOS_TOLERANCIA_DESPUES_SALIDA = 30;

    private final VueloRepository vueloRepository;
    private final VueloPasajeroRepository vueloPasajeroRepository;
    private final ReservaAsientoRepository reservaAsientoRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final EquipajeRepository equipajeRepository;
    private final UsuarioRepository usuarioRepository;

    public AbordajeService(
            VueloRepository vueloRepository,
            VueloPasajeroRepository vueloPasajeroRepository,
            ReservaAsientoRepository reservaAsientoRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            EquipajeRepository equipajeRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.vueloRepository = vueloRepository;
        this.vueloPasajeroRepository = vueloPasajeroRepository;
        this.reservaAsientoRepository = reservaAsientoRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.equipajeRepository = equipajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<AbordajeVueloResponse> listarVuelosParaAbordaje() {
        LocalDateTime fechaMinima = LocalDateTime.now()
                .minusMinutes(MINUTOS_TOLERANCIA_DESPUES_SALIDA);

        List<Vuelo> vuelos = vueloRepository.listarVuelosParaAbordaje(fechaMinima);

        if (vuelos.isEmpty()) {
            throw new RuntimeException("No hay vuelos disponibles");
        }

        return vuelos.stream()
                .map(this::convertirVueloResponse)
                .toList();
    }

    public List<AbordajeVueloResponse> listarVuelosProgramadosProximos() {

        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime fechaDesde = ahora.minusMinutes(MINUTOS_TOLERANCIA_DESPUES_SALIDA);
        LocalDateTime fechaHasta = ahora.plusHours(HORAS_ANTICIPACION_ABORDAJE);

        return vueloRepository
                .listarVuelosProgramadosProximosParaAbordaje(fechaDesde, fechaHasta)
                .stream()
                .map(this::convertirVueloResponse)
                .toList();
    }

    @Transactional
    public AbordajeVueloResponse iniciarAbordaje(Long idVuelo) {

        if (idVuelo == null) {
            throw new RuntimeException("Debe ingresar el vuelo");
        }

        Vuelo vuelo = obtenerVuelo(idVuelo);

        validarVueloProgramado(vuelo);
        validarVentanaInicioAbordaje(vuelo);

        CatalogoDetalle estadoAbordaje = obtenerEstadoVuelo("ABOR");
        Usuario usuarioActual = obtenerUsuarioActual();

        vuelo.setEstado(estadoAbordaje);
        vuelo.setUsuarioModificacion(usuarioActual);

        Vuelo vueloActualizado = vueloRepository.save(vuelo);

        return convertirVueloResponse(vueloActualizado);
    }

    @Transactional(readOnly = true)
    public AbordajePasajeroResponse buscarPasajero(Long idVuelo, String noPasaporte) {
        validarBusquedaPasajero(idVuelo, noPasaporte);

        Vuelo vuelo = obtenerVuelo(idVuelo);
        validarVueloEnAbordaje(vuelo);

        VueloPasajero reserva = vueloPasajeroRepository
                .buscarPorVueloYPasaporte(idVuelo, noPasaporte.trim())
                .orElseThrow(() -> new RuntimeException("El pasajero no se encuentra registrado en el vuelo"));

        ReservaAsiento reservaAsiento = obtenerReservaAsiento(reserva.getIdVueloPasajero());

        AbordajePasajeroResponse response = convertirPasajeroResponse(
                reserva,
                reservaAsiento != null ? reservaAsiento.getAsiento() : null
        );

        response.setMensaje("Pasajero encontrado");
        return response;
    }

    @Transactional
    public AbordajePasajeroResponse abordarPasajero(AbordarPasajeroRequest request) {
        validarAbordarRequest(request);

        Vuelo vuelo = obtenerVuelo(request.getIdVuelo());
        validarVueloEnAbordaje(vuelo);

        VueloPasajero reserva = vueloPasajeroRepository
                .buscarPorVueloYPasaporte(request.getIdVuelo(), request.getNoPasaporte().trim())
                .orElseThrow(() -> new RuntimeException("El pasajero no se encuentra registrado en el vuelo"));

        validarReservaPuedeAbordar(reserva);

        Integer maletasReservadas = obtenerEntero(reserva.getCantidadMaletas());
        Integer maletasPresentadas = obtenerEntero(request.getCantidadMaletasPresentadas());
        Integer maletasExtra = Math.max(0, maletasPresentadas - maletasReservadas);
        BigDecimal recargo = RECARGO_POR_MALETA_EXTRA.multiply(BigDecimal.valueOf(maletasExtra));

        CatalogoDetalle estadoAbordado = obtenerEstadoReserva("ABOR");

        reserva.setCantidadMaletasPresentadas(maletasPresentadas);
        reserva.setMaletasExtra(maletasExtra);
        reserva.setRecargoEquipaje(recargo);
        reserva.setFechaAbordaje(LocalDateTime.now());
        reserva.setEstado(estadoAbordado);

        VueloPasajero reservaGuardada = vueloPasajeroRepository.save(reserva);

        actualizarPesosEquipaje(reservaGuardada, request.getPesosMaletas());

        ReservaAsiento reservaAsiento = obtenerReservaAsiento(reservaGuardada.getIdVueloPasajero());

        AbordajePasajeroResponse response = convertirPasajeroResponse(
                reservaGuardada,
                reservaAsiento != null ? reservaAsiento.getAsiento() : null
        );

        if (maletasExtra > 0) {
            response.setMensaje("Se agregó " + recargo + " por recargo de equipaje");
        } else {
            response.setMensaje("Pasajero abordado correctamente");
        }

        return response;
    }

    @Transactional
    public FinalizarAbordajeResponse finalizarAbordaje(Long idVuelo) {
        if (idVuelo == null) {
            throw new RuntimeException("Debe ingresar el vuelo");
        }

        Vuelo vuelo = obtenerVuelo(idVuelo);
        validarVueloEnAbordaje(vuelo);

        CatalogoDetalle estadoReservaCancelada = obtenerEstadoReserva("CANC");
        CatalogoDetalle estadoVueloAbordado = obtenerEstadoVuelo("ABORDADO");

        List<VueloPasajero> reservasPendientes = vueloPasajeroRepository
                .listarPendientesDeAbordarPorVuelo(idVuelo);

        for (VueloPasajero reserva : reservasPendientes) {
            reserva.setEstado(estadoReservaCancelada);
        }

        vueloPasajeroRepository.saveAll(reservasPendientes);

        vuelo.setEstado(estadoVueloAbordado);
        vueloRepository.save(vuelo);

        FinalizarAbordajeResponse response = new FinalizarAbordajeResponse();
        response.setIdVuelo(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());
        response.setBoletosCancelados(reservasPendientes.size());
        response.setEstadoVuelo(estadoVueloAbordado.getValor());
        response.setMensaje("Se completó el abordaje");

        return response;
    }

    private void validarBusquedaPasajero(Long idVuelo, String noPasaporte) {
        if (idVuelo == null || noPasaporte == null || noPasaporte.isBlank()) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }
    }

    private void validarAbordarRequest(AbordarPasajeroRequest request) {
        if (
                request == null ||
                        request.getIdVuelo() == null ||
                        request.getNoPasaporte() == null ||
                        request.getNoPasaporte().isBlank() ||
                        request.getCantidadMaletasPresentadas() == null
        ) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }

        if (request.getCantidadMaletasPresentadas() < 0) {
            throw new RuntimeException("La cantidad de maletas presentadas no puede ser negativa");
        }

        if (request.getCantidadMaletasPresentadas() > 0) {
            if (request.getPesosMaletas() == null || request.getPesosMaletas().isEmpty()) {
                throw new RuntimeException("Debe ingresar el peso de las maletas");
            }

            if (request.getPesosMaletas().size() != request.getCantidadMaletasPresentadas()) {
                throw new RuntimeException("Debe ingresar el peso de cada maleta presentada");
            }

            for (BigDecimal peso : request.getPesosMaletas()) {
                if (peso == null || peso.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("El peso de cada maleta debe ser mayor a cero");
                }

                if (peso.compareTo(new BigDecimal("999.99")) > 0) {
                    throw new RuntimeException("El peso de cada maleta no puede superar 999.99");
                }
            }
        }
    }

    private Vuelo obtenerVuelo(Long idVuelo) {
        return vueloRepository.obtenerVueloParaAbordaje(idVuelo)
                .orElseThrow(() -> new RuntimeException("El vuelo seleccionado no existe"));
    }

    private void validarVueloEnAbordaje(Vuelo vuelo) {
        if (vuelo.getEstado() == null || vuelo.getEstado().getCodigo() == null) {
            throw new RuntimeException("El vuelo no tiene estado válido");
        }

        if (!"ABOR".equalsIgnoreCase(vuelo.getEstado().getCodigo())) {
            throw new RuntimeException("El vuelo no se encuentra en estado de abordaje");
        }

        if (vuelo.getFechaSalida() == null) {
            throw new RuntimeException("El vuelo no tiene fecha de salida válida");
        }

        LocalDateTime limitePermitido = vuelo.getFechaSalida()
                .plusMinutes(MINUTOS_TOLERANCIA_DESPUES_SALIDA);

        if (LocalDateTime.now().isAfter(limitePermitido)) {
            throw new RuntimeException("El vuelo ya no está disponible para abordaje");
        }
    }

    private void validarVueloProgramado(Vuelo vuelo) {
        if (vuelo.getEstado() == null || vuelo.getEstado().getCodigo() == null) {
            throw new RuntimeException("El vuelo no tiene estado válido");
        }

        String codigoEstado = vuelo.getEstado().getCodigo();

        if ("ABOR".equalsIgnoreCase(codigoEstado)) {
            throw new RuntimeException("El vuelo ya se encuentra en estado de abordaje");
        }

        if (!"PROG".equalsIgnoreCase(codigoEstado)) {
            throw new RuntimeException("Solo se puede iniciar abordaje de vuelos programados");
        }
    }

    private void validarVentanaInicioAbordaje(Vuelo vuelo) {
        if (vuelo.getFechaSalida() == null) {
            throw new RuntimeException("El vuelo no tiene fecha de salida válida");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioPermitido = vuelo.getFechaSalida().minusHours(HORAS_ANTICIPACION_ABORDAJE);
        LocalDateTime finPermitido = vuelo.getFechaSalida().plusMinutes(MINUTOS_TOLERANCIA_DESPUES_SALIDA);

        if (ahora.isBefore(inicioPermitido)) {
            throw new RuntimeException("El abordaje solo puede iniciar cuando falten 2 horas o menos para la salida del vuelo");
        }

        if (ahora.isAfter(finPermitido)) {
            throw new RuntimeException("No se puede iniciar abordaje porque la hora de salida del vuelo ya superó la ventana permitida");
        }
    }

    private void validarReservaPuedeAbordar(VueloPasajero reserva) {
        if (reserva.getEstado() == null || reserva.getEstado().getCodigo() == null) {
            throw new RuntimeException("La reserva no tiene estado válido");
        }

        String codigoEstado = reserva.getEstado().getCodigo();

        if ("CANC".equalsIgnoreCase(codigoEstado)) {
            throw new RuntimeException("La reserva se encuentra cancelada");
        }

        if ("ABOR".equalsIgnoreCase(codigoEstado)) {
            throw new RuntimeException("El pasajero ya fue abordado");
        }
    }

    private void actualizarPesosEquipaje(
            VueloPasajero reserva,
            List<BigDecimal> pesosMaletas
    ) {
        Integer cantidadPresentada = obtenerEntero(reserva.getCantidadMaletasPresentadas());

        if (cantidadPresentada <= 0) {
            return;
        }

        List<Equipaje> equipajesExistentes = new ArrayList<>(
                equipajeRepository.findByVueloPasajero_IdVueloPasajero(reserva.getIdVueloPasajero())
        );

        equipajesExistentes.sort((a, b) -> a.getIdEquipaje().compareTo(b.getIdEquipaje()));

        CatalogoDetalle tipoEquipaje = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("TIPO_EQUIPAJE", "DOC")
                .orElseThrow(() -> new RuntimeException("Tipo de equipaje documentado no encontrado"));

        CatalogoDetalle estadoEquipaje = catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_EQUIPAJE", "REG")
                .orElseThrow(() -> new RuntimeException("Estado de equipaje registrado no encontrado"));

        for (int i = 0; i < cantidadPresentada; i++) {
            BigDecimal peso = pesosMaletas.get(i);

            Equipaje equipaje;

            if (i < equipajesExistentes.size()) {
                equipaje = equipajesExistentes.get(i);
            } else {
                equipaje = new Equipaje();
                equipaje.setVueloPasajero(reserva);
                equipaje.setTipoEquipaje(tipoEquipaje);
                equipaje.setEstado(estadoEquipaje);
                equipaje.setDescripcion("Maleta extra registrada " + (i + 1));
                equipaje.setUsuarioCreacion(reserva.getUsuarioCreacion());
            }

            equipaje.setPeso(peso);
            equipajeRepository.save(equipaje);
        }
    }

    private CatalogoDetalle obtenerEstadoReserva(String codigo) {
        return catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_RESERVA", codigo)
                .orElseThrow(() -> new RuntimeException("No se encontró el estado de reserva " + codigo));
    }

    private CatalogoDetalle obtenerEstadoVuelo(String codigo) {
        return catalogoDetalleRepository
                .buscarPorCatalogoYCodigo("ESTADO_VUELO", codigo)
                .orElseThrow(() -> new RuntimeException("No se encontró el estado de vuelo " + codigo));
    }

    private ReservaAsiento obtenerReservaAsiento(Long idReserva) {
        return reservaAsientoRepository.obtenerPorReserva(idReserva).orElse(null);
    }

    private Integer obtenerEntero(Integer valor) {
        return valor != null ? valor : 0;
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

    private AbordajeVueloResponse convertirVueloResponse(Vuelo vuelo) {
        AbordajeVueloResponse response = new AbordajeVueloResponse();

        response.setIdVuelo(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());
        response.setFechaSalida(vuelo.getFechaSalida());
        response.setFechaLlegada(vuelo.getFechaLlegada());

        if (vuelo.getAerolinea() != null) {
            response.setAerolineaNombre(vuelo.getAerolinea().getNombre());
        }

        if (vuelo.getAeropuertoOrigen() != null) {
            response.setOrigen(vuelo.getAeropuertoOrigen().getNombre());
        }

        if (vuelo.getAeropuertoDestino() != null) {
            response.setDestino(vuelo.getAeropuertoDestino().getNombre());
        }

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

    private AbordajePasajeroResponse convertirPasajeroResponse(
            VueloPasajero reserva,
            Asiento asiento
    ) {
        AbordajePasajeroResponse response = new AbordajePasajeroResponse();

        response.setIdReserva(reserva.getIdVueloPasajero());
        response.setCodigoPaseAbordar(reserva.getCodigoPaseAbordar());
        response.setCantidadMaletasReservadas(obtenerEntero(reserva.getCantidadMaletas()));
        response.setCantidadMaletasPresentadas(obtenerEntero(reserva.getCantidadMaletasPresentadas()));
        response.setMaletasExtra(obtenerEntero(reserva.getMaletasExtra()));
        response.setRecargoEquipaje(reserva.getRecargoEquipaje() != null ? reserva.getRecargoEquipaje() : BigDecimal.ZERO);
        response.setFechaAbordaje(reserva.getFechaAbordaje());

        if (reserva.getVuelo() != null) {
            response.setCodigoVuelo(reserva.getVuelo().getCodigoVuelo());
        }

        if (reserva.getPasajero() != null) {
            response.setNombrePasajero(reserva.getPasajero().getNombreCompleto());
            response.setNoPasaporte(reserva.getPasajero().getNoPasaporte());
        }

        if (reserva.getClaseVuelo() != null) {
            response.setClaseVuelo(reserva.getClaseVuelo().getValor());
        }

        if (reserva.getEstado() != null) {
            response.setEstado(reserva.getEstado().getValor());
        }

        response.setAsiento(construirNumeroAsiento(asiento));

        return response;
    }

    private String construirNumeroAsiento(Asiento asiento) {
        if (asiento == null) {
            return null;
        }

        String fila = asiento.getFila() != null ? asiento.getFila().toString() : "";
        String letra = asiento.getLetra() != null ? asiento.getLetra() : "";

        return fila + letra;
    }
}
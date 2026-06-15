package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ConsultaVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Vuelo;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaVueloService {

    private final VueloRepository vueloRepository;
    private final ReporteArchivoService reporteArchivoService;

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ConsultaVueloService(
            VueloRepository vueloRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.vueloRepository = vueloRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public ConsultaVueloResponse consultarPorNumeroVuelo(String numeroVuelo) {
        String numeroNormalizado = validarNumeroVuelo(numeroVuelo);

        Vuelo vuelo = buscarVuelo(numeroNormalizado)
                .orElseThrow(() -> new RuntimeException("El número de vuelo ingresado no se encontró"));

        return convertirResponse(vuelo);
    }

    public byte[] generarPdf(String numeroVuelo) {
        ConsultaVueloResponse consulta = consultarPorNumeroVuelo(numeroVuelo);

        return reporteArchivoService.generarPdf(
                "Consulta de vuelo " + consulta.getCodigoVuelo(),
                List.of("Campo", "Valor"),
                construirFilasReporte(consulta)
        );
    }

    public byte[] generarExcel(String numeroVuelo) {
        ConsultaVueloResponse consulta = consultarPorNumeroVuelo(numeroVuelo);

        return reporteArchivoService.generarExcel(
                "Consulta de vuelo " + consulta.getCodigoVuelo(),
                List.of("Campo", "Valor"),
                construirFilasReporte(consulta)
        );
    }

    private String validarNumeroVuelo(String numeroVuelo) {
        if (numeroVuelo == null || numeroVuelo.trim().isBlank()) {
            throw new RuntimeException("Debe ingresar el número de vuelo");
        }

        return numeroVuelo.trim();
    }

    private Optional<Vuelo> buscarVuelo(String numeroVuelo) {
        Optional<Vuelo> vueloPorCodigo = vueloRepository.buscarConsultaPorCodigoVuelo(numeroVuelo);

        if (vueloPorCodigo.isPresent()) {
            return vueloPorCodigo;
        }

        if (numeroVuelo.matches("\\d+")) {
            return vueloRepository.obtenerDetalleReserva(Long.parseLong(numeroVuelo));
        }

        return Optional.empty();
    }

    private ConsultaVueloResponse convertirResponse(Vuelo vuelo) {
        ConsultaVueloResponse response = new ConsultaVueloResponse();

        response.setIdVuelo(vuelo.getIdVuelo());
        response.setCodigoVuelo(vuelo.getCodigoVuelo());

        if (vuelo.getAvion() != null) {
            response.setModeloAvion(vuelo.getAvion().getModelo());
            response.setMarcaAvion(vuelo.getAvion().getMarca());
        }

        if (vuelo.getAerolinea() != null) {
            response.setAerolinea(vuelo.getAerolinea().getNombre());
        }

        if (vuelo.getAeropuertoOrigen() != null) {
            response.setOrigen(vuelo.getAeropuertoOrigen().getNombre());
            response.setCiudadOrigen(vuelo.getAeropuertoOrigen().getCiudad());
            response.setPaisOrigen(vuelo.getAeropuertoOrigen().getPais());
        }

        if (vuelo.getAeropuertoDestino() != null) {
            response.setDestino(vuelo.getAeropuertoDestino().getNombre());
            response.setCiudadDestino(vuelo.getAeropuertoDestino().getCiudad());
            response.setPaisDestino(vuelo.getAeropuertoDestino().getPais());
        }

        response.setFechaHoraSalida(vuelo.getFechaSalida());
        response.setFechaHoraLlegada(vuelo.getFechaLlegada());

        if (vuelo.getEstado() != null) {
            response.setEstado(vuelo.getEstado().getValor());
        }

        return response;
    }

    private List<List<String>> construirFilasReporte(ConsultaVueloResponse consulta) {
        return List.of(
                List.of("Número de vuelo", valor(consulta.getCodigoVuelo())),
                List.of("Modelo del avión", valor(consulta.getModeloAvion())),
                List.of("Aerolínea", valor(consulta.getAerolinea())),
                List.of("Origen", construirUbicacion(consulta.getOrigen(), consulta.getCiudadOrigen(), consulta.getPaisOrigen())),
                List.of("Destino", construirUbicacion(consulta.getDestino(), consulta.getCiudadDestino(), consulta.getPaisDestino())),
                List.of("Fecha y hora de salida", consulta.getFechaHoraSalida() != null ? consulta.getFechaHoraSalida().format(FORMATO_FECHA_HORA) : ""),
                List.of("Fecha y hora de llegada", consulta.getFechaHoraLlegada() != null ? consulta.getFechaHoraLlegada().format(FORMATO_FECHA_HORA) : ""),
                List.of("Estado", valor(consulta.getEstado()))
        );
    }

    private String construirUbicacion(String aeropuerto, String ciudad, String pais) {
        StringBuilder ubicacion = new StringBuilder(valor(aeropuerto));

        if (ciudad != null && !ciudad.isBlank()) {
            ubicacion.append(" - ").append(ciudad);
        }

        if (pais != null && !pais.isBlank()) {
            ubicacion.append(", ").append(pais);
        }

        return ubicacion.toString();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
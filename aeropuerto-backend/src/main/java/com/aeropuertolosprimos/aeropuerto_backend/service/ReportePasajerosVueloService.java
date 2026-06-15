package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReportePasajerosVueloFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReportePasajerosVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Vuelo;
import com.aeropuertolosprimos.aeropuerto_backend.entity.VueloPasajero;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloPasajeroRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class ReportePasajerosVueloService {

    private final VueloRepository vueloRepository;
    private final VueloPasajeroRepository vueloPasajeroRepository;
    private final UsuarioRepository usuarioRepository;
    private final AerolineaRepository aerolineaRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReportePasajerosVueloService(
            VueloRepository vueloRepository,
            VueloPasajeroRepository vueloPasajeroRepository,
            UsuarioRepository usuarioRepository,
            AerolineaRepository aerolineaRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.vueloRepository = vueloRepository;
        this.vueloPasajeroRepository = vueloPasajeroRepository;
        this.usuarioRepository = usuarioRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public List<ReportePasajerosVueloResponse> buscar(ReportePasajerosVueloFiltro filtro) {
        String numeroVuelo = validarNumeroVuelo(filtro);

        Vuelo vuelo = buscarVuelo(numeroVuelo)
                .orElseThrow(() -> new RuntimeException("El número de vuelo ingresado no existe."));

        validarUsuarioMismaAerolinea(vuelo);

        List<VueloPasajero> pasajeros = vueloPasajeroRepository
                .listarPasajerosReportePorVuelo(vuelo.getIdVuelo());

        if (pasajeros.isEmpty()) {
            throw new RuntimeException("El vuelo consultado no tiene pasajeros registrados.");
        }

        return pasajeros.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public byte[] generarPdf(ReportePasajerosVueloFiltro filtro) {
        List<ReportePasajerosVueloResponse> datos = buscar(filtro);
        String numeroVuelo = datos.get(0).getNumeroVuelo();

        return reporteArchivoService.generarPdf(
                "Reporte de pasajeros por vuelo " + numeroVuelo,
                encabezados(),
                construirFilas(datos)
        );
    }

    public byte[] generarExcel(ReportePasajerosVueloFiltro filtro) {
        List<ReportePasajerosVueloResponse> datos = buscar(filtro);
        String numeroVuelo = datos.get(0).getNumeroVuelo();

        return reporteArchivoService.generarExcel(
                "Pasajeros vuelo " + numeroVuelo,
                encabezados(),
                construirFilas(datos)
        );
    }

    private String validarNumeroVuelo(ReportePasajerosVueloFiltro filtro) {
        if (filtro == null || filtro.getNumeroVuelo() == null || filtro.getNumeroVuelo().trim().isBlank()) {
            throw new RuntimeException("Debe ingresar el número de vuelo");
        }

        return filtro.getNumeroVuelo().trim();
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

    private void validarUsuarioMismaAerolinea(Vuelo vuelo) {
        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual.getAerolinea() == null || usuarioActual.getAerolinea().getIdAerolinea() == null) {
            throw new RuntimeException("No tiene una aerolínea asignada.");
        }

        if (vuelo.getAerolinea() == null || vuelo.getAerolinea().getIdAerolinea() == null) {
            throw new RuntimeException("El vuelo consultado no tiene aerolínea asignada.");
        }

        Long idAerolineaUsuario = usuarioActual.getAerolinea().getIdAerolinea();
        Long idAerolineaVuelo = vuelo.getAerolinea().getIdAerolinea();

        if (!idAerolineaUsuario.equals(idAerolineaVuelo)) {
            throw new RuntimeException("No puede consultar vuelos de otra aerolínea.");
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new RuntimeException("No se encontró el usuario autenticado.");
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario autenticado."));
    }

    private ReportePasajerosVueloResponse convertirAResponse(VueloPasajero vueloPasajero) {
        ReportePasajerosVueloResponse response = new ReportePasajerosVueloResponse();

        response.setIdVueloPasajero(vueloPasajero.getIdVueloPasajero());

        if (vueloPasajero.getVuelo() != null) {
            response.setIdVuelo(vueloPasajero.getVuelo().getIdVuelo());
            response.setNumeroVuelo(vueloPasajero.getVuelo().getCodigoVuelo());

            if (vueloPasajero.getVuelo().getAerolinea() != null) {
                response.setAerolinea(vueloPasajero.getVuelo().getAerolinea().getNombre());
            }
        }

        Pasajero pasajero = vueloPasajero.getPasajero();

        if (pasajero != null) {
            response.setNombrePasajero(pasajero.getNombreCompleto());
            response.setNumeroPasaporte(pasajero.getNoPasaporte());
            response.setNacionalidad(pasajero.getNacionalidad());
            response.setEdad(calcularEdad(pasajero));
            response.setTelefono(construirTelefono(pasajero));
            response.setCorreoElectronico(pasajero.getCorreo());
        }

        return response;
    }

    private Integer calcularEdad(Pasajero pasajero) {
        if (pasajero.getFechaNacimiento() == null) {
            return null;
        }

        return Period.between(pasajero.getFechaNacimiento(), LocalDate.now()).getYears();
    }

    private String construirTelefono(Pasajero pasajero) {
        String codigoArea = pasajero.getCodigoArea() == null ? "" : pasajero.getCodigoArea().trim();
        String telefono = pasajero.getTelefono() == null ? "" : pasajero.getTelefono().trim();

        if (codigoArea.isBlank()) {
            return telefono;
        }

        if (telefono.isBlank()) {
            return codigoArea;
        }

        return codigoArea + " " + telefono;
    }

    private List<String> encabezados() {
        return List.of(
                "Nombre del pasajero",
                "Número de pasaporte",
                "Nacionalidad",
                "Edad",
                "Teléfono",
                "Correo electrónico"
        );
    }

    private List<List<String>> construirFilas(List<ReportePasajerosVueloResponse> datos) {
        return datos.stream()
                .map(pasajero -> List.of(
                        valor(pasajero.getNombrePasajero()),
                        valor(pasajero.getNumeroPasaporte()),
                        valor(pasajero.getNacionalidad()),
                        pasajero.getEdad() == null ? "" : pasajero.getEdad().toString(),
                        valor(pasajero.getTelefono()),
                        valor(pasajero.getCorreoElectronico())
                ))
                .toList();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
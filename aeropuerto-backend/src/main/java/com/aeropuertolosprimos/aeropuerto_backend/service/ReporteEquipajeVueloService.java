package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteEquipajeVueloRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteEquipajeVueloResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Equipaje;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Vuelo;
import com.aeropuertolosprimos.aeropuerto_backend.repository.EquipajeRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteEquipajeVueloService {

    private final VueloRepository vueloRepository;
    private final EquipajeRepository equipajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReporteEquipajeVueloService(
            VueloRepository vueloRepository,
            EquipajeRepository equipajeRepository,
            UsuarioRepository usuarioRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.vueloRepository = vueloRepository;
        this.equipajeRepository = equipajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    @Transactional(readOnly = true)
    public List<ReporteEquipajeVueloResponse> buscar(ReporteEquipajeVueloRequest request, String username) {
        String numeroVuelo = validarNumeroVuelo(request);
        Vuelo vuelo = obtenerVuelo(numeroVuelo);

        validarUsuarioPuedeConsultar(vuelo, username);

        List<Equipaje> equipajes = equipajeRepository.buscarPorVueloParaReporte(vuelo.getIdVuelo());

        if (equipajes.isEmpty()) {
            throw new RuntimeException("No se encontraron equipajes para el vuelo consultado.");
        }

        return equipajes.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] generarPdf(ReporteEquipajeVueloRequest request, String username) {
        List<ReporteEquipajeVueloResponse> datos = buscar(request, username);
        String numeroVuelo = datos.get(0).getNumeroVuelo();

        return reporteArchivoService.generarPdf(
                "Reporte de equipaje por vuelo " + numeroVuelo,
                encabezados(),
                construirFilas(datos)
        );
    }

    @Transactional(readOnly = true)
    public byte[] generarExcel(ReporteEquipajeVueloRequest request, String username) {
        List<ReporteEquipajeVueloResponse> datos = buscar(request, username);
        String numeroVuelo = datos.get(0).getNumeroVuelo();

        return reporteArchivoService.generarExcel(
                "Reporte equipaje vuelo " + numeroVuelo,
                encabezados(),
                construirFilas(datos)
        );
    }

    private String validarNumeroVuelo(ReporteEquipajeVueloRequest request) {
        if (request == null || request.getNumeroVuelo() == null || request.getNumeroVuelo().trim().isBlank()) {
            throw new RuntimeException("Debe ingresar los campos obligatorios");
        }

        return request.getNumeroVuelo().trim();
    }

    private Vuelo obtenerVuelo(String numeroVuelo) {
        Optional<Vuelo> vueloPorCodigo = vueloRepository.buscarConsultaPorCodigoVuelo(numeroVuelo);

        if (vueloPorCodigo.isPresent()) {
            return vueloPorCodigo.get();
        }

        if (numeroVuelo.matches("\\d+")) {
            return vueloRepository.obtenerDetalleReserva(Long.parseLong(numeroVuelo))
                    .orElseThrow(() -> new RuntimeException("El número de vuelo ingresado no existe."));
        }

        throw new RuntimeException("El número de vuelo ingresado no existe.");
    }

    private void validarUsuarioPuedeConsultar(Vuelo vuelo, String username) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("No puede consultar vuelos de otra aerolínea.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No puede consultar vuelos de otra aerolínea."));

        if (usuario.getAerolinea() == null || usuario.getAerolinea().getIdAerolinea() == null) {
            throw new RuntimeException("No puede consultar vuelos de otra aerolínea.");
        }

        if (vuelo.getAerolinea() == null || vuelo.getAerolinea().getIdAerolinea() == null) {
            throw new RuntimeException("No puede consultar vuelos de otra aerolínea.");
        }

        Long idAerolineaUsuario = usuario.getAerolinea().getIdAerolinea();
        Long idAerolineaVuelo = vuelo.getAerolinea().getIdAerolinea();

        if (!idAerolineaUsuario.equals(idAerolineaVuelo)) {
            throw new RuntimeException("No puede consultar vuelos de otra aerolínea.");
        }
    }

    private ReporteEquipajeVueloResponse convertirAResponse(Equipaje equipaje) {
        ReporteEquipajeVueloResponse response = new ReporteEquipajeVueloResponse();

        response.setIdEquipaje(equipaje.getIdEquipaje());
        response.setMaleta(construirMaleta(equipaje));
        response.setPeso(equipaje.getPeso() != null ? equipaje.getPeso() : BigDecimal.ZERO);

        if (equipaje.getVueloPasajero() != null) {
            if (equipaje.getVueloPasajero().getVuelo() != null) {
                response.setNumeroVuelo(equipaje.getVueloPasajero().getVuelo().getCodigoVuelo());
            }

            Pasajero pasajero = equipaje.getVueloPasajero().getPasajero();

            if (pasajero != null) {
                response.setNombrePasajero(pasajero.getNombreCompleto());
            }
        }

        return response;
    }

    private String construirMaleta(Equipaje equipaje) {
        String descripcion = valor(equipaje.getDescripcion());
        String tipo = equipaje.getTipoEquipaje() != null ? valor(equipaje.getTipoEquipaje().getValor()) : "";

        if (!descripcion.isBlank() && !tipo.isBlank()) {
            return descripcion + " - " + tipo;
        }

        if (!descripcion.isBlank()) {
            return descripcion;
        }

        if (!tipo.isBlank()) {
            return tipo;
        }

        return "Maleta " + equipaje.getIdEquipaje();
    }

    private List<String> encabezados() {
        return List.of("Nombre del pasajero", "Maleta", "Peso");
    }

    private List<List<String>> construirFilas(List<ReporteEquipajeVueloResponse> datos) {
        return datos.stream()
                .map(item -> List.of(
                        valor(item.getNombrePasajero()),
                        valor(item.getMaleta()),
                        formatearPeso(item.getPeso())
                ))
                .toList();
    }

    private String formatearPeso(BigDecimal peso) {
        if (peso == null) {
            return "0.00";
        }

        return peso.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
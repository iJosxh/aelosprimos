package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAerolineasAeropuertoFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAerolineasAeropuertoResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.AerolineaAeropuerto;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaAeropuertoRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AeropuertoRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AvionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteAerolineasAeropuertoService {

    private final AeropuertoRepository aeropuertoRepository;
    private final AerolineaAeropuertoRepository aerolineaAeropuertoRepository;
    private final AvionRepository avionRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReporteAerolineasAeropuertoService(
            AeropuertoRepository aeropuertoRepository,
            AerolineaAeropuertoRepository aerolineaAeropuertoRepository,
            AvionRepository avionRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.aeropuertoRepository = aeropuertoRepository;
        this.aerolineaAeropuertoRepository = aerolineaAeropuertoRepository;
        this.avionRepository = avionRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public List<ReporteAerolineasAeropuertoResponse> buscar(ReporteAerolineasAeropuertoFiltro filtro) {
        Long idAeropuerto = validarFiltro(filtro);

        aeropuertoRepository.buscarActivoPorId(idAeropuerto)
                .orElseThrow(() -> new RuntimeException("El aeropuerto seleccionado no existe o no está activo."));

        List<AerolineaAeropuerto> relaciones = aerolineaAeropuertoRepository
                .listarAerolineasActivasPorAeropuerto(idAeropuerto);

        if (relaciones.isEmpty()) {
            throw new RuntimeException("El aeropuerto consultado no tiene aerolíneas");
        }

        Map<Long, Aerolinea> aerolineasUnicas = new LinkedHashMap<>();

        for (AerolineaAeropuerto relacion : relaciones) {
            if (relacion.getAerolinea() != null && relacion.getAerolinea().getIdAerolinea() != null) {
                aerolineasUnicas.putIfAbsent(
                        relacion.getAerolinea().getIdAerolinea(),
                        relacion.getAerolinea()
                );
            }
        }

        List<ReporteAerolineasAeropuertoResponse> respuesta = new ArrayList<>();

        for (Aerolinea aerolinea : aerolineasUnicas.values()) {
            respuesta.add(convertirAResponse(aerolinea));
        }

        return respuesta;
    }

    public byte[] generarPdf(ReporteAerolineasAeropuertoFiltro filtro) {
        List<ReporteAerolineasAeropuertoResponse> datos = buscar(filtro);

        return reporteArchivoService.generarPdf(
                "Reporte de aerolíneas por aeropuerto",
                encabezados(),
                construirFilas(datos)
        );
    }

    public byte[] generarExcel(ReporteAerolineasAeropuertoFiltro filtro) {
        List<ReporteAerolineasAeropuertoResponse> datos = buscar(filtro);

        return reporteArchivoService.generarExcel(
                "Reporte aerolíneas aeropuerto",
                encabezados(),
                construirFilas(datos)
        );
    }

    private Long validarFiltro(ReporteAerolineasAeropuertoFiltro filtro) {
        if (filtro == null || filtro.getIdAeropuerto() == null) {
            throw new RuntimeException("Debe seleccionar un aeropuerto.");
        }

        if (filtro.getIdAeropuerto() <= 0) {
            throw new RuntimeException("Debe seleccionar un aeropuerto válido.");
        }

        return filtro.getIdAeropuerto();
    }

    private ReporteAerolineasAeropuertoResponse convertirAResponse(Aerolinea aerolinea) {
        ReporteAerolineasAeropuertoResponse response = new ReporteAerolineasAeropuertoResponse();

        response.setIdAerolinea(aerolinea.getIdAerolinea());
        response.setNombreAerolinea(aerolinea.getNombre());
        response.setCantidadAviones(avionRepository.contarAvionesActivosPorAerolinea(aerolinea.getIdAerolinea()));
        response.setDestinosAutorizados(aerolineaAeropuertoRepository.contarAeropuertosAutorizados(aerolinea.getIdAerolinea()));

        return response;
    }

    private List<String> encabezados() {
        return List.of(
                "Nombre aerolínea",
                "Cantidad de aviones",
                "Destinos autorizados"
        );
    }

    private List<List<String>> construirFilas(List<ReporteAerolineasAeropuertoResponse> datos) {
        return datos.stream()
                .map(item -> List.of(
                        valor(item.getNombreAerolinea()),
                        numero(item.getCantidadAviones()),
                        numero(item.getDestinosAutorizados())
                ))
                .toList();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private String numero(Long valor) {
        return valor == null ? "0" : String.valueOf(valor);
    }
}
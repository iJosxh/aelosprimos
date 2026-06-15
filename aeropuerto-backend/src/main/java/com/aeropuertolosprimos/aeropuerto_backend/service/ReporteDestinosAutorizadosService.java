package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteDestinosAutorizadosResponse;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaAeropuertoRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteDestinosAutorizadosService {

    private static final String MENSAJE_CAMPOS_OBLIGATORIOS = "Debe ingresar los campos obligatorios";
    private static final String MENSAJE_AEROLINEA_NO_EXISTE = "La aerolínea seleccionada no existe.";
    private static final String MENSAJE_SIN_DESTINOS = "La aerolínea consultada no tiene destinos autorizados";

    private static final String TITULO_REPORTE = "Reporte de destinos autorizados por aerolínea";

    private static final List<String> ENCABEZADOS = List.of(
            "Nombre Aeropuerto",
            "País del aeropuerto",
            "Ciudad del aeropuerto"
    );

    private final AerolineaRepository aerolineaRepository;
    private final AerolineaAeropuertoRepository aerolineaAeropuertoRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReporteDestinosAutorizadosService(
            AerolineaRepository aerolineaRepository,
            AerolineaAeropuertoRepository aerolineaAeropuertoRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.aerolineaRepository = aerolineaRepository;
        this.aerolineaAeropuertoRepository = aerolineaAeropuertoRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public List<ReporteDestinosAutorizadosResponse> buscar(ReporteDestinosAutorizadosFiltroRequest filtros) {
        validarFiltros(filtros);
        validarAerolineaActiva(filtros.getIdAerolinea());

        List<ReporteDestinosAutorizadosResponse> destinos =
                aerolineaAeropuertoRepository.buscarDestinosAutorizadosReporte(filtros.getIdAerolinea());

        if (destinos.isEmpty()) {
            throw new IllegalArgumentException(MENSAJE_SIN_DESTINOS);
        }

        return destinos;
    }

    public byte[] generarPdf(ReporteDestinosAutorizadosFiltroRequest filtros) {
        List<ReporteDestinosAutorizadosResponse> destinos = buscar(filtros);

        return reporteArchivoService.generarPdf(
                TITULO_REPORTE,
                ENCABEZADOS,
                convertirAFilas(destinos)
        );
    }

    public byte[] generarExcel(ReporteDestinosAutorizadosFiltroRequest filtros) {
        List<ReporteDestinosAutorizadosResponse> destinos = buscar(filtros);

        return reporteArchivoService.generarExcel(
                TITULO_REPORTE,
                ENCABEZADOS,
                convertirAFilas(destinos)
        );
    }

    private void validarFiltros(ReporteDestinosAutorizadosFiltroRequest filtros) {
        if (filtros == null || filtros.getIdAerolinea() == null) {
            throw new IllegalArgumentException(MENSAJE_CAMPOS_OBLIGATORIOS);
        }
    }

    private void validarAerolineaActiva(Long idAerolinea) {
        boolean existeActiva = aerolineaRepository.existsByIdAerolineaAndEstado_Codigo(
                idAerolinea,
                "ACT"
        );

        if (!existeActiva) {
            throw new IllegalArgumentException(MENSAJE_AEROLINEA_NO_EXISTE);
        }
    }

    private List<List<String>> convertirAFilas(List<ReporteDestinosAutorizadosResponse> destinos) {
        return destinos.stream()
                .map(destino -> List.of(
                        valor(destino.getNombreAeropuerto()),
                        valor(destino.getPaisAeropuerto()),
                        valor(destino.getCiudadAeropuerto())
                ))
                .toList();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
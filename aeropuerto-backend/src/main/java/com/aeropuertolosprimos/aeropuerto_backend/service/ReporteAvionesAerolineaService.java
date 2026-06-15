package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAvionesAerolineaFiltro;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteAvionesAerolineaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Avion;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.ReporteAvionesAerolineaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteAvionesAerolineaService {

    private static final String TITULO_REPORTE = "Reporte de aviones por aerolínea";

    private final ReporteAvionesAerolineaRepository reporteRepository;
    private final AerolineaRepository aerolineaRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReporteAvionesAerolineaService(
            ReporteAvionesAerolineaRepository reporteRepository,
            AerolineaRepository aerolineaRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.reporteRepository = reporteRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    public List<ReporteAvionesAerolineaResponse> buscar(ReporteAvionesAerolineaFiltro filtro) {
        validarFiltro(filtro);

        Aerolinea aerolinea = aerolineaRepository.findById(filtro.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("La aerolínea seleccionada no existe."));

        List<Avion> aviones = reporteRepository.buscarAvionesActivosPorAerolinea(aerolinea.getIdAerolinea());

        if (aviones.isEmpty()) {
            throw new RuntimeException("La aerolínea consultada no tiene aviones");
        }

        return aviones.stream()
                .map(this::convertirResponse)
                .toList();
    }

    public byte[] generarPdf(ReporteAvionesAerolineaFiltro filtro) {
        List<ReporteAvionesAerolineaResponse> datos = buscar(filtro);

        return reporteArchivoService.generarPdf(
                TITULO_REPORTE,
                encabezados(),
                construirFilas(datos)
        );
    }

    public byte[] generarExcel(ReporteAvionesAerolineaFiltro filtro) {
        List<ReporteAvionesAerolineaResponse> datos = buscar(filtro);

        return reporteArchivoService.generarExcel(
                TITULO_REPORTE,
                encabezados(),
                construirFilas(datos)
        );
    }

    private void validarFiltro(ReporteAvionesAerolineaFiltro filtro) {
        if (filtro == null || filtro.getIdAerolinea() == null) {
            throw new RuntimeException("Debe seleccionar una aerolínea.");
        }
    }

    private ReporteAvionesAerolineaResponse convertirResponse(Avion avion) {
        Long cantidadVuelos = reporteRepository.contarVuelosPorAvion(avion.getIdAvion());

        return new ReporteAvionesAerolineaResponse(
                avion.getIdAvion(),
                avion.getModelo(),
                avion.getMarca(),
                avion.getAnio(),
                avion.getCapacidad(),
                cantidadVuelos
        );
    }

    private List<String> encabezados() {
        return List.of(
                "Modelo del avión",
                "Marca",
                "Año",
                "Cantidad de pasajeros",
                "Cantidad de vuelos"
        );
    }

    private List<List<String>> construirFilas(List<ReporteAvionesAerolineaResponse> datos) {
        return datos.stream()
                .map(avion -> List.of(
                        valor(avion.getModelo()),
                        valor(avion.getMarca()),
                        valor(avion.getAnio()),
                        valor(avion.getCantidadPasajeros()),
                        valor(avion.getCantidadVuelos())
                ))
                .toList();
    }

    private String valor(Object dato) {
        return dato == null ? "" : dato.toString();
    }
}
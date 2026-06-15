package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaFiltroRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.ReporteBoletosReservadosDiaResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.VueloPasajeroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteBoletosReservadosDiaService {

    private static final DateTimeFormatter FORMATO_FECHA_RESERVA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VueloPasajeroRepository vueloPasajeroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteArchivoService reporteArchivoService;

    public ReporteBoletosReservadosDiaService(
            VueloPasajeroRepository vueloPasajeroRepository,
            UsuarioRepository usuarioRepository,
            ReporteArchivoService reporteArchivoService
    ) {
        this.vueloPasajeroRepository = vueloPasajeroRepository;
        this.usuarioRepository = usuarioRepository;
        this.reporteArchivoService = reporteArchivoService;
    }

    @Transactional(readOnly = true)
    public List<ReporteBoletosReservadosDiaResponse> buscar(
            ReporteBoletosReservadosDiaFiltroRequest filtro,
            String username
    ) {
        LocalDate fecha = validarFecha(filtro);
        Long idAerolinea = obtenerIdAerolineaUsuario(username);

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

        List<ReporteBoletosReservadosDiaResponse> datos =
                vueloPasajeroRepository.listarBoletosReservadosPorDia(
                        idAerolinea,
                        inicioDia,
                        finDia
                );

        if (datos.isEmpty()) {
            throw new RuntimeException("No se encontraron boletos reservados para la fecha seleccionada.");
        }

        return datos;
    }

    @Transactional(readOnly = true)
    public byte[] generarPdf(ReporteBoletosReservadosDiaFiltroRequest filtro, String username) {
        List<ReporteBoletosReservadosDiaResponse> datos = buscar(filtro, username);

        return reporteArchivoService.generarPdf(
                "Reporte de boletos reservados por dia",
                encabezados(),
                construirFilas(datos)
        );
    }

    @Transactional(readOnly = true)
    public byte[] generarExcel(ReporteBoletosReservadosDiaFiltroRequest filtro, String username) {
        List<ReporteBoletosReservadosDiaResponse> datos = buscar(filtro, username);

        return reporteArchivoService.generarExcel(
                "Boletos reservados dia",
                encabezados(),
                construirFilas(datos)
        );
    }

    private LocalDate validarFecha(ReporteBoletosReservadosDiaFiltroRequest filtro) {
        if (filtro == null || filtro.getFecha() == null) {
            throw new RuntimeException("Debe ingresar la fecha de consulta.");
        }

        return filtro.getFecha();
    }

    private Long obtenerIdAerolineaUsuario(String username) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("No tiene una aerolÃ­nea asignada.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No tiene una aerolÃ­nea asignada."));

        if (usuario.getAerolinea() == null || usuario.getAerolinea().getIdAerolinea() == null) {
            throw new RuntimeException("No tiene una aerolÃ­nea asignada.");
        }

        return usuario.getAerolinea().getIdAerolinea();
    }

    private List<String> encabezados() {
        return List.of(
                "ID",
                "No. boleto",
                "Monto",
                "Fecha de reserva"
        );
    }

    private List<List<String>> construirFilas(List<ReporteBoletosReservadosDiaResponse> datos) {
        return datos.stream()
                .map(item -> List.of(
                        item.getIdBoleto() == null ? "" : item.getIdBoleto().toString(),
                        valor(item.getNumeroBoleto()),
                        formatearMonto(item.getMonto()),
                        formatearFechaReserva(item.getFechaReserva())
                ))
                .toList();
    }

    private String formatearMonto(BigDecimal monto) {
        if (monto == null) {
            return "0.00";
        }

        return monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatearFechaReserva(LocalDateTime fechaReserva) {
        if (fechaReserva == null) {
            return "";
        }

        return fechaReserva.format(FORMATO_FECHA_RESERVA);
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}

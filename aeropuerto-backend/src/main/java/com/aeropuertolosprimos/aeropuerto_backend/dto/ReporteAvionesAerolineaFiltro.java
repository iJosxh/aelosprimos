package com.aeropuertolosprimos.aeropuerto_backend.dto;

public class ReporteAvionesAerolineaFiltro {

    private Long idAerolinea;

    public ReporteAvionesAerolineaFiltro() {
    }

    public ReporteAvionesAerolineaFiltro(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }
}
package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.util.List;

public class CrearTripulacionRequest {

    private Long idAerolinea;
    private String nombreEquipo;

    private Long idPiloto;
    private Long idCopiloto;
    private Long idIngenieroVuelo;

    private List<Long> idTripulantesCabina;

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Long getIdPiloto() {
        return idPiloto;
    }

    public void setIdPiloto(Long idPiloto) {
        this.idPiloto = idPiloto;
    }

    public Long getIdCopiloto() {
        return idCopiloto;
    }

    public void setIdCopiloto(Long idCopiloto) {
        this.idCopiloto = idCopiloto;
    }

    public Long getIdIngenieroVuelo() {
        return idIngenieroVuelo;
    }

    public void setIdIngenieroVuelo(Long idIngenieroVuelo) {
        this.idIngenieroVuelo = idIngenieroVuelo;
    }

    public List<Long> getIdTripulantesCabina() {
        return idTripulantesCabina;
    }

    public void setIdTripulantesCabina(List<Long> idTripulantesCabina) {
        this.idTripulantesCabina = idTripulantesCabina;
    }
}
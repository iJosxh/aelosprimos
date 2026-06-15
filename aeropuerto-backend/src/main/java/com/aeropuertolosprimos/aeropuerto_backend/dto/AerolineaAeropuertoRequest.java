package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.util.List;

public class AerolineaAeropuertoRequest {

    private Long idAerolinea;
    private List<Long> idsAeropuertos;

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public List<Long> getIdsAeropuertos() {
        return idsAeropuertos;
    }

    public void setIdsAeropuertos(List<Long> idsAeropuertos) {
        this.idsAeropuertos = idsAeropuertos;
    }
}
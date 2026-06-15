package com.aeropuertolosprimos.aeropuerto_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class AbordarPasajeroRequest {

    private Long idVuelo;
    private String noPasaporte;
    private Integer cantidadMaletasPresentadas;
    private List<BigDecimal> pesosMaletas;

    public Long getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(Long idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getNoPasaporte() {
        return noPasaporte;
    }

    public void setNoPasaporte(String noPasaporte) {
        this.noPasaporte = noPasaporte;
    }

    public Integer getCantidadMaletasPresentadas() {
        return cantidadMaletasPresentadas;
    }

    public void setCantidadMaletasPresentadas(Integer cantidadMaletasPresentadas) {
        this.cantidadMaletasPresentadas = cantidadMaletasPresentadas;
    }

    public List<BigDecimal> getPesosMaletas() {
        return pesosMaletas;
    }

    public void setPesosMaletas(List<BigDecimal> pesosMaletas) {
        this.pesosMaletas = pesosMaletas;
    }
}
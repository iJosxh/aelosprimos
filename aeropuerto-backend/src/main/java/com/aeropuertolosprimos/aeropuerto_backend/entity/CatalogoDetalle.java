package com.aeropuertolosprimos.aeropuerto_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "catalogo_detalle")
public class CatalogoDetalle {

    @Id
    @Column(name = "id_catalogo_detalle")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "valor")
    private String valor;

    // ===== GETTERS Y SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}

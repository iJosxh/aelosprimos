package com.aeropuertolosprimos.aeropuerto_backend.dto;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;

public class RegistroDTO {

    private Usuario username;
    private Pasajero pasajero;

    public Usuario getUsername() { return username; }
    public void setUsername(Usuario usuario) { this.username = usuario; }

    public Pasajero getPasajero() { return pasajero; }
    public void setPasajero(Pasajero pasajero) { this.pasajero = pasajero; }
}
package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import com.aeropuertolosprimos.aeropuerto_backend.repository.PasajeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasajeroService {

    @Autowired
    private PasajeroRepository repo;

    public boolean existePasaporte(String noPasaporte) {
        return repo.existsByNoPasaporte(noPasaporte);
    }

    public boolean existeCorreo(String Correo) {
        return repo.existsByCorreo(Correo);
    }
}

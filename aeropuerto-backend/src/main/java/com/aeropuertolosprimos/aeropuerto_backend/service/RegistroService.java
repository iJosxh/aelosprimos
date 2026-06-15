package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.RegistroDTO;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Pasajero;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.PasajeroRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class RegistroService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrarCompleto(RegistroDTO dto) {

        if (pasajeroRepository.existsByNoPasaporte(dto.getPasajero().getNoPasaporte())) {
            throw new IllegalArgumentException("El pasaporte ya existe");
        }

        if (pasajeroRepository.existsByCorreo(dto.getPasajero().getCorreo())) {
            throw new IllegalArgumentException("El correo ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername().getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getUsername().getPassword()));

        CatalogoDetalle estado = new CatalogoDetalle();
        estado.setId(1L);
        usuario.setEstado(estado);

        CatalogoDetalle rol = new CatalogoDetalle();
        rol.setId(11L);
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        usuarioGuardado.setUsuarioCreacion(usuarioGuardado);
        usuarioRepository.save(usuarioGuardado);

        Pasajero pasajero = new Pasajero();
        pasajero.setNoPasaporte(dto.getPasajero().getNoPasaporte());
        pasajero.setNombreCompleto(dto.getPasajero().getNombreCompleto());
        pasajero.setFechaNacimiento(dto.getPasajero().getFechaNacimiento());
        pasajero.setNacionalidad(dto.getPasajero().getNacionalidad());
        pasajero.setCorreo(dto.getPasajero().getCorreo());
        pasajero.setCodigoArea(dto.getPasajero().getCodigoArea());
        pasajero.setTelefono(dto.getPasajero().getTelefono());
        pasajero.setTelefonoEmergencia(dto.getPasajero().getTelefonoEmergencia());
        pasajero.setDireccion(dto.getPasajero().getDireccion());

        // 🔥 RELACIONES
        pasajero.setUsuario(usuarioGuardado);

        // 🔥 AUDITORÍA
        pasajero.setUsuarioCreacion(usuarioGuardado);

        // 🔥 ESTADO
        CatalogoDetalle estadoPasajero = new CatalogoDetalle();
        estadoPasajero.setId(1L);
        pasajero.setEstado(estadoPasajero);

        pasajeroRepository.save(pasajero);
    }
}
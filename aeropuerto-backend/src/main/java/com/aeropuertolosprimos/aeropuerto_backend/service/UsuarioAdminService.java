package com.aeropuertolosprimos.aeropuerto_backend.service;

import com.aeropuertolosprimos.aeropuerto_backend.dto.UsuarioAdminRequest;
import com.aeropuertolosprimos.aeropuerto_backend.dto.UsuarioAdminResponse;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Aerolinea;
import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import com.aeropuertolosprimos.aeropuerto_backend.repository.AerolineaRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.CatalogoDetalleRepository;
import com.aeropuertolosprimos.aeropuerto_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoDetalleRepository catalogoDetalleRepository;
    private final AerolineaRepository aerolineaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminService(
            UsuarioRepository usuarioRepository,
            CatalogoDetalleRepository catalogoDetalleRepository,
            AerolineaRepository aerolineaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoDetalleRepository = catalogoDetalleRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioAdminResponse registrar(UsuarioAdminRequest request) {
        validarRequest(request);

        String usernameLimpio = request.getUsername().trim();

        if (usuarioRepository.existsByUsername(usernameLimpio)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        CatalogoDetalle rol = catalogoDetalleRepository.findById(request.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));

        validarRolPermitido(rol);

        CatalogoDetalle estadoActivo = catalogoDetalleRepository.buscarPorCatalogoYCodigo("ESTADO", "ACT")
                .orElseThrow(() -> new IllegalArgumentException("No existe el estado ACTIVO en catálogo."));

        Aerolinea aerolinea = aerolineaRepository.buscarActivaPorId(request.getIdAerolinea())
                .orElseThrow(() -> new IllegalArgumentException("La aerolínea seleccionada no existe o no está activa."));

        Usuario usuarioActual = obtenerUsuarioActual();

        Usuario usuario = new Usuario();
        usuario.setUsername(usernameLimpio);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado(estadoActivo);
        usuario.setAerolinea(aerolinea);
        usuario.setUsuarioCreacion(usuarioActual);

        Usuario guardado = usuarioRepository.save(usuario);

        return convertirAResponse(guardado);
    }

    public List<UsuarioAdminResponse> listar() {
        return usuarioRepository.listarUsuariosAdministrativos()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<CatalogoDetalle> listarRolesAdministrativos() {
        return catalogoDetalleRepository.listarRolesAdministrativos();
    }

    private void validarRequest(UsuarioAdminRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el nombre de usuario.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar la contraseña.");
        }

        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 6 caracteres.");
        }

        if (!request.getPassword().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una letra mayúscula.");
        }

        if (!request.getPassword().matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un número.");
        }

        if (!request.getPassword().matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un carácter especial.");
        }

        if (request.getIdRol() == null) {
            throw new IllegalArgumentException("Debe seleccionar un rol.");
        }
    }

    private void validarRolPermitido(CatalogoDetalle rol) {
        if (rol.getCodigo() == null) {
            throw new IllegalArgumentException("El rol seleccionado no tiene código configurado.");
        }

        String codigoRol = rol.getCodigo();

        if (!codigoRol.equals("ADMIN_AEROLINEA") && !codigoRol.equals("ADMIN_ABORDAJE")) {
            throw new IllegalArgumentException("Solo se pueden crear usuarios Administrador de Aerolínea o Administrador de Abordaje.");
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();

            return usuarioRepository.findByUsername(username)
                    .orElseGet(() -> usuarioRepository.findById(1L)
                            .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario del sistema.")));
        }

        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario del sistema."));
    }

    private UsuarioAdminResponse convertirAResponse(Usuario usuario) {
        UsuarioAdminResponse response = new UsuarioAdminResponse();

        response.setIdUsuario(usuario.getIdUsuario());
        response.setUsername(usuario.getUsername());

        if (usuario.getRol() != null) {
            response.setIdRol(usuario.getRol().getId());
            response.setCodigoRol(usuario.getRol().getCodigo());
            response.setRol(usuario.getRol().getValor());
        }

        if (usuario.getEstado() != null) {
            response.setIdEstado(usuario.getEstado().getId());
            response.setCodigoEstado(usuario.getEstado().getCodigo());
            response.setEstado(usuario.getEstado().getValor());
        }

        if (usuario.getAerolinea() != null) {
            response.setIdAerolinea(usuario.getAerolinea().getIdAerolinea());
            response.setAerolinea(usuario.getAerolinea().getNombre());
        }

        return response;
    }
}

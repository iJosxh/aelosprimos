package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    // 🔐 Buscar usuario por username (para login)
    @EntityGraph(attributePaths = {"rol", "estado", "aerolinea"})
    Optional<Usuario> findByUsername(String username);

    @Query("""
        SELECT u
        FROM Usuario u
        LEFT JOIN FETCH u.rol
        LEFT JOIN FETCH u.estado
        LEFT JOIN FETCH u.aerolinea
        WHERE u.rol.codigo IN ('ADMIN_AEROLINEA', 'ADMIN_ABORDAJE')
        ORDER BY u.username ASC
    """)
    List<Usuario> listarUsuariosAdministrativos();

}
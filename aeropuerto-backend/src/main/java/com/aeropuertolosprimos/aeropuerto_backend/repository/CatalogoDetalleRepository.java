package com.aeropuertolosprimos.aeropuerto_backend.repository;

import com.aeropuertolosprimos.aeropuerto_backend.entity.CatalogoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogoDetalleRepository extends JpaRepository<CatalogoDetalle, Long> {

    @Query(
            value = """
            SELECT cd.*
            FROM catalogo_detalle cd
            INNER JOIN catalogo c ON c.id_catalogo = cd.id_catalogo
            WHERE c.nombre = :nombreCatalogo
            AND cd.codigo = :codigo
            LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<CatalogoDetalle> buscarPorCatalogoYCodigo(
            @Param("nombreCatalogo") String nombreCatalogo,
            @Param("codigo") String codigo
    );

    @Query(
            value = """
            SELECT cd.*
            FROM catalogo_detalle cd
            INNER JOIN catalogo c ON c.id_catalogo = cd.id_catalogo
            WHERE c.nombre = :nombreCatalogo
            ORDER BY cd.valor ASC
        """,
            nativeQuery = true
    )
    List<CatalogoDetalle> listarPorCatalogo(
            @Param("nombreCatalogo") String nombreCatalogo
    );

    @Query(
            value = """
            SELECT cd.*
            FROM catalogo_detalle cd
            INNER JOIN catalogo c ON c.id_catalogo = cd.id_catalogo
            WHERE c.nombre = 'ROL'
            AND cd.codigo IN ('ADMIN_AEROLINEA', 'ADMIN_ABORDAJE')
            ORDER BY cd.valor ASC
        """,
            nativeQuery = true
    )
    List<CatalogoDetalle> listarRolesAdministrativos();
}
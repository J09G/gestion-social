package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Producto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends MongoRepository<Producto, String> {

        List<Producto> findByActivoTrue(Sort sort);

    List<Producto> findByIdCategoriaAndActivoTrue(String idCategoria, Sort sort);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Sort sort);

        @Query("{ $expr: { $lt: ['$stockActual', '$umbralCritico'] }, activo: true }")
    List<Producto> findProductosCriticos();

    Optional<Producto> findByNombreAndTallaAndActivoTrue(String nombre, String talla);
    Optional<Producto> findByNombreAndTallaIsNullAndActivoTrue(String nombre);
}
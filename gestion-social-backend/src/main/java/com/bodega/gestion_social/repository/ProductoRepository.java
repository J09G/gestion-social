package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByActivoTrue();

    List<Producto> findByIdCategoriaAndActivoTrue(String idCategoria);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    @Query("{ $expr: { $lt: ['$stockActual', '$umbralCritico'] }, activo: true }")
    List<Producto> findProductosCriticos();
}
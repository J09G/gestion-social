package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoriaRepository extends MongoRepository<Categoria, String> {
    Optional<Categoria> findByNombre(String nombre);
}
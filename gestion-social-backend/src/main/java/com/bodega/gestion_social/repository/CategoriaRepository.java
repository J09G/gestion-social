package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoriaRepository extends MongoRepository<Categoria, String> {
    // MongoRepository ya provee: findAll, findById, save, deleteById, etc.
}
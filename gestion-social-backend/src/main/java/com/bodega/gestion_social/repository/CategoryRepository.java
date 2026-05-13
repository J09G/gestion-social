package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    // MongoRepository ya provee: findAll, findById, save, deleteById, etc.
}

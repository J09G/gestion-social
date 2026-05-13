package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(String categoryId);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Compara dos campos del mismo documento: currentStock < criticalThreshold
    @Query("{ $expr: { $lt: ['$currentStock', '$criticalThreshold'] }, active: true }")
    List<Product> findCriticalProducts();
}

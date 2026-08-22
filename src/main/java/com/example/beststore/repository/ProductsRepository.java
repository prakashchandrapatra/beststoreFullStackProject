package com.example.beststore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beststore.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{
	

}

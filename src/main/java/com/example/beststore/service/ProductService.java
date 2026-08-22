package com.example.beststore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beststore.models.Product;
import com.example.beststore.repository.ProductsRepository;

@Service
public class ProductService {
    
	@Autowired
	private ProductsRepository productRepository;
	//Other methods like getAllProducts(), getProduct(), addProduct(), updateProduct()
   public void deleteProduct(int id) {
	   Product product = productRepository.findById(id)
	                    .orElseThrow(() -> new RuntimeException("Product not found"));
	                    
	                    productRepository.delete(product);
   }
}

package com.example.beststore.controllers;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductsRepository;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {" http://localhost:5173/","http://localhost:5174"})
public class ProductRestController {

    @Autowired
    private ProductsRepository repo;

    //------------------------------------
    // GET ALL PRODUCTS
    //------------------------------------
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> products = repo.findAll();

        System.out.println("================================");
        System.out.println("PRODUCT COUNT: " + products.size());
        System.out.println("PRODUCTS: " + products);
        System.out.println("================================");

        return ResponseEntity.ok(products);
    }
//    @GetMapping
//    public ResponseEntity<List<Product>> getAllProducts() {
//        List<Product> products = repo.findAll();
//
//        System.out.println("PRODUCTS FROM DATABASE: " + products);
//
//        return ResponseEntity.ok(products);
//    }
    //------------------------------------
    // GET PRODUCT BY ID
    //------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable int id) {

        Optional<Product> product = repo.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product.get());
    }

    //------------------------------------
    // CREATE PRODUCT
    //------------------------------------
    @PostMapping
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductDto dto){

        try{

            MultipartFile image = dto.getImageFile();

            String fileName = System.currentTimeMillis()+"_"+image.getOriginalFilename();

            String uploadDir="public/productimages/";

            Files.createDirectories(Paths.get(uploadDir));

            try(InputStream input=image.getInputStream()){
                Files.copy(input,
                        Paths.get(uploadDir+fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Product product=new Product();

            product.setName(dto.getName());
            product.setBrand(dto.getBrand());
            product.setCategory(dto.getCategory());
            product.setPrice(dto.getPrice());
            product.setDescription(dto.getDescription());
            product.setImageFileName(fileName);
            product.setCreatedAt(new Date());

            repo.save(product);

            return ResponseEntity.ok(product);

        }catch(Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    //------------------------------------
    // UPDATE PRODUCT
    //------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @ModelAttribute ProductDto dto){

        Optional<Product> optional=repo.findById(id);

        if(optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Product product=optional.get();

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        if(!dto.getImageFile().isEmpty()){

            try{

                MultipartFile image=dto.getImageFile();

                String fileName=System.currentTimeMillis()+"_"+image.getOriginalFilename();

                String uploadDir="public/productimages/";

                Files.createDirectories(Paths.get(uploadDir));

                try(InputStream input=image.getInputStream()){

                    Files.copy(input,
                            Paths.get(uploadDir+fileName),
                            StandardCopyOption.REPLACE_EXISTING);

                }

                product.setImageFileName(fileName);

            }catch(Exception ex){
                return ResponseEntity.badRequest().body(ex.getMessage());
            }

        }

        repo.save(product);

        return ResponseEntity.ok(product);

    }

    //------------------------------------
    // DELETE PRODUCT
    //------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id){

        Optional<Product> optional=repo.findById(id);

        if(optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        repo.delete(optional.get());

        return ResponseEntity.ok("Deleted Successfully");
    }
    
    // GET PRODUCT IMAGE
    @GetMapping("/productimages/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename) throws IOException {

        Path imagePath = Paths.get("public/productimages")
                .resolve(filename)
                .normalize();

        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(imagePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

}
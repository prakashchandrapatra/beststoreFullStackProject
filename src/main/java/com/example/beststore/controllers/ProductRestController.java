package com.example.beststore.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductsRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class ProductRestController {

    @Autowired
    private ProductsRepository repo;

    @Autowired
    private Cloudinary cloudinary;


    // ==========================================
    // GET ALL PRODUCTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> products = repo.findAll();

        System.out.println("================================");
        System.out.println("PRODUCT COUNT: " + products.size());
        System.out.println("PRODUCTS: " + products);
        System.out.println("================================");

        return ResponseEntity.ok(products);
    }


    // ==========================================
    // GET PRODUCT BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable int id) {

        Optional<Product> product = repo.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product.get());
    }


    // ==========================================
    // CREATE PRODUCT
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductDto dto) {

        try {

            // ------------------------------------------
            // 1. Get uploaded image
            // ------------------------------------------

            MultipartFile image = dto.getImageFile();

            if (image == null || image.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please select a product image");
            }


            // ------------------------------------------
            // 2. Upload image to Cloudinary
            // ------------------------------------------

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "beststore/products",
                            "resource_type", "image"
                    )
            );


            // ------------------------------------------
            // 3. Get Cloudinary secure URL
            // ------------------------------------------

            String imageUrl = uploadResult
                    .get("secure_url")
                    .toString();


            System.out.println("================================");
            System.out.println("CLOUDINARY IMAGE UPLOADED");
            System.out.println("IMAGE URL: " + imageUrl);
            System.out.println("================================");


            // ------------------------------------------
            // 4. Create Product
            // ------------------------------------------

            Product product = new Product();

            product.setName(dto.getName());
            product.setBrand(dto.getBrand());
            product.setCategory(dto.getCategory());
            product.setPrice(dto.getPrice());
            product.setDescription(dto.getDescription());
            product.setCreatedAt(new Date());

            // Store Cloudinary URL in existing column
            product.setImageFileName(imageUrl);


            // ------------------------------------------
            // 5. Save product in MySQL
            // ------------------------------------------

            repo.save(product);

            return ResponseEntity.ok(product);

        } catch (Exception ex) {

            ex.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body("Product creation failed: " + ex.getMessage());
        }
    }


    // ==========================================
    // UPDATE PRODUCT
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @ModelAttribute ProductDto dto) {

        Optional<Product> optional = repo.findById(id);

        if (optional.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        Product product = optional.get();


        try {

            // ------------------------------------------
            // Update normal product information
            // ------------------------------------------

            product.setName(dto.getName());
            product.setBrand(dto.getBrand());
            product.setCategory(dto.getCategory());
            product.setPrice(dto.getPrice());
            product.setDescription(dto.getDescription());


            // ------------------------------------------
            // Check if a new image was selected
            // ------------------------------------------

            MultipartFile image = dto.getImageFile();

            if (image != null && !image.isEmpty()) {


                // ------------------------------------------
                // Upload new image to Cloudinary
                // ------------------------------------------

                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "beststore/products",
                                "resource_type", "image"
                        )
                );


                // ------------------------------------------
                // Get new Cloudinary URL
                // ------------------------------------------

                String imageUrl = uploadResult
                        .get("secure_url")
                        .toString();


                // ------------------------------------------
                // Replace image URL in database
                // ------------------------------------------

                product.setImageFileName(imageUrl);


                System.out.println("================================");
                System.out.println("PRODUCT IMAGE UPDATED");
                System.out.println("NEW IMAGE URL: " + imageUrl);
                System.out.println("================================");
            }


            // ------------------------------------------
            // Save updated product
            // ------------------------------------------

            repo.save(product);

            return ResponseEntity.ok(product);

        } catch (Exception ex) {

            ex.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body("Product update failed: " + ex.getMessage());
        }
    }


    // ==========================================
    // DELETE PRODUCT
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable int id) {

        Optional<Product> optional = repo.findById(id);

        if (optional.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        Product product = optional.get();

        /*
         * We delete the database product here.
         *
         * We intentionally do NOT delete the Cloudinary
         * image yet, because existing products may still
         * contain old/local image names.
         */

        repo.delete(product);

        return ResponseEntity.ok("Deleted Successfully");
    }
 // ==========================================
 // MIGRATE EXISTING PRODUCT IMAGES TO CLOUDINARY
 // ==========================================

 @GetMapping("/migrate-images")
 public ResponseEntity<?> migrateImagesToCloudinary() {

     try {

         String imageFolder =
                 "C:\\Users\\Acer\\eclipse-workspace\\Hibernet again\\beststore\\public\\productimages";

         Path folderPath = Paths.get(imageFolder);

         if (!Files.exists(folderPath)) {

             return ResponseEntity
                     .badRequest()
                     .body("Image folder not found: " + imageFolder);
         }

         List<Product> products = repo.findAll();

         int success = 0;
         int skipped = 0;
         int failed = 0;

         StringBuilder result = new StringBuilder();

         for (Product product : products) {

             String oldImageName = product.getImageFileName();

             // ------------------------------------------
             // Skip products already using Cloudinary
             // ------------------------------------------

             if (oldImageName == null ||
                     oldImageName.isBlank() ||
                     oldImageName.startsWith("https://res.cloudinary.com/")) {

                 skipped++;

                 result.append(
                         "SKIPPED: Product ID "
                         + product.getId()
                         + " - "
                         + product.getName()
                         + "\n"
                 );

                 continue;
             }

             // ------------------------------------------
             // Find local image
             // ------------------------------------------

             Path imagePath = folderPath.resolve(oldImageName);

             if (!Files.exists(imagePath)) {

                 failed++;

                 result.append(
                         "FAILED - IMAGE NOT FOUND: Product ID "
                         + product.getId()
                         + " - "
                         + oldImageName
                         + "\n"
                 );

                 continue;
             }

             try {

                 // ------------------------------------------
                 // Upload image to Cloudinary
                 // ------------------------------------------

                 Map<?, ?> uploadResult =
                         cloudinary.uploader().upload(
                                 imagePath.toFile(),
                                 ObjectUtils.asMap(
                                         "folder", "beststore/products",
                                         "resource_type", "image"
                                 )
                         );

                 // ------------------------------------------
                 // Get Cloudinary URL
                 // ------------------------------------------

                 String cloudinaryUrl =
                         uploadResult
                                 .get("secure_url")
                                 .toString();

                 // ------------------------------------------
                 // Update database
                 // ------------------------------------------

                 product.setImageFileName(cloudinaryUrl);

                 repo.save(product);

                 success++;

                 result.append(
                         "SUCCESS: Product ID "
                         + product.getId()
                         + " - "
                         + oldImageName
                         + " -> "
                         + cloudinaryUrl
                         + "\n"
                 );

                 System.out.println(
                         "MIGRATED: "
                         + product.getId()
                         + " -> "
                         + cloudinaryUrl
                 );

             } catch (Exception e) {

                 failed++;

                 result.append(
                         "FAILED: Product ID "
                         + product.getId()
                         + " - "
                         + e.getMessage()
                         + "\n"
                 );

                 e.printStackTrace();
             }
         }

         result.append("\n==============================\n");
         result.append("MIGRATION COMPLETE\n");
         result.append("==============================\n");
         result.append("SUCCESS: " + success + "\n");
         result.append("SKIPPED: " + skipped + "\n");
         result.append("FAILED: " + failed + "\n");

         return ResponseEntity.ok(result.toString());

     } catch (Exception e) {

         e.printStackTrace();

         return ResponseEntity
                 .badRequest()
                 .body("Migration failed: " + e.getMessage());
     }
 }
}
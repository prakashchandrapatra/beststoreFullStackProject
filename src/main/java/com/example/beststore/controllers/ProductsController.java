package com.example.beststore.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.beststore.models.Product;
import com.example.beststore.models.ProductDto;
import com.example.beststore.repository.ProductsRepository;

import jakarta.validation.Valid;

//@Controller
//@RequestMapping("/products")
@RestController
@RequestMapping("/products")
public class ProductsController {

	@Autowired
	private ProductsRepository repo;
	
//	@GetMapping
//	public ResponseEntity<List<Product>> getAllProducts() {
//	    List<Product> products = repo.findAll();
//	    return ResponseEntity.ok(products);
//	}
	
//	@GetMapping({"", "/"})
//	public String showProductList(Model model) {
//		List<Product> products = repo.findAll();
//		model.addAttribute("products", products);
//		return "products/index";
//	}
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}
	
	@PostMapping("/create")
	public String createproduct(
		@Valid @ModelAttribute ProductDto productDto,
	    BindingResult result
	) {
	if ( productDto.getImageFile().isEmpty()) {
		result.addError(new FieldError("productDto" , "imageFile", "please select an image"));
	}
	
	if ( productDto.getName().isEmpty()) {
		result.addError(new FieldError("productDto" , "name", "please enter product name"));
	}
	
	if ( productDto.getBrand().isEmpty()) {
		result.addError(new FieldError("productDto" , "brand", "please enter brand name"));
	}
	
	if(result.hasErrors()) {
		return "products/CreateProduct";
	}
	
	
	//save the image inside the server
	
	MultipartFile productimages = productDto.getImageFile();
	Date createdAt = new Date();
	String storageFileName = createdAt.getTime() + "_" + productimages.getOriginalFilename();
    
	try {
		String uploadDir = "public/productimages";
		Path uploadPath = Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try (InputStream inputStream = productimages.getInputStream()) {
			Path filePath = uploadPath.resolve(storageFileName);
			Files.copy(inputStream, filePath,StandardCopyOption.REPLACE_EXISTING);
		}
	} catch (Exception ex) {
	   System.out.println("Exception:"  + ex.getMessage());;
	}
	
	//save product in database
   Product product = new Product();
   product.setName(productDto.getName());
   product.setBrand(productDto.getBrand());
   product.setCategory(productDto.getCategory());
   product.setPrice(productDto.getPrice());
   product.setDescription(productDto.getDescription());
   product.setCreatedAt(createdAt);
   product.setImageFileName(storageFileName);
	
		repo.save(product);
		
		return "redirect:/products";
	}

	@GetMapping("/edit")
	public String showEditPage(
			Model model,
			@RequestParam int id
			) {
		try {
		 Product product = repo.findById(id).get();
		 model.addAttribute("product", product);
		 
		 ProductDto productDto = new ProductDto();
		 productDto.setName(product.getName());
		 productDto.setBrand(product.getBrand());
		 productDto.setCategory(product.getCategory());
		 productDto.setPrice(product.getPrice());
		 productDto.setDescription(product.getDescription());
		 
		 model.addAttribute("productDto", productDto);
		}
		catch(Exception ex) {
			System.out.println("Exception:" + ex.getMessage());
			return "redirect:/products";
		}
		
		return "products/EditProduct";
	}
	@PostMapping("/edit")
	public String updateProduct(
		Model model,
		@RequestParam int id,
		@Valid @ModelAttribute ProductDto productDto,
		BindingResult result
		) {
		
		try {
			Product product = repo.findById(id).get();
			model.addAttribute("product", product);
			if(result.hasErrors()) {
				return "products/EditProduct";
			}
			
			if(!productDto.getImageFile().isEmpty()) {
				//delete old image
				String uploadDir = "public/productimages/";
				Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
				try {
					Files.delete(oldImagePath);
				} catch (Exception ex) {
					// TODO: handle exception
					System.out.println("Exception:" + ex.getMessage());
				}
				
				//save new image file
				
				MultipartFile image = productDto.getImageFile();
				Date createdAt = new Date();
				String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				product.setImageFileName(storageFileName);
			}
			   product.setName(productDto.getName());
			   product.setBrand(productDto.getBrand());
			   product.setCategory(productDto.getCategory());
			   product.setPrice(productDto.getPrice());
			   product.setDescription(productDto.getDescription());
			   
			   repo.save(product);
			   
		} catch (Exception ex) {
			// TODO: handle exception
			System.out.println("Exception:" + ex.getMessage());
		}
		
		return "redirect:/products";
	}
	
	@GetMapping("/delete")
	public String deleteProduct(
			@RequestParam int id
			) {
		
		try {
			Product product = repo.findById(id).get();
			
			//also we need to delete product image in the folder
			Path imagePath = Paths.get("public/productimages" + product.getImageFileName());
			try {
				Files.delete(imagePath);
			} 
			catch (Exception ex) {
				// TODO Auto-generated catch block
				System.out.println("Exception: " + ex.getMessage());
			}
			
			//delete the product
			repo.delete(product);
		} catch (Exception ex) {
			// TODO: handle exception
			System.out.println("Exception:" + ex.getMessage());
		}
		
		return "redirect:/products";
	}
}

package com.example.beststore.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beststore.dto.LoginRequest;
import com.example.beststore.models.AuthProvider;
import com.example.beststore.models.Role;
import com.example.beststore.models.User;
import com.example.beststore.repository.UserRepository;
import com.example.beststore.service.JwtService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins =  {"http://localhost:5173","http://localhost:5174"}
            )
public class UserController {
   @Autowired
    private final JwtService jwtService;
   @Autowired
    private final PasswordEncoder passwordEncoder;
  @Autowired
	private UserRepository userRepository;

   public UserController(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService) {
	    this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
	//get all users
	@GetMapping
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	//Get user by id
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(
			@PathVariable Long id){
		
				return userRepository.findById(id)
						.map(user -> ResponseEntity.ok(user))
						.orElse(ResponseEntity.notFound().build());
		
	}
	//Register user
	@PostMapping("/register")
	public ResponseEntity<User> registerUser(
			@RequestBody User user){
		if(userRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity
					.badRequest()
					.build();
		}
		user.setProvider(AuthProvider.LOCAL);
		user.setRole(Role.USER);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		User savedUser = userRepository.save(user);
		
		return ResponseEntity.ok(savedUser);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(
	        @RequestBody LoginRequest request) {

	    Optional<User> optionalUser =
	            userRepository.findByEmail(request.getEmail());

	    if (optionalUser.isEmpty()) {

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of(
	                        "message",
	                        "Invalid email or password"
	                ));
	    }

	    User user = optionalUser.get();

	    // Check password
	    if (!passwordEncoder.matches(
	            request.getPassword(),
	            user.getPassword())) {

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of(
	                        "message",
	                        "Invalid email or password"
	                ));
	    }

	    // Generate JWT
	    String token = jwtService.generateToken(user.getEmail());

	    System.out.println("JWT GENERATED: " + token);

	    // Return JWT to React
	    return ResponseEntity.ok(
	            Map.of(
	                    "message", "Login successful",
	                    "token", token,
	                    "name", user.getName(),
	                    "email", user.getEmail()
	            )
	    );
	}
	//Update user
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id ,@RequestBody User userData){
		
		return userRepository.findById(id)
				.map(user -> {
					
					user.setName(userData.getName());
					user.setEmail(userData.getEmail());
					user.setPassword(userData.getPassword());
					user.setProvider(userData.getProvider());
					user.setRole(userData.getRole());
					user.setProvider(AuthProvider.LOCAL);
					
					User updatedUser = userRepository.save(user);
					
					return ResponseEntity.ok(updatedUser);
				})
				.orElse(ResponseEntity.notFound().build());
	}
	
	//Delete user
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id){
		if(!userRepository.existsById(id)) {
			return ResponseEntity
					.notFound()
					.build();
		}
		
		userRepository.deleteById(id);
		
		return ResponseEntity.ok("User deleted Successfully");
	}
	
}

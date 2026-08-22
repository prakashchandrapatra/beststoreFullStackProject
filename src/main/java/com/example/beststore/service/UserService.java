package com.example.beststore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beststore.models.AuthProvider;
import com.example.beststore.models.Role;
import com.example.beststore.models.User;
import com.example.beststore.repository.UserRepository;

@Service
public  class UserService  implements UserDetailsService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // Register a normal LOCAL user
    public User registerUser(User user) {

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // Normal registration
        user.setProvider(AuthProvider.LOCAL);

        // Default role
        user.setRole(Role.USER);

        return userRepository.save(user);
    }


    // Find user by email
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }


    // Find user by ID
    public Optional<User> findById(Long id) {

        return userRepository.findById(id);
    }


    // Get all users
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // Delete user
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(username)
				  .orElseThrow(() ->
				      new UsernameNotFoundException(
				    		  "User not found with email: " + username
				    		  )
						  );
		return org.springframework.security.core.userdetails.User
				  .withUsername(user.getEmail())
				  .password(user.getPassword())
				  .roles(user.getRole().name())
				  .build();
	}
}



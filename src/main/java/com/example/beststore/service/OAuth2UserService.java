package com.example.beststore.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beststore.models.AuthProvider;
import com.example.beststore.models.Role;
import com.example.beststore.models.User;
import com.example.beststore.repository.UserRepository;
@Service
public class OAuth2UserService {
 
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public OAuth2UserService(
           UserRepository userRepository,
           PasswordEncoder passwordEncoder) {
    	
    	this.userRepository = userRepository;
    	this.passwordEncoder = passwordEncoder;
    }
    public User saveOrGetGoogleUser(String email, String name) {

        Optional<User> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Generate a random password because Google users
        // don't use your application's password login
        String randomPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(randomPassword));

        user.setProvider(AuthProvider.GOOGLE);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
    		
    
}

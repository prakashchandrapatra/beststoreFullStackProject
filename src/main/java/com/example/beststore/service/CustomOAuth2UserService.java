package com.example.beststore.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.beststore.models.AuthProvider;
import com.example.beststore.models.Role;
import com.example.beststore.models.User;
import com.example.beststore.repository.UserRepository;

@Service
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService();

    public CustomOAuth2UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // Get user information from Google
        OAuth2User oauthUser = delegate.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Google account email not found");
        }

        Optional<User> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {

            // Create new Google user
            User user = new User();

            user.setName(name);
            user.setEmail(email);

            // Google users don't use normal password login.
            // Generate a random encoded password.
            String randomPassword = UUID.randomUUID().toString();

            user.setPassword(
                    passwordEncoder.encode(randomPassword)
            );

            user.setProvider(AuthProvider.GOOGLE);
            user.setRole(Role.USER);

            userRepository.save(user);

            System.out.println(
                    "New Google user created: " + email
            );

        } else {

            // Existing user
            User user = existingUser.get();

            user.setName(name);
            user.setProvider(AuthProvider.GOOGLE);

            userRepository.save(user);

            System.out.println(
                    "Existing Google user logged in: " + email
            );
        }

        return oauthUser;
    }
}



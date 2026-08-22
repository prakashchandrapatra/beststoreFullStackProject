package com.example.beststore.service;

import java.util.Optional;

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

    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService();

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oauthUser = delegate.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {

            User user = new User();

            user.setName(name);
            user.setEmail(email);
            user.setPassword("GOOGLE_USER");
            user.setProvider(AuthProvider.GOOGLE);
            user.setRole(Role.USER);
            user.setProvider(AuthProvider.GOOGLE);

            userRepository.save(user);

        } else {

            User user = existingUser.get();

            user.setName(name);
            user.setProvider(AuthProvider.GOOGLE);

            userRepository.save(user);
        }

        return oauthUser;
    }
}

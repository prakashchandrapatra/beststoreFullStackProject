package com.example.beststore.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.beststore.service.JwtService;
import com.example.beststore.service.OAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    
    private final OAuth2UserService oauth2UserService;
    
    public OAuth2AuthenticationSuccessHandler(JwtService jwtService ,  OAuth2UserService oauth2UserService) {
		this.jwtService =  jwtService;
		this.oauth2UserService = oauth2UserService;
    	
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
    	OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
    	String email = oauthUser.getAttribute("email");
    	String name = oauthUser.getAttribute("name");
    	// Same JWT generation you already use for normal login
    	oauth2UserService.saveOrGetGoogleUser(email, name);
    	String token = jwtService.generateToken(email);
    	
    	String redirectUrl = "http://localhost:5173/oauth-success"
    			   + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
    			   + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
    			   + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
    	response.sendRedirect(redirectUrl);
    }
}

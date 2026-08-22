package com.example.beststore.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.beststore.service.CustomOAuth2UserService;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtFilter jwtFilter;
    
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService , JwtFilter jwtFilter, OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
		this.jwtFilter = jwtFilter;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/error",
                    "/api/users/**",
                    "/api/auth/**",
                    "/oauth2/**",
                    "/login/**"
                )
                .permitAll()
                //   // Products - temporarily public for testing
                .requestMatchers("/api/products/**")
                .permitAll()
                .requestMatchers("/productimages/**")
                .permitAll()
                .requestMatchers("/api/videos/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(jwtFilter, 
            		UsernamePasswordAuthenticationFilter.class
            		)
// Google OAuth2 Login
//            .oauth2Login(oauth -> oauth
//                .userInfoEndpoint(userInfo -> userInfo
//                    .userService(customOAuth2UserService)
//                )
//                .defaultSuccessUrl(
//                    "http://localhost:5173/oauth-success",
//                    true
//                )
//            );
            .oauth2Login(oauth -> oauth
            	    .userInfoEndpoint(userInfo -> userInfo
            	        .userService(customOAuth2UserService)
            	    )
            	    .successHandler(oAuth2SuccessHandler)
            	);

        return http.build();
    }
    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    	CorsConfiguration configuration = new CorsConfiguration();
    	 // React frontend
    	
    	configuration.setAllowedOrigins(
    			List.of("http://localhost:5173" , "http://localhost:5174")
    			);
        // Allowed HTTP methods
    	configuration.setAllowedMethods(
    	   List.of("GET","POST","PUT","DELETE","OPTIONS")
    			);
    	   // Allow all headers including Authorization
    	configuration.setAllowedHeaders( 
    			List.of("*")
    			);
    	  // Allow credentials
    	configuration.setAllowCredentials(true);
    	
    	UrlBasedCorsConfigurationSource source =  new UrlBasedCorsConfigurationSource();
    	
    	source.registerCorsConfiguration("/**", configuration);
    	
    	return source;
    }
//	@Bean
//    public PasswordEncoder passwordEncoder() {
//    	
//    	return new BCryptPasswordEncoder();
//    }
}
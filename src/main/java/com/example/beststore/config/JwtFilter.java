package com.example.beststore.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.beststore.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	
	public JwtFilter(
			JwtService jwtService,
			UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String username = null;
		String jwt = null;
		
		//Check whether Authorization header exists
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			
			try {
				username = jwtService.extractUsername(jwt);
				 System.out.println("JWT USERNAME: " + username);
			} catch (Exception e) {
				// invalid token
				System.out.println("Invalid jwt token" + e.getMessage());
		}
		//Authenticate user if token is valid
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		
		if(jwtService.isTokenValid(jwt, userDetails)) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
					);
			authentication.setDetails( 
					new WebAuthenticationDetailsSource()
					.buildDetails(request)
					);
			SecurityContextHolder
			.getContext()
			.setAuthentication(authentication);
			System.out.println("JWT AUTHENTICATION SUCCESS");
		}
		}
		
//		filterChain.doFilter(request, response);
	}
		filterChain.doFilter(request, response);
	}
}

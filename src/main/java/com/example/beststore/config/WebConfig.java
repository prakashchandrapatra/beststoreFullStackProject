package com.example.beststore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/productimages/**")
		        .addResourceLocations("file:C:/Users/Acer/eclipse-workspace/Hibernet again/beststore/public/productimages/");
	}
}

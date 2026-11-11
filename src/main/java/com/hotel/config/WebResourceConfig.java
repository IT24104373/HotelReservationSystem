package com.hotel.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "uploads", "hotels")
                .toAbsolutePath()
                .normalize();
        Path legacyUploadDirectory = Paths.get(System.getProperty("user.dir"),
                "src", "main", "resources", "static", "uploads", "hotels")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/hotels/**")
                .addResourceLocations(
                        uploadDirectory.toUri().toString(),
                        legacyUploadDirectory.toUri().toString());
    }
}

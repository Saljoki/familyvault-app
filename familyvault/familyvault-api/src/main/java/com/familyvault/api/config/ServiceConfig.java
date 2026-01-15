package com.familyvault.api.config;

import com.familyvault.core.application.port.out.persistence.FamilyRepository;
import com.familyvault.core.application.port.out.persistence.FileRepository;
import com.familyvault.core.application.port.out.persistence.UserRepository;
import com.familyvault.core.application.port.out.security.PasswordEncoderPort;
import com.familyvault.core.application.port.out.security.TokenPort;
import com.familyvault.core.application.port.out.storage.FileStoragePort;
import com.familyvault.core.application.service.auth.AuthService;
import com.familyvault.core.application.service.file.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for wiring up application services.
 * This keeps the core module free from Spring dependencies.
 */
@Configuration
public class ServiceConfig {

    @Bean
    public AuthService authService(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenPort tokenPort
    ) {
        return new AuthService(userRepository, passwordEncoder, tokenPort);
    }

    @Bean
    public FileService fileService(
            FileRepository fileRepository,
            FamilyRepository familyRepository,
            FileStoragePort fileStorage
    ) {
        return new FileService(fileRepository, familyRepository, fileStorage);
    }
}

package com.familyvault.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.familyvault.api",
        "com.familyvault.infrastructure"
})
@EntityScan(basePackages = "com.familyvault.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.familyvault.infrastructure.persistence.repository")
public class FamilyVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyVaultApplication.class, args);
    }
}

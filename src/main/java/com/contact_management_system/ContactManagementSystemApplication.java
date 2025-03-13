package com.contact_management_system;

import com.contact_management_system.configurations.JwtPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtPropertiesConfig.class)
public class ContactManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagementSystemApplication.class, args);
    }

}

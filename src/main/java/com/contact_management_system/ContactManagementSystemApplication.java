package com.contact_management_system;

import com.contact_management_system.configurations.JWTPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.DIRECT;

@SpringBootApplication
@EnableConfigurationProperties(JWTPropertiesConfig.class)
@EnableSpringDataWebSupport(pageSerializationMode = DIRECT)
public class ContactManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagementSystemApplication.class, args);
    }

}

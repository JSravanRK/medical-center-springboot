package com.medicalcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Medical Center Spring Boot Application
 *
 * Converted from Java Servlet project to Spring Boot.
 * Original: NetBeans + Tomcat + raw JDBC + Servlets
 * Converted: Spring Boot + Spring MVC + JdbcTemplate + JSP
 */
@SpringBootApplication
public class MedicalCenterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MedicalCenterApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MedicalCenterApplication.class, args);
    }
}

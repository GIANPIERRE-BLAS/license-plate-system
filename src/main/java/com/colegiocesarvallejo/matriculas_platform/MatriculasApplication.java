package com.colegiocesarvallejo.matriculas_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MatriculasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatriculasApplication.class, args);

        System.out.println("========================================");
        System.out.println("🎓 Plataforma de Matrículas Iniciada  🎓");
        System.out.println("🏫 Colegio César Vallejo              🏫");
        System.out.println("=-=======================================");
        System.out.println("🌐 Frontend: http://localhost:8080/login.html");
        System.out.println("📡 API: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
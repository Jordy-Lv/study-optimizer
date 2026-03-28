package com.jordy.studyoptimizer;

import com.jordy.studyoptimizer.github.GitHubProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// @EnableConfigurationProperties activa el binding de los records anotados con
// @ConfigurationProperties (aqui, la config del modulo github) para inyectarlos.
@SpringBootApplication
@EnableConfigurationProperties(GitHubProperties.class)
public class StudyOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyOptimizerApplication.class, args);
    }
}

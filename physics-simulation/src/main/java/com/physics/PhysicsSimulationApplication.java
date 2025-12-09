package com.physics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Physics Simulation Engine.
 * This Spring Boot application provides a REST API backend for 2D physics simulation.
 */
@SpringBootApplication
public class PhysicsSimulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhysicsSimulationApplication.class, args);
    }
}

package com.physics.config;

import com.physics.engine.PhysicsWorld;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * Defines Spring beans for the physics simulation.
 */
@Configuration
public class AppConfig {

    /**
     * Create a singleton PhysicsWorld bean.
     * This ensures all services share the same physics world instance.
     */
    @Bean
    public PhysicsWorld physicsWorld() {
        return new PhysicsWorld();
    }
}

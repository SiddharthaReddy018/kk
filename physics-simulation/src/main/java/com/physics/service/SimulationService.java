package com.physics.service;

import com.physics.engine.PhysicsWorld;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for simulation control operations.
 * Handles starting, pausing, resetting, and stepping the simulation.
 */
@Service
public class SimulationService {
    
    private final PhysicsWorld physicsWorld;
    
    // Default time step (1/60th of a second for 60fps)
    private static final double DEFAULT_DT = 1.0 / 60.0;

    public SimulationService(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Start the simulation.
     */
    public void start() {
        physicsWorld.start();
    }

    /**
     * Pause the simulation.
     */
    public void pause() {
        physicsWorld.pause();
    }

    /**
     * Reset the simulation to initial state.
     */
    public void reset() {
        physicsWorld.reset();
    }

    /**
     * Step the simulation forward by a time delta.
     * @param dt Time delta in seconds (null uses default)
     */
    public void step(Double dt) {
        double deltaTime = (dt != null && dt > 0) ? dt : DEFAULT_DT;
        physicsWorld.step(deltaTime);
    }

    /**
     * Get current simulation state.
     * @return Map containing bodies, positions, velocities, and collisions
     */
    public Map<String, Object> getState() {
        return physicsWorld.getState();
    }

    /**
     * Check if simulation is running.
     * @return true if running
     */
    public boolean isRunning() {
        return physicsWorld.isRunning();
    }
}

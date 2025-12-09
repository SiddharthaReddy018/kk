package com.physics.service;

import com.physics.dto.ApplyForceRequest;
import com.physics.dto.ApplyImpulseRequest;
import com.physics.dto.GravityRequest;
import com.physics.engine.Forces;
import com.physics.engine.PhysicsWorld;
import com.physics.model.PhysicsBody;
import com.physics.model.Vector2D;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling force-related operations.
 * Manages force application, impulses, and gravity settings.
 */
@Service
public class ForceService {
    
    private final PhysicsWorld physicsWorld;

    public ForceService(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Apply a force to a physics body.
     * @param request The apply force request
     * @return true if force was applied, false if body not found
     */
    public boolean applyForce(ApplyForceRequest request) {
        if (request.getId() == null || request.getForce() == null || request.getForce().length < 2) {
            return false;
        }

        Optional<PhysicsBody> optBody = physicsWorld.getObject(request.getId());
        if (optBody.isEmpty()) {
            return false;
        }

        Vector2D force = new Vector2D(request.getForce()[0], request.getForce()[1]);
        
        // Set as a persistent custom force
        physicsWorld.setCustomForce(request.getId(), force);
        
        return true;
    }

    /**
     * Apply an impulse to a physics body (instantaneous velocity change).
     * @param request The apply impulse request
     * @return true if impulse was applied, false if body not found
     */
    public boolean applyImpulse(ApplyImpulseRequest request) {
        if (request.getId() == null || request.getImpulse() == null || request.getImpulse().length < 2) {
            return false;
        }

        Optional<PhysicsBody> optBody = physicsWorld.getObject(request.getId());
        if (optBody.isEmpty()) {
            return false;
        }

        Vector2D impulse = new Vector2D(request.getImpulse()[0], request.getImpulse()[1]);
        Forces.applyImpulse(optBody.get(), impulse);
        
        return true;
    }

    /**
     * Change global gravity.
     * @param request The gravity request
     */
    public void setGravity(GravityRequest request) {
        if (request.getGravity() != null && request.getGravity().length >= 2) {
            physicsWorld.setGravity(request.getGravity()[0], request.getGravity()[1]);
        }
    }

    /**
     * Get current gravity setting.
     * @return Current gravity vector as array
     */
    public double[] getGravity() {
        return physicsWorld.getGravity().toArray();
    }

    /**
     * Clear custom force from a body.
     * @param id The body ID
     * @return true if cleared, false if body not found
     */
    public boolean clearForce(int id) {
        Optional<PhysicsBody> optBody = physicsWorld.getObject(id);
        if (optBody.isEmpty()) {
            return false;
        }
        
        physicsWorld.clearCustomForce(id);
        return true;
    }
}

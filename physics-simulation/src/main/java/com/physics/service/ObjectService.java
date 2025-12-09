package com.physics.service;

import com.physics.dto.CreateObjectRequest;
import com.physics.dto.UpdateObjectRequest;
import com.physics.engine.PhysicsWorld;
import com.physics.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing physics objects.
 * Handles CRUD operations for physics bodies in the simulation.
 */
@Service
public class ObjectService {
    
    private final PhysicsWorld physicsWorld;

    public ObjectService(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Create a new physics object from request.
     * @param request The create object request
     * @return The created physics body
     */
    public PhysicsBody createObject(CreateObjectRequest request) {
        String type = request.getType().toLowerCase();
        double mass = request.getMass() != null ? request.getMass() : 1.0;
        
        double[] pos = request.getPosition();
        Vector2D position = (pos != null && pos.length >= 2) 
            ? new Vector2D(pos[0], pos[1]) 
            : new Vector2D(0, 0);
        
        double[] vel = request.getVelocity();
        Vector2D velocity = (vel != null && vel.length >= 2) 
            ? new Vector2D(vel[0], vel[1]) 
            : new Vector2D(0, 0);

        PhysicsBody body;
        int id = physicsWorld.getNextId();

        switch (type) {
            case "circle":
                double radius = request.getRadius() != null ? request.getRadius() : 1.0;
                body = new CircleBody(id, mass, position, velocity, radius);
                break;
            case "rectangle":
                double width = request.getWidth() != null ? request.getWidth() : 1.0;
                double height = request.getHeight() != null ? request.getHeight() : 1.0;
                body = new RectangleBody(id, mass, position, velocity, width, height);
                break;
            case "square":
                double sideLength = request.getSideLength() != null ? request.getSideLength() : 1.0;
                body = new SquareBody(id, mass, position, velocity, sideLength);
                break;
            default:
                throw new IllegalArgumentException("Unknown object type: " + type);
        }

        physicsWorld.addObject(body);
        return body;
    }

    /**
     * Get all physics objects.
     * @return List of all physics bodies
     */
    public List<PhysicsBody> getAllObjects() {
        return physicsWorld.getObjects();
    }

    /**
     * Get a physics object by ID.
     * @param id The object ID
     * @return Optional containing the body if found
     */
    public Optional<PhysicsBody> getObject(int id) {
        return physicsWorld.getObject(id);
    }

    /**
     * Update a physics object.
     * @param id The object ID to update
     * @param request The update request
     * @return The updated physics body, or empty if not found
     */
    public Optional<PhysicsBody> updateObject(int id, UpdateObjectRequest request) {
        Optional<PhysicsBody> optBody = physicsWorld.getObject(id);
        
        if (optBody.isEmpty()) {
            return Optional.empty();
        }

        PhysicsBody body = optBody.get();

        // Update common properties
        if (request.getMass() != null) {
            body.setMass(request.getMass());
        }
        
        if (request.getPosition() != null && request.getPosition().length >= 2) {
            body.setPosition(new Vector2D(request.getPosition()[0], request.getPosition()[1]));
        }
        
        if (request.getVelocity() != null && request.getVelocity().length >= 2) {
            body.setVelocity(new Vector2D(request.getVelocity()[0], request.getVelocity()[1]));
        }

        // Update shape-specific properties
        if (body instanceof CircleBody && request.getRadius() != null) {
            ((CircleBody) body).setRadius(request.getRadius());
        }
        
        if (body instanceof RectangleBody) {
            RectangleBody rect = (RectangleBody) body;
            if (request.getWidth() != null) {
                rect.setWidth(request.getWidth());
            }
            if (request.getHeight() != null) {
                rect.setHeight(request.getHeight());
            }
        }
        
        if (body instanceof SquareBody && request.getSideLength() != null) {
            ((SquareBody) body).setSideLength(request.getSideLength());
        }

        return Optional.of(body);
    }

    /**
     * Delete a physics object.
     * @param id The object ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteObject(int id) {
        return physicsWorld.removeObject(id);
    }
}

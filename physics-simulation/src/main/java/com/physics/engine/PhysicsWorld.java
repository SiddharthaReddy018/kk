package com.physics.engine;

import com.physics.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Physics World class - the main simulation engine.
 * Converted from C++ PhysicsWorld class in physics_world.h/cpp
 * 
 * This class manages all physics bodies, applies forces, handles collisions,
 * and advances the simulation.
 */
public class PhysicsWorld {
    
    // List of objects in the physics world
    private final List<PhysicsBody> bodies;
    
    // Mapping from object IDs to custom forces
    private final Map<Integer, Vector2D> customForces;
    
    // Gravity vector applied to all objects (default: downward)
    private Vector2D gravity;
    
    // Simulation state
    private boolean running;
    
    // Collision pairs detected in last step
    private final List<int[]> collisionPairs;
    
    // Static friction coefficient
    private double staticFrictionCoefficient = 0.5;
    
    // Kinetic friction coefficient
    private double kineticFrictionCoefficient = 0.3;
    
    // Next available ID for new objects
    private int nextId = 1;

    /**
     * Default constructor - creates physics world with default gravity.
     */
    public PhysicsWorld() {
        this.bodies = new CopyOnWriteArrayList<>();
        this.customForces = new ConcurrentHashMap<>();
        this.gravity = new Vector2D(0, 9.81);  // Default gravity (downward in screen coordinates)
        this.running = false;
        this.collisionPairs = new ArrayList<>();
    }

    /**
     * Add an object to the physics world.
     * Converted from C++ PhysicsWorld::addObject method.
     */
    public void addObject(PhysicsBody body) {
        if (body.getId() == 0) {
            body.setId(nextId++);
        } else {
            nextId = Math.max(nextId, body.getId() + 1);
        }
        bodies.add(body);
    }

    /**
     * Remove an object from the physics world by ID.
     */
    public boolean removeObject(int id) {
        customForces.remove(id);
        return bodies.removeIf(body -> body.getId() == id);
    }

    /**
     * Get an object by ID.
     */
    public Optional<PhysicsBody> getObject(int id) {
        return bodies.stream()
            .filter(body -> body.getId() == id)
            .findFirst();
    }

    /**
     * Get all objects in the physics world.
     * Converted from C++ PhysicsWorld::getObjects method.
     */
    public List<PhysicsBody> getObjects() {
        return new ArrayList<>(bodies);
    }

    /**
     * Set a custom force for a specific object by its ID.
     * Converted from C++ PhysicsWorld::setCustomForce method.
     */
    public void setCustomForce(int objectId, Vector2D force) {
        customForces.put(objectId, force);
    }

    /**
     * Get the custom force for a specific object (if it exists).
     * Converted from C++ PhysicsWorld::getCustomForceForObject method.
     */
    public Vector2D getCustomForceForObject(int objectId) {
        return customForces.getOrDefault(objectId, new Vector2D(0, 0));
    }

    /**
     * Clear custom force for an object.
     */
    public void clearCustomForce(int objectId) {
        customForces.remove(objectId);
    }

    /**
     * Apply gravity and custom forces to all objects.
     * Converted from C++ PhysicsWorld::applyForces method.
     */
    public void applyForces() {
        for (PhysicsBody body : bodies) {
            // Apply gravity to each object (if it has mass)
            if (body.getMass() > 0) {
                Vector2D gravityForce = gravity.multiply(body.getMass());
                body.applyForce(gravityForce);
            }

            // Apply custom user-defined forces
            int objectId = body.getId();
            Vector2D customForce = getCustomForceForObject(objectId);

            // If there is a custom force, apply it
            if (customForce.length() > 0) {
                Forces.applyCustomForce(body, customForce);
            }
        }
    }

    /**
     * Update the world by one step, advancing the simulation.
     * Converted from C++ PhysicsWorld::step method.
     * 
     * @param deltaTime Time step in seconds
     */
    public void step(double deltaTime) {
        // Reset forces from previous step
        for (PhysicsBody body : bodies) {
            body.resetForces();
        }

        // Apply forces to all objects
        applyForces();

        // Update object positions based on their velocity and deltaTime
        for (PhysicsBody body : bodies) {
            body.update(deltaTime);
        }

        // Handle collision detection and resolution
        handleCollisions();
    }

    /**
     * Collision detection and resolution.
     * Converted from C++ PhysicsWorld::handleCollisions method.
     */
    public void handleCollisions() {
        collisionPairs.clear();
        
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                PhysicsBody bodyA = bodies.get(i);
                PhysicsBody bodyB = bodies.get(j);

                if (detectCollision(bodyA, bodyB)) {
                    resolveCollision(bodyA, bodyB);
                    collisionPairs.add(new int[]{bodyA.getId(), bodyB.getId()});
                }
            }
        }
    }

    /**
     * Detects collision between two objects using their colliders.
     * Converted from C++ PhysicsWorld::detectCollision method.
     */
    public boolean detectCollision(PhysicsBody bodyA, PhysicsBody bodyB) {
        Collider colliderA = bodyA.getCollider();
        Collider colliderB = bodyB.getCollider();

        if (colliderA != null && colliderB != null) {
            return colliderA.checkCollision(colliderB);
        }

        return false;
    }

    /**
     * Resolves the collision between two objects.
     * Converted from C++ PhysicsWorld::resolveCollision method.
     */
    public void resolveCollision(PhysicsBody bodyA, PhysicsBody bodyB) {
        Collider colliderA = bodyA.getCollider();
        Collider colliderB = bodyB.getCollider();

        if (colliderA != null && colliderB != null) {
            colliderA.resolveCollision(bodyA, bodyB);
        }
    }

    /**
     * Get collision pairs from last step.
     */
    public List<int[]> getCollisionPairs() {
        return new ArrayList<>(collisionPairs);
    }

    // Getters and Setters for simulation properties
    
    public Vector2D getGravity() {
        return gravity;
    }

    public void setGravity(Vector2D gravity) {
        this.gravity = new Vector2D(gravity);
    }

    public void setGravity(double x, double y) {
        this.gravity = new Vector2D(x, y);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {
        this.running = true;
    }

    public void pause() {
        this.running = false;
    }

    public double getStaticFrictionCoefficient() {
        return staticFrictionCoefficient;
    }

    public void setStaticFrictionCoefficient(double coefficient) {
        this.staticFrictionCoefficient = coefficient;
    }

    public double getKineticFrictionCoefficient() {
        return kineticFrictionCoefficient;
    }

    public void setKineticFrictionCoefficient(double coefficient) {
        this.kineticFrictionCoefficient = coefficient;
    }

    /**
     * Reset the simulation to initial state.
     */
    public void reset() {
        bodies.clear();
        customForces.clear();
        collisionPairs.clear();
        running = false;
        nextId = 1;
        gravity = new Vector2D(0, 9.81);
    }

    /**
     * Get simulation state as a map (for API response).
     */
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        
        List<Map<String, Object>> bodiesState = new ArrayList<>();
        for (PhysicsBody body : bodies) {
            Map<String, Object> bodyState = new HashMap<>();
            bodyState.put("id", body.getId());
            bodyState.put("position", body.getPosition().toArray());
            bodyState.put("velocity", body.getVelocity().toArray());
            bodyState.put("type", body.getType());
            bodiesState.add(bodyState);
        }
        
        state.put("bodies", bodiesState);
        state.put("running", running);
        state.put("gravity", gravity.toArray());
        state.put("collisions", collisionPairs);
        
        return state;
    }

    /**
     * Get next available ID.
     */
    public int getNextId() {
        return nextId;
    }
}

package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base Physics Body class representing an object in the physics simulation.
 * Converted from C++ Object class in object.h/cpp
 * 
 * This class provides common properties and methods for all physics objects
 * including position, velocity, acceleration, mass, and force application.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CircleBody.class, name = "circle"),
    @JsonSubTypes.Type(value = RectangleBody.class, name = "rectangle"),
    @JsonSubTypes.Type(value = SquareBody.class, name = "square")
})
public abstract class PhysicsBody {
    
    @JsonProperty("id")
    protected int id;
    
    @JsonProperty("mass")
    protected double mass;
    
    @JsonProperty("position")
    protected Vector2D position;
    
    @JsonProperty("velocity")
    protected Vector2D velocity;
    
    @JsonIgnore
    protected Vector2D acceleration;
    
    @JsonIgnore
    protected Vector2D netForce;
    
    @JsonIgnore
    protected Collider collider;

    // Default constructor for JSON deserialization
    public PhysicsBody() {
        this.id = 0;
        this.mass = 1.0;
        this.position = new Vector2D();
        this.velocity = new Vector2D();
        this.acceleration = new Vector2D();
        this.netForce = new Vector2D();
    }

    /**
     * Constructor with ID, mass, position, and velocity.
     * Converted from C++ Object constructor.
     */
    public PhysicsBody(int id, double mass, Vector2D position, Vector2D velocity) {
        this.id = id;
        this.mass = mass;
        this.position = new Vector2D(position);
        this.velocity = new Vector2D(velocity);
        this.acceleration = new Vector2D();
        this.netForce = new Vector2D();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = new Vector2D(position);
        // Update collider position when body position changes
        if (collider != null) {
            collider.updatePosition(this.position);
        }
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = new Vector2D(velocity);
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    @JsonIgnore
    public Collider getCollider() {
        return collider;
    }

    /**
     * Apply force to the object.
     * Converted from C++ Object::applyForce method.
     */
    public void applyForce(Vector2D force) {
        netForce = netForce.add(force);
    }

    /**
     * Update object's state based on forces and delta time.
     * Converted from C++ Object::update method.
     */
    public void update(double deltaTime) {
        acceleration = calculateAcceleration();
        velocity = velocity.add(acceleration.multiply(deltaTime));
        updatePosition(deltaTime);
    }

    /**
     * Reset accumulated forces.
     * Converted from C++ Object::resetForces method.
     */
    public void resetForces() {
        netForce = new Vector2D(0, 0);
    }

    /**
     * Update position based on velocity and deltaTime.
     * Converted from C++ Object::updatePosition method.
     */
    public void updatePosition(double deltaTime) {
        position = position.add(velocity.multiply(deltaTime));
        // Update collider position
        if (collider != null) {
            collider.updatePosition(position);
        }
    }

    /**
     * Calculate acceleration from net force and mass.
     * Converted from C++ Object::calculateAcceleration method.
     */
    protected Vector2D calculateAcceleration() {
        if (mass > 0) {
            return netForce.divide(mass);
        }
        return new Vector2D(0, 0);
    }

    /**
     * Calculate area of the object (implemented by subclasses).
     * Converted from C++ Object::calculateArea pure virtual method.
     */
    @JsonIgnore
    public abstract double calculateArea();

    /**
     * Get the type of the physics body.
     */
    @JsonIgnore
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("PhysicsBody[id=%d, mass=%.2f, pos=%s, vel=%s]", 
            id, mass, position, velocity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhysicsBody other = (PhysicsBody) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Circle physics body.
 * Converted from C++ Circle class in object.h/cpp
 */
public class CircleBody extends PhysicsBody {
    
    @JsonProperty("radius")
    private double radius;

    // Default constructor for JSON deserialization
    public CircleBody() {
        super();
        this.radius = 1.0;
        initCollider();
    }

    /**
     * Constructor for Circle body.
     * Converted from C++ Circle constructor.
     */
    public CircleBody(int id, double mass, Vector2D position, Vector2D velocity, double radius) {
        super(id, mass, position, velocity);
        this.radius = radius;
        initCollider();
    }

    private void initCollider() {
        this.collider = new CircleCollider(position, radius);
    }

    // Getters and Setters
    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        // Update collider when radius changes
        if (collider instanceof CircleCollider) {
            ((CircleCollider) collider).setRadius(radius);
        }
    }

    @Override
    @JsonIgnore
    public double calculateArea() {
        return Math.PI * radius * radius;
    }

    @Override
    @JsonIgnore
    public String getType() {
        return "circle";
    }

    @Override
    public void setPosition(Vector2D position) {
        super.setPosition(position);
        if (collider instanceof CircleCollider) {
            ((CircleCollider) collider).setCenter(this.position);
        }
    }

    @Override
    public String toString() {
        return String.format("CircleBody[id=%d, mass=%.2f, pos=%s, vel=%s, radius=%.2f]",
            id, mass, position, velocity, radius);
    }
}

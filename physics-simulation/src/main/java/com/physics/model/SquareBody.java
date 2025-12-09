package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Square physics body.
 * Converted from C++ Square class in object.h/cpp
 */
public class SquareBody extends PhysicsBody {
    
    @JsonProperty("sideLength")
    private double sideLength;

    // Default constructor for JSON deserialization
    public SquareBody() {
        super();
        this.sideLength = 1.0;
        initCollider();
    }

    /**
     * Constructor for Square body.
     * Converted from C++ Square constructor.
     */
    public SquareBody(int id, double mass, Vector2D position, Vector2D velocity, double sideLength) {
        super(id, mass, position, velocity);
        this.sideLength = sideLength;
        initCollider();
    }

    private void initCollider() {
        this.collider = new AABBCollider(position, sideLength, sideLength);
    }

    // Getters and Setters
    public double getSideLength() {
        return sideLength;
    }

    /**
     * Set side length of the square.
     * Converted from C++ Square::setSideLength method.
     */
    public void setSideLength(double sideLength) {
        this.sideLength = sideLength;
        // Update collider when side length changes
        if (collider instanceof AABBCollider) {
            AABBCollider aabb = (AABBCollider) collider;
            aabb.setWidth(sideLength);
            aabb.setHeight(sideLength);
        }
    }

    @Override
    @JsonIgnore
    public double calculateArea() {
        return sideLength * sideLength;
    }

    @Override
    @JsonIgnore
    public String getType() {
        return "square";
    }

    @Override
    public String toString() {
        return String.format("SquareBody[id=%d, mass=%.2f, pos=%s, vel=%s, sideLength=%.2f]",
            id, mass, position, velocity, sideLength);
    }
}

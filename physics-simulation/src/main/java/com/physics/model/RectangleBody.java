package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rectangle physics body.
 * Converted from C++ Rectangle class in object.h/cpp
 */
public class RectangleBody extends PhysicsBody {
    
    @JsonProperty("width")
    private double width;
    
    @JsonProperty("height")
    private double height;

    // Default constructor for JSON deserialization
    public RectangleBody() {
        super();
        this.width = 1.0;
        this.height = 1.0;
        initCollider();
    }

    /**
     * Constructor for Rectangle body.
     * Converted from C++ Rectangle constructor.
     */
    public RectangleBody(int id, double mass, Vector2D position, Vector2D velocity, double width, double height) {
        super(id, mass, position, velocity);
        this.width = width;
        this.height = height;
        initCollider();
    }

    private void initCollider() {
        this.collider = new AABBCollider(position, width, height);
    }

    // Getters and Setters
    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Set dimensions of the rectangle.
     * Converted from C++ Rectangle::setDimensions method.
     */
    public void setDimensions(double width, double height) {
        this.width = width;
        this.height = height;
        // Update collider when dimensions change
        if (collider instanceof AABBCollider) {
            AABBCollider aabb = (AABBCollider) collider;
            aabb.setWidth(width);
            aabb.setHeight(height);
        }
    }

    public void setWidth(double width) {
        this.width = width;
        if (collider instanceof AABBCollider) {
            ((AABBCollider) collider).setWidth(width);
        }
    }

    public void setHeight(double height) {
        this.height = height;
        if (collider instanceof AABBCollider) {
            ((AABBCollider) collider).setHeight(height);
        }
    }

    @Override
    @JsonIgnore
    public double calculateArea() {
        return width * height;
    }

    @Override
    @JsonIgnore
    public String getType() {
        return "rectangle";
    }

    @Override
    public String toString() {
        return String.format("RectangleBody[id=%d, mass=%.2f, pos=%s, vel=%s, width=%.2f, height=%.2f]",
            id, mass, position, velocity, width, height);
    }
}

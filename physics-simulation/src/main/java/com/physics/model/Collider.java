package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base Collider class for collision detection.
 * Converted from C++ Collider class in collider.h
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "colliderType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AABBCollider.class, name = "aabb"),
    @JsonSubTypes.Type(value = CircleCollider.class, name = "circle")
})
public abstract class Collider {
    
    /**
     * Check if this collider overlaps with another collider.
     * @param other The other collider to check against
     * @return true if collision detected
     */
    public abstract boolean checkCollision(Collider other);
    
    /**
     * Resolve collision between two physics bodies.
     * @param bodyA First physics body
     * @param bodyB Second physics body
     */
    public abstract void resolveCollision(PhysicsBody bodyA, PhysicsBody bodyB);
    
    /**
     * Update collider position based on body position.
     * @param position New position
     */
    public abstract void updatePosition(Vector2D position);
    
    /**
     * Get the collider type as string.
     * @return collider type
     */
    @JsonIgnore
    public abstract String getColliderType();
}

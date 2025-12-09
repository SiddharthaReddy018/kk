package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Axis-Aligned Bounding Box Collider for rectangular objects.
 * Converted from C++ AABBCollider class in collider.h/cpp
 */
public class AABBCollider extends Collider {
    
    @JsonProperty("position")
    private Vector2D position;  // Top-left corner of the rectangle
    
    @JsonProperty("width")
    private double width;
    
    @JsonProperty("height")
    private double height;

    public AABBCollider() {
        this.position = new Vector2D();
        this.width = 0;
        this.height = 0;
    }

    public AABBCollider(Vector2D position, double width, double height) {
        this.position = new Vector2D(position);
        this.width = width;
        this.height = height;
    }

    // Getters and Setters
    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public void updatePosition(Vector2D position) {
        this.position = new Vector2D(position);
    }

    @Override
    @JsonIgnore
    public String getColliderType() {
        return "aabb";
    }

    @Override
    public boolean checkCollision(Collider other) {
        if (other instanceof AABBCollider) {
            return checkAABBCollision((AABBCollider) other);
        } else if (other instanceof CircleCollider) {
            return checkAABBCircleCollision(this, (CircleCollider) other);
        }
        return false;
    }

    /**
     * AABB vs AABB collision detection
     * Converted from C++ checkAABBCollision function
     */
    private boolean checkAABBCollision(AABBCollider other) {
        boolean xOverlap = position.x < other.position.x + other.width && 
                          position.x + width > other.position.x;
        boolean yOverlap = position.y < other.position.y + other.height && 
                          position.y + height > other.position.y;
        return xOverlap && yOverlap;
    }

    /**
     * AABB vs Circle collision detection
     * Converted from C++ checkAABBCircleCollision function
     */
    public static boolean checkAABBCircleCollision(AABBCollider aabb, CircleCollider circle) {
        double closestX = Math.max(aabb.position.x, Math.min(circle.getCenter().x, aabb.position.x + aabb.width));
        double closestY = Math.max(aabb.position.y, Math.min(circle.getCenter().y, aabb.position.y + aabb.height));

        Vector2D closestPoint = new Vector2D(closestX, closestY);
        double distance = closestPoint.distance(circle.getCenter());
        return distance < circle.getRadius();
    }

    @Override
    public void resolveCollision(PhysicsBody bodyA, PhysicsBody bodyB) {
        Vector2D posA = bodyA.getPosition();
        Vector2D posB = bodyB.getPosition();
        Vector2D velA = bodyA.getVelocity();
        Vector2D velB = bodyB.getVelocity();
        double massA = bodyA.getMass();
        double massB = bodyB.getMass();

        // Calculate collision normal (from A to B)
        Vector2D normal;

        // Find the overlap direction
        double overlapX = (posA.x + width) - posB.x;
        if (Math.abs(overlapX) > Math.abs(posA.x - (posB.x + width))) {
            overlapX = posA.x - (posB.x + width);
        }

        double overlapY = (posA.y + height) - posB.y;
        if (Math.abs(overlapY) > Math.abs(posA.y - (posB.y + height))) {
            overlapY = posA.y - (posB.y + height);
        }

        // Use the smallest overlap to determine collision normal
        if (Math.abs(overlapX) < Math.abs(overlapY)) {
            normal = new Vector2D(overlapX > 0 ? 1.0 : -1.0, 0.0);
        } else {
            normal = new Vector2D(0.0, overlapY > 0 ? 1.0 : -1.0);
        }

        // Calculate relative velocity
        Vector2D relativeVel = velB.subtract(velA);
        double velocityAlongNormal = relativeVel.dot(normal);

        // Don't resolve if objects are moving apart
        if (velocityAlongNormal > 0) {
            return;
        }

        // Coefficient of restitution (elasticity)
        double e = 0.8;

        // Calculate impulse scalar (with division by zero protection)
        double invMassSum = 0.0;
        if (massA > 0) invMassSum += 1.0 / massA;
        if (massB > 0) invMassSum += 1.0 / massB;
        
        if (invMassSum == 0) return;

        double j = -(1.0 + e) * velocityAlongNormal;
        j /= invMassSum;

        // Apply impulse
        Vector2D impulse = normal.multiply(j);
        if (massA > 0) {
            bodyA.setVelocity(velA.subtract(impulse.divide(massA)));
        }
        if (massB > 0) {
            bodyB.setVelocity(velB.add(impulse.divide(massB)));
        }

        // Apply friction
        double friction = 0.2;
        Vector2D tangent = relativeVel.subtract(normal.multiply(velocityAlongNormal));
        if (tangent.lengthSquared() > 0.0001) {
            tangent = tangent.normalized();
            double jt = -relativeVel.dot(tangent);
            jt /= invMassSum;

            // Clamp friction
            Vector2D frictionImpulse;
            if (Math.abs(jt) < j * friction) {
                frictionImpulse = tangent.multiply(jt);
            } else {
                frictionImpulse = tangent.multiply(-j * friction);
            }

            if (massA > 0) {
                bodyA.setVelocity(bodyA.getVelocity().subtract(frictionImpulse.divide(massA)));
            }
            if (massB > 0) {
                bodyB.setVelocity(bodyB.getVelocity().add(frictionImpulse.divide(massB)));
            }
        }
    }
}

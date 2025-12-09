package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Circle Collider for round/spherical objects.
 * Converted from C++ CircleCollider class in collider.h/cpp
 */
public class CircleCollider extends Collider {
    
    @JsonProperty("center")
    private Vector2D center;
    
    @JsonProperty("radius")
    private double radius;

    public CircleCollider() {
        this.center = new Vector2D();
        this.radius = 0;
    }

    public CircleCollider(Vector2D center, double radius) {
        this.center = new Vector2D(center);
        this.radius = radius;
    }

    // Getters and Setters
    public Vector2D getCenter() {
        return center;
    }

    public void setCenter(Vector2D center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public void updatePosition(Vector2D position) {
        this.center = new Vector2D(position);
    }

    @Override
    @JsonIgnore
    public String getColliderType() {
        return "circle";
    }

    @Override
    public boolean checkCollision(Collider other) {
        if (other instanceof CircleCollider) {
            return checkCircleCollision((CircleCollider) other);
        } else if (other instanceof AABBCollider) {
            return AABBCollider.checkAABBCircleCollision((AABBCollider) other, this);
        }
        return false;
    }

    /**
     * Circle vs Circle collision detection
     * Converted from C++ checkCircleCollision function
     */
    private boolean checkCircleCollision(CircleCollider other) {
        double distance = center.subtract(other.center).length();
        return distance < (radius + other.radius);
    }

    @Override
    public void resolveCollision(PhysicsBody bodyA, PhysicsBody bodyB) {
        Vector2D posA = bodyA.getPosition();
        Vector2D posB = bodyB.getPosition();
        Vector2D velA = bodyA.getVelocity();
        Vector2D velB = bodyB.getVelocity();
        double massA = bodyA.getMass();
        double massB = bodyB.getMass();

        // Calculate collision normal
        Vector2D diff = posB.subtract(posA);
        if (diff.lengthSquared() < 0.0001) {
            // Objects at same position, use default normal
            diff = new Vector2D(1, 0);
        }
        Vector2D normal = diff.normalized();
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

        // Apply friction (similar to AABB collision)
        double friction = 0.2;
        Vector2D tangent = relativeVel.subtract(normal.multiply(velocityAlongNormal));
        if (tangent.lengthSquared() > 0.0001) {
            tangent = tangent.normalized();
            double jt = -relativeVel.dot(tangent);
            jt /= invMassSum;

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

package com.physics.engine;

import com.physics.model.PhysicsBody;
import com.physics.model.Vector2D;

/**
 * Forces class for applying various forces to physics bodies.
 * Converted from C++ Forces class in forces.h/cpp
 */
public class Forces {
    
    // Default gravitational constant (Earth's gravity)
    public static final double DEFAULT_GRAVITY = 9.8;
    
    // Internal state to check if gravity is active
    private static boolean gravityEnabled = true;

    /**
     * Apply user-defined custom force to an object.
     * Converted from C++ Forces::applyCustomForce method.
     */
    public static void applyCustomForce(PhysicsBody body, Vector2D force) {
        body.applyForce(force);
    }

    /**
     * Apply gravity force to an object.
     * Converted from C++ Forces::applyGravity method.
     */
    public static void applyGravity(PhysicsBody body, double gravity) {
        if (gravityEnabled && body.getMass() > 0) {
            // Apply downward force (positive Y in screen coordinates)
            Vector2D gravityForce = new Vector2D(0, gravity * body.getMass());
            body.applyForce(gravityForce);
        }
    }

    /**
     * Apply gravity with default value.
     */
    public static void applyGravity(PhysicsBody body) {
        applyGravity(body, DEFAULT_GRAVITY);
    }

    /**
     * Toggle gravity on/off.
     * Converted from C++ Forces::toggleGravity method.
     */
    public static void toggleGravity(boolean enable) {
        gravityEnabled = enable;
    }

    /**
     * Check if gravity is enabled.
     */
    public static boolean isGravityEnabled() {
        return gravityEnabled;
    }

    /**
     * Apply static friction to prevent motion until force threshold is overcome.
     * Converted from C++ Forces::applyStaticFriction method.
     */
    public static void applyStaticFriction(PhysicsBody body, double staticFrictionCoefficient, Vector2D surfaceNormal) {
        // No friction applied if object is moving
        if (body.getVelocity().length() > 0) {
            return;
        }

        // Calculate static friction force magnitude (force normal * static coefficient)
        Vector2D frictionForce = surfaceNormal.multiply(staticFrictionCoefficient * body.getMass());

        // Apply the static friction force to object, preventing motion (opposes any applied forces)
        body.applyForce(frictionForce.negate());
    }

    /**
     * Apply kinetic friction opposing motion.
     * Converted from C++ Forces::applyKineticFriction method.
     */
    public static void applyKineticFriction(PhysicsBody body, double kineticFrictionCoefficient, Vector2D surfaceNormal) {
        if (body.getVelocity().length() == 0) {
            return;
        }

        // Friction force is proportional to velocity and opposite to the motion direction
        Vector2D frictionDirection = body.getVelocity().normalized().negate();
        Vector2D frictionForce = frictionDirection.multiply(kineticFrictionCoefficient * body.getMass());

        // Apply kinetic friction force to the object
        body.applyForce(frictionForce);
    }

    /**
     * Apply impulse (instantaneous change in velocity).
     * @param body The physics body to apply impulse to
     * @param impulse The impulse vector
     */
    public static void applyImpulse(PhysicsBody body, Vector2D impulse) {
        if (body.getMass() > 0) {
            // Impulse directly changes velocity: v = v + impulse/mass
            Vector2D velocityChange = impulse.divide(body.getMass());
            body.setVelocity(body.getVelocity().add(velocityChange));
        }
    }
}

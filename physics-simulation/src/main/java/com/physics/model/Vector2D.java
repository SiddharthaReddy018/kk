// new one 
package com.physics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 2D Vector class for physics calculations.
 * Converted from C++ Vector2D class in vector2d.h
 */
public class Vector2D {
    
    @JsonProperty("x")
    public double x;
    
    @JsonProperty("y")
    public double y;

    // Constructors
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor
    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }

    // Vector addition
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    // Vector addition in place
    public Vector2D addInPlace(Vector2D other) {
        x += other.x;
        y += other.y;
        return this;
    }

    // Vector subtraction
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    // Vector subtraction in place
    public Vector2D subtractInPlace(Vector2D other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    // Scalar multiplication
    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    // Scalar multiplication in place
    public Vector2D multiplyInPlace(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    // Scalar division
    public Vector2D divide(double scalar) {
        if (scalar == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(x / scalar, y / scalar);
    }

    // Scalar division in place
    public Vector2D divideInPlace(double scalar) {
        if (scalar != 0) {
            x /= scalar;
            y /= scalar;
        }
        return this;
    }

    // Unary negation
    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    // Length (magnitude) of the vector
    @JsonIgnore
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    // Squared length (magnitude) of the vector
    @JsonIgnore
    public double lengthSquared() {
        return x * x + y * y;
    }

    // Normalize the vector
    public Vector2D normalized() {
        double len = length();
        if (len == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(x / len, y / len);
    }

    // Dot product of two vectors
    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }

    // Distance between two points
    public double distance(Vector2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Get the perpendicular vector (90 degrees rotation)
    public Vector2D perpendicular() {
        return new Vector2D(-y, x);
    }

    // Check equality
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D other = (Vector2D) obj;
        return Double.compare(other.x, x) == 0 && Double.compare(other.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }

    // Static factory method for creating from array (for JSON deserialization)
    public static Vector2D fromArray(double[] arr) {
        if (arr == null || arr.length < 2) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(arr[0], arr[1]);
    }

    // Convert to array (for JSON serialization)
    @JsonIgnore
    public double[] toArray() {
        return new double[]{x, y};
    }
}

package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for changing global gravity via API.
 */
public class GravityRequest {
    
    @JsonProperty("gravity")
    private double[] gravity;

    // Default constructor
    public GravityRequest() {
        this.gravity = new double[]{0, 9.81};
    }

    // Getters and Setters
    public double[] getGravity() {
        return gravity;
    }

    public void setGravity(double[] gravity) {
        this.gravity = gravity;
    }
}

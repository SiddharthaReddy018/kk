package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for stepping the simulation by a time delta via API.
 */
public class StepRequest {
    
    @JsonProperty("dt")
    private Double dt;

    // Default constructor
    public StepRequest() {
        this.dt = 1.0 / 60.0;  // Default: 1/60th of a second (60fps)
    }

    // Getters and Setters
    public Double getDt() {
        return dt;
    }

    public void setDt(Double dt) {
        this.dt = dt;
    }
}

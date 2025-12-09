package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for applying impulse to a physics object via API.
 */
public class ApplyImpulseRequest {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("impulse")
    private double[] impulse;

    // Default constructor
    public ApplyImpulseRequest() {
        this.impulse = new double[]{0, 0};
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double[] getImpulse() {
        return impulse;
    }

    public void setImpulse(double[] impulse) {
        this.impulse = impulse;
    }
}

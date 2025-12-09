package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for applying force to a physics object via API.
 * Matches the SRS sample input format.
 */
public class ApplyForceRequest {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("force")
    private double[] force;

    // Default constructor
    public ApplyForceRequest() {
        this.force = new double[]{0, 0};
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double[] getForce() {
        return force;
    }

    public void setForce(double[] force) {
        this.force = force;
    }
}

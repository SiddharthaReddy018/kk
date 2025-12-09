package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.physics.model.PhysicsBody;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for scene data (save/load operations).
 * Matches the SRS file format example.
 */
public class SceneData {
    
    @JsonProperty("bodies")
    private List<PhysicsBody> bodies;
    
    @JsonProperty("gravity")
    private double[] gravity;

    // Default constructor
    public SceneData() {
        this.bodies = new ArrayList<>();
        this.gravity = new double[]{0, 9.81};
    }

    // Getters and Setters
    public List<PhysicsBody> getBodies() {
        return bodies;
    }

    public void setBodies(List<PhysicsBody> bodies) {
        this.bodies = bodies;
    }

    public double[] getGravity() {
        return gravity;
    }

    public void setGravity(double[] gravity) {
        this.gravity = gravity;
    }
}

package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for creating a new physics object via API.
 * Matches the SRS sample input format.
 */
public class CreateObjectRequest {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("mass")
    private Double mass;
    
    @JsonProperty("position")
    private double[] position;
    
    @JsonProperty("velocity")
    private double[] velocity;
    
    // Shape-specific properties
    @JsonProperty("radius")
    private Double radius;
    
    @JsonProperty("width")
    private Double width;
    
    @JsonProperty("height")
    private Double height;
    
    @JsonProperty("sideLength")
    private Double sideLength;

    // Default constructor
    public CreateObjectRequest() {
        this.mass = 1.0;
        this.position = new double[]{0, 0};
        this.velocity = new double[]{0, 0};
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getSideLength() {
        return sideLength;
    }

    public void setSideLength(Double sideLength) {
        this.sideLength = sideLength;
    }
}

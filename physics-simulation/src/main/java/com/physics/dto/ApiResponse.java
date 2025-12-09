package com.physics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API response DTO.
 * Matches the SRS sample output format.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private Object data;

    // Default constructor
    public ApiResponse() {
    }

    // Success response with message
    public static ApiResponse success(String message) {
        ApiResponse response = new ApiResponse();
        response.setStatus("success");
        response.setMessage(message);
        return response;
    }

    // Success response with data
    public static ApiResponse success(String message, Object data) {
        ApiResponse response = new ApiResponse();
        response.setStatus("success");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // Error response
    public static ApiResponse error(String message) {
        ApiResponse response = new ApiResponse();
        response.setStatus("error");
        response.setMessage(message);
        return response;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

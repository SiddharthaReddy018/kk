package com.physics.controller;

import com.physics.dto.ApiResponse;
import com.physics.dto.ApplyForceRequest;
import com.physics.dto.ApplyImpulseRequest;
import com.physics.dto.GravityRequest;
import com.physics.service.ForceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for force handling routes.
 * Implements SRS Section 4.2 Force Handling Routes.
 */
@RestController
@RequestMapping("/forces")
@CrossOrigin(origins = "*")
public class ForceController {
    
    private final ForceService forceService;

    public ForceController(ForceService forceService) {
        this.forceService = forceService;
    }

    /**
     * POST /forces/apply - Apply a force to a body
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> applyForce(@RequestBody ApplyForceRequest request) {
        try {
            boolean applied = forceService.applyForce(request);
            if (applied) {
                return ResponseEntity.ok(ApiResponse.success(
                    "Force applied to body " + request.getId()
                ));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Object not found with ID: " + request.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to apply force: " + e.getMessage()));
        }
    }

    /**
     * POST /forces/impulse - Apply an impulse
     */
    @PostMapping("/impulse")
    public ResponseEntity<ApiResponse> applyImpulse(@RequestBody ApplyImpulseRequest request) {
        try {
            boolean applied = forceService.applyImpulse(request);
            if (applied) {
                return ResponseEntity.ok(ApiResponse.success(
                    "Impulse applied to body " + request.getId()
                ));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Object not found with ID: " + request.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to apply impulse: " + e.getMessage()));
        }
    }

    /**
     * POST /forces/gravity - Change global gravity
     */
    @PostMapping("/gravity")
    public ResponseEntity<ApiResponse> setGravity(@RequestBody GravityRequest request) {
        try {
            forceService.setGravity(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Gravity updated to [" + request.getGravity()[0] + ", " + request.getGravity()[1] + "]"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to set gravity: " + e.getMessage()));
        }
    }

    /**
     * GET /forces/gravity - Get current gravity
     */
    @GetMapping("/gravity")
    public ResponseEntity<?> getGravity() {
        return ResponseEntity.ok(forceService.getGravity());
    }
}

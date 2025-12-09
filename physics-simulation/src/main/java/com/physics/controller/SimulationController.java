package com.physics.controller;

import com.physics.dto.ApiResponse;
import com.physics.dto.StepRequest;
import com.physics.service.SimulationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for simulation control routes.
 * Implements SRS Section 4.3 Simulation Control Routes and 4.5 State Retrieval.
 */
@RestController
@RequestMapping("/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {
    
    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    /**
     * POST /simulation/start - Begin simulation
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse> startSimulation() {
        try {
            simulationService.start();
            return ResponseEntity.ok(ApiResponse.success("Simulation started"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to start simulation: " + e.getMessage()));
        }
    }

    /**
     * POST /simulation/pause - Pause simulation
     */
    @PostMapping("/pause")
    public ResponseEntity<ApiResponse> pauseSimulation() {
        try {
            simulationService.pause();
            return ResponseEntity.ok(ApiResponse.success("Simulation paused"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to pause simulation: " + e.getMessage()));
        }
    }

    /**
     * POST /simulation/reset - Reset engine
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetSimulation() {
        try {
            simulationService.reset();
            return ResponseEntity.ok(ApiResponse.success("Simulation has been reset to initial state."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to reset simulation: " + e.getMessage()));
        }
    }

    /**
     * POST /simulation/step - Move simulation by dt
     */
    @PostMapping("/step")
    public ResponseEntity<ApiResponse> stepSimulation(@RequestBody(required = false) StepRequest request) {
        try {
            Double dt = (request != null) ? request.getDt() : null;
            simulationService.step(dt);
            return ResponseEntity.ok(ApiResponse.success(
                "Simulation stepped by " + (dt != null ? dt : "1/60") + " seconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to step simulation: " + e.getMessage()));
        }
    }

    /**
     * GET /simulation/state - Return body positions, velocities, collisions
     */
    @GetMapping("/state")
    public ResponseEntity<Map<String, Object>> getState() {
        return ResponseEntity.ok(simulationService.getState());
    }

    /**
     * GET /simulation/running - Check if simulation is running
     */
    @GetMapping("/running")
    public ResponseEntity<Boolean> isRunning() {
        return ResponseEntity.ok(simulationService.isRunning());
    }
}

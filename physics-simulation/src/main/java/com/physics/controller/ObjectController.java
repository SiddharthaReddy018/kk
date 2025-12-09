package com.physics.controller;

import com.physics.dto.ApiResponse;
import com.physics.dto.CreateObjectRequest;
import com.physics.dto.UpdateObjectRequest;
import com.physics.model.PhysicsBody;
import com.physics.service.ObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for object handling routes.
 * Implements SRS Section 4.1 Object Handling Routes.
 */
@RestController
@RequestMapping("/objects")
@CrossOrigin(origins = "*")
public class ObjectController {
    
    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    /**
     * POST /objects/create - Add a new body
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createObject(@RequestBody CreateObjectRequest request) {
        try {
            PhysicsBody body = objectService.createObject(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Body created with ID: " + body.getId(), 
                body
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create object: " + e.getMessage()));
        }
    }

    /**
     * GET /objects/all - Retrieve all bodies
     */
    @GetMapping("/all")
    public ResponseEntity<List<PhysicsBody>> getAllObjects() {
        return ResponseEntity.ok(objectService.getAllObjects());
    }

    /**
     * GET /objects/{id} - Retrieve specific body
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getObject(@PathVariable int id) {
        Optional<PhysicsBody> body = objectService.getObject(id);
        if (body.isPresent()) {
            return ResponseEntity.ok(body.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Object not found with ID: " + id));
    }

    /**
     * PUT /objects/{id} - Update body details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateObject(
            @PathVariable int id, 
            @RequestBody UpdateObjectRequest request) {
        try {
            Optional<PhysicsBody> body = objectService.updateObject(id, request);
            if (body.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(
                    "Body updated with ID: " + id,
                    body.get()
                ));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Object not found with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update object: " + e.getMessage()));
        }
    }

    /**
     * DELETE /objects/{id} - Remove body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteObject(@PathVariable int id) {
        boolean deleted = objectService.deleteObject(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Body deleted with ID: " + id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Object not found with ID: " + id));
    }
}

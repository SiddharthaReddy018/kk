package com.physics.controller;

import com.physics.dto.ApiResponse;
import com.physics.dto.SceneData;
import com.physics.service.SceneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * REST Controller for scene management routes.
 * Implements SRS Section 4.4 Scene Management Routes.
 */
@RestController
@RequestMapping("/scene")
@CrossOrigin(origins = "*")
public class SceneController {
    
    private final SceneService sceneService;

    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    /**
     * POST /scene/save - Save current scene
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveScene(@RequestBody(required = false) Map<String, String> request) {
        try {
            String filename = (request != null) ? request.get("filename") : null;
            String savedFile = sceneService.saveScene(filename);
            return ResponseEntity.ok(ApiResponse.success(
                "Scene saved to " + savedFile
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to save scene: " + e.getMessage()));
        }
    }

    /**
     * POST /scene/load - Load scene from file
     */
    @PostMapping("/load")
    public ResponseEntity<ApiResponse> loadScene(@RequestBody(required = false) Map<String, String> request) {
        try {
            String filename = (request != null) ? request.get("filename") : null;
            SceneData sceneData = sceneService.loadScene(filename);
            return ResponseEntity.ok(ApiResponse.success(
                "Scene loaded with " + sceneData.getBodies().size() + " bodies",
                sceneData
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Failed to load scene: " + e.getMessage()));
        }
    }

    /**
     * GET /scene/current - Get current scene data (without saving)
     */
    @GetMapping("/current")
    public ResponseEntity<SceneData> getCurrentScene() {
        return ResponseEntity.ok(sceneService.getCurrentScene());
    }
}

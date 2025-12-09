package com.physics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.physics.dto.SceneData;
import com.physics.engine.PhysicsWorld;
import com.physics.model.PhysicsBody;
import com.physics.model.Vector2D;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Service for scene management operations.
 * Handles saving and loading scenes to/from JSON files.
 */
@Service
public class SceneService {
    
    private final PhysicsWorld physicsWorld;
    private final ObjectMapper objectMapper;
    
    // Default scene file path
    private static final String DEFAULT_SCENE_FILE = "scene.json";

    public SceneService(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Save current scene to file.
     * @param filename Optional filename (uses default if null)
     * @return The filename where scene was saved
     * @throws IOException if save fails
     */
    public String saveScene(String filename) throws IOException {
        String file = (filename != null && !filename.isEmpty()) ? filename : DEFAULT_SCENE_FILE;
        
        SceneData sceneData = new SceneData();
        sceneData.setBodies(new ArrayList<>(physicsWorld.getObjects()));
        sceneData.setGravity(physicsWorld.getGravity().toArray());
        
        objectMapper.writeValue(new File(file), sceneData);
        
        return file;
    }

    /**
     * Load scene from file.
     * @param filename Optional filename (uses default if null)
     * @return The loaded scene data
     * @throws IOException if load fails
     */
    public SceneData loadScene(String filename) throws IOException {
        String file = (filename != null && !filename.isEmpty()) ? filename : DEFAULT_SCENE_FILE;
        
        File sceneFile = new File(file);
        if (!sceneFile.exists()) {
            throw new IOException("Scene file not found: " + file);
        }
        
        SceneData sceneData = objectMapper.readValue(sceneFile, SceneData.class);
        
        // Clear current world and load new data
        physicsWorld.reset();
        
        // Set gravity
        if (sceneData.getGravity() != null && sceneData.getGravity().length >= 2) {
            physicsWorld.setGravity(new Vector2D(sceneData.getGravity()[0], sceneData.getGravity()[1]));
        }
        
        // Add bodies
        for (PhysicsBody body : sceneData.getBodies()) {
            physicsWorld.addObject(body);
        }
        
        return sceneData;
    }

    /**
     * Get current scene data (without saving to file).
     * @return Current scene data
     */
    public SceneData getCurrentScene() {
        SceneData sceneData = new SceneData();
        sceneData.setBodies(new ArrayList<>(physicsWorld.getObjects()));
        sceneData.setGravity(physicsWorld.getGravity().toArray());
        return sceneData;
    }

    /**
     * Check if a scene file exists.
     * @param filename The filename to check
     * @return true if file exists
     */
    public boolean sceneExists(String filename) {
        String file = (filename != null && !filename.isEmpty()) ? filename : DEFAULT_SCENE_FILE;
        return new File(file).exists();
    }
}

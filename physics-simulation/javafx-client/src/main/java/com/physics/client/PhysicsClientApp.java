package com.physics.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PhysicsClientApp extends Application {
    
    private static final String BASE_URL = "http://localhost:8080";
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final double POLL_INTERVAL_MS = 16.67;
    
    private Canvas canvas;
    private GraphicsContext gc;
    private HttpClient httpClient;
    private Timer pollTimer;
    private boolean isRunning = false;
    
    private Label statusLabel;
    private TextArea logArea;

    @Override
    public void start(Stage primaryStage) {
        httpClient = HttpClient.newHttpClient();
        
        BorderPane root = new BorderPane();
        
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        VBox controlPanel = createControlPanel();
        VBox statusPanel = createStatusPanel();
        
        root.setCenter(canvas);
        root.setRight(controlPanel);
        root.setBottom(statusPanel);
        
        Scene scene = new Scene(root, CANVAS_WIDTH + 250, CANVAS_HEIGHT + 100);
        primaryStage.setTitle("Physics Simulation - JavaFX Client");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> cleanup());
        primaryStage.show();
        
        clearCanvas();
        log("Client started. Connect to backend at " + BASE_URL);
    }
    
    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(240);
        panel.setStyle("-fx-background-color: #f0f0f0;");
        
        Label title = new Label("Simulation Controls");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Button startBtn = new Button("Start Simulation");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> sendPost("/simulation/start"));
        
        Button pauseBtn = new Button("Pause Simulation");
        pauseBtn.setMaxWidth(Double.MAX_VALUE);
        pauseBtn.setOnAction(e -> sendPost("/simulation/pause"));
        
        Button resetBtn = new Button("Reset Simulation");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setOnAction(e -> sendPost("/simulation/reset"));
        
        Button stepBtn = new Button("Step Simulation");
        stepBtn.setMaxWidth(Double.MAX_VALUE);
        stepBtn.setOnAction(e -> sendPost("/simulation/step"));
        
        Separator sep1 = new Separator();
        
        Label pollLabel = new Label("Visualization");
        pollLabel.setStyle("-fx-font-weight: bold;");
        
        Button startPollBtn = new Button("Start Polling");
        startPollBtn.setMaxWidth(Double.MAX_VALUE);
        startPollBtn.setOnAction(e -> startPolling());
        
        Button stopPollBtn = new Button("Stop Polling");
        stopPollBtn.setMaxWidth(Double.MAX_VALUE);
        stopPollBtn.setOnAction(e -> stopPolling());
        
        Button refreshBtn = new Button("Refresh Once");
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        refreshBtn.setOnAction(e -> fetchAndRender());
        
        Separator sep2 = new Separator();
        
        Label createLabel = new Label("Create Object");
        createLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("circle", "rectangle", "square");
        typeCombo.setValue("circle");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        
        TextField massField = new TextField("1.0");
        massField.setPromptText("Mass");
        
        TextField radiusField = new TextField("20.0");
        radiusField.setPromptText("Radius/Size");
        
        Button createBtn = new Button("Create Object");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            String type = typeCombo.getValue();
            double mass = Double.parseDouble(massField.getText());
            double radius = Double.parseDouble(radiusField.getText());
            createObject(type, mass, radius);
        });
        
        statusLabel = new Label("Status: Ready");
        statusLabel.setStyle("-fx-text-fill: green;");
        
        panel.getChildren().addAll(
            title, startBtn, pauseBtn, resetBtn, stepBtn,
            sep1, pollLabel, startPollBtn, stopPollBtn, refreshBtn,
            sep2, createLabel, typeCombo, massField, radiusField, createBtn,
            statusLabel
        );
        
        return panel;
    }
    
    private VBox createStatusPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(5));
        panel.setStyle("-fx-background-color: #e0e0e0;");
        
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(3);
        logArea.setStyle("-fx-font-family: monospace;");
        
        panel.getChildren().add(logArea);
        return panel;
    }
    
    private void clearCanvas() {
        gc.setFill(Color.rgb(45, 55, 60));
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        gc.setStroke(Color.rgb(60, 70, 80));
        gc.setLineWidth(0.5);
        for (int x = 0; x < CANVAS_WIDTH; x += 50) {
            gc.strokeLine(x, 0, x, CANVAS_HEIGHT);
        }
        for (int y = 0; y < CANVAS_HEIGHT; y += 50) {
            gc.strokeLine(0, y, CANVAS_WIDTH, y);
        }
    }
    
    private static class BodyData {
        int id;
        double x, y;
        String type;
        double radius = 10;
        double width = 20;
        double height = 20;
        double sideLength = 20;
    }
    
    private List<BodyData> parseBodies(String json) {
        List<BodyData> bodies = new ArrayList<>();
        
        int bodiesStart = json.indexOf("\"bodies\"");
        if (bodiesStart == -1) return bodies;
        
        int arrayStart = json.indexOf("[", bodiesStart);
        if (arrayStart == -1) return bodies;
        
        int depth = 0;
        int objectStart = -1;
        
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 1) objectStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 1 && objectStart != -1) {
                    String objectJson = json.substring(objectStart, i + 1);
                    BodyData body = parseBodyObject(objectJson);
                    if (body != null) bodies.add(body);
                    objectStart = -1;
                }
            } else if (c == ']' && depth == 1) {
                break;
            }
        }
        
        return bodies;
    }
    
    private BodyData parseBodyObject(String json) {
        BodyData body = new BodyData();
        
        Pattern idPattern = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            body.id = Integer.parseInt(idMatcher.group(1));
        }
        
        Pattern typePattern = Pattern.compile("\"type\"\\s*:\\s*\"(\\w+)\"");
        Matcher typeMatcher = typePattern.matcher(json);
        if (typeMatcher.find()) {
            body.type = typeMatcher.group(1);
        } else {
            body.type = "circle";
        }
        
        Pattern posPattern = Pattern.compile("\"position\"\\s*:\\s*\\[\\s*([\\d.\\-E]+)\\s*,\\s*([\\d.\\-E]+)\\s*\\]");
        Matcher posMatcher = posPattern.matcher(json);
        if (posMatcher.find()) {
            body.x = Double.parseDouble(posMatcher.group(1));
            body.y = Double.parseDouble(posMatcher.group(2));
        }
        
        Pattern radiusPattern = Pattern.compile("\"radius\"\\s*:\\s*([\\d.\\-E]+)");
        Matcher radiusMatcher = radiusPattern.matcher(json);
        if (radiusMatcher.find()) {
            body.radius = Double.parseDouble(radiusMatcher.group(1));
        }
        
        Pattern widthPattern = Pattern.compile("\"width\"\\s*:\\s*([\\d.\\-E]+)");
        Matcher widthMatcher = widthPattern.matcher(json);
        if (widthMatcher.find()) {
            body.width = Double.parseDouble(widthMatcher.group(1));
        }
        
        Pattern heightPattern = Pattern.compile("\"height\"\\s*:\\s*([\\d.\\-E]+)");
        Matcher heightMatcher = heightPattern.matcher(json);
        if (heightMatcher.find()) {
            body.height = Double.parseDouble(heightMatcher.group(1));
        }
        
        Pattern sidePattern = Pattern.compile("\"sideLength\"\\s*:\\s*([\\d.\\-E]+)");
        Matcher sideMatcher = sidePattern.matcher(json);
        if (sideMatcher.find()) {
            body.sideLength = Double.parseDouble(sideMatcher.group(1));
        }
        
        return body;
    }
    
    private boolean parseRunning(String json) {
        Pattern pattern = Pattern.compile("\"running\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }
    
    private void renderState(String json) {
        clearCanvas();
        
        List<BodyData> bodies = parseBodies(json);
        boolean running = parseRunning(json);
        
        for (BodyData body : bodies) {
            renderBody(body);
        }
        
        int bodyCount = bodies.size();
        Platform.runLater(() -> {
            statusLabel.setText("Bodies: " + bodyCount + " | " + (running ? "Running" : "Paused"));
            statusLabel.setStyle(running ? "-fx-text-fill: green;" : "-fx-text-fill: orange;");
        });
    }
    
    private void renderBody(BodyData body) {
        double x = body.x;
        double y = body.y;
        
        Color color = Color.hsb((body.id * 60) % 360, 0.7, 0.9);
        gc.setFill(color);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        
        switch (body.type) {
            case "circle":
                double radius = body.radius;
                gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
                break;
            case "rectangle":
                gc.fillRect(x, y, body.width, body.height);
                gc.strokeRect(x, y, body.width, body.height);
                break;
            case "square":
                double side = body.sideLength;
                gc.fillRect(x, y, side, side);
                gc.strokeRect(x, y, side, side);
                break;
            default:
                gc.fillOval(x - 10, y - 10, 20, 20);
                gc.strokeOval(x - 10, y - 10, 20, 20);
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText("ID:" + body.id, x - 10, y - body.radius - 5);
    }
    
    private void fetchAndRender() {
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/simulation/state"))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    String json = response.body();
                    Platform.runLater(() -> renderState(json));
                }
            } catch (Exception e) {
                Platform.runLater(() -> log("Error fetching state: " + e.getMessage()));
            }
        }).start();
    }
    
    private void sendPost(String endpoint) {
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .header("Content-Type", "application/json")
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                Platform.runLater(() -> {
                    log(endpoint + " -> " + response.statusCode());
                    fetchAndRender();
                });
            } catch (Exception e) {
                Platform.runLater(() -> log("Error: " + e.getMessage()));
            }
        }).start();
    }
    
    private void createObject(String type, double mass, double size) {
        new Thread(() -> {
            try {
                String json;
                double x = Math.random() * (CANVAS_WIDTH - 100) + 50;
                double y = Math.random() * (CANVAS_HEIGHT - 100) + 50;
                
                if (type.equals("circle")) {
                    json = String.format(
                        "{\"type\":\"circle\",\"mass\":%.2f,\"radius\":%.2f,\"position\":[%.2f,%.2f],\"velocity\":[0,0]}",
                        mass, size, x, y
                    );
                } else if (type.equals("rectangle")) {
                    json = String.format(
                        "{\"type\":\"rectangle\",\"mass\":%.2f,\"width\":%.2f,\"height\":%.2f,\"position\":[%.2f,%.2f],\"velocity\":[0,0]}",
                        mass, size, size * 0.6, x, y
                    );
                } else {
                    json = String.format(
                        "{\"type\":\"square\",\"mass\":%.2f,\"sideLength\":%.2f,\"position\":[%.2f,%.2f],\"velocity\":[0,0]}",
                        mass, size, x, y
                    );
                }
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/objects/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                Platform.runLater(() -> {
                    log("Created " + type + " -> " + response.statusCode());
                    fetchAndRender();
                });
            } catch (Exception e) {
                Platform.runLater(() -> log("Error creating object: " + e.getMessage()));
            }
        }).start();
    }
    
    private void startPolling() {
        if (isRunning) return;
        isRunning = true;
        
        pollTimer = new Timer(true);
        pollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchAndRender();
            }
        }, 0, (long) POLL_INTERVAL_MS);
        
        log("Started polling at ~60fps");
    }
    
    private void stopPolling() {
        if (!isRunning) return;
        isRunning = false;
        
        if (pollTimer != null) {
            pollTimer.cancel();
            pollTimer = null;
        }
        
        log("Stopped polling");
    }
    
    private void log(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.appendText("[" + timestamp + "] " + message + "\n");
    }
    
    private void cleanup() {
        stopPolling();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
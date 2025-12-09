# Physics Simulation Engine -- Quick Reference & Ownership Guide

## 🧪 Project Introduction

This project is a **Java-based 2D Physics Simulation Engine** designed
to mimic real‑world physics using: - Vector mathematics\
- Forces and motion equations\
- Collision detection algorithms\
- Object‑oriented body modelling\
- REST API controls for simulation\
- Clean multi‑layer architecture (Controller → Service → Model)

It allows users to **create objects, apply forces, simulate motion,
detect collisions**, and control everything through a structured and
modular backend.\
Each team member is responsible for specific modules, ensuring clean
separation of concerns and easier debugging during vivas.

------------------------------------------------------------------------

# Physics Simulation Engine (Java)

A 2D Physics Simulation Engine built with Java + Spring Boot backend and JavaFX visualization client.

## Project Overview


- **Backend**: Spring Boot REST API for physics simulation
- **Frontend**: JavaFX client for real-time visualization
- **Storage**: JSON file-based scene persistence

## Project Structure

```
physics-simulation/
├── pom.xml                          # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/physics/
│   │   │   ├── PhysicsSimulationApplication.java  # Main entry point
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java                 # Spring configuration
│   │   │   ├── controller/
│   │   │   │   ├── ObjectController.java          # Object CRUD endpoints
│   │   │   │   ├── ForceController.java           # Force application endpoints
│   │   │   │   ├── SimulationController.java      # Simulation control endpoints
│   │   │   │   └── SceneController.java           # Scene save/load endpoints
│   │   │   ├── service/
│   │   │   │   ├── ObjectService.java
│   │   │   │   ├── ForceService.java
│   │   │   │   ├── SimulationService.java
│   │   │   │   └── SceneService.java
│   │   │   ├── engine/
│   │   │   │   ├── PhysicsWorld.java              # Main physics engine
│   │   │   │   └── Forces.java                    # Force calculations
│   │   │   ├── model/
│   │   │   │   ├── Vector2D.java                  # 2D vector math
│   │   │   │   ├── PhysicsBody.java               # Base physics body
│   │   │   │   ├── CircleBody.java
│   │   │   │   ├── RectangleBody.java
│   │   │   │   ├── SquareBody.java
│   │   │   │   ├── Collider.java                  # Base collider
│   │   │   │   ├── AABBCollider.java
│   │   │   │   └── CircleCollider.java
│   │   │   └── dto/
│   │   │       ├── CreateObjectRequest.java
│   │   │       ├── UpdateObjectRequest.java
│   │   │       ├── ApplyForceRequest.java
│   │   │       ├── ApplyImpulseRequest.java
│   │   │       ├── GravityRequest.java
│   │   │       ├── StepRequest.java
│   │   │       ├── SceneData.java
│   │   │       └── ApiResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/physics/
├── javafx-client/                   # JavaFX visualization client
│   ├── pom.xml
│   └── src/main/java/com/physics/client/
│       └── PhysicsClientApp.java
└── README.md
```

## Requirements

- **Java 21** (or later)
- **Maven 3.8+**
- **JavaFX SDK 21** (downloaded separately for the JavaFX client)

## Quick Start

### 1. Start the Spring Boot Backend

```bash
cd physics-simulation

# Or using Maven
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

### 2. Test with Postman or cURL

**Create a circle:**
```bash
curl -X POST http://localhost:8080/objects/create \
  -H "Content-Type: application/json" \
  -d '{"type":"circle","radius":15.0,"mass":2.0}'
```

**Get all objects:**
```bash
curl http://localhost:8080/objects/all
```

**Step simulation:**
```bash
curl -X POST http://localhost:8080/simulation/step \
  -H "Content-Type: application/json" \
  -d '{"dt":0.016}'
```

**Get simulation state:**
```bash
curl http://localhost:8080/simulation/state
```

### 3. Run the JavaFX Client (Optional)

#### Option A: Using Local JavaFX SDK

If you have JavaFX SDK downloaded locally:

```bash
cd javafx-client

# Set path to your JavaFX SDK
export PATH_TO_FX="/path/to/javafx-sdk-21/lib"

# Compile and run
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics \
  -d target/classes src/main/java/com/physics/client/PhysicsClientApp.java

java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics \
  -cp target/classes com.physics.client.PhysicsClientApp
```

#### Option B: Using Maven (requires JavaFX dependencies in pom.xml)

```bash
cd javafx-client
mvn javafx:run
```

## API Endpoints

### Object Handling (Section 4.1)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/objects/create` | Add a new body |
| GET | `/objects/all` | Retrieve all bodies |
| GET | `/objects/{id}` | Retrieve specific body |
| PUT | `/objects/{id}` | Update body details |
| DELETE | `/objects/{id}` | Remove body |

### Force Handling (Section 4.2)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/forces/apply` | Apply a force to a body |
| POST | `/forces/impulse` | Apply an impulse |
| POST | `/forces/gravity` | Change global gravity |

### Simulation Control (Section 4.3)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/simulation/start` | Begin simulation |
| POST | `/simulation/pause` | Pause simulation |
| POST | `/simulation/reset` | Reset engine |
| POST | `/simulation/step` | Move simulation by dt |

### Scene Management (Section 4.4)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/scene/save` | Save current scene |
| POST | `/scene/load` | Load scene from file |

### State Retrieval (Section 4.5)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/simulation/state` | Return body positions, velocities, collisions |

## Sample Requests

### Create Circle
```json
POST /objects/create
{
  "type": "circle",
  "radius": 15.0,
  "mass": 2.0,
  "position": [100, 100],
  "velocity": [0, 0]
}
```

### Create Rectangle
```json
POST /objects/create
{
  "type": "rectangle",
  "width": 50.0,
  "height": 30.0,
  "mass": 5.0,
  "position": [200, 200],
  "velocity": [1.0, 0]
}
```

### Apply Force
```json
POST /forces/apply
{
  "id": 1,
  "force": [50.0, -10.0]
}
```

### Set Gravity
```json
POST /forces/gravity
{
  "gravity": [0, 9.81]
}
```

### Step Simulation
```json
POST /simulation/step
{
  "dt": 0.016
}
```

## Postman Setup

1. **Import Collection**: Create a new collection "Physics Simulation"

2. **Set Variables**:
   - Create variable `base_url` = `http://localhost:8080`

3. **Create Requests**:
   - Create Object: `POST {{base_url}}/objects/create`
   - Get All Objects: `GET {{base_url}}/objects/all`
   - Step Simulation: `POST {{base_url}}/simulation/step`
   - Get State: `GET {{base_url}}/simulation/state`

4. **Test Workflow**:
   ```
   1. Create a circle
   2. Create another circle at different position
   3. Apply force to first circle
   4. Step simulation multiple times
   5. Observe collision detection in state
   ```

## VS Code Setup

### Extensions Recommended
- Extension Pack for Java
- Spring Boot Extension Pack
- Maven for Java

### Launch Configuration

Create `.vscode/launch.json`:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "PhysicsSimulationApplication",
      "request": "launch",
      "mainClass": "com.physics.PhysicsSimulationApplication",
      "projectName": "physics-simulation"
    }
  ]
}
```

### Tasks Configuration

Create `.vscode/tasks.json`:
```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Build",
      "type": "shell",
      "command": "./mvnw compile",
      "group": "build"
    },
    {
      "label": "Run",
      "type": "shell",
      "command": "./mvnw spring-boot:run",
      "group": "test"
    }
  ]
}
```

## JavaFX Path Configuration

If you have JavaFX SDK downloaded locally, set the path in your environment:

**Windows:**
```batch
set PATH_TO_FX=C:\path\to\javafx-sdk-21\lib
```

**Linux/Mac:**
```bash
export PATH_TO_FX=/path/to/javafx-sdk-21/lib
```

Then run with:
```bash
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics -jar physics-client.jar
```

## File Format

Scene files are stored in JSON format:

```json
{
  "bodies": [
    {
      "id": 1,
      "type": "circle",
      "position": [10.5, 20.0],
      "velocity": [0.0, -9.8],
      "mass": 2.0,
      "radius": 15.0
    }
  ],
  "gravity": [0, 9.81]
}
```

## Architecture Notes

### C++ to Java Mapping

| C++ Class | Java Class | Notes |
|-----------|------------|-------|
| Vector2D | Vector2D | Same methods, Java naming |
| Object (base) | PhysicsBody | Abstract base class |
| Circle | CircleBody | Extends PhysicsBody |
| Rectangle | RectangleBody | Extends PhysicsBody |
| Square | SquareBody | Extends PhysicsBody |
| Collider | Collider | Abstract base |
| AABBCollider | AABBCollider | Rectangle collision |
| CircleCollider | CircleCollider | Circle collision |
| PhysicsWorld | PhysicsWorld | Main engine |
| Forces | Forces | Static utility class |

### Key Differences from C++

1. **No JNI**: Pure Java implementation
2. **REST API**: HTTP interface instead of direct calls
3. **Spring Boot**: Dependency injection, configuration
4. **JSON Storage**: File-based persistence
5. **JavaFX**: Replaces ImGui for visualization



## Troubleshooting

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080
# Kill the process
kill -9 <PID>
```

### JavaFX Not Found
Make sure JavaFX SDK is properly installed and PATH_TO_FX is set correctly.


------------------------------------------------------------------------

## 📌 Quick Reference: Who Did What?

### Harsha \[BT2024148\] → **PHYSICS MATH** 🎓

**Files:** Vector2D.java, Forces.java, PhysicsWorld.java (calculations)\
**Key Words:** Vectors, F=ma, Euler Integration, Dot Product, Forces\



### Siddhartha Reddy \[BT2024049\] → **OBJECT DESIGN** 🏗️

**Files:** PhysicsBody.java, CircleBody.java, RectangleBody.java,
SquareBody.java\
**Key Words:** Inheritance, Polymorphism, Abstract Class, OOP\



### Varshith \[IMT2024044\] → **COLLISION** 💥

**Files:** Collider.java, AABBCollider.java, CircleCollider.java\
**Key Words:** AABB, Circle Collision, Intersection, Normals\



### Vivek \[BT2024039\] → **REST API** 🌐

**Files:** ObjectController, ForceController, SimulationController,
SceneController\
**Key Words:** REST, HTTP, GET/POST, Spring Controllers, Routing\



### Rishi Kowsik \[BT2024131\] → **BUSINESS LOGIC** 🧠

**Files:** ObjectService, ForceService, SimulationService, SceneService\
**Key Words:** CRUD, State Management, Validation, Persistence.



### Rugvedh \[IMT2024051\] → **FRAMEWORK** 🚀

**Files:** Main App, AppConfig, All DTOs, application.properties\
**Key Words:** Framework setup, Dependency Injection, DTOs,
Configuration.

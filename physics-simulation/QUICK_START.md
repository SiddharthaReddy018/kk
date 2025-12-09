# Quick Start Guide

## Prerequisites

1. **Java 21** - Download from https://adoptium.net/ or use SDKMAN
2. **Maven** - Download from https://maven.apache.org/download.cgi (or use the included wrapper)
3. **JavaFX SDK 21** (optional, for visualization) - Download from https://openjfx.io/

## Step 1: Run the Backend

```bash
# Navigate to project directory
cd physics-simulation

# Run with Maven (if Maven is installed)
mvn spring-boot:run

# OR use the wrapper script
./mvnw spring-boot:run      # Linux/Mac
mvnw.cmd spring-boot:run    # Windows
```

The server starts at **http://localhost:8080**

## Step 2: Test with cURL

Open a new terminal and test the API:

```bash
# Create a circle
curl -X POST http://localhost:8080/objects/create \
  -H "Content-Type: application/json" \
  -d '{"type":"circle","radius":15.0,"mass":2.0,"position":[100,100]}'

# Create another circle
curl -X POST http://localhost:8080/objects/create \
  -H "Content-Type: application/json" \
  -d '{"type":"circle","radius":20.0,"mass":3.0,"position":[200,100]}'

# Get all objects
curl http://localhost:8080/objects/all

# Apply force
curl -X POST http://localhost:8080/forces/apply \
  -H "Content-Type: application/json" \
  -d '{"id":1,"force":[50.0,0]}'

# Step simulation
curl -X POST http://localhost:8080/simulation/step \
  -H "Content-Type: application/json" \
  -d '{"dt":0.016}'

# Get simulation state
curl http://localhost:8080/simulation/state

# Reset simulation
curl -X POST http://localhost:8080/simulation/reset
```

## Step 3: Use Postman (Recommended)

1. Open Postman
2. Import the collection from `postman/Physics_Simulation_API.postman_collection.json`
3. The collection includes all API endpoints pre-configured

## Step 4: Run JavaFX Client (Optional)

### If you have JavaFX SDK downloaded:

**Linux/Mac:**
```bash
export PATH_TO_FX=/path/to/javafx-sdk-21/lib

cd javafx-client
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics \
  -d target/classes src/main/java/com/physics/client/PhysicsClientApp.java

java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics \
  -cp target/classes com.physics.client.PhysicsClientApp
```

**Windows:**
```batch
set PATH_TO_FX=C:\path\to\javafx-sdk-21\lib

cd javafx-client
javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.graphics ^
  -d target\classes src\main\java\com\physics\client\PhysicsClientApp.java

java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.graphics ^
  -cp target\classes com.physics.client.PhysicsClientApp
```

## VS Code Setup

1. Install extensions:
   - "Extension Pack for Java"
   - "Spring Boot Extension Pack"

2. Open the `physics-simulation` folder in VS Code

3. Press `F5` to run the application (uses the included launch.json)

4. Use the built-in terminal to test with cURL

## Common Issues

### "Port 8080 already in use"
```bash
# Find and kill the process
lsof -i :8080
kill -9 <PID>
```

### "Java version not found"
Make sure JAVA_HOME is set:
```bash
export JAVA_HOME=/path/to/java-21
export PATH=$JAVA_HOME/bin:$PATH
```

### "Maven not found"
Use the wrapper script (./mvnw) or install Maven globally.

## API Quick Reference

| Action | Method | Endpoint |
|--------|--------|----------|
| Create body | POST | /objects/create |
| Get all | GET | /objects/all |
| Get one | GET | /objects/{id} |
| Update | PUT | /objects/{id} |
| Delete | DELETE | /objects/{id} |
| Apply force | POST | /forces/apply |
| Apply impulse | POST | /forces/impulse |
| Set gravity | POST | /forces/gravity |
| Start | POST | /simulation/start |
| Pause | POST | /simulation/pause |
| Reset | POST | /simulation/reset |
| Step | POST | /simulation/step |
| Get state | GET | /simulation/state |
| Save scene | POST | /scene/save |
| Load scene | POST | /scene/load |

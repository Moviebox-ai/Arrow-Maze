package com.example.arrowmaze3d.player;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;
import com.example.arrowmaze3d.world.Direction;

public class PlayerController {
    private final Vector3i currentGridPos = new Vector3i();
    private final Vector3i targetGridPos = new Vector3i();
    private Direction facingDirection = Direction.NORTH;

    private final Vector3 currentWorldPos = new Vector3();
    private final Vector3 startWorldPos = new Vector3();
    private final Vector3 targetWorldPos = new Vector3();

    private ModelInstance modelInstance;
    private PlayerState state = PlayerState.IDLE;
    private float moveProgress = 1.0f;
    private float moveSpeed = 5.0f; // tiles per sec
    private int stepCount = 0;

    public PlayerController(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public void setPosition(Vector3i gridPos, Direction facing) {
        this.currentGridPos.set(gridPos);
        this.targetGridPos.set(gridPos);
        this.facingDirection = facing;
        this.state = PlayerState.IDLE;
        this.moveProgress = 1.0f;

        float wx = gridPos.x * Constants.GRID_CELL_SIZE;
        float wy = (gridPos.y * Constants.GRID_ELEVATION_STEP) + 0.6f;
        float wz = gridPos.z * Constants.GRID_CELL_SIZE;

        currentWorldPos.set(wx, wy, wz);
        startWorldPos.set(currentWorldPos);
        targetWorldPos.set(currentWorldPos);

        updateVisualTransform();
    }

    public void moveTo(Vector3i newGridPos, Direction newFacing) {
        if (state == PlayerState.MOVING) return;

        this.facingDirection = newFacing;
        this.targetGridPos.set(newGridPos);
        this.startWorldPos.set(currentWorldPos);

        float tx = newGridPos.x * Constants.GRID_CELL_SIZE;
        float ty = (newGridPos.y * Constants.GRID_ELEVATION_STEP) + 0.6f;
        float tz = newGridPos.z * Constants.GRID_CELL_SIZE;
        this.targetWorldPos.set(tx, ty, tz);

        this.moveProgress = 0.0f;
        this.state = PlayerState.MOVING;
        this.stepCount++;
    }

    public void update(float delta) {
        if (state == PlayerState.MOVING) {
            moveProgress += delta * moveSpeed;
            if (moveProgress >= 1.0f) {
                moveProgress = 1.0f;
                currentGridPos.set(targetGridPos);
                currentWorldPos.set(targetWorldPos);
                state = PlayerState.IDLE;
            } else {
                currentWorldPos.set(startWorldPos).lerp(targetWorldPos, moveProgress);
            }
            updateVisualTransform();
        }
    }

    private void updateVisualTransform() {
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(currentWorldPos);
            modelInstance.transform.rotate(0, 1, 0, facingDirection.rotationYDegrees);
        }
    }

    public Vector3i getCurrentGridPos() { return currentGridPos; }
    public PlayerState getState() { return state; }
    public void setState(PlayerState state) { this.state = state; }
    public ModelInstance getModelInstance() { return modelInstance; }
    public Vector3 getCurrentWorldPos() { return currentWorldPos; }
    public int getStepCount() { return stepCount; }
    public void setStepCount(int count) { this.stepCount = count; }
    public Direction getFacingDirection() { return facingDirection; }
}

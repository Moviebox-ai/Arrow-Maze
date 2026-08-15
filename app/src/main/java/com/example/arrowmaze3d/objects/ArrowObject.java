package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;
import com.example.arrowmaze3d.world.Direction;

public class ArrowObject extends GameObject3D {

    public enum ArrowType {
        DIRECTIONAL,
        ROTATABLE,
        REVERSIBLE,
        SPLIT,
        REDIRECTOR
    }

    private ArrowType type;
    private Direction direction;
    private boolean autoRotateOnStep;

    public ArrowObject(String id, Vector3i gridPos, ModelInstance modelInstance, ArrowType type, Direction direction) {
        super(id, gridPos, modelInstance);
        this.type = type;
        this.direction = direction;
        this.autoRotateOnStep = (type == ArrowType.ROTATABLE);
        updateRotationVisual();
    }

    public void updateRotationVisual() {
        if (modelInstance != null) {
            float worldX = gridPosition.x * Constants.GRID_CELL_SIZE;
            float worldY = gridPosition.y * Constants.GRID_ELEVATION_STEP;
            float worldZ = gridPosition.z * Constants.GRID_CELL_SIZE;
            modelInstance.transform.setToTranslation(worldX, worldY, worldZ);
            modelInstance.transform.rotate(0, 1, 0, direction.rotationYDegrees);
        }
    }

    @Override
    public void onStepOn() {
        // Trigger step-on logic if needed
    }

    @Override
    public void onStepOff() {
        if (type == ArrowType.ROTATABLE || autoRotateOnStep) {
            rotateClockwise();
        } else if (type == ArrowType.REVERSIBLE) {
            reverseDirection();
        }
    }

    public void rotateClockwise() {
        this.direction = direction.rotateClockwise();
        updateRotationVisual();
    }

    public void reverseDirection() {
        this.direction = direction.reverse();
        updateRotationVisual();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        updateRotationVisual();
    }

    public ArrowType getType() {
        return type;
    }
}

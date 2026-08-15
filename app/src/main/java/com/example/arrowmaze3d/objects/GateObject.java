package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;
import com.example.arrowmaze3d.world.Direction;

public class GateObject extends GameObject3D {
    private Direction passageDirection; // Passage allowed only when moving in this direction
    private boolean isOpen;

    public GateObject(String id, Vector3i gridPos, ModelInstance modelInstance, Direction passageDirection, boolean isOpen) {
        super(id, gridPos, modelInstance);
        this.passageDirection = passageDirection;
        this.isOpen = isOpen;
        updateVisual();
    }

    public void updateVisual() {
        if (modelInstance != null) {
            float worldX = gridPosition.x * Constants.GRID_CELL_SIZE;
            float worldY = (gridPosition.y * Constants.GRID_ELEVATION_STEP) + (isOpen ? -0.8f : 0f);
            float worldZ = gridPosition.z * Constants.GRID_CELL_SIZE;
            modelInstance.transform.setToTranslation(worldX, worldY, worldZ);
            if (passageDirection != null) {
                modelInstance.transform.rotate(0, 1, 0, passageDirection.rotationYDegrees);
            }
        }
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
        updateVisual();
    }

    public Direction getPassageDirection() {
        return passageDirection;
    }
}

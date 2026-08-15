package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;

public class LockGateObject extends GameObject3D {
    private boolean unlocked = false;
    private float currentYOffset = 0f;

    public LockGateObject(String id, Vector3i gridPosition, ModelInstance modelInstance) {
        super(id, gridPosition, modelInstance);
        updateWorldTransform();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public void update(float delta) {
        if (unlocked && currentYOffset > -1.2f) {
            currentYOffset -= delta * 3.0f;
            if (currentYOffset < -1.2f) currentYOffset = -1.2f;
            updateWorldTransform();
        }
    }

    @Override
    public void updateWorldTransform() {
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(
                gridPosition.x * Constants.GRID_CELL_SIZE,
                gridPosition.y * Constants.GRID_ELEVATION_STEP + currentYOffset,
                gridPosition.z * Constants.GRID_CELL_SIZE
            );
        }
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}
}

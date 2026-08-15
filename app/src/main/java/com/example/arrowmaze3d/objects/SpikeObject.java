package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;

public class SpikeObject extends GameObject3D {
    private boolean active = true;

    public SpikeObject(String id, Vector3i gridPosition, ModelInstance modelInstance) {
        super(id, gridPosition, modelInstance);
        updateWorldTransform();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        updateWorldTransform();
    }

    public void update(float delta) {
    }

    @Override
    public void updateWorldTransform() {
        if (modelInstance != null) {
            float yOffset = active ? 0.05f : -0.35f;
            modelInstance.transform.setToTranslation(
                gridPosition.x * Constants.GRID_CELL_SIZE,
                gridPosition.y * Constants.GRID_ELEVATION_STEP + yOffset,
                gridPosition.z * Constants.GRID_CELL_SIZE
            );
        }
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}
}

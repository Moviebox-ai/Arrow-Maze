package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;

public class StarObject extends GameObject3D {
    private boolean collected = false;
    private float animTime = 0f;

    public StarObject(String id, Vector3i gridPosition, ModelInstance modelInstance) {
        super(id, gridPosition, modelInstance);
        updateWorldTransform();
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void update(float delta) {
        if (collected) return;
        animTime += delta;
        if (modelInstance != null) {
            float floatOffset = (float) Math.sin(animTime * 4f) * 0.1f + 0.4f;
            float rotation = (animTime * 120f) % 360f;
            modelInstance.transform.setToTranslation(
                gridPosition.x * Constants.GRID_CELL_SIZE,
                gridPosition.y * Constants.GRID_ELEVATION_STEP + floatOffset,
                gridPosition.z * Constants.GRID_CELL_SIZE
            );
            modelInstance.transform.rotate(Vector3.Y, rotation);
        }
    }

    @Override
    public void updateWorldTransform() {
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(
                gridPosition.x * Constants.GRID_CELL_SIZE,
                gridPosition.y * Constants.GRID_ELEVATION_STEP + 0.4f,
                gridPosition.z * Constants.GRID_CELL_SIZE
            );
        }
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}
}

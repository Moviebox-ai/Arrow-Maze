package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;

public abstract class GameObject3D {
    protected String id;
    protected Vector3i gridPosition;
    protected ModelInstance modelInstance;

    public GameObject3D(String id, Vector3i gridPosition, ModelInstance modelInstance) {
        this.id = id;
        this.gridPosition = gridPosition.cpy();
        this.modelInstance = modelInstance;
        updateWorldTransform();
    }

    public void updateWorldTransform() {
        if (modelInstance != null) {
            float worldX = gridPosition.x * Constants.GRID_CELL_SIZE;
            float worldY = gridPosition.y * Constants.GRID_ELEVATION_STEP;
            float worldZ = gridPosition.z * Constants.GRID_CELL_SIZE;
            modelInstance.transform.setToTranslation(worldX, worldY, worldZ);
        }
    }

    public String getId() {
        return id;
    }

    public Vector3i getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(Vector3i pos) {
        this.gridPosition.set(pos);
        updateWorldTransform();
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public abstract void onStepOn();
    public abstract void onStepOff();
}

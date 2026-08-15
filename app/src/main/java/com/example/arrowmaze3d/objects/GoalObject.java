package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Vector3i;

public class GoalObject extends GameObject3D {
    private float rotationAngle = 0f;

    public GoalObject(String id, Vector3i gridPos, ModelInstance modelInstance) {
        super(id, gridPos, modelInstance);
    }

    public void updateAnimation(float delta) {
        rotationAngle = (rotationAngle + delta * 90f) % 360f;
        if (modelInstance != null) {
            updateWorldTransform();
            modelInstance.transform.rotate(0, 1, 0, rotationAngle);
        }
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}
}

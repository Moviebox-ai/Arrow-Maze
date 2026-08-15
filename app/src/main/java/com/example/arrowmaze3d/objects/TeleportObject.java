package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Vector3i;

public class TeleportObject extends GameObject3D {
    private Vector3i targetDestination;

    public TeleportObject(String id, Vector3i gridPos, ModelInstance modelInstance, Vector3i targetDestination) {
        super(id, gridPos, modelInstance);
        this.targetDestination = targetDestination;
    }

    @Override
    public void onStepOn() {}

    @Override
    public void onStepOff() {}

    public Vector3i getTargetDestination() {
        return targetDestination;
    }
}

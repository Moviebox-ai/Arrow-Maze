package com.example.arrowmaze3d.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.example.arrowmaze3d.utilities.Vector3i;

public class SwitchObject extends GameObject3D {
    private String linkedTargetId;
    private boolean isActivated;

    public SwitchObject(String id, Vector3i gridPos, ModelInstance modelInstance, String linkedTargetId) {
        super(id, gridPos, modelInstance);
        this.linkedTargetId = linkedTargetId;
        this.isActivated = false;
    }

    @Override
    public void onStepOn() {
        this.isActivated = !isActivated;
    }

    @Override
    public void onStepOff() {
        // Keeps state or toggles
    }

    public String getLinkedTargetId() {
        return linkedTargetId;
    }

    public boolean isActivated() {
        return isActivated;
    }
}

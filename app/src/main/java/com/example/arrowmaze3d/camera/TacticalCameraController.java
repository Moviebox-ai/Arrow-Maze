package com.example.arrowmaze3d.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.utilities.Constants;

public class TacticalCameraController {
    private final PerspectiveCamera camera;
    private final Vector3 targetPosition = new Vector3();
    private float distance = 22.0f;
    private float pitchAngle = 48.0f; // degrees
    private float yawAngle = 45.0f;   // degrees

    public TacticalCameraController(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public void setTarget(float x, float y, float z) {
        targetPosition.set(x, y, z);
        snapToTarget();
    }

    public void snapToTarget() {
        double pitchRad = Math.toRadians(pitchAngle);
        double yawRad = Math.toRadians(yawAngle);

        float horizDist = (float) (distance * Math.cos(pitchRad));
        float vertDist = (float) (distance * Math.sin(pitchRad));

        float camX = targetPosition.x + (float) (horizDist * Math.sin(yawRad));
        float camY = targetPosition.y + vertDist;
        float camZ = targetPosition.z + (float) (horizDist * Math.cos(yawRad));

        camera.position.set(camX, camY, camZ);
        camera.lookAt(targetPosition);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    public void update(float delta) {
        // Calculate camera position based on target, distance, pitch, and yaw
        double pitchRad = Math.toRadians(pitchAngle);
        double yawRad = Math.toRadians(yawAngle);

        float horizDist = (float) (distance * Math.cos(pitchRad));
        float vertDist = (float) (distance * Math.sin(pitchRad));

        float camX = targetPosition.x + (float) (horizDist * Math.sin(yawRad));
        float camY = targetPosition.y + vertDist;
        float camZ = targetPosition.z + (float) (horizDist * Math.cos(yawRad));

        camera.position.lerp(new Vector3(camX, camY, camZ), Math.min(1.0f, delta * 8f));
        camera.lookAt(targetPosition);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    public float getYawAngle() {
        return yawAngle;
    }

    public void setYawAngle(float yaw) {
        this.yawAngle = yaw;
    }

    public void rotateYaw(float deltaDegrees) {
        yawAngle = (yawAngle + deltaDegrees) % 360f;
    }

    public void rotateYawSnap(boolean clockwise) {
        float step = clockwise ? 90f : -90f;
        yawAngle = Math.round((yawAngle + step) / 90f) * 90f;
    }

    public void zoom(float amount) {
        distance = Math.max(Constants.CAMERA_MIN_ZOOM, Math.min(Constants.CAMERA_MAX_ZOOM, distance + amount));
    }

    public Vector3 getTargetPosition() {
        return targetPosition;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}

package com.example.arrowmaze3d.game;

import com.badlogic.gdx.graphics.Color;
import com.example.arrowmaze3d.world.Direction;

public class ArrowTile {
    public enum TileType {
        STONE,
        CYAN,
        RED,
        PURPLE,
        GOLD
    }

    public int gridX;
    public int gridY;
    public Direction direction;
    public TileType type;

    // Tap to escape state
    public boolean isEscaped = false;
    public boolean isFlying = false;
    public float flyProgress = 0f; // 0.0 to 1.0
    public float flySpeed = 4.5f;
    public float flyOffsetX = 0f;
    public float flyOffsetY = 0f;

    // Blocked shake effect
    public float shakeTime = 0f;
    public float shakeOffsetX = 0f;
    public float shakeOffsetY = 0f;

    // Visual pulse / hint
    public boolean isHinted = false;
    public float pulseTime = 0f;
    public float rotationAngle = 0f;

    public ArrowTile(int gridX, int gridY, Direction direction, TileType type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.direction = direction;
        this.type = type;
        this.rotationAngle = getDirectionAngle(direction);
    }

    public static float getDirectionAngle(Direction dir) {
        switch (dir) {
            case NORTH: return 0f;
            case EAST: return -90f;
            case SOUTH: return 180f;
            case WEST: return 90f;
            default: return 0f;
        }
    }

    public void update(float delta) {
        if (isFlying) {
            flyProgress += delta * flySpeed;
            float distance = flyProgress * 1400f;
            switch (direction) {
                case NORTH:
                    flyOffsetY = distance;
                    flyOffsetX = 0f;
                    break;
                case SOUTH:
                    flyOffsetY = -distance;
                    flyOffsetX = 0f;
                    break;
                case EAST:
                    flyOffsetX = distance;
                    flyOffsetY = 0f;
                    break;
                case WEST:
                    flyOffsetX = -distance;
                    flyOffsetY = 0f;
                    break;
            }
            if (flyProgress >= 1.0f) {
                isFlying = false;
                isEscaped = true;
            }
        }

        if (shakeTime > 0f) {
            shakeTime -= delta;
            float shakeMagnitude = Math.min(1f, shakeTime * 4f) * 14f;
            // Shake in the blocked direction
            switch (direction) {
                case NORTH:
                case SOUTH:
                    shakeOffsetY = (float) Math.sin(shakeTime * 40f) * shakeMagnitude;
                    shakeOffsetX = 0f;
                    break;
                case EAST:
                case WEST:
                    shakeOffsetX = (float) Math.sin(shakeTime * 40f) * shakeMagnitude;
                    shakeOffsetY = 0f;
                    break;
            }
            if (shakeTime <= 0f) {
                shakeOffsetX = 0f;
                shakeOffsetY = 0f;
            }
        }
    }

    public void triggerShake() {
        shakeTime = 0.35f;
    }

    public void startFlyEscape() {
        isFlying = true;
        flyProgress = 0f;
    }

    public Color getBaseColor() {
        switch (type) {
            case CYAN:
                return new Color(0x0E / 255f, 0x74 / 255f, 0x90 / 255f, 1f); // Dark Teal/Cyan
            case RED:
                return new Color(0x88 / 255f, 0x22 / 255f, 0x1E / 255f, 1f); // Crimson Red
            case PURPLE:
                return new Color(0x58 / 255f, 0x1C / 255f, 0x87 / 255f, 1f); // Royal Purple
            case GOLD:
                return new Color(0xB4 / 255f, 0x82 / 255f, 0x12 / 255f, 1f); // Glowing Amber Gold
            case STONE:
            default:
                return new Color(0x33 / 255f, 0x38 / 255f, 0x3E / 255f, 1f); // Dark Stone Slate
        }
    }

    public Color getArrowGlyphColor() {
        if (shakeTime > 0f) {
            return new Color(1.0f, 0.3f, 0.3f, 1f); // Flash red on block
        }
        if (isHinted) {
            return new Color(0.3f, 1.0f, 0.5f, 1f); // Neon Green for hint
        }
        switch (type) {
            case CYAN:
                return new Color(0xA5 / 255f, 0xF3 / 255f, 0xFC / 255f, 1f); // Light Glowing Cyan
            case RED:
                return new Color(0xFE / 255f, 0xCA / 255f, 0xCA / 255f, 1f); // Light Coral
            case PURPLE:
                return new Color(0xE9 / 255f, 0xD5 / 255f, 0xFF / 255f, 1f); // Light Violet
            case GOLD:
                return new Color(0xFE / 255f, 0xF0 / 255f, 0x8A / 255f, 1f); // Bright Gold
            case STONE:
            default:
                return new Color(0xD6 / 255f, 0xCE / 255f, 0xB9 / 255f, 1f); // Carved Sandstone Cream
        }
    }
}

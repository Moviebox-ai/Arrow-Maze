package com.example.arrowmaze3d.game;

import com.badlogic.gdx.graphics.Color;
import com.example.arrowmaze3d.world.Direction;

public class ArrowTile {
    public enum TileType {
        STONE,
        START_GREEN,
        CYAN,
        RED,
        PURPLE,
        GOLD,
        EXIT_DOOR
    }

    public int gridX;
    public int gridY;
    public Direction direction;
    public TileType type;
    public boolean isVisited = false;
    public boolean isHinted = false;
    public int hintStepIndex = -1;
    public float pulseTime = 0f;
    public float rotationAngle = 0f;
    public float targetRotation = 0f;

    public ArrowTile(int gridX, int gridY, Direction direction, TileType type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.direction = direction;
        this.type = type;
        this.rotationAngle = getDirectionAngle(direction);
        this.targetRotation = this.rotationAngle;
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

    public void rotateClockwise() {
        switch (direction) {
            case NORTH: direction = Direction.EAST; break;
            case EAST: direction = Direction.SOUTH; break;
            case SOUTH: direction = Direction.WEST; break;
            case WEST: direction = Direction.NORTH; break;
        }
        targetRotation = getDirectionAngle(direction);
    }

    public Color getBaseColor() {
        switch (type) {
            case START_GREEN:
                return new Color(0x3B / 255f, 0x82 / 255f, 0x24 / 255f, 1f); // Vibrant Forest Green
            case CYAN:
                return new Color(0x0E / 255f, 0x74 / 255f, 0x90 / 255f, 1f); // Dark Teal/Cyan
            case RED:
                return new Color(0x88 / 255f, 0x22 / 255f, 0x1E / 255f, 1f); // Crimson Red
            case PURPLE:
                return new Color(0x58 / 255f, 0x1C / 255f, 0x87 / 255f, 1f); // Royal Purple
            case GOLD:
                return new Color(0xB4 / 255f, 0x82 / 255f, 0x12 / 255f, 1f); // Glowing Amber Gold
            case EXIT_DOOR:
                return new Color(0x2E / 255f, 0x7D / 255f, 0x32 / 255f, 1f); // Emerald Exit Green
            case STONE:
            default:
                return new Color(0x33 / 255f, 0x38 / 255f, 0x3E / 255f, 1f); // Dark Stone Slate
        }
    }

    public Color getArrowGlyphColor() {
        switch (type) {
            case START_GREEN:
                return new Color(0xD9 / 255f, 0xF9 / 255f, 0x9D / 255f, 1f); // Light Glowing Green
            case CYAN:
                return new Color(0xA5 / 255f, 0xF3 / 255f, 0xFC / 255f, 1f); // Light Glowing Cyan
            case RED:
                return new Color(0xFE / 255f, 0xCA / 255f, 0xCA / 255f, 1f); // Light Coral
            case PURPLE:
                return new Color(0xE9 / 255f, 0xD5 / 255f, 0xFF / 255f, 1f); // Light Violet
            case GOLD:
                return new Color(0xFE / 255f, 0xF0 / 255f, 0x8A / 255f, 1f); // Bright Gold
            case EXIT_DOOR:
                return new Color(0x86 / 255f, 0xEF / 255f, 0xAC / 255f, 1f); // Neon Exit Green
            case STONE:
            default:
                return new Color(0xD6 / 255f, 0xCE / 255f, 0xB9 / 255f, 1f); // Carved Sandstone Cream
        }
    }
}

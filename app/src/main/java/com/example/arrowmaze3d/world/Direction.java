package com.example.arrowmaze3d.world;

import com.example.arrowmaze3d.utilities.Vector3i;

public enum Direction {
    NORTH(0, 0, -1, 0f),
    EAST(1, 0, 0, 270f),
    SOUTH(0, 0, 1, 180f),
    WEST(-1, 0, 0, 90f),
    UP(0, 1, 0, 0f),
    DOWN(0, -1, 0, 0f);

    public final int dx;
    public final int dy;
    public final int dz;
    public final float rotationYDegrees;

    Direction(int dx, int dy, int dz, float rotationYDegrees) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.rotationYDegrees = rotationYDegrees;
    }

    public Direction rotateClockwise() {
        switch (this) {
            case NORTH: return EAST;
            case EAST: return SOUTH;
            case SOUTH: return WEST;
            case WEST: return NORTH;
            default: return this;
        }
    }

    public Direction rotateCounterClockwise() {
        switch (this) {
            case NORTH: return WEST;
            case WEST: return SOUTH;
            case SOUTH: return EAST;
            case EAST: return NORTH;
            default: return this;
        }
    }

    public Direction reverse() {
        switch (this) {
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case UP: return DOWN;
            case DOWN: return UP;
            default: return NORTH;
        }
    }

    public Vector3i getVector() {
        return new Vector3i(dx, dy, dz);
    }

    public static Direction fromString(String str) {
        if (str == null) return NORTH;
        try {
            return Direction.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORTH;
        }
    }
}

package com.example.arrowmaze3d.world;

import com.example.arrowmaze3d.utilities.Vector3i;

public class GridTile {
    public enum TileType {
        EMPTY,
        FLOOR,
        STONE,
        GRASS,
        WATER,
        WALL,
        HOLE
    }

    private final Vector3i gridPosition;
    private TileType type;

    public GridTile(Vector3i gridPosition, TileType type) {
        this.gridPosition = gridPosition.cpy();
        this.type = type;
    }

    public Vector3i getGridPosition() {
        return gridPosition;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isWalkable() {
        return type == TileType.FLOOR || type == TileType.STONE || type == TileType.GRASS || type == TileType.WATER;
    }
}

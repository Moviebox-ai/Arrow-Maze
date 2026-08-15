package com.example.arrowmaze3d.level;

import com.badlogic.gdx.graphics.Color;
import com.example.arrowmaze3d.world.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Robust Grid-based Data Model for Maze Levels.
 * Defines tile types (Arrows, Walls, Portals, Key Doors, Collectibles, Exit Doors)
 * and the core puzzle validation and simulation mechanics.
 */
public class MazeLevelModel {

    // Unique Identifier & Level Metadata
    public String levelId;
    public int levelNumber;
    public String title;
    public int width;
    public int height;

    // Start & Goal Positions
    public int startX;
    public int startY;
    public int exitX;
    public int exitY;

    // The 2D Grid of Maze Tiles [x][y]
    public MazeTile[][] tiles;

    // Level Economy & Star Metrics
    public int parMoves;
    public int threeStarMoves;
    public int rewardCoins;
    public String themeName;

    // Optional Pre-calculated Solution Path (List of [x, y] coordinates)
    public List<int[]> optimalPath;

    /**
     * Enumeration of all interactive and structural tile types.
     */
    public enum TileType {
        EMPTY(false, "Empty Void"),
        FLOOR(true, "Stone Floor"),
        WALL(false, "Impassable Wall"),
        ARROW(true, "Directional Arrow"),
        ROTATABLE_ARROW(true, "Rotatable Arrow"),
        START_PAD(true, "Sanctuary Start"),
        EXIT_DOOR(true, "Castle Exit Door"),
        GOLD_COIN(true, "Sun Medallion Bonus"),
        KEY(true, "Iron Key"),
        LOCKED_DOOR(false, "Iron Gate"),
        TELEPORT(true, "Arcane Rift");

        public final boolean isWalkableByDefault;
        public final String displayName;

        TileType(boolean isWalkableByDefault, String displayName) {
            this.isWalkableByDefault = isWalkableByDefault;
            this.displayName = displayName;
        }
    }

    /**
     * Represents an individual cell / tile inside the maze grid.
     */
    public static class MazeTile {
        public int x;
        public int y;
        public TileType type;
        public Direction direction;
        public Color customColor;

        // Dynamic State Properties
        public boolean isLocked;
        public boolean isVisited;
        public boolean isHinted;
        public int hintStep;
        public int linkedTargetX;
        public int linkedTargetY;
        public int coinsValue;

        public MazeTile(int x, int y, TileType type) {
            this(x, y, type, Direction.NORTH);
        }

        public MazeTile(int x, int y, TileType type, Direction direction) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.direction = direction != null ? direction : Direction.NORTH;
            this.isLocked = (type == TileType.LOCKED_DOOR);
            this.isVisited = false;
            this.isHinted = false;
            this.hintStep = 0;
            this.linkedTargetX = -1;
            this.linkedTargetY = -1;
            this.coinsValue = (type == TileType.GOLD_COIN) ? 50 : 0;
            this.customColor = getDefaultColorForType(type);
        }

        public boolean isWalkable() {
            if (type == TileType.WALL) return false;
            if (type == TileType.LOCKED_DOOR && isLocked) return false;
            return type.isWalkableByDefault;
        }

        public void rotateClockwise() {
            if (type == TileType.ARROW || type == TileType.ROTATABLE_ARROW) {
                this.direction = this.direction.rotateClockwise();
            }
        }

        public void rotateCounterClockwise() {
            if (type == TileType.ARROW || type == TileType.ROTATABLE_ARROW) {
                this.direction = this.direction.rotateCounterClockwise();
            }
        }

        public Color getDefaultColorForType(TileType t) {
            switch (t) {
                case START_PAD:
                    return new Color(0x10 / 255f, 0xB9 / 255f, 0x81 / 255f, 1f); // Emerald
                case EXIT_DOOR:
                    return new Color(0x22 / 255f, 0xC5 / 255f, 0x5E / 255f, 1f); // Neon Green
                case GOLD_COIN:
                    return new Color(0xFB / 255f, 0xBF / 255f, 0x24 / 255f, 1f); // Sun Amber
                case WALL:
                    return new Color(0x1F / 255f, 0x24 / 255f, 0x2D / 255f, 1f); // Wrought Slate
                case LOCKED_DOOR:
                    return new Color(0xEF / 255f, 0x44 / 255f, 0x44 / 255f, 1f); // Crimson Lock
                case KEY:
                    return new Color(0xF5 / 255f, 0x9E / 255f, 0x0B / 255f, 1f); // Golden Key
                case TELEPORT:
                    return new Color(0x8B / 255f, 0x5C / 255f, 0xF6 / 255f, 1f); // Arcane Purple
                case FLOOR:
                    return new Color(0x37 / 255f, 0x41 / 255f, 0x51 / 255f, 1f); // Granite Floor
                case ARROW:
                case ROTATABLE_ARROW:
                default:
                    return new Color(0x38 / 255f, 0xBD / 255f, 0xF8 / 255f, 1f); // Radiant Cyan
            }
        }
    }

    public MazeLevelModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new MazeTile[width][height];
        this.optimalPath = new ArrayList<>();
        this.parMoves = width * height;
        this.threeStarMoves = width + height;
        this.rewardCoins = 100;
        this.themeName = "ANCIENT_DUNGEON";

        // Initialize with default floor tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new MazeTile(x, y, TileType.FLOOR);
            }
        }
    }

    /**
     * Helper to retrieve tile safely with boundary checks.
     */
    public MazeTile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return tiles[x][y];
    }

    /**
     * Sets a tile in the grid and updates start/exit positions if applicable.
     */
    public void setTile(int x, int y, MazeTile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
            if (tile.type == TileType.START_PAD) {
                this.startX = x;
                this.startY = y;
            } else if (tile.type == TileType.EXIT_DOOR) {
                this.exitX = x;
                this.exitY = y;
            }
        }
    }

    /**
     * Verifies if moving from (fromX, fromY) to (toX, toY) is mechanically valid.
     */
    public boolean canTraverse(int fromX, int fromY, int toX, int toY) {
        MazeTile from = getTile(fromX, fromY);
        MazeTile to = getTile(toX, toY);

        if (from == null || to == null) return false;
        if (!to.isWalkable()) return false;

        int dx = toX - fromX;
        int dy = toY - fromY;

        // If 'from' tile has an arrow constraint, check directional alignment
        if (from.type == TileType.ARROW || from.type == TileType.ROTATABLE_ARROW) {
            switch (from.direction) {
                case NORTH: if (dx == 0 && dy < 0) return true; break;
                case SOUTH: if (dx == 0 && dy > 0) return true; break;
                case EAST:  if (dy == 0 && dx > 0) return true; break;
                case WEST:  if (dy == 0 && dx < 0) return true; break;
            }
        }

        // Direct 1-step orthogonal adjacency
        return (Math.abs(dx) + Math.abs(dy) == 1);
    }
}

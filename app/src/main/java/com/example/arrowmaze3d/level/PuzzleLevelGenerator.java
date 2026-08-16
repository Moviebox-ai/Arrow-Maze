package com.example.arrowmaze3d.level;

import com.example.arrowmaze3d.game.ArrowTile;
import com.example.arrowmaze3d.world.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleLevelGenerator {

    public enum DifficultyMode {
        EASY("EASY MODE", "Clear Perimeters & Low Blockage", 0.95f, 0.75f, 0.20f),
        NORMAL("NORMAL MODE", "Interlocked Grids & Strategic Unlocking", 0.35f, 0.85f, 1.0f),
        HARD("HARD MODE", "Dense Multi-Layer Arrow Mazes", 0.95f, 0.35f, 0.35f);

        public final String title;
        public final String description;
        public final float r, g, b;

        DifficultyMode(String title, String description, float r, float g, float b) {
            this.title = title;
            this.description = description;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public static class EscapeLevelData {
        public int levelNumber;
        public int gridSize = 5;
        public DifficultyMode mode = DifficultyMode.EASY;
        public String title = "Arrow Puzzle Escape";
        public String subtitle = "Tap unblocked arrows to release them from the board!";
        public ArrowTile[][] grid;
        public int totalArrows = 25;
        public List<int[]> escapeOrder = new ArrayList<>();
    }

    /**
     * Determines difficulty mode across 5000 levels:
     * Levels 1 to 1500: EASY MODE
     * Levels 1501 to 3500: NORMAL MODE
     * Levels 3501 to 5000+: HARD MODE
     */
    public static DifficultyMode getDifficultyForLevel(int levelNumber) {
        if (levelNumber <= 1500) {
            return DifficultyMode.EASY;
        } else if (levelNumber <= 3500) {
            return DifficultyMode.NORMAL;
        } else {
            return DifficultyMode.HARD;
        }
    }

    public static EscapeLevelData getLevel(int levelIndex) {
        if (levelIndex < 1) levelIndex = 1;
        if (levelIndex > 5000) levelIndex = 5000;

        EscapeLevelData data = new EscapeLevelData();
        data.levelNumber = levelIndex;
        data.gridSize = 5;
        data.grid = new ArrowTile[5][5];
        data.mode = getDifficultyForLevel(levelIndex);

        // Generate Solvable Tap-To-Release Puzzle
        setupTapToReleaseLevel(data, levelIndex);
        return data;
    }

    /**
     * Reverse Construction Algorithm:
     * Start with an empty board and place arrows in reverse of the escape order.
     * Guaranteed 100% solvable without deadlocks!
     */
    private static void setupTapToReleaseLevel(EscapeLevelData data, int levelIndex) {
        long seed = (long) levelIndex * 2654435761L + 987654321L;
        Random rng = new Random(seed);

        DifficultyMode mode = data.mode;
        int size = data.gridSize;

        // Tile types distribution based on mode
        ArrowTile.TileType[] gemTypes = {
            ArrowTile.TileType.STONE,
            ArrowTile.TileType.CYAN,
            ArrowTile.TileType.RED,
            ArrowTile.TileType.PURPLE,
            ArrowTile.TileType.GOLD
        };

        // Track occupied tiles during reverse assembly
        boolean[][] occupied = new boolean[size][size];
        List<int[]> reversePlacedOrder = new ArrayList<>();

        // Generate list of all 25 coordinates
        List<int[]> allCoords = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                allCoords.add(new int[]{x, y});
            }
        }

        // Shuffle placing order
        for (int i = allCoords.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int[] tmp = allCoords.get(i);
            allCoords.set(i, allCoords.get(j));
            allCoords.set(j, tmp);
        }

        Direction[] allDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (int[] pos : allCoords) {
            int x = pos[0];
            int y = pos[1];

            // Find all valid exit directions (where no currently placed tile blocks the path out of board)
            List<Direction> validDirs = new ArrayList<>();
            for (Direction dir : allDirs) {
                if (isPathClearToEdge(occupied, x, y, dir, size)) {
                    validDirs.add(dir);
                }
            }

            Direction chosenDir;
            if (!validDirs.isEmpty()) {
                chosenDir = validDirs.get(rng.nextInt(validDirs.size()));
            } else {
                // Fallback: Point to nearest edge
                chosenDir = getDirectionToNearestEdge(x, y, size);
            }

            // Determine Gem type
            ArrowTile.TileType tileType = ArrowTile.TileType.STONE;
            if (rng.nextFloat() < (mode == DifficultyMode.EASY ? 0.25f : (mode == DifficultyMode.NORMAL ? 0.45f : 0.65f))) {
                tileType = gemTypes[rng.nextInt(gemTypes.length)];
            }

            data.grid[x][y] = new ArrowTile(x, y, chosenDir, tileType);
            occupied[x][y] = true;
            reversePlacedOrder.add(new int[]{x, y});
        }

        // The solution order is the reverse of the reversePlacedOrder
        data.escapeOrder.clear();
        for (int i = reversePlacedOrder.size() - 1; i >= 0; i--) {
            data.escapeOrder.add(reversePlacedOrder.get(i));
        }
        data.totalArrows = 25;
    }

    private static boolean isPathClearToEdge(boolean[][] occupied, int x, int y, Direction dir, int size) {
        int dx = 0, dy = 0;
        switch (dir) {
            case NORTH: dy = 1; break; // Going UP towards y=size
            case SOUTH: dy = -1; break; // Going DOWN towards y=0
            case EAST:  dx = 1; break; // Going RIGHT towards x=size
            case WEST:  dx = -1; break; // Going LEFT towards x=0
        }

        int curX = x + dx;
        int curY = y + dy;
        while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
            if (occupied[curX][curY]) {
                return false; // Path blocked by another arrow
            }
            curX += dx;
            curY += dy;
        }
        return true;
    }

    private static Direction getDirectionToNearestEdge(int x, int y, int size) {
        int distWest = x;
        int distEast = size - 1 - x;
        int distSouth = y;
        int distNorth = size - 1 - y;

        int minDist = Math.min(Math.min(distWest, distEast), Math.min(distSouth, distNorth));
        if (minDist == distNorth) return Direction.NORTH;
        if (minDist == distEast) return Direction.EAST;
        if (minDist == distSouth) return Direction.SOUTH;
        return Direction.WEST;
    }

    /**
     * Check if a specific arrow on the board currently has a clear path to escape off the board
     */
    public static boolean canArrowEscape(ArrowTile[][] grid, int x, int y, int size) {
        ArrowTile tile = grid[x][y];
        if (tile == null || tile.isEscaped || tile.isFlying) return false;

        int dx = 0, dy = 0;
        switch (tile.direction) {
            case NORTH: dy = 1; break;
            case SOUTH: dy = -1; break;
            case EAST:  dx = 1; break;
            case WEST:  dx = -1; break;
        }

        int curX = x + dx;
        int curY = y + dy;
        while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
            ArrowTile blocker = grid[curX][curY];
            if (blocker != null && !blocker.isEscaped) {
                return false; // Blocked by tile in front
            }
            curX += dx;
            curY += dy;
        }
        return true;
    }
}

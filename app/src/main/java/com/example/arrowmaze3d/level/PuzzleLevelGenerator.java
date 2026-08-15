package com.example.arrowmaze3d.level;

import com.example.arrowmaze3d.game.ArrowTile;
import com.example.arrowmaze3d.world.Direction;
import java.util.ArrayList;
import java.util.List;

public class PuzzleLevelGenerator {

    public static class EscapeLevelData {
        public int levelNumber;
        public int gridSize = 5;
        public String title = "Arrow Puzzle Escape";
        public String subtitle = "Solve the arrow puzzle to open the door";
        public int startX = 0;
        public int startY = 1;
        public int exitX = 4;
        public int exitY = 4;
        public ArrowTile[][] grid;
        public List<int[]> solutionPath = new ArrayList<>();
    }

    public static EscapeLevelData getLevel(int levelIndex) {
        EscapeLevelData data = new EscapeLevelData();
        data.levelNumber = levelIndex;
        data.gridSize = 5;
        data.grid = new ArrowTile[5][5];

        // Specific Level 15 (Exact layout from the User's Screenshot)
        if (levelIndex == 15) {
            setupLevel15(data);
            return data;
        }

        // Procedural & Handcrafted levels 1 to 50
        setupProceduralLevel(data, levelIndex);
        return data;
    }

    private static void setupLevel15(EscapeLevelData data) {
        data.startX = 0;
        data.startY = 1;
        data.exitX = 4;
        data.exitY = 4;

        // Row 0 (Y=0, Top in screen display)
        data.grid[0][0] = new ArrowTile(0, 0, Direction.EAST, ArrowTile.TileType.STONE);
        data.grid[1][0] = new ArrowTile(1, 0, Direction.NORTH, ArrowTile.TileType.CYAN);
        data.grid[2][0] = new ArrowTile(2, 0, Direction.EAST, ArrowTile.TileType.STONE);
        data.grid[3][0] = new ArrowTile(3, 0, Direction.EAST, ArrowTile.TileType.RED);
        data.grid[4][0] = new ArrowTile(4, 0, Direction.SOUTH, ArrowTile.TileType.STONE);

        // Row 1 (Y=1)
        data.grid[0][1] = new ArrowTile(0, 1, Direction.NORTH, ArrowTile.TileType.START_GREEN);
        data.grid[1][1] = new ArrowTile(1, 1, Direction.WEST, ArrowTile.TileType.STONE);
        data.grid[2][1] = new ArrowTile(2, 1, Direction.SOUTH, ArrowTile.TileType.STONE);
        data.grid[3][1] = new ArrowTile(3, 1, Direction.SOUTH, ArrowTile.TileType.STONE);
        data.grid[4][1] = new ArrowTile(4, 1, Direction.WEST, ArrowTile.TileType.STONE);

        // Row 2 (Y=2)
        data.grid[0][2] = new ArrowTile(0, 2, Direction.NORTH, ArrowTile.TileType.STONE);
        data.grid[1][2] = new ArrowTile(1, 2, Direction.WEST, ArrowTile.TileType.PURPLE);
        data.grid[2][2] = new ArrowTile(2, 2, Direction.EAST, ArrowTile.TileType.GOLD);
        data.grid[3][2] = new ArrowTile(3, 2, Direction.WEST, ArrowTile.TileType.STONE);
        data.grid[4][2] = new ArrowTile(4, 2, Direction.NORTH, ArrowTile.TileType.STONE);

        // Row 3 (Y=3)
        data.grid[0][3] = new ArrowTile(0, 3, Direction.SOUTH, ArrowTile.TileType.STONE);
        data.grid[1][3] = new ArrowTile(1, 3, Direction.SOUTH, ArrowTile.TileType.STONE);
        data.grid[2][3] = new ArrowTile(2, 3, Direction.EAST, ArrowTile.TileType.STONE);
        data.grid[3][3] = new ArrowTile(3, 3, Direction.NORTH, ArrowTile.TileType.CYAN);
        data.grid[4][3] = new ArrowTile(4, 3, Direction.EAST, ArrowTile.TileType.STONE);

        // Row 4 (Y=4, Bottom in screen display)
        data.grid[0][4] = new ArrowTile(0, 4, Direction.WEST, ArrowTile.TileType.STONE);
        data.grid[1][4] = new ArrowTile(1, 4, Direction.SOUTH, ArrowTile.TileType.RED);
        data.grid[2][4] = new ArrowTile(2, 4, Direction.WEST, ArrowTile.TileType.STONE);
        data.grid[3][4] = new ArrowTile(3, 4, Direction.SOUTH, ArrowTile.TileType.STONE);
        data.grid[4][4] = new ArrowTile(4, 4, Direction.EAST, ArrowTile.TileType.EXIT_DOOR);

        // Level 15 Solution Path:
        // (0,1) -> (0,0) -> (2,0) -> (3,0) -> (4,0) -> (4,1) -> (4,2) ... -> (4,4)
        data.solutionPath.clear();
        data.solutionPath.add(new int[]{0, 1});
        data.solutionPath.add(new int[]{0, 0});
        data.solutionPath.add(new int[]{2, 0});
        data.solutionPath.add(new int[]{3, 0});
        data.solutionPath.add(new int[]{4, 0});
        data.solutionPath.add(new int[]{4, 1});
        data.solutionPath.add(new int[]{2, 1});
        data.solutionPath.add(new int[]{2, 2});
        data.solutionPath.add(new int[]{3, 2});
        data.solutionPath.add(new int[]{3, 3});
        data.solutionPath.add(new int[]{4, 3});
        data.solutionPath.add(new int[]{4, 4});
    }

    private static void setupProceduralLevel(EscapeLevelData data, int levelIndex) {
        int seed = levelIndex * 1337 + 42;
        data.startX = 0;
        data.startY = (levelIndex % 3 == 0) ? 0 : 1;
        data.exitX = 4;
        data.exitY = 4;

        Direction[] allDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                int pseudo = (seed + x * 31 + y * 71) % 4;
                Direction dir = allDirs[Math.abs(pseudo)];
                ArrowTile.TileType type = ArrowTile.TileType.STONE;

                if (x == data.startX && y == data.startY) {
                    type = ArrowTile.TileType.START_GREEN;
                    dir = (data.startY == 0) ? Direction.EAST : Direction.NORTH;
                } else if (x == data.exitX && y == data.exitY) {
                    type = ArrowTile.TileType.EXIT_DOOR;
                    dir = Direction.EAST;
                } else if (x == 2 && y == 2) {
                    type = ArrowTile.TileType.GOLD;
                    dir = Direction.EAST;
                } else if ((x == 1 && y == 0) || (x == 3 && y == 3)) {
                    type = ArrowTile.TileType.CYAN;
                } else if ((x == 3 && y == 0) || (x == 1 && y == 4)) {
                    type = ArrowTile.TileType.RED;
                } else if (x == 1 && y == 2) {
                    type = ArrowTile.TileType.PURPLE;
                }

                data.grid[x][y] = new ArrowTile(x, y, dir, type);
            }
        }

        // Build a guaranteed solution path
        data.solutionPath.clear();
        int curX = data.startX;
        int curY = data.startY;
        data.solutionPath.add(new int[]{curX, curY});

        while (curX != data.exitX || curY != data.exitY) {
            if (curX < data.exitX && (curY == data.exitY || ((curX + curY + seed) % 2 == 0))) {
                data.grid[curX][curY].direction = Direction.EAST;
                curX++;
            } else if (curY < data.exitY) {
                data.grid[curX][curY].direction = Direction.SOUTH;
                curY++;
            } else if (curX < data.exitX) {
                data.grid[curX][curY].direction = Direction.EAST;
                curX++;
            } else {
                break;
            }
            data.solutionPath.add(new int[]{curX, curY});
        }
        data.grid[data.exitX][data.exitY].direction = Direction.EAST;
    }
}

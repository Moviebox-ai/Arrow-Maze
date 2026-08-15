package com.example.arrowmaze3d.level;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    public String id;
    public int worldIndex = 1;
    public int levelIndex = 1;
    public String name = "Level";
    public int difficulty = 1;
    public String theme = "DIRECTIONAL_TEMPLE";

    public int sizeX = 7;
    public int sizeY = 1;
    public int sizeZ = 7;

    public int threeStarMoves = 12;
    public int twoStarMoves = 18;
    public int maxAllowedMoves = 25;

    public PosData playerSpawn = new PosData(1, 0, 1, "NORTH");
    public PosData goalLocation = new PosData(5, 0, 5, "NORTH");

    // Optional custom tile layout: 'S'=STONE, 'G'=GRASS, 'W'=WATER, '#' = WALL, '.' = STONE
    public List<String> tileMap = new ArrayList<>();

    public List<ObjectData> objects = new ArrayList<>();

    public static class PosData {
        public int x;
        public int y;
        public int z;
        public String dir = "NORTH";

        public PosData() {}

        public PosData(int x, int y, int z, String dir) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dir = dir;
        }
    }

    public static class ObjectData {
        public String id;
        public String type; // ARROW, ROTATABLE_ARROW, REVERSIBLE_ARROW, SPLIT_ARROW, SPIKES, KEY, LOCK_GATE, STAR, SWITCH, GATE, TELEPORT
        public String color = "GREEN"; // GREEN, RED, BLUE, ORANGE, PURPLE
        public int x;
        public int y;
        public int z;
        public String direction = "NORTH";
        public String linkedId = "";
        public boolean isOpen = false;
        public PosData targetPos = null;

        public ObjectData() {}

        public ObjectData(String id, String type, int x, int y, int z, String direction, String color) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.direction = direction;
            this.color = color;
        }
    }
}

package com.example.arrowmaze3d.level;

import com.example.arrowmaze3d.utilities.Constants;
import java.util.Arrays;

public class LevelRepository {

    public static LevelData generateProceduralLevel(int world, int level) {
        LevelData data = new LevelData();
        data.id = "w" + world + "_l" + level;
        data.worldIndex = world;
        data.levelIndex = level;

        data.sizeX = 7;
        data.sizeY = 1;
        data.sizeZ = 7;
        data.theme = Constants.THEME_TEMPLE;
        data.name = "Sky Maze " + level;

        // Custom Tile Map: S=Stone, G=Grass, W=Water, #=Wall
        data.tileMap = Arrays.asList(
            "#######",
            "#SGGSS#",
            "#SSGSG#",
            "#SGSGS#",
            "#GSSGS#",
            "#SSGSP#",
            "#######"
        );

        data.playerSpawn = new LevelData.PosData(1, 0, 1, "SOUTH");
        data.goalLocation = new LevelData.PosData(5, 0, 5, "NORTH");

        // 1. Spikes
        LevelData.ObjectData spike1 = new LevelData.ObjectData("spk_1", "SPIKES", 2, 0, 4, "NORTH", "STEEL");
        LevelData.ObjectData spike2 = new LevelData.ObjectData("spk_2", "SPIKES", 4, 0, 2, "NORTH", "STEEL");
        data.objects.add(spike1);
        data.objects.add(spike2);

        // 2. 24k Golden Key and Lock Gate
        LevelData.ObjectData key = new LevelData.ObjectData("key_1", "KEY", 3, 0, 3, "NORTH", "GOLD");
        LevelData.ObjectData lockGate = new LevelData.ObjectData("lock_1", "LOCK_GATE", 4, 0, 5, "NORTH", "GOLD");
        data.objects.add(key);
        data.objects.add(lockGate);

        // 3. Bonus Star
        LevelData.ObjectData star = new LevelData.ObjectData("star_1", "STAR", 5, 0, 1, "NORTH", "GOLD");
        data.objects.add(star);

        // 4. Color-Coded Jewel Arrow Blocks
        // Green SOUTH arrow at (1, 1)
        LevelData.ObjectData arrGreen1 = new LevelData.ObjectData("arr_g1", "ARROW", 1, 0, 1, "SOUTH", "GREEN");
        data.objects.add(arrGreen1);

        // Red EAST arrow at (1, 5)
        LevelData.ObjectData arrRed = new LevelData.ObjectData("arr_r1", "ARROW", 1, 0, 5, "EAST", "RED");
        data.objects.add(arrRed);

        // Blue NORTH arrow at (3, 5) - Rotatable
        LevelData.ObjectData arrBlue = new LevelData.ObjectData("arr_b1", "ROTATABLE_ARROW", 3, 0, 5, "NORTH", "BLUE");
        data.objects.add(arrBlue);

        // Orange EAST arrow at (3, 3) where the key is
        LevelData.ObjectData arrOrange = new LevelData.ObjectData("arr_o1", "ARROW", 3, 0, 3, "EAST", "ORANGE");
        data.objects.add(arrOrange);

        // Purple SOUTH arrow at (5, 3) leading to gate & portal
        LevelData.ObjectData arrPurple = new LevelData.ObjectData("arr_p1", "ARROW", 5, 0, 3, "SOUTH", "PURPLE");
        data.objects.add(arrPurple);

        // Additional obstacles for higher levels
        if (level > 2) {
            LevelData.ObjectData sw = new LevelData.ObjectData("sw_1", "SWITCH", 5, 0, 1, "NORTH", "CYAN");
            sw.linkedId = "gate_1";
            data.objects.add(sw);

            LevelData.ObjectData gate = new LevelData.ObjectData("gate_1", "GATE", 3, 0, 1, "NORTH", "GATE");
            gate.isOpen = false;
            data.objects.add(gate);
        }

        data.threeStarMoves = 10;
        data.twoStarMoves = 16;
        data.maxAllowedMoves = 30;

        return data;
    }
}

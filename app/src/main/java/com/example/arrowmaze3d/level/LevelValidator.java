package com.example.arrowmaze3d.level;

import com.example.arrowmaze3d.utilities.AppLogger;

public class LevelValidator {

    public static boolean validate(LevelData levelData) {
        if (levelData == null) {
            AppLogger.d("LevelValidator: levelData is null");
            return false;
        }

        if (levelData.sizeX <= 0 || levelData.sizeZ <= 0) {
            AppLogger.d("LevelValidator: invalid grid dimensions");
            return false;
        }

        if (levelData.playerSpawn == null || levelData.goalLocation == null) {
            AppLogger.d("LevelValidator: missing spawn or goal location");
            return false;
        }

        return true;
    }
}

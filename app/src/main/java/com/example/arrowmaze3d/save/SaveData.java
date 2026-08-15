package com.example.arrowmaze3d.save;

import java.util.HashMap;
import java.util.Map;

public class SaveData {
    public int highestUnlockedWorld = 1;
    public int coins = 2450;
    public int currentLevel = 15;
    public Map<String, Integer> levelStars = new HashMap<>();
    public Map<String, Integer> bestMoves = new HashMap<>();

    public int getStarsForLevel(int world, int level) {
        String key = "w" + world + "_l" + level;
        Integer stars = levelStars.get(key);
        return stars != null ? stars : 0;
    }

    public void setStarsForLevel(int world, int level, int stars) {
        String key = "w" + world + "_l" + level;
        int current = getStarsForLevel(world, level);
        if (stars > current) {
            levelStars.put(key, stars);
        }
    }

    public int getBestMovesForLevel(int world, int level) {
        String key = "w" + world + "_l" + level;
        Integer moves = bestMoves.get(key);
        return moves != null ? moves : 0;
    }

    public void setBestMovesForLevel(int world, int level, int moves) {
        String key = "w" + world + "_l" + level;
        int current = getBestMovesForLevel(world, level);
        if (current == 0 || moves < current) {
            bestMoves.put(key, moves);
        }
    }

    public boolean isLevelUnlocked(int world, int level) {
        if (world < highestUnlockedWorld) return true;
        if (world > highestUnlockedWorld) return false;
        if (level == 1) return true;
        // Level is unlocked if previous level in same world was completed (earned >= 1 star)
        return getStarsForLevel(world, level - 1) > 0;
    }
}

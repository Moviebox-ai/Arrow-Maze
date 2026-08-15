package com.example.arrowmaze3d.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.example.arrowmaze3d.settings.GameSettings;
import com.example.arrowmaze3d.utilities.Constants;
import java.util.Map;

public class SaveManager {
    private final Preferences prefs;
    private final SaveData saveData;
    private final GameSettings settings;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        saveData = new SaveData();
        settings = new GameSettings();
        load();
    }

    public void load() {
        saveData.highestUnlockedWorld = prefs.getInteger("highestUnlockedWorld", 1);
        saveData.coins = prefs.getInteger("coins", 2450);
        saveData.currentLevel = prefs.getInteger("currentLevel", 15);
        settings.setMusicVolume(prefs.getFloat("musicVolume", 0.8f));
        settings.setSfxVolume(prefs.getFloat("sfxVolume", 1.0f));
        settings.setSoundEnabled(prefs.getBoolean("soundEnabled", true));
        settings.setMusicEnabled(prefs.getBoolean("musicEnabled", true));
        settings.setHapticsEnabled(prefs.getBoolean("hapticsEnabled", true));
        settings.setGraphicsQuality(prefs.getString("graphicsQuality", "HIGH"));

        // Load level stats for worlds 1..4, levels 1..10
        for (int w = 1; w <= 4; w++) {
            for (int l = 1; l <= 10; l++) {
                String key = "w" + w + "_l" + l;
                int stars = prefs.getInteger("stars_" + key, 0);
                int moves = prefs.getInteger("moves_" + key, 0);
                if (stars > 0) saveData.levelStars.put(key, stars);
                if (moves > 0) saveData.bestMoves.put(key, moves);
            }
        }
    }

    public void save() {
        prefs.putInteger("highestUnlockedWorld", saveData.highestUnlockedWorld);
        prefs.putInteger("coins", saveData.coins);
        prefs.putInteger("currentLevel", saveData.currentLevel);
        prefs.putFloat("musicVolume", settings.getMusicVolume());
        prefs.putFloat("sfxVolume", settings.getSfxVolume());
        prefs.putBoolean("soundEnabled", settings.isSoundEnabled());
        prefs.putBoolean("musicEnabled", settings.isMusicEnabled());
        prefs.putBoolean("hapticsEnabled", settings.isHapticsEnabled());
        prefs.putString("graphicsQuality", settings.getGraphicsQuality());

        for (Map.Entry<String, Integer> entry : saveData.levelStars.entrySet()) {
            prefs.putInteger("stars_" + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : saveData.bestMoves.entrySet()) {
            prefs.putInteger("moves_" + entry.getKey(), entry.getValue());
        }

        prefs.flush();
    }

    public SaveData getSaveData() {
        return saveData;
    }

    public GameSettings getSettings() {
        return settings;
    }
}

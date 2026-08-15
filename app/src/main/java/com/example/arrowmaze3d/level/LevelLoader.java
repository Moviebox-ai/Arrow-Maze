package com.example.arrowmaze3d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.example.arrowmaze3d.utilities.AppLogger;

public class LevelLoader {
    private final Json json = new Json();

    public LevelData loadLevel(int world, int level) {
        String path = "levels/world_0" + world + "/level_0" + (level < 10 ? "0" + level : level) + ".json";
        FileHandle file = Gdx.files.internal(path);

        if (file.exists()) {
            try {
                LevelData data = json.fromJson(LevelData.class, file);
                if (LevelValidator.validate(data)) {
                    return data;
                }
            } catch (Exception e) {
                AppLogger.e("Failed to parse level JSON from " + path + ", generating fallback", e);
            }
        }

        // Generate procedural level if JSON file is missing or invalid
        return LevelRepository.generateProceduralLevel(world, level);
    }
}

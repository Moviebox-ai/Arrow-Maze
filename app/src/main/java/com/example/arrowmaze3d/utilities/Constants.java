package com.example.arrowmaze3d.utilities;

public class Constants {
    public static final String GAME_NAME = "Arrow Maze 3D";
    public static final String VERSION = "1.0.0";

    // Rendering Defaults
    public static final float VIRTUAL_WIDTH = 1080f;
    public static final float VIRTUAL_HEIGHT = 1920f;
    public static final float GRID_CELL_SIZE = 2.0f;
    public static final float GRID_ELEVATION_STEP = 1.5f;

    // Camera Defaults
    public static final float CAMERA_DEFAULT_FOV = 60f;
    public static final float CAMERA_NEAR = 0.5f;
    public static final float CAMERA_FAR = 150f;
    public static final float CAMERA_MIN_ZOOM = 8f;
    public static final float CAMERA_MAX_ZOOM = 40f;

    // Preferences Key
    public static final String PREFS_NAME = "ArrowMaze3D_Preferences";

    // World Themes
    public static final String THEME_TEMPLE = "DIRECTIONAL_TEMPLE";
    public static final String THEME_CITADEL = "CLOCKWORK_CITADEL";
    public static final String THEME_CRYSTAL = "CRYSTAL_LABYRINTH";
    public static final String THEME_OBSERVATORY = "GRAVITY_OBSERVATORY";
}

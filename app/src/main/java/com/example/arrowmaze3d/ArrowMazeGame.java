package com.example.arrowmaze3d;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.example.arrowmaze3d.assets.GameAssetManager;
import com.example.arrowmaze3d.audio.AudioManager;
import com.example.arrowmaze3d.camera.TacticalCameraController;
import com.example.arrowmaze3d.rendering.RenderEngine3D;
import com.example.arrowmaze3d.save.SaveManager;
import com.example.arrowmaze3d.screens.ScreenManager;
import com.example.arrowmaze3d.utilities.AppLogger;

public class ArrowMazeGame extends Game {
    private ScreenManager screenManager;
    private GameAssetManager assetManager;
    private SaveManager saveManager;
    private AudioManager audioManager;
    private RenderEngine3D renderEngine;
    private TacticalCameraController cameraController;

    @Override
    public void create() {
        AppLogger.d("Initializing Arrow Maze 3D Core Systems...");

        assetManager = new GameAssetManager();
        saveManager = new SaveManager();
        audioManager = new AudioManager(saveManager.getSettings());

        PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 15f, 15f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        cameraController = new TacticalCameraController(camera);
        renderEngine = new RenderEngine3D();
        screenManager = new ScreenManager(this);

        screenManager.showSplash();
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public GameAssetManager getAssetManager() {
        return assetManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public AudioManager getAudio() {
        return audioManager;
    }

    public RenderEngine3D getRenderEngine() {
        return renderEngine;
    }

    public TacticalCameraController getCameraController() {
        return cameraController;
    }

    @Override
    public void dispose() {
        if (screenManager != null && getScreen() != null) {
            getScreen().hide();
        }
        if (renderEngine != null) renderEngine.dispose();
        if (audioManager != null) audioManager.dispose();
        if (assetManager != null) assetManager.dispose();
        super.dispose();
    }
}

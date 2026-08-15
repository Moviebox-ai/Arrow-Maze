package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.Screen;
import com.example.arrowmaze3d.ArrowMazeGame;

public class ScreenManager {
    private final ArrowMazeGame game;

    public ScreenManager(ArrowMazeGame game) {
        this.game = game;
    }

    public void showSplash() {
        game.setScreen(new SplashScreen(game));
    }

    public void showMainMenu() {
        game.setScreen(new MainMenuScreen(game));
    }

    public void showWorldSelect() {
        game.setScreen(new WorldSelectScreen(game));
    }

    public void showLevelSelect(int worldIndex) {
        game.setScreen(new LevelSelectScreen(game, worldIndex));
    }

    public void showGameplay(int worldIndex, int levelIndex) {
        game.setScreen(new ArrowEscapeScreen(game, levelIndex));
    }

    public void showArrowEscape(int levelNumber) {
        game.setScreen(new ArrowEscapeScreen(game, levelNumber));
    }

    public void showSettings() {
        game.setScreen(new SettingsScreen(game));
    }
}

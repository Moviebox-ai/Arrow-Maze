package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.arrowmaze3d.ArrowMazeGame;

public class MainMenuScreen extends BaseScreen {

    public MainMenuScreen(ArrowMazeGame game) {
        super(game);
        setupUI();
    }

    private void setupUI() {
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label title = new Label("ARROW PUZZLE ESCAPE", skin, "title");
        Label subtitle = new Label("Solve the arrow puzzle to open the door", skin, "small");

        int currentLevel = game.getSaveManager().getSaveData().currentLevel;
        if (currentLevel < 1) currentLevel = 1;
        com.example.arrowmaze3d.level.PuzzleLevelGenerator.DifficultyMode mode = 
            com.example.arrowmaze3d.level.PuzzleLevelGenerator.getDifficultyForLevel(currentLevel);

        final int targetLevel = currentLevel;
        TextButton playBtn = new TextButton("PLAY LEVEL " + currentLevel + "\n[" + mode.title + "]", skin, "gold");
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                game.getScreenManager().showArrowEscape(targetLevel);
            }
        });

        TextButton continueBtn = new TextButton("SELECT LEVEL (1 - 5000)", skin, "default");
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                game.getScreenManager().showLevelSelect(targetLevel);
            }
        });

        TextButton settingsBtn = new TextButton("SETTINGS", skin, "default");
        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                game.getScreenManager().showSettings();
            }
        });

        root.add(title).padBottom(10).row();
        root.add(subtitle).padBottom(100).row();
        root.add(playBtn).width(600).height(130).padBottom(30).row();
        root.add(continueBtn).width(600).height(120).padBottom(30).row();
        root.add(settingsBtn).width(600).height(120).row();

        stage.addActor(root);
    }
}

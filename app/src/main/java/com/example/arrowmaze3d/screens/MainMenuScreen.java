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

        TextButton playBtn = new TextButton("PLAY LEVEL 15", skin, "gold");
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                int currentLevel = game.getSaveManager().getSaveData().currentLevel;
                game.getScreenManager().showArrowEscape(currentLevel > 0 ? currentLevel : 15);
            }
        });

        TextButton continueBtn = new TextButton("SELECT LEVEL", skin, "default");
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                game.getScreenManager().showLevelSelect(1);
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

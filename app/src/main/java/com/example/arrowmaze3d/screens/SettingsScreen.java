package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.settings.GameSettings;

public class SettingsScreen extends BaseScreen {

    public SettingsScreen(ArrowMazeGame game) {
        super(game);
        setupUI();
    }

    private void setupUI() {
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(80);

        Label title = new Label("SETTINGS", skin, "title");
        root.add(title).padBottom(80).row();

        final GameSettings settings = game.getSaveManager().getSettings();

        // Sound Effects Toggle
        final TextButton sfxBtn = new TextButton("SOUND SFX: " + (settings.isSoundEnabled() ? "ON" : "OFF"), skin, "default");
        sfxBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !settings.isSoundEnabled();
                settings.setSoundEnabled(newState);
                sfxBtn.setText("SOUND SFX: " + (newState ? "ON" : "OFF"));
                game.getSaveManager().save();
                if (newState) game.getAudio().playTrigger();
            }
        });

        // Music Toggle
        final TextButton musicBtn = new TextButton("MUSIC: " + (settings.isMusicEnabled() ? "ON" : "OFF"), skin, "default");
        musicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !settings.isMusicEnabled();
                settings.setMusicEnabled(newState);
                musicBtn.setText("MUSIC: " + (newState ? "ON" : "OFF"));
                game.getSaveManager().save();
                game.getAudio().playTrigger();
            }
        });

        // Haptics Toggle
        final TextButton hapticBtn = new TextButton("HAPTICS: " + (settings.isHapticsEnabled() ? "ON" : "OFF"), skin, "default");
        hapticBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = !settings.isHapticsEnabled();
                settings.setHapticsEnabled(newState);
                hapticBtn.setText("HAPTICS: " + (newState ? "ON" : "OFF"));
                game.getSaveManager().save();
                if (newState) game.getAudio().triggerHaptic();
            }
        });

        TextButton backBtn = new TextButton("< BACK TO MENU", skin, "gold");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                game.getScreenManager().showMainMenu();
            }
        });

        root.add(sfxBtn).width(750).height(120).padBottom(30).row();
        root.add(musicBtn).width(750).height(120).padBottom(30).row();
        root.add(hapticBtn).width(750).height(120).padBottom(80).row();
        root.add(backBtn).width(500).height(110).row();

        stage.addActor(root);
    }
}

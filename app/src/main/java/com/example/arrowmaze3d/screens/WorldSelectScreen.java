package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.arrowmaze3d.ArrowMazeGame;

public class WorldSelectScreen extends BaseScreen {

    public WorldSelectScreen(ArrowMazeGame game) {
        super(game);
        setupUI();
    }

    private void setupUI() {
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(60);

        Label title = new Label("SELECT WORLD", skin, "title");
        root.add(title).padBottom(60).row();

        String[] worlds = {
            "WORLD 1 - DIRECTIONAL TEMPLE",
            "WORLD 2 - CLOCKWORK CITADEL",
            "WORLD 3 - CRYSTAL LABYRINTH",
            "WORLD 4 - GRAVITY OBSERVATORY"
        };

        int highestUnlocked = game.getSaveManager().getSaveData().highestUnlockedWorld;

        for (int i = 0; i < worlds.length; i++) {
            final int worldIndex = i + 1;
            boolean isUnlocked = (worldIndex <= highestUnlocked);

            String labelText = worlds[i] + (isUnlocked ? "" : " [LOCKED]");
            TextButton worldBtn = new TextButton(labelText, skin, isUnlocked ? "gold" : "default");

            if (isUnlocked) {
                worldBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.getAudio().playTrigger();
                        game.getScreenManager().showLevelSelect(worldIndex);
                    }
                });
            }

            root.add(worldBtn).width(850).height(140).padBottom(30).row();
        }

        TextButton backBtn = new TextButton("< BACK TO MENU", skin, "default");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                game.getScreenManager().showMainMenu();
            }
        });
        root.add(backBtn).width(400).height(100).padTop(40).row();

        stage.addActor(root);
    }
}

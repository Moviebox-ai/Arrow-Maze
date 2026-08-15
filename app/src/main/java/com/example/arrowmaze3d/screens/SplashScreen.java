package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.utilities.Constants;

public class SplashScreen extends BaseScreen {
    private float timer = 0f;

    public SplashScreen(ArrowMazeGame game) {
        super(game);
        setupUI();
    }

    private void setupUI() {
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);

        Label titleLabel = new Label("ARROW MAZE 3D", skin, "title");
        Label subLabel = new Label("Tactical Vector Puzzle Mechanics", skin, "default");
        Label loadingLabel = new Label("Loading Systems...", skin, "small");

        root.add(titleLabel).padBottom(20).row();
        root.add(subLabel).padBottom(80).row();
        root.add(loadingLabel).bottom();

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        timer += delta;
        if (timer >= 1.5f) {
            game.getScreenManager().showMainMenu();
        }
    }
}

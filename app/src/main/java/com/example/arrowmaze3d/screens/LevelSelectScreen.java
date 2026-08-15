package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.arrowmaze3d.ArrowMazeGame;

public class LevelSelectScreen extends BaseScreen {
    private final int worldIndex;

    public LevelSelectScreen(ArrowMazeGame game, int worldIndex) {
        super(game);
        this.worldIndex = worldIndex;
        setupUI();
    }

    private void setupUI() {
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(60);

        Label title = new Label("CHOOSE LEVEL", skin, "title");
        root.add(title).padBottom(30).row();

        Table grid = new Table();
        for (int i = 1; i <= 20; i++) {
            final int levelNum = i;
            boolean isUnlocked = true; // All accessible for testing/playing
            int stars = game.getSaveManager().getSaveData().getStarsForLevel(1, levelNum);

            StringBuilder starText = new StringBuilder();
            for (int s = 0; s < 3; s++) {
                starText.append(s < stars ? "* " : "- ");
            }

            String btnText = "LVL " + levelNum + "\n" + (stars > 0 ? starText.toString().trim() : (levelNum == 15 ? "[FEATURED]" : "***"));
            TextButton levelBtn = new TextButton(btnText, skin, (levelNum == 15) ? "gold" : "default");

            levelBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.getAudio().playTrigger();
                    game.getScreenManager().showArrowEscape(levelNum);
                }
            });

            grid.add(levelBtn).width(210).height(140).pad(10);
            if (i % 4 == 0) grid.row();
        }

        root.add(grid).padBottom(30).row();

        TextButton backBtn = new TextButton("< MAIN MENU", skin, "default");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                game.getScreenManager().showMainMenu();
            }
        });
        root.add(backBtn).width(400).height(100).row();

        stage.addActor(root);
    }
}

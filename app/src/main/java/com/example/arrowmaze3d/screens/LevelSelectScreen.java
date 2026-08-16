package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator.DifficultyMode;

public class LevelSelectScreen extends BaseScreen {
    private int selectedMode = 0; // 0: EASY (1-1500), 1: NORMAL (1501-3500), 2: HARD (3501-5000)
    private int pageOffset = 0;
    private static final int LEVELS_PER_PAGE = 24;

    public LevelSelectScreen(ArrowMazeGame game, int initialLevel) {
        super(game);
        if (initialLevel <= 1500) {
            selectedMode = 0;
            pageOffset = Math.max(0, (initialLevel - 1) / LEVELS_PER_PAGE * LEVELS_PER_PAGE);
        } else if (initialLevel <= 3500) {
            selectedMode = 1;
            pageOffset = Math.max(1500, 1500 + (initialLevel - 1501) / LEVELS_PER_PAGE * LEVELS_PER_PAGE);
        } else {
            selectedMode = 2;
            pageOffset = Math.max(3500, 3500 + (initialLevel - 3501) / LEVELS_PER_PAGE * LEVELS_PER_PAGE);
        }
        setupUI();
    }

    private void setupUI() {
        stage.clear();
        Skin skin = game.getAssetManager().getUiSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(40);

        Label title = new Label("SELECT LEVEL (1 - 5000)", skin, "title");
        root.add(title).padBottom(15).row();

        // Mode Selector Tabs (Easy, Normal, Hard)
        Table modeTabs = new Table();
        TextButton easyTab = new TextButton("EASY (1-1500)", skin, selectedMode == 0 ? "gold" : "default");
        TextButton normalTab = new TextButton("NORMAL (1501-3500)", skin, selectedMode == 1 ? "gold" : "default");
        TextButton hardTab = new TextButton("HARD (3501-5000)", skin, selectedMode == 2 ? "gold" : "default");

        easyTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                selectedMode = 0;
                pageOffset = 0;
                setupUI();
            }
        });

        normalTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                selectedMode = 1;
                pageOffset = 1500;
                setupUI();
            }
        });

        hardTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                selectedMode = 2;
                pageOffset = 3500;
                setupUI();
            }
        });

        modeTabs.add(easyTab).width(310).height(90).pad(6);
        modeTabs.add(normalTab).width(350).height(90).pad(6);
        modeTabs.add(hardTab).width(340).height(90).pad(6);
        root.add(modeTabs).padBottom(15).row();

        // Level Range Subtitle & Current Progress
        int currentLevel = game.getSaveManager().getSaveData().currentLevel;
        DifficultyMode currentMode = PuzzleLevelGenerator.getDifficultyForLevel(currentLevel);
        Label sub = new Label("Current: Level " + currentLevel + " [" + currentMode.title + "]", skin, "small");
        root.add(sub).padBottom(15).row();

        // Level Grid
        Table grid = new Table();
        int minRange = (selectedMode == 0) ? 1 : ((selectedMode == 1) ? 1501 : 3501);
        int maxRange = (selectedMode == 0) ? 1500 : ((selectedMode == 1) ? 3500 : 5000);

        int startLvl = minRange + (pageOffset - ((selectedMode == 0) ? 0 : ((selectedMode == 1) ? 1500 : 3500)));
        if (startLvl < minRange) startLvl = minRange;

        int columns = 4;
        for (int i = 0; i < LEVELS_PER_PAGE; i++) {
            final int lvlNum = startLvl + i;
            if (lvlNum > maxRange) break;

            boolean isCurrent = (lvlNum == currentLevel);
            int stars = game.getSaveManager().getSaveData().getStarsForLevel(1, lvlNum);

            StringBuilder starText = new StringBuilder();
            for (int s = 0; s < 3; s++) {
                starText.append(s < stars ? "* " : "- ");
            }

            String btnText = "LVL " + lvlNum + "\n" + (isCurrent ? "[PLAYING]" : (stars > 0 ? starText.toString().trim() : "***"));
            TextButton levelBtn = new TextButton(btnText, skin, isCurrent ? "gold" : "default");

            levelBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.getAudio().playTrigger();
                    game.getScreenManager().showArrowEscape(lvlNum);
                }
            });

            grid.add(levelBtn).width(230).height(120).pad(8);
            if ((i + 1) % columns == 0) grid.row();
        }

        root.add(grid).padBottom(20).row();

        // Page Navigation Buttons
        Table navTable = new Table();
        TextButton prevPageBtn = new TextButton("<< PREV 24", skin, "default");
        TextButton jumpCurrentBtn = new TextButton("GO TO MY LEVEL (" + currentLevel + ")", skin, "gold");
        TextButton nextPageBtn = new TextButton("NEXT 24 >>", skin, "default");

        final int finalMin = minRange;
        final int finalMax = maxRange;
        final int currentStartLvl = startLvl;

        prevPageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                if (currentStartLvl - LEVELS_PER_PAGE >= finalMin) {
                    pageOffset -= LEVELS_PER_PAGE;
                }
                setupUI();
            }
        });

        jumpCurrentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playTrigger();
                game.getScreenManager().showArrowEscape(currentLevel);
            }
        });

        nextPageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                if (currentStartLvl + LEVELS_PER_PAGE <= finalMax) {
                    pageOffset += LEVELS_PER_PAGE;
                }
                setupUI();
            }
        });

        navTable.add(prevPageBtn).width(280).height(90).pad(8);
        navTable.add(jumpCurrentBtn).width(440).height(90).pad(8);
        navTable.add(nextPageBtn).width(280).height(90).pad(8);
        root.add(navTable).padBottom(20).row();

        // Back to Main Menu Button
        TextButton backBtn = new TextButton("< BACK TO MAIN MENU", skin, "default");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getAudio().playStep();
                game.getScreenManager().showMainMenu();
            }
        });
        root.add(backBtn).width(480).height(90).row();

        stage.addActor(root);
    }
}

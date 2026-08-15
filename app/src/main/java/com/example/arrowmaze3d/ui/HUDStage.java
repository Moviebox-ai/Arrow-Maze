package com.example.arrowmaze3d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.arrowmaze3d.camera.TacticalCameraController;
import com.example.arrowmaze3d.game.GameController;
import com.example.arrowmaze3d.world.Direction;

public class HUDStage {
    private final Stage stage;
    private final Skin skin;
    private final GameController gameController;
    private final TacticalCameraController cameraController;

    private Label levelTitleLabel;
    private Label movesLabel;
    private Label coinsLabel;
    private Label goalLabel;
    private TextButton hintBtn;
    private TextButton undoBtn;

    private Runnable onPauseListener;
    private Runnable onNextLevelListener;
    private Runnable onRestartListener;

    private VictoryDialog victoryDialog;

    public HUDStage(Skin skin, GameController gameController, TacticalCameraController cameraController,
                    Runnable onPauseListener, Runnable onNextLevelListener, Runnable onRestartListener) {
        this.skin = skin;
        this.gameController = gameController;
        this.cameraController = cameraController;
        this.onPauseListener = onPauseListener;
        this.onNextLevelListener = onNextLevelListener;
        this.onRestartListener = onRestartListener;
        this.stage = new Stage(new FitViewport(1080, 1920));

        setupUI();
    }

    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top();
        rootTable.setTouchable(Touchable.childrenOnly);

        // 1. Top HUD Bar (Level Badge, Moves Counter, Coins, Pause Button)
        Table topBar = new Table();
        topBar.setBackground(skin.getDrawable("surfaceDrawable"));
        topBar.pad(15, 25, 15, 25);
        topBar.setTouchable(Touchable.childrenOnly);

        levelTitleLabel = new Label("LEVEL 1\n* * *", skin, "title");
        movesLabel = new Label("MOVES\n0 / 25", skin, "title");
        coinsLabel = new Label("1250 +", skin, "gold");

        TextButton pauseBtn = new TextButton("||", skin, "gold");
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onPauseListener != null) onPauseListener.run();
            }
        });

        topBar.add(levelTitleLabel).width(260).left();
        topBar.add(movesLabel).expandX().center();
        topBar.add(coinsLabel).padRight(20);
        topBar.add(pauseBtn).width(90).height(90);

        rootTable.add(topBar).fillX().padTop(35).row();

        // 2. Middle Area (Left Sidebar Action Buttons)
        Table middleTable = new Table();
        middleTable.top().left();
        middleTable.setTouchable(Touchable.childrenOnly);

        Table leftToolbar = new Table();
        leftToolbar.padLeft(25).padTop(30);
        leftToolbar.setTouchable(Touchable.childrenOnly);

        hintBtn = new TextButton("HINT\n(3)", skin, "gold");
        hintBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.useHint();
            }
        });

        undoBtn = new TextButton("UNDO\n(3)", skin, "gold");
        undoBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.undoLastMove();
            }
        });

        TextButton restartBtn = new TextButton("RESTART\n(R)", skin, "default");
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onRestartListener != null) onRestartListener.run();
            }
        });

        TextButton camRotateBtn = new TextButton("ROTATE\nCAM", skin, "default");
        camRotateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (cameraController != null) cameraController.rotateYawSnap(true);
            }
        });

        leftToolbar.add(hintBtn).width(150).height(105).padBottom(16).row();
        leftToolbar.add(undoBtn).width(150).height(105).padBottom(16).row();
        leftToolbar.add(restartBtn).width(150).height(105).padBottom(16).row();
        leftToolbar.add(camRotateBtn).width(150).height(105).row();

        middleTable.add(leftToolbar).left().top();
        rootTable.add(middleTable).expand().fill().row();

        // 3. Goal Card Banner
        Table goalCard = new Table();
        goalCard.setBackground(skin.getDrawable("cardDrawable"));
        goalCard.pad(12, 28, 12, 28);
        goalCard.setTouchable(Touchable.childrenOnly);
        goalLabel = new Label("GOAL: Collect 24k Key & Reach Portal", skin, "default");
        goalCard.add(goalLabel);
        rootTable.add(goalCard).padBottom(20).row();

        // 4. Bottom Power-Ups Bar (Hammer, Magnet, Swap, Freeze)
        Table powerUpBar = new Table();
        powerUpBar.setBackground(skin.getDrawable("surfaceDrawable"));
        powerUpBar.pad(14, 18, 14, 18);
        powerUpBar.setTouchable(Touchable.childrenOnly);

        TextButton hammerBtn = new TextButton("HAMMER\n100", skin, "default");
        hammerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.useHammer();
            }
        });

        TextButton magnetBtn = new TextButton("MAGNET\n150", skin, "default");
        magnetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.useMagnet();
            }
        });

        TextButton swapBtn = new TextButton("SWAP\n200", skin, "default");
        swapBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.useSwapArrows();
            }
        });

        TextButton freezeBtn = new TextButton("FREEZE\n250", skin, "default");
        freezeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) gameController.useFreeze();
            }
        });

        powerUpBar.add(hammerBtn).width(230).height(105).pad(8);
        powerUpBar.add(magnetBtn).width(230).height(105).pad(8);
        powerUpBar.add(swapBtn).width(230).height(105).pad(8);
        powerUpBar.add(freezeBtn).width(230).height(105).pad(8);

        rootTable.add(powerUpBar).fillX().padBottom(25).row();

        stage.addActor(rootTable);

        // Victory Dialog
        victoryDialog = new VictoryDialog(skin, onNextLevelListener, onRestartListener);
        stage.addActor(victoryDialog);
        victoryDialog.setVisible(false);
    }

    public void update() {
        if (gameController != null && gameController.getCurrentLevelData() != null) {
            int levelNum = gameController.getCurrentLevelData().levelIndex;
            int moves = gameController.getPlayerController() != null ? gameController.getPlayerController().getStepCount() : 0;
            int maxMoves = gameController.getCurrentLevelData().maxAllowedMoves;

            levelTitleLabel.setText("LEVEL " + levelNum + "\n* * *");
            movesLabel.setText("MOVES\n" + moves + "/" + maxMoves);
            coinsLabel.setText(gameController.getCoinsBalance() + " +");

            hintBtn.setText("HINT\n(" + gameController.getHintsRemaining() + ")");
            undoBtn.setText("UNDO\n(" + gameController.getUndosRemaining() + ")");

            if (gameController.getKeysCollected() > 0) {
                goalLabel.setText("KEY COLLECTED! Unlock the gate & reach portal");
            } else {
                goalLabel.setText("GOAL: Collect key, avoid spikes & reach portal");
            }

            if (gameController.getGameState() == com.example.arrowmaze3d.game.GameState.COMPLETED) {
                if (!victoryDialog.isVisible()) {
                    int stars = gameController.calculateStars();
                    victoryDialog.show(stars, moves);
                }
            }
        }
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}

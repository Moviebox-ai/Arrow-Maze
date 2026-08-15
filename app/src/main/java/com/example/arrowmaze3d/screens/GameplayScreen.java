package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.game.GameController;
import com.example.arrowmaze3d.game.GameState;
import com.example.arrowmaze3d.save.SaveData;

import com.example.arrowmaze3d.ui.HUDStage;
import com.example.arrowmaze3d.world.Direction;

public class GameplayScreen extends BaseScreen {
    private final int worldIndex;
    private final int levelIndex;

    private GameController gameController;
    private HUDStage hudStage;
    private boolean hasSavedProgress = false;

    public GameplayScreen(ArrowMazeGame game, int worldIndex, int levelIndex) {
        super(game);
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
    }

    @Override
    public void show() {
        gameController = new GameController(
            game.getRenderEngine(),
            game.getCameraController(),
            game.getAudio()
        );

        gameController.loadLevel(worldIndex, levelIndex);

        hudStage = new HUDStage(
            game.getAssetManager().getUiSkin(),
            gameController,
            game.getCameraController(),
            new Runnable() {
                @Override
                public void run() {
                    game.getAudio().playTrigger();
                    game.getScreenManager().showLevelSelect(worldIndex);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    game.getAudio().playTrigger();
                    saveCurrentProgress();
                    if (levelIndex < 10) {
                        game.getScreenManager().showGameplay(worldIndex, levelIndex + 1);
                    } else {
                        int highest = game.getSaveManager().getSaveData().highestUnlockedWorld;
                        if (worldIndex >= highest && worldIndex < 4) {
                            game.getSaveManager().getSaveData().highestUnlockedWorld = worldIndex + 1;
                            game.getSaveManager().save();
                        }
                        game.getScreenManager().showWorldSelect();
                    }
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    game.getAudio().playTrigger();
                    hasSavedProgress = false;
                    gameController.loadLevel(worldIndex, levelIndex);
                }
            }
        );

        GestureDetector gestureDetector = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (gameController.getGameState() != GameState.PLAYING && gameController.getGameState() != GameState.READY) {
                    return false;
                }
                if (gameController.getPlayerController() == null) return false;

                // Project player world position to screen coordinates
                com.badlogic.gdx.math.Vector3 playerScreenPos = new com.badlogic.gdx.math.Vector3(
                    gameController.getPlayerController().getCurrentWorldPos()
                );
                game.getCameraController().getCamera().project(playerScreenPos);

                // Convert Gdx screen Y (y-down) to matching system
                float screenH = Gdx.graphics.getHeight();
                float playerScreenY = screenH - playerScreenPos.y;
                float playerScreenX = playerScreenPos.x;

                float diffX = x - playerScreenX;
                float diffY = y - playerScreenY;

                // Determine directional quadrant relative to player based on camera yaw
                float yaw = (game.getCameraController().getYawAngle() % 360f + 360f) % 360f;
                int quarter = Math.round(yaw / 90f) % 4;

                Direction chosenDir;
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        // Right of player on screen
                        if (quarter == 0) chosenDir = Direction.EAST;
                        else if (quarter == 1) chosenDir = Direction.SOUTH;
                        else if (quarter == 2) chosenDir = Direction.WEST;
                        else chosenDir = Direction.NORTH;
                    } else {
                        // Left of player on screen
                        if (quarter == 0) chosenDir = Direction.WEST;
                        else if (quarter == 1) chosenDir = Direction.NORTH;
                        else if (quarter == 2) chosenDir = Direction.EAST;
                        else chosenDir = Direction.SOUTH;
                    }
                } else {
                    if (diffY > 0) {
                        // Below player on screen
                        if (quarter == 0) chosenDir = Direction.SOUTH;
                        else if (quarter == 1) chosenDir = Direction.WEST;
                        else if (quarter == 2) chosenDir = Direction.NORTH;
                        else chosenDir = Direction.EAST;
                    } else {
                        // Above player on screen
                        if (quarter == 0) chosenDir = Direction.NORTH;
                        else if (quarter == 1) chosenDir = Direction.EAST;
                        else if (quarter == 2) chosenDir = Direction.SOUTH;
                        else chosenDir = Direction.WEST;
                    }
                }

                gameController.handleStepInput(chosenDir);
                return true;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if (gameController.getGameState() != GameState.PLAYING && gameController.getGameState() != GameState.READY) {
                    return false;
                }

                float yaw = (game.getCameraController().getYawAngle() % 360f + 360f) % 360f;
                int quarter = Math.round(yaw / 90f) % 4;

                Direction chosenDir;
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 100f) {
                        if (quarter == 0) chosenDir = Direction.EAST;
                        else if (quarter == 1) chosenDir = Direction.SOUTH;
                        else if (quarter == 2) chosenDir = Direction.WEST;
                        else chosenDir = Direction.NORTH;
                    } else if (velocityX < -100f) {
                        if (quarter == 0) chosenDir = Direction.WEST;
                        else if (quarter == 1) chosenDir = Direction.NORTH;
                        else if (quarter == 2) chosenDir = Direction.EAST;
                        else chosenDir = Direction.SOUTH;
                    } else {
                        return false;
                    }
                } else {
                    if (velocityY > 100f) {
                        if (quarter == 0) chosenDir = Direction.SOUTH;
                        else if (quarter == 1) chosenDir = Direction.WEST;
                        else if (quarter == 2) chosenDir = Direction.NORTH;
                        else chosenDir = Direction.EAST;
                    } else if (velocityY < -100f) {
                        if (quarter == 0) chosenDir = Direction.NORTH;
                        else if (quarter == 1) chosenDir = Direction.EAST;
                        else if (quarter == 2) chosenDir = Direction.SOUTH;
                        else chosenDir = Direction.WEST;
                    } else {
                        return false;
                    }
                }

                gameController.handleStepInput(chosenDir);
                return true;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                // Smooth camera rotation by dragging with finger
                game.getCameraController().rotateYaw(-deltaX * 0.45f);
                return true;
            }
        });

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudStage.getStage());
        multiplexer.addProcessor(gestureDetector);
        multiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void saveCurrentProgress() {
        if (hasSavedProgress) return;
        hasSavedProgress = true;

        SaveData saveData = game.getSaveManager().getSaveData();
        String key = "w" + worldIndex + "_l" + levelIndex;
        int stars = gameController.calculateStars();
        int moves = gameController.getPlayerController().getStepCount();

        int existingStars = saveData.levelStars.containsKey(key) ? saveData.levelStars.get(key) : 0;
        int existingMoves = saveData.bestMoves.containsKey(key) ? saveData.bestMoves.get(key) : 999;

        if (stars > existingStars) {
            saveData.levelStars.put(key, stars);
        }
        if (moves < existingMoves) {
            saveData.bestMoves.put(key, moves);
        }

        if (worldIndex >= saveData.highestUnlockedWorld && levelIndex >= 10 && worldIndex < 4) {
            saveData.highestUnlockedWorld = worldIndex + 1;
        }

        game.getSaveManager().save();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.07f, 0.11f, 0.16f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT | com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT);

        if (gameController != null && game.getCameraController() != null) {
            game.getCameraController().update(delta);
            gameController.update(delta);

            if (gameController.getGameState() == GameState.COMPLETED && !hasSavedProgress) {
                saveCurrentProgress();
            }

            gameController.render(game.getCameraController().getCamera());
        }

        if (hudStage != null) {
            hudStage.update();
            hudStage.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (hudStage != null) {
            hudStage.resize(width, height);
        }
        game.getCameraController().getCamera().viewportWidth = width;
        game.getCameraController().getCamera().viewportHeight = height;
        game.getCameraController().getCamera().update();
    }

    @Override
    public void dispose() {
        if (gameController != null) gameController.dispose();
        if (hudStage != null) hudStage.dispose();
        super.dispose();
    }
}

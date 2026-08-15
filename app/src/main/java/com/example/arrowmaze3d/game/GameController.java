package com.example.arrowmaze3d.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.example.arrowmaze3d.audio.AudioManager;
import com.example.arrowmaze3d.camera.TacticalCameraController;
import com.example.arrowmaze3d.level.LevelData;
import com.example.arrowmaze3d.level.LevelLoader;
import com.example.arrowmaze3d.objects.*;
import com.example.arrowmaze3d.player.PlayerController;
import com.example.arrowmaze3d.player.PlayerState;
import com.example.arrowmaze3d.rendering.ProceduralMeshBuilder;
import com.example.arrowmaze3d.rendering.RenderEngine3D;
import com.example.arrowmaze3d.utilities.Constants;
import com.example.arrowmaze3d.utilities.Vector3i;
import com.example.arrowmaze3d.world.Direction;
import com.example.arrowmaze3d.world.GridTile;
import com.example.arrowmaze3d.world.GridWorld;

public class GameController {
    private final ProceduralMeshBuilder meshBuilder;
    private final RenderEngine3D renderEngine;
    private final LevelLoader levelLoader;
    private final AudioManager audioManager;
    private final CommandHistory commandHistory;

    private GridWorld gridWorld;
    private PlayerController playerController;
    private TacticalCameraController cameraController;
    private LevelData currentLevelData;
    private GameState gameState = GameState.LOADING;

    private int keysCollected = 0;
    private int starsCollected = 0;
    private int coinsBalance = 1250;
    private int hintsRemaining = 3;
    private int undosRemaining = 3;

    private final Array<Model> createdModels = new Array<>();

    public GameController(RenderEngine3D renderEngine, TacticalCameraController cameraController, AudioManager audioManager) {
        this.renderEngine = renderEngine;
        this.cameraController = cameraController;
        this.audioManager = audioManager;
        this.meshBuilder = new ProceduralMeshBuilder();
        this.levelLoader = new LevelLoader();
        this.commandHistory = new CommandHistory();
    }

    public void loadLevel(int worldIndex, int levelIndex) {
        this.gameState = GameState.LOADING;
        this.commandHistory.clear();
        this.keysCollected = 0;
        this.starsCollected = 0;

        // Clean up previous models
        for (Model model : createdModels) {
            model.dispose();
        }
        createdModels.clear();

        this.currentLevelData = levelLoader.loadLevel(worldIndex, levelIndex);
        this.gridWorld = new GridWorld(currentLevelData.sizeX, currentLevelData.sizeY, currentLevelData.sizeZ);

        // Build 3D Models
        Model stoneModel = meshBuilder.createStoneTileModel(currentLevelData.theme);
        Model grassModel = meshBuilder.createGrassTileModel(true, false);
        Model grassTreeModel = meshBuilder.createGrassTileModel(true, true);
        Model waterModel = meshBuilder.createWaterTileModel();
        Model wallModel = meshBuilder.createWallTileModel(currentLevelData.theme);
        Model cliffModel = meshBuilder.createFloatingIslandBaseModel(currentLevelData.sizeX, currentLevelData.sizeZ);

        Model spikeModel = meshBuilder.createSpikeModel();
        Model keyModel = meshBuilder.createKeyModel();
        Model lockModel = meshBuilder.createLockGateModel();
        Model starModel = meshBuilder.createStarModel();
        Model portalModel = meshBuilder.createVortexPortalModel();
        Model playerModel = meshBuilder.createPlayerModel();
        Model switchModel = meshBuilder.createSwitchModel();
        Model gateModel = meshBuilder.createGateModel(false);

        // Color-coded arrow models
        Model arrGreen = meshBuilder.createArrowBlockModel(new Color(0x22 / 255f, 0xC5 / 255f, 0x5E / 255f, 1f));
        Model arrRed = meshBuilder.createArrowBlockModel(new Color(0xEF / 255f, 0x44 / 255f, 0x44 / 255f, 1f));
        Model arrBlue = meshBuilder.createArrowBlockModel(new Color(0x0E / 255f, 0xA5 / 255f, 0xE9 / 255f, 1f));
        Model arrOrange = meshBuilder.createArrowBlockModel(new Color(0xF5 / 255f, 0x9E / 255f, 0x0B / 255f, 1f));
        Model arrPurple = meshBuilder.createArrowBlockModel(new Color(0xA8 / 255f, 0x55 / 255f, 0xF7 / 255f, 1f));

        createdModels.add(stoneModel);
        createdModels.add(grassModel);
        createdModels.add(grassTreeModel);
        createdModels.add(waterModel);
        createdModels.add(wallModel);
        createdModels.add(cliffModel);
        createdModels.add(spikeModel);
        createdModels.add(keyModel);
        createdModels.add(lockModel);
        createdModels.add(starModel);
        createdModels.add(portalModel);
        createdModels.add(playerModel);
        createdModels.add(switchModel);
        createdModels.add(gateModel);
        createdModels.add(arrGreen);
        createdModels.add(arrRed);
        createdModels.add(arrBlue);
        createdModels.add(arrOrange);
        createdModels.add(arrPurple);

        // Add Floating Island Base
        ModelInstance cliffInst = new ModelInstance(cliffModel);
        float centerX = (currentLevelData.sizeX - 1) * Constants.GRID_CELL_SIZE * 0.5f;
        float centerZ = (currentLevelData.sizeZ - 1) * Constants.GRID_CELL_SIZE * 0.5f;
        cliffInst.transform.setToTranslation(centerX, 0f, centerZ);
        gridWorld.addRenderInstance(cliffInst);

        // Populate Tiles based on tileMap or defaults
        for (int x = 0; x < currentLevelData.sizeX; x++) {
            for (int z = 0; z < currentLevelData.sizeZ; z++) {
                Vector3i pos = new Vector3i(x, 0, z);
                char tileChar = 'S';
                if (currentLevelData.tileMap != null && z < currentLevelData.tileMap.size()) {
                    String row = currentLevelData.tileMap.get(z);
                    if (x < row.length()) {
                        tileChar = row.charAt(x);
                    }
                } else {
                    boolean isBorder = (x == 0 || x == currentLevelData.sizeX - 1 || z == 0 || z == currentLevelData.sizeZ - 1);
                    tileChar = isBorder ? '#' : 'S';
                }

                if (tileChar == '#') {
                    GridTile wallTile = new GridTile(pos, GridTile.TileType.WALL);
                    gridWorld.addTile(wallTile);
                    ModelInstance wallInst = new ModelInstance(wallModel);
                    wallInst.transform.setToTranslation(x * Constants.GRID_CELL_SIZE, 0, z * Constants.GRID_CELL_SIZE);
                    gridWorld.addRenderInstance(wallInst);
                } else if (tileChar == 'W') {
                    GridTile waterTile = new GridTile(pos, GridTile.TileType.WATER);
                    gridWorld.addTile(waterTile);
                    ModelInstance waterInst = new ModelInstance(waterModel);
                    waterInst.transform.setToTranslation(x * Constants.GRID_CELL_SIZE, 0, z * Constants.GRID_CELL_SIZE);
                    gridWorld.addRenderInstance(waterInst);
                } else if (tileChar == 'G') {
                    GridTile grassTile = new GridTile(pos, GridTile.TileType.GRASS);
                    gridWorld.addTile(grassTile);
                    boolean isPlayerOrGoal = (x == currentLevelData.playerSpawn.x && z == currentLevelData.playerSpawn.z) || (x == currentLevelData.goalLocation.x && z == currentLevelData.goalLocation.z);
                    boolean hasTree = !isPlayerOrGoal && (x == 1 && z == 5);
                    ModelInstance grassInst = new ModelInstance(hasTree ? grassTreeModel : grassModel);
                    grassInst.transform.setToTranslation(x * Constants.GRID_CELL_SIZE, 0, z * Constants.GRID_CELL_SIZE);
                    gridWorld.addRenderInstance(grassInst);
                } else {
                    GridTile stoneTile = new GridTile(pos, GridTile.TileType.STONE);
                    gridWorld.addTile(stoneTile);
                    ModelInstance stoneInst = new ModelInstance(stoneModel);
                    stoneInst.transform.setToTranslation(x * Constants.GRID_CELL_SIZE, 0, z * Constants.GRID_CELL_SIZE);
                    gridWorld.addRenderInstance(stoneInst);
                }
            }
        }

        // Add Objects (Arrows, Spikes, Keys, Locks, Stars, Switches, Gates, Portals)
        for (LevelData.ObjectData obj : currentLevelData.objects) {
            Vector3i objPos = new Vector3i(obj.x, obj.y, obj.z);
            Direction objDir = Direction.fromString(obj.direction);

            if ("SPIKES".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(spikeModel);
                SpikeObject spike = new SpikeObject(obj.id, objPos, inst);
                gridWorld.addSpike(spike);

            } else if ("KEY".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(keyModel);
                KeyObject key = new KeyObject(obj.id, objPos, inst);
                gridWorld.addKey(key);

            } else if ("LOCK_GATE".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(lockModel);
                LockGateObject lock = new LockGateObject(obj.id, objPos, inst);
                gridWorld.addLockGate(lock);

            } else if ("STAR".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(starModel);
                StarObject star = new StarObject(obj.id, objPos, inst);
                gridWorld.addStar(star);

            } else if (obj.type.contains("ARROW")) {
                Model arrowM = arrGreen;
                if ("RED".equalsIgnoreCase(obj.color)) arrowM = arrRed;
                else if ("BLUE".equalsIgnoreCase(obj.color)) arrowM = arrBlue;
                else if ("ORANGE".equalsIgnoreCase(obj.color) || "GOLD".equalsIgnoreCase(obj.color)) arrowM = arrOrange;
                else if ("PURPLE".equalsIgnoreCase(obj.color)) arrowM = arrPurple;

                ArrowObject.ArrowType aType = ArrowObject.ArrowType.DIRECTIONAL;
                if ("ROTATABLE_ARROW".equals(obj.type)) aType = ArrowObject.ArrowType.ROTATABLE;
                else if ("REVERSIBLE_ARROW".equals(obj.type)) aType = ArrowObject.ArrowType.REVERSIBLE;
                else if ("SPLIT_ARROW".equals(obj.type)) aType = ArrowObject.ArrowType.SPLIT;

                ModelInstance inst = new ModelInstance(arrowM);
                ArrowObject arrow = new ArrowObject(obj.id, objPos, inst, aType, objDir);
                gridWorld.addArrow(arrow);

            } else if ("SWITCH".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(switchModel);
                SwitchObject sw = new SwitchObject(obj.id, objPos, inst, obj.linkedId);
                gridWorld.addSwitch(sw);

            } else if ("GATE".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(gateModel);
                GateObject gate = new GateObject(obj.id, objPos, inst, objDir, obj.isOpen);
                gridWorld.addGate(gate);

            } else if ("TELEPORT".equals(obj.type)) {
                ModelInstance inst = new ModelInstance(portalModel);
                Vector3i target = (obj.targetPos != null) ? new Vector3i(obj.targetPos.x, obj.targetPos.y, obj.targetPos.z) : objPos;
                TeleportObject tp = new TeleportObject(obj.id, objPos, inst, target);
                gridWorld.addTeleporter(tp);
            }
        }

        // Add Glowing Vortex Portal Goal
        Vector3i goalPos = new Vector3i(currentLevelData.goalLocation.x, currentLevelData.goalLocation.y, currentLevelData.goalLocation.z);
        ModelInstance goalInst = new ModelInstance(portalModel);
        GoalObject goalObj = new GoalObject("goal", goalPos, goalInst);
        gridWorld.setGoal(goalObj);

        // Setup Player
        ModelInstance pInst = new ModelInstance(playerModel);
        gridWorld.addRenderInstance(pInst);
        playerController = new PlayerController(pInst);

        Vector3i spawnPos = new Vector3i(currentLevelData.playerSpawn.x, currentLevelData.playerSpawn.y, currentLevelData.playerSpawn.z);
        Direction spawnFacing = Direction.fromString(currentLevelData.playerSpawn.dir);
        playerController.setPosition(spawnPos, spawnFacing);

        // Setup Camera Target
        cameraController.setTarget(centerX, 0.5f, centerZ);

        this.gameState = GameState.READY;
    }

    public void handleStepInput(Direction dir) {
        if (gameState != GameState.PLAYING && gameState != GameState.READY) return;
        if (playerController.getState() == PlayerState.MOVING) return;

        gameState = GameState.PLAYING;
        Vector3i currentPos = playerController.getCurrentGridPos();
        Vector3i targetPos = currentPos.cpy().add(dir.dx, dir.dy, dir.dz);

        // Check if there is a locked gate in front
        LockGateObject lock = gridWorld.getLockGate(targetPos);
        if (lock != null && !lock.isUnlocked()) {
            if (keysCollected > 0) {
                keysCollected--;
                lock.setUnlocked(true);
                audioManager.playTrigger();
                audioManager.triggerHaptic();
            } else {
                audioManager.triggerHaptic();
                return;
            }
        }

        if (!gridWorld.isValidStep(targetPos, dir)) {
            audioManager.triggerHaptic();
            return;
        }

        // Push current state to undo history
        commandHistory.push(currentPos, playerController.getFacingDirection(), playerController.getStepCount());

        // Step off previous arrow
        ArrowObject currArrow = gridWorld.getArrow(currentPos);
        if (currArrow != null) {
            currArrow.onStepOff();
            audioManager.playRotate();
        }

        // Move avatar
        playerController.moveTo(targetPos, dir);
        audioManager.playStep();

        // Check key pickup
        KeyObject key = gridWorld.getKey(targetPos);
        if (key != null && !key.isCollected()) {
            key.setCollected(true);
            keysCollected++;
            gridWorld.removeKey(targetPos);
            audioManager.playTrigger();
        }

        // Check star pickup
        StarObject star = gridWorld.getStar(targetPos);
        if (star != null && !star.isCollected()) {
            star.setCollected(true);
            starsCollected++;
            coinsBalance += 50;
            gridWorld.removeStar(targetPos);
            audioManager.playTrigger();
        }

        // Check Switch Trigger
        SwitchObject sw = gridWorld.getSwitch(targetPos);
        if (sw != null) {
            sw.onStepOn();
            audioManager.playTrigger();
        }

        // Check Teleport
        TeleportObject tp = gridWorld.getTeleporter(targetPos);
        if (tp != null) {
            playerController.setPosition(tp.getTargetDestination(), dir);
            audioManager.playTeleport();
        }

        // Check Victory Condition
        GoalObject goal = gridWorld.getGoal();
        if (goal != null && targetPos.equals(goal.getGridPosition())) {
            triggerVictory();
        }
    }

    public boolean useHammer() {
        if (coinsBalance >= 100) {
            coinsBalance -= 100;
            // Clear all spikes on grid
            for (SpikeObject spk : gridWorld.getAllSpikes().values()) {
                spk.setActive(false);
            }
            audioManager.playTrigger();
            return true;
        }
        return false;
    }

    public boolean useMagnet() {
        if (coinsBalance >= 150) {
            coinsBalance -= 150;
            // Collect any key on grid
            for (Vector3i pos : new java.util.ArrayList<>(gridWorld.getAllKeys().keySet())) {
                KeyObject key = gridWorld.getKey(pos);
                if (key != null && !key.isCollected()) {
                    key.setCollected(true);
                    keysCollected++;
                    gridWorld.removeKey(pos);
                }
            }
            // Collect any bonus stars on grid
            for (Vector3i pos : new java.util.ArrayList<>(gridWorld.getAllStars().keySet())) {
                StarObject star = gridWorld.getStar(pos);
                if (star != null && !star.isCollected()) {
                    star.setCollected(true);
                    starsCollected++;
                    coinsBalance += 50;
                    gridWorld.removeStar(pos);
                }
            }
            if (keysCollected == 0) {
                keysCollected = 1; // Guarantee at least 1 key if board has gates
            }
            audioManager.playTrigger();
            return true;
        }
        return false;
    }

    public boolean useSwapArrows() {
        if (coinsBalance >= 200) {
            coinsBalance -= 200;
            for (ArrowObject arr : gridWorld.getAllArrows().values()) {
                arr.rotateClockwise();
            }
            audioManager.playRotate();
            return true;
        }
        return false;
    }

    public boolean useFreeze() {
        if (coinsBalance >= 250) {
            coinsBalance -= 250;
            for (SpikeObject spk : gridWorld.getAllSpikes().values()) {
                spk.setActive(false);
            }
            audioManager.playTrigger();
            return true;
        }
        return false;
    }

    public boolean useHint() {
        if (hintsRemaining > 0) {
            hintsRemaining--;
            audioManager.playTrigger();
            return true;
        }
        return false;
    }

    public boolean undoLastMove() {
        if (undosRemaining > 0 && commandHistory.canUndo()) {
            CommandHistory.MoveRecord record = commandHistory.pop();
            if (record != null) {
                undosRemaining--;
                playerController.setPosition(record.playerPos, record.playerFacing);
                playerController.setStepCount(record.stepCount);
                audioManager.playStep();
                return true;
            }
        }
        return false;
    }

    private void triggerVictory() {
        this.gameState = GameState.COMPLETED;
        playerController.setState(PlayerState.WON);
        coinsBalance += 100;
        audioManager.playExitDoor();
        audioManager.playVictory();
        audioManager.triggerHaptic();
    }

    public void update(float delta) {
        if (playerController != null) {
            playerController.update(delta);
        }
        if (gridWorld != null) {
            if (gridWorld.getGoal() != null) {
                gridWorld.getGoal().updateAnimation(delta);
            }
            for (SpikeObject spk : gridWorld.getAllSpikes().values()) {
                spk.update(delta);
            }
            for (KeyObject key : gridWorld.getAllKeys().values()) {
                key.update(delta);
            }
            for (LockGateObject lock : gridWorld.getAllLockGates().values()) {
                lock.update(delta);
            }
            for (StarObject star : gridWorld.getAllStars().values()) {
                star.update(delta);
            }
        }
    }

    public void render(PerspectiveCamera camera) {
        if (gridWorld != null) {
            renderEngine.render(camera, gridWorld.getRenderInstances());
        }
    }

    public int calculateStars() {
        if (playerController == null || currentLevelData == null) return 1;
        int moves = playerController.getStepCount();
        if (moves <= currentLevelData.threeStarMoves) return 3;
        if (moves <= currentLevelData.twoStarMoves) return 2;
        return 1;
    }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }
    public PlayerController getPlayerController() { return playerController; }
    public LevelData getCurrentLevelData() { return currentLevelData; }
    public int getCoinsBalance() { return coinsBalance; }
    public int getHintsRemaining() { return hintsRemaining; }
    public int getUndosRemaining() { return undosRemaining; }
    public int getKeysCollected() { return keysCollected; }

    public void dispose() {
        for (Model model : createdModels) {
            model.dispose();
        }
        createdModels.clear();
    }
}

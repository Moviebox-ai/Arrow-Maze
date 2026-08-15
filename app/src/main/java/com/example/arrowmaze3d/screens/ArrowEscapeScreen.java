package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.game.ArrowTile;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator;
import com.example.arrowmaze3d.world.Direction;
import java.util.ArrayList;
import java.util.List;

public class ArrowEscapeScreen extends BaseScreen {
    private final int levelNumber;
    private PuzzleLevelGenerator.EscapeLevelData levelData;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont regularFont;
    private BitmapFont smallFont;
    private BitmapFont hintFont;

    private int playerX;
    private int playerY;
    private List<Vector2> playerPath = new ArrayList<>();
    private boolean isLevelWon = false;
    private float winAnimationTimer = 0f;
    private float torchFlicker = 0f;

    // Hint state
    private boolean hintActive = false;
    private float hintTimer = 0f;

    // UI Bounds (Virtual Coordinate System: 1080 x 1920)
    private float boardX, boardY, boardSize;
    private float tileSize;

    // Torch flame particle simulation
    private static class Particle {
        float x, y;
        float vx, vy;
        float life, maxLife;
        Color color;
    }
    private final Array<Particle> particles = new Array<>();

    public ArrowEscapeScreen(ArrowMazeGame game, int levelNumber) {
        super(game);
        this.levelNumber = levelNumber;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);

        regularFont = new BitmapFont();
        regularFont.getData().setScale(2.2f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.5f);

        hintFont = new BitmapFont();
        hintFont.getData().setScale(2.8f);

        loadLevel(levelNumber);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen coordinates to virtual 1080x1920
                float vx = (screenX / (float) Gdx.graphics.getWidth()) * 1080f;
                float vy = (1f - (screenY / (float) Gdx.graphics.getHeight())) * 1920f;
                handleTouch(vx, vy);
                return true;
            }
        });
    }

    private void loadLevel(int lvl) {
        levelData = PuzzleLevelGenerator.getLevel(lvl);
        playerX = levelData.startX;
        playerY = levelData.startY;
        playerPath.clear();
        playerPath.add(new Vector2(playerX, playerY));
        isLevelWon = false;
        winAnimationTimer = 0f;
        hintActive = false;
        hintTimer = 0f;

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (levelData.grid[x][y] != null) {
                    levelData.grid[x][y].isVisited = (x == playerX && y == playerY);
                    levelData.grid[x][y].isHinted = false;
                }
            }
        }
    }

    private void handleTouch(float x, float y) {
        if (isLevelWon) {
            // Tap to proceed to next level
            if (winAnimationTimer > 1.2f) {
                int nextLvl = levelNumber + 1;
                game.getSaveManager().getSaveData().currentLevel = nextLvl;
                game.getSaveManager().getSaveData().coins += 100;
                game.getSaveManager().save();
                game.setScreen(new ArrowEscapeScreen(game, nextLvl));
            }
            return;
        }

        // 1. Check Top Bar buttons
        // Pause Button (Top Left)
        if (x >= 40 && x <= 140 && y >= 1760 && y <= 1860) {
            game.getAudio().playTrigger();
            game.getScreenManager().showLevelSelect(1);
            return;
        }

        // Coin '+' Button (Top Right)
        if (x >= 800 && x <= 1040 && y >= 1760 && y <= 1860) {
            game.getAudio().playTrigger();
            game.getSaveManager().getSaveData().coins += 500;
            game.getSaveManager().save();
            return;
        }

        // 2. Check Bottom Action Buttons
        // LEVELS Button (Bottom Left)
        if (x >= 40 && x <= 220 && y >= 60 && y <= 220) {
            game.getAudio().playTrigger();
            game.getScreenManager().showLevelSelect(1);
            return;
        }

        // RESET Button (Bottom Right Top)
        if (x >= 860 && x <= 1040 && y >= 250 && y <= 400) {
            game.getAudio().playTrigger();
            loadLevel(levelNumber);
            return;
        }

        // HINT Button (Bottom Right Bottom)
        if (x >= 860 && x <= 1040 && y >= 70 && y <= 220) {
            triggerHint();
            return;
        }

        // 3. Check Puzzle Grid Tile Touches
        if (x >= boardX && x <= boardX + boardSize && y >= boardY && y <= boardY + boardSize) {
            int cellX = (int) ((x - boardX) / tileSize);
            int cellY = 4 - (int) ((y - boardY) / tileSize); // Invert Y for 0=top, 4=bottom

            if (cellX >= 0 && cellX < 5 && cellY >= 0 && cellY < 5) {
                onTileTapped(cellX, cellY);
            }
        }
    }

    private void onTileTapped(int cellX, int cellY) {
        ArrowTile tapped = levelData.grid[cellX][cellY];
        ArrowTile current = levelData.grid[playerX][playerY];

        // If player taps the current tile, advance along current arrow's direction!
        if (cellX == playerX && cellY == playerY) {
            stepInDirection(current.direction);
            return;
        }

        // If player taps any adjacent tile or reachable tile along arrow
        int dx = cellX - playerX;
        int dy = cellY - playerY;

        boolean isReachable = false;
        if (current.direction == Direction.EAST && dy == 0 && dx > 0) isReachable = true;
        if (current.direction == Direction.WEST && dy == 0 && dx < 0) isReachable = true;
        if (current.direction == Direction.SOUTH && dx == 0 && dy > 0) isReachable = true;
        if (current.direction == Direction.NORTH && dx == 0 && dy < 0) isReachable = true;

        // Also allow direct adjacent single steps
        if (Math.abs(dx) + Math.abs(dy) == 1) {
            isReachable = true;
        }

        if (isReachable) {
            moveToTile(cellX, cellY);
        } else {
            // Optional: rotate tapped tile arrow
            tapped.rotateClockwise();
            game.getAudio().playTrigger();
        }
    }

    private void stepInDirection(Direction dir) {
        int nextX = playerX;
        int nextY = playerY;

        switch (dir) {
            case NORTH: nextY--; break;
            case SOUTH: nextY++; break;
            case EAST: nextX++; break;
            case WEST: nextX--; break;
        }

        if (nextX >= 0 && nextX < 5 && nextY >= 0 && nextY < 5) {
            moveToTile(nextX, nextY);
        } else {
            game.getAudio().playTrigger();
        }
    }

    private void moveToTile(int nextX, int nextY) {
        playerX = nextX;
        playerY = nextY;
        playerPath.add(new Vector2(playerX, playerY));
        levelData.grid[playerX][playerY].isVisited = true;
        game.getAudio().playTrigger();

        // Check Gold Coin bonus tile
        if (levelData.grid[playerX][playerY].type == ArrowTile.TileType.GOLD) {
            game.getSaveManager().getSaveData().coins += 50;
            game.getSaveManager().save();
        }

        // Check Exit Door reached
        if (playerX == levelData.exitX && playerY == levelData.exitY) {
            triggerWin();
        }
    }

    private void triggerHint() {
        if (hintActive) return;

        int currentCoins = game.getSaveManager().getSaveData().coins;
        if (currentCoins >= 50) {
            game.getSaveManager().getSaveData().coins -= 50;
            game.getSaveManager().save();
        }

        hintActive = true;
        hintTimer = 10f;
        game.getAudio().playTrigger();

        for (int i = 0; i < levelData.solutionPath.size(); i++) {
            int[] pos = levelData.solutionPath.get(i);
            ArrowTile t = levelData.grid[pos[0]][pos[1]];
            t.isHinted = true;
            t.hintStepIndex = i + 1;
        }
    }

    private void triggerWin() {
        isLevelWon = true;
        winAnimationTimer = 0f;
        game.getAudio().playVictory();
        game.getSaveManager().getSaveData().setStarsForLevel(1, levelNumber, 3);
        game.getSaveManager().save();
    }

    @Override
    public void render(float delta) {
        updateTorchParticles(delta);

        if (hintActive) {
            hintTimer -= delta;
            if (hintTimer <= 0f) {
                hintActive = false;
                for (int y = 0; y < 5; y++) {
                    for (int x = 0; x < 5; x++) {
                        levelData.grid[x][y].isHinted = false;
                    }
                }
            }
        }

        if (isLevelWon) {
            winAnimationTimer += delta;
        }

        // Virtual Screen Setup
        Gdx.gl.glClearColor(0.06f, 0.07f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Calculate 5x5 board dimensions centered on 1080x1920 screen
        boardSize = 820f;
        boardX = (1080f - boardSize) / 2f;
        boardY = 560f;
        tileSize = boardSize / 5f;

        torchFlicker += delta * 4f;

        // 1. Render Background & Scene Shapes
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        renderDungeonEnvironment(shapeRenderer);
        renderStonePuzzleTablet(shapeRenderer);
        renderPathTrace(shapeRenderer);
        renderAncientButtonBackings(shapeRenderer);

        shapeRenderer.end();

        // 2. Render UI Texts and Overlays with SpriteBatch
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
        batch.begin();

        renderTopPlaqueAndHUD(batch);
        renderBottomButtons(batch);

        if (isLevelWon) {
            renderVictoryOverlay(batch);
        }

        batch.end();
    }

    private void renderAncientButtonBackings(ShapeRenderer sr) {
        // 1. Top Left Pause Button (Round Ancient Bronze Medallion)
        float px = 90f, py = 1810f, pr = 46f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.8f); // Drop shadow
        sr.circle(px + 4f, py - 5f, pr);
        sr.setColor(0.42f, 0.32f, 0.18f, 1f); // Bronze Outer Rim
        sr.circle(px, py, pr);
        sr.setColor(0.24f, 0.18f, 0.10f, 1f); // Bronze Inner Bed
        sr.circle(px, py, pr - 6f);

        // 2. Top Header Ancient Stone Banner Slab
        float hx = 170f, hy = 1710f, hw = 610f, hh = 170f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.85f);
        sr.rect(hx + 6f, hy - 8f, hw, hh);
        sr.setColor(0.22f, 0.24f, 0.28f, 1f); // Heavy Granite Slab
        sr.rect(hx, hy, hw, hh);
        // Bevel highlight (Top-Left)
        sr.setColor(0.35f, 0.37f, 0.42f, 1f);
        sr.rect(hx, hy + hh - 4f, hw, 4f);
        sr.rect(hx, hy, 4f, hh);
        // Bevel shadow (Bottom-Right)
        sr.setColor(0.12f, 0.13f, 0.16f, 1f);
        sr.rect(hx, hy, hw, 4f);
        sr.rect(hx + hw - 4f, hy, 4f, hh);
        // Inner chiseled border groove
        sr.setColor(0.10f, 0.11f, 0.14f, 1f);
        sr.rect(hx + 8f, hy + 8f, hw - 16f, hh - 16f);

        // 3. Top Right Gold Coin Pill
        float cx = 810f, cy = 1760f, cw = 230f, ch = 100f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.8f);
        sr.rect(cx + 4f, cy - 5f, cw, ch);
        sr.setColor(0.35f, 0.28f, 0.12f, 1f); // Bronze Gold Frame
        sr.rect(cx, cy, cw, ch);
        sr.setColor(0.18f, 0.15f, 0.08f, 1f);
        sr.rect(cx + 4f, cy + 4f, cw - 8f, ch - 8f);
        // Gold Coin Symbol
        sr.setColor(0.95f, 0.75f, 0.15f, 1f);
        sr.circle(cx + 42f, cy + ch / 2f, 24f);
        sr.setColor(0.65f, 0.45f, 0.08f, 1f);
        sr.circle(cx + 42f, cy + ch / 2f, 16f);

        // 4. Bottom Left "LEVELS" Stone Button
        float lx = 40f, ly = 60f, lw = 180f, lh = 180f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.85f);
        sr.rect(lx + 5f, ly - 6f, lw, lh);
        sr.setColor(0.24f, 0.26f, 0.30f, 1f);
        sr.rect(lx, ly, lw, lh);
        sr.setColor(0.38f, 0.40f, 0.45f, 1f);
        sr.rect(lx, ly + lh - 4f, lw, 4f);
        sr.rect(lx, ly, 4f, lh);
        sr.setColor(0.12f, 0.13f, 0.16f, 1f);
        sr.rect(lx, ly, lw, 4f);
        sr.rect(lx + lw - 4f, ly, 4f, lh);
        sr.rect(lx + 8f, ly + 8f, lw - 16f, lh - 16f);

        // 5. Bottom Right "RESET" Ancient Wheel Button
        float rx = 955f, ry = 320f, rr = 75f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.85f);
        sr.circle(rx + 5f, ry - 6f, rr);
        sr.setColor(0.26f, 0.28f, 0.32f, 1f);
        sr.circle(rx, ry, rr);
        sr.setColor(0.38f, 0.40f, 0.46f, 1f);
        sr.circle(rx, ry, rr - 4f);
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.circle(rx, ry, rr - 10f);

        // 6. Bottom Right "HINT $50" Ancient Stone Slab Button
        float hbx = 865f, hby = 60f, hbw = 180f, hbh = 180f;
        sr.setColor(0.03f, 0.03f, 0.04f, 0.85f);
        sr.rect(hbx + 5f, hby - 6f, hbw, hbh);
        sr.setColor(0.38f, 0.30f, 0.14f, 1f); // Gold Bronze Trim
        sr.rect(hbx, hby, hbw, hbh);
        sr.setColor(0.20f, 0.16f, 0.10f, 1f);
        sr.rect(hbx + 6f, hby + 6f, hbw - 12f, hbh - 12f);
        // Corner gold rivets
        sr.setColor(0.95f, 0.80f, 0.25f, 1f);
        sr.circle(hbx + 14f, hby + hbh - 14f, 4f);
        sr.circle(hbx + hbw - 14f, hby + hbh - 14f, 4f);
        sr.circle(hbx + 14f, hby + 14f, 4f);
        sr.circle(hbx + hbw - 14f, hby + 14f, 4f);
    }

    private void updateTorchParticles(float delta) {
        if (particles.size < 40) {
            // Left Torch
            Particle pLeft = new Particle();
            pLeft.x = 42f + MathUtils.random(-8f, 8f);
            pLeft.y = 1080f + MathUtils.random(-5f, 5f);
            pLeft.vx = MathUtils.random(-15f, 25f);
            pLeft.vy = MathUtils.random(60f, 130f);
            pLeft.life = 0f;
            pLeft.maxLife = MathUtils.random(0.4f, 0.9f);
            pLeft.color = MathUtils.randomBoolean(0.6f) ? new Color(1f, 0.7f, 0.1f, 0.8f) : new Color(1f, 0.3f, 0.05f, 0.7f);
            particles.add(pLeft);

            // Right Torch
            Particle pRight = new Particle();
            pRight.x = 905f + MathUtils.random(-8f, 8f);
            pRight.y = 1060f + MathUtils.random(-5f, 5f);
            pRight.vx = MathUtils.random(-25f, 15f);
            pRight.vy = MathUtils.random(60f, 130f);
            pRight.life = 0f;
            pRight.maxLife = MathUtils.random(0.4f, 0.9f);
            pRight.color = MathUtils.randomBoolean(0.6f) ? new Color(1f, 0.7f, 0.1f, 0.8f) : new Color(1f, 0.3f, 0.05f, 0.7f);
            particles.add(pRight);
        }

        for (int i = particles.size - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.life += delta;
            if (p.life >= p.maxLife) {
                particles.removeIndex(i);
            } else {
                p.x += p.vx * delta;
                p.y += p.vy * delta;
            }
        }
    }

    private void renderDungeonEnvironment(ShapeRenderer sr) {
        // 1. Deep Ancient Dungeon Wall (Dark granite stonework)
        sr.setColor(0.08f, 0.09f, 0.12f, 1f);
        sr.rect(0, 0, 1080, 1920);

        // Ancient large stone masonry blocks with mortar seams & varying shades
        float flickerIntensity = 1f + MathUtils.sin(torchFlicker) * 0.14f;

        for (int row = 0; row < 18; row++) {
            float y = row * 110f;
            // Mortar horizontal groove
            sr.setColor(0.05f, 0.05f, 0.07f, 1f);
            sr.rect(0, y, 1080, 5f);

            float offset = (row % 2 == 0) ? 0 : 90f;
            for (float x = offset; x < 1080; x += 180f) {
                // Mortar vertical groove
                sr.setColor(0.05f, 0.05f, 0.07f, 1f);
                sr.rect(x, y, 5f, 110f);

                // Subtle ancient stone weathering variations
                int hash = (row * 37 + (int) x) % 5;
                if (hash == 0) {
                    sr.setColor(0.12f, 0.13f, 0.16f, 0.4f);
                    sr.rect(x + 5f, y + 5f, 170f, 100f);
                } else if (hash == 1) {
                    sr.setColor(0.10f, 0.11f, 0.14f, 0.5f);
                    sr.rect(x + 5f, y + 5f, 170f, 100f);
                }

                // Tiny stone crack details
                if ((row + (int)x) % 7 == 0) {
                    sr.setColor(0.04f, 0.04f, 0.06f, 0.8f);
                    sr.rectLine(x + 30f, y + 30f, x + 60f, y + 70f, 2f);
                    sr.rectLine(x + 60f, y + 70f, x + 85f, y + 55f, 2f);
                }
            }
        }

        // 2. Ancient Cobblestone Dungeon Floor at Bottom
        sr.setColor(0.07f, 0.08f, 0.10f, 1f);
        sr.rect(0, 0, 1080, 490);
        // Floor border flagstone
        sr.setColor(0.15f, 0.16f, 0.19f, 1f);
        sr.rect(0, 485f, 1080, 12f);
        sr.setColor(0.04f, 0.04f, 0.05f, 1f);
        sr.rect(0, 483f, 1080, 4f);

        // Ancient Green Moss & Lichen patches in floor corners and crevices
        sr.setColor(0.12f, 0.28f, 0.14f, 0.85f); // Dark Forest Moss
        sr.circle(40f, 495f, 35f);
        sr.circle(120f, 490f, 24f);
        sr.circle(980f, 495f, 40f);
        sr.circle(1040f, 500f, 30f);
        sr.setColor(0.22f, 0.45f, 0.18f, 0.9f); // Bright Ancient Moss Lichen
        sr.circle(45f, 497f, 22f);
        sr.circle(115f, 492f, 15f);
        sr.circle(975f, 497f, 26f);
        sr.circle(1035f, 502f, 18f);

        // 3. Ancient Heavy Arched Castle Dungeon Door (Right side)
        float doorX = 910f;
        float doorY = 680f;
        float doorW = 160f;
        float doorH = 370f;

        // Heavy Chiseled Stone Outer Arch Frame
        sr.setColor(0.20f, 0.22f, 0.26f, 1f);
        sr.rect(doorX - 20f, doorY - 12f, doorW + 40f, doorH + 24f);
        sr.setColor(0.12f, 0.13f, 0.16f, 1f);
        sr.rect(doorX - 8f, doorY - 4f, doorW + 16f, doorH + 8f);

        // Ancient Dark Oak Wooden Planks
        sr.setColor(0.22f, 0.13f, 0.08f, 1f);
        sr.rect(doorX, doorY, doorW, doorH);

        // Wood grain vertical slots
        sr.setColor(0.12f, 0.07f, 0.04f, 1f);
        sr.rect(doorX + 50f, doorY, 5f, doorH);
        sr.rect(doorX + 105f, doorY, 5f, doorH);

        // Forged Wrought-Iron Strap Hinges & Crossbars
        sr.setColor(0.12f, 0.13f, 0.15f, 1f);
        sr.rect(doorX - 14f, doorY + 60f, doorW + 14f, 20f);
        sr.rect(doorX - 14f, doorY + 240f, doorW + 14f, 20f);

        // Iron Rivet Studs on Door
        sr.setColor(0.35f, 0.36f, 0.40f, 1f);
        for (float hy : new float[]{doorY + 70f, doorY + 250f}) {
            sr.circle(doorX + 25f, hy, 5f);
            sr.circle(doorX + 80f, hy, 5f);
            sr.circle(doorX + 135f, hy, 5f);
        }

        // Heavy Iron Door Ring Handle
        sr.setColor(0.25f, 0.26f, 0.30f, 1f);
        sr.circle(doorX + 30f, doorY + 160f, 16f);
        sr.setColor(0.12f, 0.13f, 0.15f, 1f);
        sr.circle(doorX + 30f, doorY + 160f, 9f);

        // Ancient Illuminated Green "EXIT" Sign Tablet above Door
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.rect(doorX - 10f, doorY + doorH + 15f, doorW + 20f, 65f);
        sr.setColor(0.04f, 0.38f, 0.18f, 1f);
        sr.rect(doorX - 2f, doorY + doorH + 20f, doorW + 4f, 55f);
        // Inner Neon Green Exit Glow
        sr.setColor(0.12f, 0.85f, 0.38f, 0.9f);
        sr.rect(doorX + 4f, doorY + doorH + 26f, doorW - 8f, 4f);

        // 4. Ancient Flaming Wall Torch Sconces (Left & Right)
        // Multi-layered Atmospheric Fire Glow Halos (Warm Orange & Yellow)
        // Left Torch Glow
        sr.setColor(0.70f * flickerIntensity, 0.35f * flickerIntensity, 0.04f, 0.12f);
        sr.circle(42f, 1080f, 190f * flickerIntensity);
        sr.setColor(0.85f * flickerIntensity, 0.48f * flickerIntensity, 0.06f, 0.22f);
        sr.circle(42f, 1080f, 110f * flickerIntensity);
        sr.setColor(1.0f * flickerIntensity, 0.70f * flickerIntensity, 0.15f, 0.40f);
        sr.circle(42f, 1080f, 45f * flickerIntensity);

        // Left Wrought-Iron Sconce Bracket
        sr.setColor(0.16f, 0.17f, 0.20f, 1f);
        sr.rect(20f, 1020f, 44f, 55f);
        sr.rect(34f, 960f, 16f, 65f);
        sr.rectLine(42f, 960f, 75f, 1010f, 6f); // Diagonal Iron Strut

        // Right Torch Glow
        sr.setColor(0.70f * flickerIntensity, 0.35f * flickerIntensity, 0.04f, 0.12f);
        sr.circle(895f, 1060f, 190f * flickerIntensity);
        sr.setColor(0.85f * flickerIntensity, 0.48f * flickerIntensity, 0.06f, 0.22f);
        sr.circle(895f, 1060f, 110f * flickerIntensity);
        sr.setColor(1.0f * flickerIntensity, 0.70f * flickerIntensity, 0.15f, 0.40f);
        sr.circle(895f, 1060f, 45f * flickerIntensity);

        // Right Wrought-Iron Sconce Bracket
        sr.setColor(0.16f, 0.17f, 0.20f, 1f);
        sr.rect(873f, 1000f, 44f, 55f);
        sr.rect(887f, 940f, 16f, 65f);
        sr.rectLine(895f, 940f, 862f, 990f, 6f); // Diagonal Iron Strut

        // 5. Burning Torch Fire Cores & Flying Flame Embers
        for (Particle p : particles) {
            float alpha = 1f - (p.life / p.maxLife);
            sr.setColor(p.color.r, p.color.g, p.color.b, alpha * 0.9f);
            sr.circle(p.x, p.y, 7f * alpha + 2f);
        }
    }

    private void renderStonePuzzleTablet(ShapeRenderer sr) {
        float framePad = 26f;

        // 1. Deep Cast Shadow under Stone Tablet
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.rect(boardX - framePad + 16f, boardY - framePad - 18f, boardSize + framePad * 2f, boardSize + framePad * 2f);

        // 2. Heavy Weathered Stone Outer Tablet Frame
        sr.setColor(0.25f, 0.27f, 0.32f, 1f); // Weathered Granite
        sr.rect(boardX - framePad, boardY - framePad, boardSize + framePad * 2f, boardSize + framePad * 2f);

        // Outer Frame Highlight Bevel (Top-Left)
        sr.setColor(0.38f, 0.40f, 0.46f, 1f);
        sr.rect(boardX - framePad, boardY + boardSize + framePad - 6f, boardSize + framePad * 2f, 6f);
        sr.rect(boardX - framePad, boardY - framePad, 6f, boardSize + framePad * 2f);

        // Outer Frame Shadow Bevel (Bottom-Right)
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.rect(boardX - framePad, boardY - framePad, boardSize + framePad * 2f, 6f);
        sr.rect(boardX + boardSize + framePad - 6f, boardY - framePad, 6f, boardSize + framePad * 2f);

        // Inner Chiseled Inset Groove
        sr.setColor(0.12f, 0.13f, 0.16f, 1f);
        sr.rect(boardX - 10f, boardY - 10f, boardSize + 20f, boardSize + 20f);

        // 3. Ancient Bronze/Iron Corner Brackets with Rivets
        sr.setColor(0.18f, 0.19f, 0.22f, 1f); // Iron brackets
        float bSize = 34f;
        // Top-Left bracket
        sr.rect(boardX - framePad, boardY + boardSize + framePad - bSize, bSize, bSize);
        // Top-Right bracket
        sr.rect(boardX + boardSize + framePad - bSize, boardY + boardSize + framePad - bSize, bSize, bSize);
        // Bottom-Left bracket
        sr.rect(boardX - framePad, boardY - framePad, bSize, bSize);
        // Bottom-Right bracket
        sr.rect(boardX + boardSize + framePad - bSize, boardY - framePad, bSize, bSize);

        // Bronze Rivet Studs in corners
        sr.setColor(0.70f, 0.55f, 0.25f, 1f);
        sr.circle(boardX - framePad + 16f, boardY + boardSize + framePad - 16f, 7f);
        sr.circle(boardX + boardSize + framePad - 16f, boardY + boardSize + framePad - 16f, 7f);
        sr.circle(boardX - framePad + 16f, boardY - framePad + 16f, 7f);
        sr.circle(boardX + boardSize + framePad - 16f, boardY - framePad + 16f, 7f);

        // 4. Board Interior Dark Granite Sunk Bed
        sr.setColor(0.08f, 0.09f, 0.11f, 1f);
        sr.rect(boardX, boardY, boardSize, boardSize);

        // 5. Render 25 Individual Ancient Stone Arrow Tiles
        float tilePad = 5f;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                ArrowTile tile = levelData.grid[x][y];
                if (tile == null) continue;

                float tx = boardX + x * tileSize + tilePad;
                float ty = boardY + (4 - y) * tileSize + tilePad;
                float tw = tileSize - tilePad * 2f;
                float th = tileSize - tilePad * 2f;

                // Deep Tile Drop Shadow
                sr.setColor(0.03f, 0.03f, 0.04f, 0.9f);
                sr.rect(tx + 5f, ty - 6f, tw, th);

                // Base Tile Color
                Color baseCol = tile.getBaseColor();
                sr.setColor(baseCol);
                sr.rect(tx, ty, tw, th);

                // 3D Top-Left Highlight Bevel
                sr.setColor(Math.min(baseCol.r + 0.18f, 1f), Math.min(baseCol.g + 0.18f, 1f), Math.min(baseCol.b + 0.18f, 1f), 1f);
                sr.rect(tx, ty + th - 6f, tw, 6f);
                sr.rect(tx, ty, 6f, th);

                // 3D Bottom-Right Shadow Bevel
                sr.setColor(baseCol.r * 0.55f, baseCol.g * 0.55f, baseCol.b * 0.55f, 1f);
                sr.rect(tx, ty, tw, 6f);
                sr.rect(tx + tw - 6f, ty, 6f, th);

                // Carved Dark Inset Bed for the Arrow Glyph
                sr.setColor(baseCol.r * 0.40f, baseCol.g * 0.40f, baseCol.b * 0.40f, 1f);
                sr.rect(tx + 14f, ty + 14f, tw - 28f, th - 28f);

                // Vector 3D Carved Arrow Glyph
                renderVectorArrow(sr, tx + tw / 2f, ty + th / 2f, tw * 0.38f, tile.direction, tile.getArrowGlyphColor(), tile.type == ArrowTile.TileType.EXIT_DOOR);

                // Active Player Tile - Radiant Glowing Gold Halo
                if (x == playerX && y == playerY) {
                    float pulse = (MathUtils.sin(torchFlicker * 3.5f) + 1f) * 0.5f;
                    sr.setColor(1.0f, 0.92f, 0.25f, 0.85f * pulse + 0.25f);
                    sr.rect(tx - 4f, ty - 4f, tw + 8f, 5f);
                    sr.rect(tx - 4f, ty + th - 1f, tw + 8f, 5f);
                    sr.rect(tx - 4f, ty - 4f, 5f, th + 8f);
                    sr.rect(tx + tw - 1f, ty - 4f, 5f, th + 8f);
                }

                // Hinted Tile Golden Glowing Aura
                if (tile.isHinted) {
                    sr.setColor(1.0f, 0.80f, 0.10f, 0.95f);
                    sr.rect(tx - 3f, ty - 3f, tw + 6f, 4f);
                    sr.rect(tx - 3f, ty + th - 1f, tw + 6f, 4f);
                    sr.rect(tx - 3f, ty - 3f, 4f, th + 6f);
                    sr.rect(tx + tw - 1f, ty - 3f, 4f, th + 6f);
                }
            }
        }
    }

    private void renderVectorArrow(ShapeRenderer sr, float cx, float cy, float r, Direction dir, Color arrowCol, boolean isExitDoor) {
        if (isExitDoor) {
            // Draw Exit Gateway Portal Icon
            sr.setColor(arrowCol);
            sr.rect(cx - r * 0.7f, cy - r * 0.8f, r * 1.4f, r * 1.6f);
            sr.setColor(0.10f, 0.25f, 0.14f, 1f);
            sr.rect(cx - r * 0.45f, cy - r * 0.65f, r * 0.9f, r * 1.3f);
            sr.setColor(arrowCol);
            sr.rectLine(cx - r * 0.2f, cy, cx + r * 0.4f, cy, 6f);
            sr.triangle(cx + r * 0.4f, cy + r * 0.3f, cx + r * 0.4f, cy - r * 0.3f, cx + r * 0.75f, cy);
            return;
        }

        // Calculate rotation angles
        float angleDeg = 0f;
        switch (dir) {
            case NORTH: angleDeg = 90f; break;
            case EAST:  angleDeg = 0f; break;
            case SOUTH: angleDeg = -90f; break;
            case WEST:  angleDeg = 180f; break;
        }

        float rad = angleDeg * MathUtils.degreesToRadians;
        float cos = MathUtils.cos(rad);
        float sin = MathUtils.sin(rad);

        // Arrow Shaft
        float shaftLen = r * 0.75f;
        float shaftThick = 12f;
        float sx1 = cx - cos * shaftLen;
        float sy1 = cy - sin * shaftLen;
        float sx2 = cx + cos * (r * 0.2f);
        float sy2 = cy + sin * (r * 0.2f);

        // Dark Shadow Under Shaft
        sr.setColor(0.05f, 0.05f, 0.06f, 0.6f);
        sr.rectLine(sx1 + 3f, sy1 - 3f, sx2 + 3f, sy2 - 3f, shaftThick);

        // Glowing Arrow Shaft
        sr.setColor(arrowCol);
        sr.rectLine(sx1, sy1, sx2, sy2, shaftThick);

        // Arrow Head Triangle Points
        float tipX = cx + cos * (r * 0.95f);
        float tipY = cy + sin * (r * 0.95f);

        float perpX = -sin;
        float perpY = cos;

        float headBaseX = cx + cos * (r * 0.15f);
        float headBaseY = cy + sin * (r * 0.15f);

        float headWing = r * 0.65f;
        float w1x = headBaseX + perpX * headWing;
        float w1y = headBaseY + perpY * headWing;
        float w2x = headBaseX - perpX * headWing;
        float w2y = headBaseY - perpY * headWing;

        // Dark Shadow Under Head
        sr.setColor(0.05f, 0.05f, 0.06f, 0.6f);
        sr.triangle(w1x + 3f, w1y - 3f, w2x + 3f, w2y - 3f, tipX + 3f, tipY - 3f);

        // Glowing Arrow Head
        sr.setColor(arrowCol);
        sr.triangle(w1x, w1y, w2x, w2y, tipX, tipY);
    }

    private void renderPathTrace(ShapeRenderer sr) {
        if (playerPath.size() < 2) return;

        sr.setColor(0.3f, 0.85f, 1.0f, 0.8f);
        for (int i = 0; i < playerPath.size() - 1; i++) {
            Vector2 p1 = playerPath.get(i);
            Vector2 p2 = playerPath.get(i + 1);

            float x1 = boardX + p1.x * tileSize + tileSize / 2f;
            float y1 = boardY + (4 - p1.y) * tileSize + tileSize / 2f;
            float x2 = boardX + p2.x * tileSize + tileSize / 2f;
            float y2 = boardY + (4 - p2.y) * tileSize + tileSize / 2f;

            sr.rectLine(x1, y1, x2, y2, 6f);
            sr.circle(x1, y1, 8f);
        }
        Vector2 last = playerPath.get(playerPath.size() - 1);
        float lx = boardX + last.x * tileSize + tileSize / 2f;
        float ly = boardY + (4 - last.y) * tileSize + tileSize / 2f;
        sr.circle(lx, ly, 10f);
    }

    private void renderTopPlaqueAndHUD(SpriteBatch sb) {
        // 1. Top Left Pause Button (Stone Coin)
        drawStoneButton(40f, 1760f, 100f, 100f, "||", new Color(0.95f, 0.85f, 0.5f, 1f));

        // 2. Center Stone Header Plaque
        float plaqueX = 180f;
        float plaqueY = 1710f;
        float plaqueW = 600f;
        float plaqueH = 170f;

        // Plaque Text
        titleFont.setColor(0.98f, 0.84f, 0.40f, 1f); // Gold LEVEL 15
        titleFont.draw(sb, "LEVEL " + levelNumber, plaqueX, plaqueY + 145f, plaqueW, 1, false);

        regularFont.setColor(0.92f, 0.88f, 0.80f, 1f);
        regularFont.draw(sb, "Arrow Puzzle Escape", plaqueX, plaqueY + 95f, plaqueW, 1, false);

        smallFont.setColor(0.70f, 0.75f, 0.80f, 1f);
        smallFont.draw(sb, "Solve the arrow puzzle to ", plaqueX - 30f, plaqueY + 45f, plaqueW, 1, false);

        smallFont.setColor(0.35f, 0.95f, 0.45f, 1f); // Neon Green "open the door"
        smallFont.draw(sb, "open the door", plaqueX + 165f, plaqueY + 45f, plaqueW, 1, false);

        // 3. Top Right Gold Coin Badge
        float coinX = 810f;
        float coinY = 1760f;
        int coins = game.getSaveManager().getSaveData().coins;
        drawGoldCoinPill(coinX, coinY, 230f, 100f, "$" + coins + " +");

        // 4. "EXIT" text over door
        regularFont.setColor(0.35f, 0.98f, 0.50f, 1f);
        regularFont.draw(sb, "EXIT", 945f, 1085f);

        // 5. Render Step Numbers if Hint is Active
        if (hintActive) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    ArrowTile tile = levelData.grid[x][y];
                    if (tile != null && tile.isHinted && tile.hintStepIndex > 0) {
                        float tx = boardX + x * tileSize + 8f;
                        float ty = boardY + (4 - y) * tileSize + 8f;
                        float th = tileSize - 16f;

                        smallFont.setColor(1.0f, 0.95f, 0.2f, 1f);
                        smallFont.draw(sb, "#" + tile.hintStepIndex, tx + 6f, ty + th - 6f);
                    }
                }
            }
        }
    }

    private void renderBottomButtons(SpriteBatch sb) {
        // 1. Bottom Left "LEVELS" button
        drawStoneButtonWithSubtext(40f, 60f, 170f, 170f, "===", "LEVELS");

        // 2. Bottom Right "RESET" button
        drawStoneButtonWithSubtext(870f, 240f, 170f, 160f, "@", "RESET");

        // 3. Bottom Right "HINT 50" button
        drawStoneButtonWithSubtext(870f, 60f, 170f, 160f, "*", "HINT\n$ 50");
    }

    private void drawStoneButton(float x, float y, float w, float h, String label, Color fontColor) {
        regularFont.setColor(fontColor);
        regularFont.draw(batch, label, x, y + h * 0.65f, w, 1, false);
    }

    private void drawGoldCoinPill(float x, float y, float w, float h, String text) {
        regularFont.setColor(1f, 0.9f, 0.3f, 1f);
        regularFont.draw(batch, text, x + 10f, y + h * 0.65f, w - 20f, 1, false);
    }

    private void drawStoneButtonWithSubtext(float x, float y, float w, float h, String icon, String text) {
        regularFont.setColor(0.95f, 0.90f, 0.80f, 1f);
        regularFont.draw(batch, icon, x, y + h * 0.78f, w, 1, false);

        smallFont.setColor(0.90f, 0.85f, 0.70f, 1f);
        smallFont.draw(batch, text, x, y + h * 0.40f, w, 1, false);
    }

    private void renderVictoryOverlay(SpriteBatch sb) {
        float cx = 540f;
        float cy = 960f;

        titleFont.setColor(0.4f, 1.0f, 0.5f, 1f);
        titleFont.draw(sb, "DOOR UNLOCKED!", cx - 400f, cy + 80f, 800f, 1, false);

        regularFont.setColor(1f, 0.9f, 0.3f, 1f);
        regularFont.draw(sb, "+100 COINS REWARD", cx - 300f, cy + 10f, 600f, 1, false);

        smallFont.setColor(0.9f, 0.9f, 0.9f, 1f);
        smallFont.draw(sb, "Tap anywhere to continue to next level", cx - 400f, cy - 60f, 800f, 1, false);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (titleFont != null) titleFont.dispose();
        if (regularFont != null) regularFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (hintFont != null) hintFont.dispose();
        super.dispose();
    }
}

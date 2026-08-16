package com.example.arrowmaze3d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.ArrowMazeGame;
import com.example.arrowmaze3d.game.ArrowTile;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator.DifficultyMode;
import com.example.arrowmaze3d.level.PuzzleLevelGenerator.EscapeLevelData;
import com.example.arrowmaze3d.world.Direction;
import java.util.ArrayList;
import java.util.List;

public class ArrowEscapeScreen extends BaseScreen {

    private int levelNumber;
    private EscapeLevelData levelData;
    private ArrowTile[][] grid;
    private int gridSize = 5;

    // Rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont regularFont;
    private BitmapFont smallFont;

    // Board Geometry
    private float boardOriginX = 90f;
    private float boardOriginY = 460f;
    private float tileSize = 160f;
    private float tileGap = 25f;

    // Gameplay Progress
    private int remainingArrows = 25;
    private int totalArrows = 25;
    private int movesCount = 0;
    private boolean isLevelWon = false;
    private float victoryTimer = 0f;

    // Visual Particles for Escaped Arrows
    private static class EscapeParticle {
        float x, y, vx, vy, life, maxLife;
        Color color;
    }
    private final List<EscapeParticle> particles = new ArrayList<>();

    public ArrowEscapeScreen(ArrowMazeGame game, int levelNumber) {
        super(game);
        this.levelNumber = levelNumber;
        initGameplay();
    }

    private void initGameplay() {
        levelData = PuzzleLevelGenerator.getLevel(levelNumber);
        gridSize = levelData.gridSize;
        grid = levelData.grid;
        remainingArrows = levelData.totalArrows;
        totalArrows = levelData.totalArrows;
        movesCount = 0;
        isLevelWon = false;
        victoryTimer = 0f;
        particles.clear();
    }

    @Override
    public void show() {
        super.show();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        titleFont = game.getAssetManager().getUiSkin().has("title-font", BitmapFont.class)
                ? game.getAssetManager().getUiSkin().getFont("title-font")
                : game.getAssetManager().getUiSkin().getFont("default-font");
        regularFont = game.getAssetManager().getUiSkin().getFont("default-font");
        smallFont = game.getAssetManager().getUiSkin().has("small-font", BitmapFont.class)
                ? game.getAssetManager().getUiSkin().getFont("small-font")
                : game.getAssetManager().getUiSkin().getFont("default-font");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.09f, 0.11f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        updateGame(delta);

        // 1. Render Background & 3D Stone Plinth Architecture
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        renderBackgroundDecorations(shapeRenderer);
        renderStoneBoardPlinth(shapeRenderer);
        renderArrowTiles(shapeRenderer);
        renderParticles(shapeRenderer);
        renderAncientButtonBackings(shapeRenderer);

        if (isLevelWon) {
            renderVictoryModalBacking(shapeRenderer);
        }

        shapeRenderer.end();

        // 2. Render UI Texts and Overlays
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        renderHeaderUI(batch);
        renderBottomButtons(batch);

        if (isLevelWon) {
            renderVictoryOverlay(batch);
        }

        batch.end();
    }

    private void updateGame(float delta) {
        // Update all tiles
        int activeCount = 0;
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                ArrowTile tile = grid[x][y];
                if (tile != null) {
                    tile.update(delta);
                    if (!tile.isEscaped) {
                        activeCount++;
                    }
                }
            }
        }
        remainingArrows = activeCount;

        // Check Victory Condition
        if (remainingArrows == 0 && !isLevelWon) {
            isLevelWon = true;
            victoryTimer = 0f;
            game.getAudio().playExitDoor();
            game.getSaveManager().getSaveData().setStarsForLevel(1, levelNumber, 3);
            game.getSaveManager().getSaveData().currentLevel = Math.min(5000, levelNumber + 1);
            game.getSaveManager().getSaveData().coins += 100;
            game.getSaveManager().save();
        }

        if (isLevelWon) {
            victoryTimer += delta;
        }

        // Update Particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            EscapeParticle p = particles.get(i);
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.life -= delta;
            if (p.life <= 0) {
                particles.remove(i);
            }
        }
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        Vector3 touch3D = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        stage.getViewport().unproject(touch3D);
        float touchX = touch3D.x;
        float touchY = touch3D.y;

        // If Level is Won, tapping advances to next level
        if (isLevelWon && victoryTimer > 0.4f) {
            game.getAudio().playTrigger();
            if (levelNumber < 5000) {
                levelNumber++;
                initGameplay();
            } else {
                game.getScreenManager().showLevelSelect(1);
            }
            return;
        }

        // 1. Check Top Left Pause Button
        if (Vector2.dst(touchX, touchY, 90f, 1810f) < 55f) {
            game.getAudio().playStep();
            game.getScreenManager().showMainMenu();
            return;
        }

        // 2. Check Bottom Left "LEVELS" Button
        if (touchX >= 40f && touchX <= 220f && touchY >= 60f && touchY <= 240f) {
            game.getAudio().playStep();
            game.getScreenManager().showLevelSelect(levelNumber);
            return;
        }

        // 3. Check Bottom Right "RESET" Button
        if (Vector2.dst(touchX, touchY, 955f, 320f) < 80f) {
            game.getAudio().playStep();
            initGameplay();
            return;
        }

        // 4. Check Bottom Right "HINT" Button
        if (touchX >= 865f && touchX <= 1045f && touchY >= 60f && touchY <= 240f) {
            triggerHint();
            return;
        }

        // 5. Check Tap on Arrow Grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                ArrowTile tile = grid[x][y];
                if (tile == null || tile.isEscaped || tile.isFlying) continue;

                float tx = boardOriginX + x * (tileSize + tileGap);
                float ty = boardOriginY + y * (tileSize + tileGap);

                if (touchX >= tx && touchX <= tx + tileSize && touchY >= ty && touchY <= ty + tileSize) {
                    onArrowTapped(x, y);
                    return;
                }
            }
        }
    }

    private void onArrowTapped(int x, int y) {
        ArrowTile tile = grid[x][y];
        if (tile == null || tile.isEscaped || tile.isFlying) return;

        boolean canEscape = PuzzleLevelGenerator.canArrowEscape(grid, x, y, gridSize);
        movesCount++;

        if (canEscape) {
            // RELEASE ARROW - FLY OFF BOARD
            tile.startFlyEscape();
            tile.isHinted = false;
            game.getAudio().playTrigger();
            spawnEscapeBurst(boardOriginX + x * (tileSize + tileGap) + tileSize / 2f,
                             boardOriginY + y * (tileSize + tileGap) + tileSize / 2f,
                             tile.getArrowGlyphColor());
        } else {
            // BLOCKED - WOBBLE & SHAKE ERROR
            tile.triggerShake();
            game.getAudio().playStep();
            game.getAudio().triggerHaptic();
        }
    }

    private void triggerHint() {
        // Find the first available arrow that can escape
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                ArrowTile tile = grid[x][y];
                if (tile != null && !tile.isEscaped && !tile.isFlying) {
                    if (PuzzleLevelGenerator.canArrowEscape(grid, x, y, gridSize)) {
                        tile.isHinted = true;
                        game.getAudio().playRotate();
                        return;
                    }
                }
            }
        }
    }

    private void spawnEscapeBurst(float x, float y, Color col) {
        for (int i = 0; i < 18; i++) {
            EscapeParticle p = new EscapeParticle();
            p.x = x;
            p.y = y;
            float angle = (float) (Math.random() * Math.PI * 2);
            float speed = 250f + (float) Math.random() * 400f;
            p.vx = (float) Math.cos(angle) * speed;
            p.vy = (float) Math.sin(angle) * speed;
            p.life = 0.5f + (float) Math.random() * 0.4f;
            p.maxLife = p.life;
            p.color = new Color(col.r, col.g, col.b, 1f);
            particles.add(p);
        }
    }

    private void renderBackgroundDecorations(ShapeRenderer sr) {
        // Subtle wall backing
        sr.setColor(0.06f, 0.07f, 0.09f, 1f);
        sr.rect(0, 0, 1080, 1920);
    }

    private void renderStoneBoardPlinth(ShapeRenderer sr) {
        float plinthX = boardOriginX - 25f;
        float plinthY = boardOriginY - 25f;
        float plinthW = gridSize * tileSize + (gridSize - 1) * tileGap + 50f;
        float plinthH = gridSize * tileSize + (gridSize - 1) * tileGap + 50f;

        // Plinth Drop Shadow
        sr.setColor(0.02f, 0.02f, 0.03f, 0.9f);
        sr.rect(plinthX + 8f, plinthY - 10f, plinthW, plinthH);

        // Ancient Granite Plinth Base
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.rect(plinthX, plinthY, plinthW, plinthH);

        // Gold Beveled Outer Trim
        sr.setColor(0.40f, 0.32f, 0.15f, 1f);
        sr.rect(plinthX, plinthY + plinthH - 5f, plinthW, 5f);
        sr.rect(plinthX, plinthY, 5f, plinthH);
        sr.setColor(0.08f, 0.09f, 0.11f, 1f);
        sr.rect(plinthX, plinthY, plinthW, 5f);
        sr.rect(plinthX + plinthW - 5f, plinthY, 5f, plinthH);

        // Empty tile sockets on board bed
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                float sx = boardOriginX + x * (tileSize + tileGap);
                float sy = boardOriginY + y * (tileSize + tileGap);
                sr.setColor(0.09f, 0.10f, 0.12f, 1f);
                sr.rect(sx, sy, tileSize, tileSize);
                sr.setColor(0.06f, 0.07f, 0.08f, 1f);
                sr.rect(sx + 4f, sy + 4f, tileSize - 8f, tileSize - 8f);
            }
        }
    }

    private void renderArrowTiles(ShapeRenderer sr) {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                ArrowTile tile = grid[x][y];
                if (tile == null || tile.isEscaped) continue;

                float baseX = boardOriginX + x * (tileSize + tileGap) + tile.shakeOffsetX + tile.flyOffsetX;
                float baseY = boardOriginY + y * (tileSize + tileGap) + tile.shakeOffsetY + tile.flyOffsetY;

                // Tile Drop Shadow
                sr.setColor(0.02f, 0.02f, 0.03f, 0.7f);
                sr.rect(baseX + 4f, baseY - 5f, tileSize, tileSize);

                // Tile Base Block
                Color baseColor = tile.getBaseColor();
                sr.setColor(baseColor);
                sr.rect(baseX, baseY, tileSize, tileSize);

                // Top & Left Highlight
                sr.setColor(baseColor.r + 0.15f, baseColor.g + 0.15f, baseColor.b + 0.15f, 1f);
                sr.rect(baseX, baseY + tileSize - 4f, tileSize, 4f);
                sr.rect(baseX, baseY, 4f, tileSize);

                // Bottom & Right Shadow
                sr.setColor(baseColor.r * 0.6f, baseColor.g * 0.6f, baseColor.b * 0.6f, 1f);
                sr.rect(baseX, baseY, tileSize, 4f);
                sr.rect(baseX + tileSize - 4f, baseY, 4f, tileSize);

                // Hint Border Glowing Accent
                if (tile.isHinted) {
                    sr.setColor(0.3f, 1.0f, 0.5f, 0.8f);
                    sr.rect(baseX - 3f, baseY - 3f, tileSize + 6f, 3f);
                    sr.rect(baseX - 3f, baseY + tileSize, tileSize + 6f, 3f);
                    sr.rect(baseX - 3f, baseY, 3f, tileSize);
                    sr.rect(baseX + tileSize, baseY, 3f, tileSize);
                }

                // Render Carved Directional Arrow Glyph
                renderArrowGlyph(sr, baseX + tileSize / 2f, baseY + tileSize / 2f, tile.direction, tile.getArrowGlyphColor());
            }
        }
    }

    private void renderArrowGlyph(ShapeRenderer sr, float cx, float cy, Direction dir, Color col) {
        sr.setColor(col);
        float shaftW = 22f;
        float shaftL = 55f;
        float headW = 60f;
        float headL = 40f;

        switch (dir) {
            case NORTH:
                sr.rect(cx - shaftW / 2f, cy - shaftL / 2f, shaftW, shaftL);
                sr.triangle(cx, cy + shaftL / 2f + headL,
                            cx - headW / 2f, cy + shaftL / 2f,
                            cx + headW / 2f, cy + shaftL / 2f);
                break;
            case SOUTH:
                sr.rect(cx - shaftW / 2f, cy - shaftL / 2f, shaftW, shaftL);
                sr.triangle(cx, cy - shaftL / 2f - headL,
                            cx - headW / 2f, cy - shaftL / 2f,
                            cx + headW / 2f, cy - shaftL / 2f);
                break;
            case EAST:
                sr.rect(cx - shaftL / 2f, cy - shaftW / 2f, shaftL, shaftW);
                sr.triangle(cx + shaftL / 2f + headL, cy,
                            cx + shaftL / 2f, cy + headW / 2f,
                            cx + shaftL / 2f, cy - headW / 2f);
                break;
            case WEST:
                sr.rect(cx - shaftL / 2f, cy - shaftW / 2f, shaftL, shaftW);
                sr.triangle(cx - shaftL / 2f - headL, cy,
                            cx - shaftL / 2f, cy + headW / 2f,
                            cx - shaftL / 2f, cy - headW / 2f);
                break;
        }
    }

    private void renderParticles(ShapeRenderer sr) {
        for (EscapeParticle p : particles) {
            float alpha = p.life / p.maxLife;
            sr.setColor(p.color.r, p.color.g, p.color.b, alpha);
            sr.circle(p.x, p.y, 6f * alpha);
        }
    }

    private void renderAncientButtonBackings(ShapeRenderer sr) {
        // 1. Top Left Pause Button
        float px = 90f, py = 1810f, pr = 46f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.circle(px + 4f, py - 6f, pr);
        sr.setColor(0.48f, 0.38f, 0.20f, 1f);
        sr.circle(px, py, pr);
        sr.setColor(0.24f, 0.18f, 0.10f, 1f);
        sr.circle(px, py, pr - 6f);
        sr.setColor(0.95f, 0.85f, 0.45f, 1f);
        sr.rect(px - 14f, py - 18f, 9f, 36f);
        sr.rect(px + 5f, py - 18f, 9f, 36f);

        // 2. Top Header Ancient Stone Banner Slab
        float hx = 160f, hy = 1715f, hw = 630f, hh = 165f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.rect(hx + 6f, hy - 8f, hw, hh);
        sr.setColor(0.24f, 0.26f, 0.30f, 1f);
        sr.rect(hx, hy, hw, hh);
        sr.setColor(0.40f, 0.42f, 0.48f, 1f);
        sr.rect(hx, hy + hh - 4f, hw, 4f);
        sr.rect(hx, hy, 4f, hh);
        sr.setColor(0.12f, 0.13f, 0.15f, 1f);
        sr.rect(hx, hy, hw, 4f);
        sr.rect(hx + hw - 4f, hy, 4f, hh);
        sr.setColor(0.10f, 0.11f, 0.14f, 1f);
        sr.rect(hx + 8f, hy + 8f, hw - 16f, hh - 16f);
        sr.setColor(0.85f, 0.70f, 0.20f, 0.45f);
        sr.rect(hx + 10f, hy + 10f, hw - 20f, 2f);

        // 3. Top Right Remaining Counter Pill
        float cx = 810f, cy = 1760f, cw = 230f, ch = 100f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.rect(cx + 4f, cy - 6f, cw, ch);
        sr.setColor(0.48f, 0.38f, 0.15f, 1f);
        sr.rect(cx, cy, cw, ch);
        sr.setColor(0.18f, 0.15f, 0.08f, 1f);
        sr.rect(cx + 4f, cy + 4f, cw - 8f, ch - 8f);
        sr.setColor(0.98f, 0.80f, 0.18f, 1f);
        sr.circle(cx + 42f, cy + ch / 2f, 24f);
        sr.setColor(0.65f, 0.45f, 0.08f, 1f);
        sr.circle(cx + 42f, cy + ch / 2f, 16f);
        sr.setColor(0.98f, 0.85f, 0.30f, 1f);
        sr.circle(cx + 42f, cy + ch / 2f, 8f);

        // 4. Bottom Left "LEVELS" Button
        float lx = 40f, ly = 60f, lw = 180f, lh = 180f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.rect(lx + 5f, ly - 6f, lw, lh);
        sr.setColor(0.24f, 0.26f, 0.30f, 1f);
        sr.rect(lx, ly, lw, lh);
        sr.setColor(0.40f, 0.42f, 0.48f, 1f);
        sr.rect(lx, ly + lh - 4f, lw, 4f);
        sr.rect(lx, ly, 4f, lh);
        sr.setColor(0.12f, 0.13f, 0.16f, 1f);
        sr.rect(lx, ly, lw, 4f);
        sr.rect(lx + lw - 4f, ly, 4f, lh);
        sr.rect(lx + 8f, ly + 8f, lw - 16f, lh - 16f);
        sr.setColor(0.95f, 0.85f, 0.5f, 1f);
        float mgX = lx + lw / 2f - 24f;
        float mgY = ly + lh * 0.62f;
        sr.rect(mgX, mgY, 20f, 20f);
        sr.rect(mgX + 28f, mgY, 20f, 20f);
        sr.rect(mgX, mgY - 28f, 20f, 20f);
        sr.rect(mgX + 28f, mgY - 28f, 20f, 20f);

        // 5. Bottom Right "RESET" Button
        float rx = 955f, ry = 320f, rr = 75f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.circle(rx + 5f, ry - 6f, rr);
        sr.setColor(0.28f, 0.30f, 0.35f, 1f);
        sr.circle(rx, ry, rr);
        sr.setColor(0.42f, 0.45f, 0.52f, 1f);
        sr.circle(rx, ry, rr - 4f);
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.circle(rx, ry, rr - 10f);
        sr.setColor(0.95f, 0.85f, 0.5f, 1f);
        sr.circle(rx, ry + 12f, 24f);
        sr.setColor(0.14f, 0.15f, 0.18f, 1f);
        sr.circle(rx, ry + 12f, 16f);
        sr.setColor(0.95f, 0.85f, 0.5f, 1f);
        sr.triangle(rx + 10f, ry + 36f, rx + 28f, ry + 24f, rx + 28f, ry + 42f);

        // 6. Bottom Right "HINT" Button
        float hbx = 865f, hby = 60f, hbw = 180f, hbh = 180f;
        sr.setColor(0.02f, 0.02f, 0.03f, 0.85f);
        sr.rect(hbx + 5f, hby - 6f, hbw, hbh);
        sr.setColor(0.48f, 0.38f, 0.16f, 1f);
        sr.rect(hbx, hby, hbw, hbh);
        sr.setColor(0.22f, 0.17f, 0.10f, 1f);
        sr.rect(hbx + 6f, hby + 6f, hbw - 12f, hbh - 12f);
        sr.setColor(0.98f, 0.85f, 0.30f, 1f);
        sr.circle(hbx + 14f, hby + hbh - 14f, 4f);
        sr.circle(hbx + hbw - 14f, hby + hbh - 14f, 4f);
        sr.circle(hbx + 14f, hby + 14f, 4f);
        sr.circle(hbx + hbw - 14f, hby + 14f, 4f);
        float lbX = hbx + hbw / 2f;
        float lbY = hby + hbh * 0.65f;
        sr.setColor(1.0f, 0.88f, 0.20f, 1f);
        sr.circle(lbX, lbY, 20f);
        sr.rect(lbX - 10f, lbY - 26f, 20f, 12f);
        sr.setColor(0.85f, 0.70f, 0.15f, 1f);
        sr.rect(lbX - 6f, lbY - 32f, 12f, 6f);
    }

    private void renderHeaderUI(SpriteBatch sb) {
        float plaqueX = 160f;
        float plaqueY = 1715f;
        float plaqueW = 630f;

        // Level Number
        titleFont.setColor(0.98f, 0.84f, 0.40f, 1f);
        titleFont.draw(sb, "LEVEL " + levelNumber, plaqueX, plaqueY + 148f, plaqueW, 1, false);

        // Difficulty Mode Badge
        DifficultyMode mode = levelData.mode;
        smallFont.setColor(mode.r, mode.g, mode.b, 1f);
        smallFont.draw(sb, "[" + mode.title + "]", plaqueX, plaqueY + 102f, plaqueW, 1, false);

        // Instructions
        smallFont.setColor(0.70f, 0.75f, 0.80f, 1f);
        smallFont.draw(sb, "Tap arrows to release them off board!", plaqueX - 30f, plaqueY + 45f, plaqueW, 1, false);

        // Counter
        regularFont.setColor(0.95f, 0.90f, 0.75f, 1f);
        regularFont.draw(sb, remainingArrows + " / " + totalArrows, 890f, 1825f);
    }

    private void renderBottomButtons(SpriteBatch sb) {
        drawStoneButtonWithSubtext(40f, 60f, 170f, 170f, "", "LEVELS");
        drawStoneButtonWithSubtext(870f, 240f, 170f, 160f, "", "RESET");
        drawStoneButtonWithSubtext(870f, 60f, 170f, 160f, "", "HINT\n$ 50");
    }

    private void drawStoneButtonWithSubtext(float x, float y, float w, float h, String icon, String text) {
        smallFont.setColor(0.95f, 0.90f, 0.75f, 1f);
        smallFont.draw(batch, text, x, y + h * 0.38f, w, 1, false);
    }

    private void renderVictoryModalBacking(ShapeRenderer sr) {
        float cx = 540f;
        float cy = 960f;
        float mw = 760f;
        float mh = 480f;

        // Dark dim background
        sr.setColor(0.02f, 0.03f, 0.04f, 0.75f);
        sr.rect(0, 0, 1080, 1920);

        // Modal Drop Shadow
        sr.setColor(0.01f, 0.02f, 0.02f, 0.9f);
        sr.rect(cx - mw / 2f + 10f, cy - mh / 2f - 12f, mw, mh);

        // Granite Modal Plaque
        sr.setColor(0.20f, 0.22f, 0.26f, 1f);
        sr.rect(cx - mw / 2f, cy - mh / 2f, mw, mh);

        // Gold Trim Border
        sr.setColor(0.85f, 0.70f, 0.20f, 1f);
        sr.rect(cx - mw / 2f, cy + mh / 2f - 6f, mw, 6f);
        sr.rect(cx - mw / 2f, cy - mh / 2f, mw, 6f);
        sr.rect(cx - mw / 2f, cy - mh / 2f, 6f, mh);
        sr.rect(cx + mw / 2f - 6f, cy - mh / 2f, 6f, mh);

        // Inner Dark Sunk Pad
        sr.setColor(0.10f, 0.11f, 0.14f, 1f);
        sr.rect(cx - mw / 2f + 14f, cy - mh / 2f + 14f, mw - 28f, mh - 28f);

        // 3 Victory Stars
        float starY = cy + 60f;
        for (int s = -1; s <= 1; s++) {
            float sx = cx + s * 100f;
            sr.setColor(1.0f, 0.85f, 0.20f, 1f);
            sr.circle(sx, starY, 28f);
            sr.setColor(0.70f, 0.50f, 0.10f, 1f);
            sr.circle(sx, starY, 18f);
            sr.setColor(1.0f, 0.95f, 0.40f, 1f);
            sr.circle(sx, starY, 10f);
        }
    }

    private void renderVictoryOverlay(SpriteBatch sb) {
        float cx = 540f;
        float cy = 960f;

        titleFont.setColor(0.4f, 1.0f, 0.5f, 1f);
        titleFont.draw(sb, "LEVEL " + levelNumber + " CLEARED!", cx - 400f, cy + 180f, 800f, 1, false);

        DifficultyMode mode = levelData.mode;
        regularFont.setColor(mode.r, mode.g, mode.b, 1f);
        regularFont.draw(sb, mode.title + " (All Arrows Released)", cx - 400f, cy + 120f, 800f, 1, false);

        regularFont.setColor(1f, 0.9f, 0.3f, 1f);
        regularFont.draw(sb, "+100 COINS REWARD", cx - 300f, cy - 20f, 600f, 1, false);

        int nextLvl = levelNumber + 1;
        DifficultyMode nextMode = PuzzleLevelGenerator.getDifficultyForLevel(nextLvl);
        smallFont.setColor(0.35f, 0.95f, 0.45f, 1f);
        if (nextLvl <= 5000) {
            smallFont.draw(sb, "Tap anywhere for Level " + nextLvl + " (" + nextMode.title + ") >", cx - 450f, cy - 90f, 900f, 1, false);
        } else {
            smallFont.draw(sb, "All 5000 Levels Mastered! Tap to replay >", cx - 450f, cy - 90f, 900f, 1, false);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}

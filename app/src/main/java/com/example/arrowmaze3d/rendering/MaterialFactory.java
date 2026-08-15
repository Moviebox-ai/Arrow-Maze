package com.example.arrowmaze3d.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.example.arrowmaze3d.utilities.Constants;

public class MaterialFactory {

    public static Material createTileMaterial(String theme) {
        Color baseColor;
        if (Constants.THEME_CITADEL.equals(theme)) {
            baseColor = new Color(0x63 / 255f, 0x48 / 255f, 0x32 / 255f, 1f);
        } else if (Constants.THEME_CRYSTAL.equals(theme)) {
            baseColor = new Color(0x1E / 255f, 0x29 / 255f, 0x4B / 255f, 1f);
        } else if (Constants.THEME_OBSERVATORY.equals(theme)) {
            baseColor = new Color(0x11 / 255f, 0x18 / 255f, 0x27 / 255f, 1f);
        } else {
            baseColor = new Color(0x3B / 255f, 0x42 / 255f, 0x52 / 255f, 1f);
        }
        return new Material(
            ColorAttribute.createDiffuse(baseColor),
            ColorAttribute.createSpecular(new Color(0.8f, 0.8f, 0.8f, 1f)),
            FloatAttribute.createShininess(16f)
        );
    }

    public static Material createStoneMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x6B / 255f, 0x72 / 255f, 0x80 / 255f, 1f)), // Cobblestone slate
            ColorAttribute.createSpecular(new Color(0.7f, 0.7f, 0.75f, 1f)),
            FloatAttribute.createShininess(24f)
        );
    }

    public static Material createGrassMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x4A / 255f, 0xA8 / 255f, 0x27 / 255f, 1f)), // Lush fantasy meadow green
            ColorAttribute.createEmissive(new Color(0x15 / 255f, 0x42 / 255f, 0x08 / 255f, 1f)),
            ColorAttribute.createSpecular(new Color(0.4f, 0.6f, 0.3f, 1f)),
            FloatAttribute.createShininess(8f)
        );
    }

    public static Material createGrassSideMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x4E / 255f, 0x36 / 255f, 0x29 / 255f, 1f)) // Rich earth dirt
        );
    }

    public static Material createWaterMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x0E / 255f, 0xA5 / 255f, 0xE9 / 255f, 0.9f)), // Crystal cyan ocean water
            ColorAttribute.createEmissive(new Color(0x02 / 255f, 0x48 / 255f, 0x73 / 255f, 0.5f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(64f),
            new BlendingAttribute(0.92f)
        );
    }

    public static Material createSpikeMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x47 / 255f, 0x55 / 255f, 0x69 / 255f, 1f)), // Steel spikes
            ColorAttribute.createSpecular(new Color(0.95f, 0.95f, 1f, 1f)),
            FloatAttribute.createShininess(96f)
        );
    }

    public static Material createKeyMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0xFA / 255f, 0xCC / 255f, 0x15 / 255f, 1f)), // 24k Gold
            ColorAttribute.createEmissive(new Color(0xCA / 255f, 0x8A / 255f, 0x04 / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createLockGateMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0xEA / 255f, 0x58 / 255f, 0x0C / 255f, 1f)), // Heavy bronze lock
            ColorAttribute.createEmissive(new Color(0x7C / 255f, 0x2D / 255f, 0x12 / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(64f)
        );
    }

    public static Material createStarMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0xFB / 255f, 0xBF / 255f, 0x24 / 255f, 1f)), // Glowing Star
            ColorAttribute.createEmissive(new Color(0xF5 / 255f, 0x9E / 255f, 0x0B / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createFlowerMaterial(Color flowerColor) {
        return new Material(
            ColorAttribute.createDiffuse(flowerColor),
            ColorAttribute.createEmissive(flowerColor.cpy().mul(0.4f))
        );
    }

    public static Material createWoodMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x78 / 255f, 0x35 / 255f, 0x0F / 255f, 1f))
        );
    }

    public static Material createFoliageMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x22 / 255f, 0xC5 / 255f, 0x5E / 255f, 1f)),
            ColorAttribute.createEmissive(new Color(0x15 / 255f, 0x80 / 255f, 0x3D / 255f, 1f))
        );
    }

    public static Material createCliffMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x1E / 255f, 0x29 / 255f, 0x3B / 255f, 1f)),
            ColorAttribute.createSpecular(new Color(0.3f, 0.4f, 0.4f, 1f))
        );
    }

    public static Material createArrowBlockMaterial(Color arrowColor) {
        return new Material(
            ColorAttribute.createDiffuse(arrowColor),
            ColorAttribute.createEmissive(arrowColor.cpy().mul(0.35f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(48f)
        );
    }

    public static Material createArrowMaterial(Color arrowColor) {
        return new Material(
            ColorAttribute.createDiffuse(Color.WHITE),
            ColorAttribute.createEmissive(new Color(0.9f, 0.9f, 0.9f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(64f)
        );
    }

    public static Material createDungeonStoneMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x37 / 255f, 0x41 / 255f, 0x51 / 255f, 1f)),
            ColorAttribute.createSpecular(new Color(0.85f, 0.88f, 0.95f, 1f)),
            FloatAttribute.createShininess(32f)
        );
    }

    public static Material createKnightArmorMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x1E / 255f, 0x29 / 255f, 0x3B / 255f, 1f)), // Dark Obsidian Steel
            ColorAttribute.createEmissive(new Color(0x0E / 255f, 0x74 / 255f, 0x90 / 255f, 0.4f)), // Cyan Energy Underglow
            ColorAttribute.createSpecular(new Color(0.95f, 0.98f, 1.0f, 1f)),
            FloatAttribute.createShininess(96f)
        );
    }

    public static Material createGoldTrimMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0xFB / 255f, 0xBF / 255f, 0x24 / 255f, 1f)), // 24k Gold
            ColorAttribute.createEmissive(new Color(0xD9 / 255f, 0x77 / 255f, 0x06 / 255f, 0.6f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createVisorGlowMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x38 / 255f, 0xBD / 255f, 0xF8 / 255f, 1f)), // Electric Cyan Visor
            ColorAttribute.createEmissive(new Color(0x02 / 255f, 0x84 / 255f, 0xC7 / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createFlameMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(1.0f, 0.6f, 0.1f, 1f)),
            ColorAttribute.createEmissive(new Color(1.0f, 0.35f, 0.05f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createIronMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x1F / 255f, 0x24 / 255f, 0x2D / 255f, 1f)), // Wrought Iron
            ColorAttribute.createSpecular(new Color(0.6f, 0.6f, 0.65f, 1f)),
            FloatAttribute.createShininess(48f)
        );
    }

    public static Material createExitDoorGlowMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0x22 / 255f, 0xC5 / 255f, 0x5E / 255f, 1f)), // Neon Green Exit
            ColorAttribute.createEmissive(new Color(0x16 / 255f, 0xA3 / 255f, 0x4A / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createPlayerMaterial() {
        return createKnightArmorMaterial();
    }

    public static Material createGoalMaterial() {
        return new Material(
            ColorAttribute.createDiffuse(new Color(0xC0 / 255f, 0x26 / 255f, 0xD3 / 255f, 1f)), // Glowing Magenta Goal
            ColorAttribute.createEmissive(new Color(0xA8 / 255f, 0x55 / 255f, 0xF7 / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }

    public static Material createGateMaterial(boolean isOpen) {
        Color c = isOpen ? new Color(0x22 / 255f, 0xC5 / 255f, 0x5E / 255f, 0.7f) : new Color(0xEF / 255f, 0x44 / 255f, 0x44 / 255f, 0.9f);
        return new Material(
            ColorAttribute.createDiffuse(c),
            ColorAttribute.createEmissive(c.cpy().mul(0.5f))
        );
    }

    public static Material createPortalMaterial() {
        Color c = new Color(0xC0 / 255f, 0x26 / 255f, 0xD3 / 255f, 1f); // Neon Magenta/Purple Portal
        return new Material(
            ColorAttribute.createDiffuse(c),
            ColorAttribute.createEmissive(new Color(0xE8 / 255f, 0x79 / 255f, 0xF9 / 255f, 1f)),
            ColorAttribute.createSpecular(Color.WHITE),
            FloatAttribute.createShininess(128f)
        );
    }
}

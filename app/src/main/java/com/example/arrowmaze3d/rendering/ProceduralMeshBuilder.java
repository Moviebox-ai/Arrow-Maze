package com.example.arrowmaze3d.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.example.arrowmaze3d.utilities.Constants;

public class ProceduralMeshBuilder {
    private final ModelBuilder modelBuilder = new ModelBuilder();

    public Model createFloorTileModel(String theme) {
        return createStoneTileModel(theme);
    }

    public Model createStoneTileModel(String theme) {
        float size = Constants.GRID_CELL_SIZE;
        float thickness = 0.5f;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Material stoneMat = MaterialFactory.createStoneMaterial();
        MeshPartBuilder mpb = modelBuilder.part("stone", GL20.GL_TRIANGLES, attr, stoneMat);
        // Base block: top surface is exactly at Y = 0
        mpb.box(0, -thickness * 0.5f, 0, size * 0.96f, thickness, size * 0.96f);
        return modelBuilder.end();
    }

    public Model createGrassTileModel(boolean withFlowers, boolean withTree) {
        float size = Constants.GRID_CELL_SIZE;
        float thickness = 0.5f;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // Dirt Base: from Y = -0.5 to Y = -0.1
        Material dirtMat = MaterialFactory.createGrassSideMaterial();
        MeshPartBuilder mpbDirt = modelBuilder.part("dirt", GL20.GL_TRIANGLES, attr, dirtMat);
        mpbDirt.box(0, -0.3f, 0, size * 0.96f, 0.4f, size * 0.96f);

        // Grass Top: from Y = -0.1 to Y = 0.0 (flush with stone tiles)
        Material grassMat = MaterialFactory.createGrassMaterial();
        MeshPartBuilder mpbGrass = modelBuilder.part("grass", GL20.GL_TRIANGLES, attr, grassMat);
        mpbGrass.box(0, -0.05f, 0, size * 0.98f, 0.1f, size * 0.98f);

        // 3D Flowers
        if (withFlowers) {
            Material flowerMat1 = MaterialFactory.createFlowerMaterial(new Color(0xE8 / 255f, 0x79 / 255f, 0xF9 / 255f, 1f));
            MeshPartBuilder mpbFl1 = modelBuilder.part("fl1", GL20.GL_TRIANGLES, attr, flowerMat1);
            Matrix4 m1 = new Matrix4().trn(0.4f, 0.08f, 0.4f);
            mpbFl1.setVertexTransform(m1);
            mpbFl1.sphere(0.16f, 0.16f, 0.16f, 6, 6);

            Material flowerMat2 = MaterialFactory.createFlowerMaterial(new Color(0x38 / 255f, 0xBD / 255f, 0xF8 / 255f, 1f));
            MeshPartBuilder mpbFl2 = modelBuilder.part("fl2", GL20.GL_TRIANGLES, attr, flowerMat2);
            Matrix4 m2 = new Matrix4().trn(-0.4f, 0.08f, -0.35f);
            mpbFl2.setVertexTransform(m2);
            mpbFl2.sphere(0.14f, 0.14f, 0.14f, 6, 6);
        }

        // Cute Mini Tree
        if (withTree) {
            Material woodMat = MaterialFactory.createWoodMaterial();
            MeshPartBuilder mpbWood = modelBuilder.part("wood", GL20.GL_TRIANGLES, attr, woodMat);
            Matrix4 mw = new Matrix4().trn(0, 0.25f, 0);
            mpbWood.setVertexTransform(mw);
            mpbWood.cylinder(0.2f, 0.5f, 0.2f, 8);

            Material leafMat = MaterialFactory.createFoliageMaterial();
            MeshPartBuilder mpbLeaf = modelBuilder.part("leaf", GL20.GL_TRIANGLES, attr, leafMat);
            Matrix4 ml = new Matrix4().trn(0, 0.65f, 0);
            mpbLeaf.setVertexTransform(ml);
            mpbLeaf.sphere(0.75f, 0.8f, 0.75f, 8, 8);
        }

        return modelBuilder.end();
    }

    public Model createWaterTileModel() {
        float size = Constants.GRID_CELL_SIZE;
        float thickness = 0.5f;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // Water Basin/Stone floor underneath
        Material stoneMat = MaterialFactory.createStoneMaterial();
        MeshPartBuilder mpbBasin = modelBuilder.part("basin", GL20.GL_TRIANGLES, attr, stoneMat);
        mpbBasin.box(0, -thickness * 0.5f, 0, size * 0.96f, thickness, size * 0.96f);

        // Water Surface: flush at Y = -0.02f
        Material waterMat = MaterialFactory.createWaterMaterial();
        MeshPartBuilder mpbWater = modelBuilder.part("water", GL20.GL_TRIANGLES, attr, waterMat);
        mpbWater.box(0, -0.02f, 0, size * 0.92f, 0.04f, size * 0.92f);

        // Ripple Ring at surface
        Material rippleMat = MaterialFactory.createPortalMaterial();
        MeshPartBuilder mpbRipple = modelBuilder.part("ripple", GL20.GL_TRIANGLES, attr, rippleMat);
        Matrix4 mr = new Matrix4().trn(0, 0.005f, 0);
        mpbRipple.setVertexTransform(mr);
        mpbRipple.cylinder(0.6f, 0.01f, 0.6f, 12);

        return modelBuilder.end();
    }

    public Model createWallTileModel(String theme) {
        float size = Constants.GRID_CELL_SIZE;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Material mat = MaterialFactory.createStoneMaterial();
        MeshPartBuilder mpb = modelBuilder.part("wall", GL20.GL_TRIANGLES, attr, mat);

        // Base foundation block (Y = -0.5f to Y = 0.0f)
        mpb.box(0, -0.25f, 0, size * 0.96f, 0.5f, size * 0.96f);

        // Low decorative boundary stone curb (Y = 0.0f to Y = 0.35f)
        mpb.box(0, 0.175f, 0, size * 0.92f, 0.35f, size * 0.92f);

        return modelBuilder.end();
    }

    public Model createSpikeModel() {
        modelBuilder.begin();
        Material spikeMat = MaterialFactory.createSpikeMaterial();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        MeshPartBuilder mpb = modelBuilder.part("spikes", GL20.GL_TRIANGLES, attr, spikeMat);

        // Base Plate
        mpb.box(0, 0.06f, 0, 1.6f, 0.12f, 1.6f);

        // 4 Spikes
        float[][] offsets = {{-0.4f, -0.4f}, {0.4f, -0.4f}, {-0.4f, 0.4f}, {0.4f, 0.4f}};
        for (float[] off : offsets) {
            Matrix4 coneMat = new Matrix4();
            coneMat.trn(off[0], 0.55f, off[1]);
            mpb.setVertexTransform(coneMat);
            mpb.cone(0.45f, 0.85f, 0.45f, 8);
        }

        return modelBuilder.end();
    }

    public Model createKeyModel() {
        modelBuilder.begin();
        Material keyMat = MaterialFactory.createKeyMaterial();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        MeshPartBuilder mpb = modelBuilder.part("key", GL20.GL_TRIANGLES, attr, keyMat);

        // Key Shaft
        Matrix4 shaftM = new Matrix4().trn(0, 0, 0);
        mpb.setVertexTransform(shaftM);
        mpb.cylinder(0.12f, 0.9f, 0.12f, 8);

        // Key Head Ring
        Matrix4 headM = new Matrix4().trn(0, 0.5f, 0);
        mpb.setVertexTransform(headM);
        mpb.sphere(0.38f, 0.38f, 0.38f, 8, 8);

        // Key Teeth
        Matrix4 toothM = new Matrix4().trn(0.16f, -0.3f, 0);
        mpb.setVertexTransform(toothM);
        mpb.box(0.24f, 0.12f, 0.12f);

        return modelBuilder.end();
    }

    public Model createLockGateModel() {
        float size = Constants.GRID_CELL_SIZE;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Material lockMat = MaterialFactory.createLockGateMaterial();
        MeshPartBuilder mpb = modelBuilder.part("lock", GL20.GL_TRIANGLES, attr, lockMat);

        // Lock Block
        mpb.box(0, 0.4f, 0, size * 0.94f, 0.85f, size * 0.94f);

        // Keyhole Plate
        Material goldMat = MaterialFactory.createKeyMaterial();
        MeshPartBuilder mpbHole = modelBuilder.part("hole", GL20.GL_TRIANGLES, attr, goldMat);
        Matrix4 hm = new Matrix4().trn(0, 0.5f, 0);
        mpbHole.setVertexTransform(hm);
        mpbHole.cylinder(0.45f, 0.9f, 0.45f, 10);

        return modelBuilder.end();
    }

    public Model createStarModel() {
        return createCoinModel();
    }

    public Model createCoinModel() {
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Material goldMat = MaterialFactory.createGoldTrimMaterial();
        MeshPartBuilder mpb = modelBuilder.part("coin_rim", GL20.GL_TRIANGLES, attr, goldMat);

        // 3D Medallion Cylinder with beveled rim
        Matrix4 m = new Matrix4().trn(0, 0.5f, 0);
        mpb.setVertexTransform(m);
        mpb.cylinder(0.7f, 0.16f, 0.7f, 16);

        // Center Star / Emblem
        Material coreMat = MaterialFactory.createVisorGlowMaterial();
        MeshPartBuilder mpbCore = modelBuilder.part("coin_core", GL20.GL_TRIANGLES, attr, coreMat);
        mpbCore.setVertexTransform(m);
        mpbCore.sphere(0.35f, 0.35f, 0.35f, 10, 10);

        return modelBuilder.end();
    }

    public Model createArrowBlockModel(Color blockColor) {
        float size = Constants.GRID_CELL_SIZE;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // 1. Lower Stone Bevel Base (Y = 0 to 0.15)
        Material stoneBaseMat = MaterialFactory.createDungeonStoneMaterial();
        MeshPartBuilder mpbBase = modelBuilder.part("base_pedestal", GL20.GL_TRIANGLES, attr, stoneBaseMat);
        mpbBase.box(0, 0.08f, 0, size * 0.95f, 0.16f, size * 0.95f);

        // 2. Colored Gemstone / Energy Tablet Body (Y = 0.15 to 0.38)
        Material bodyMat = MaterialFactory.createArrowBlockMaterial(blockColor);
        MeshPartBuilder mpbBody = modelBuilder.part("body_slab", GL20.GL_TRIANGLES, attr, bodyMat);
        mpbBody.box(0, 0.26f, 0, size * 0.88f, 0.20f, size * 0.88f);

        // 3. 4 Corner Metal Rivets
        Material metalMat = MaterialFactory.createIronMaterial();
        MeshPartBuilder mpbRivets = modelBuilder.part("rivets", GL20.GL_TRIANGLES, attr, metalMat);
        float offset = size * 0.38f;
        float[][] corners = {{-offset, -offset}, {offset, -offset}, {-offset, offset}, {offset, offset}};
        for (float[] c : corners) {
            Matrix4 rm = new Matrix4().trn(c[0], 0.36f, c[1]);
            mpbRivets.setVertexTransform(rm);
            mpbRivets.cylinder(0.12f, 0.08f, 0.12f, 8);
        }

        // 4. Embossed 3D Arrow Glyph (Pointing North / -Z)
        Material arrowMat = MaterialFactory.createArrowMaterial(blockColor);
        MeshPartBuilder mpbArrow = modelBuilder.part("arrow_glyph", GL20.GL_TRIANGLES, attr, arrowMat);

        // Arrow Shaft (Raised 3D box)
        mpbArrow.box(0f, 0.40f, 0.18f, 0.32f, 0.10f, 0.48f);

        // Arrow Head (Raised 3D Pyramid / Cone)
        Matrix4 coneMat = new Matrix4();
        coneMat.setToRotation(Vector3.X, -90);
        coneMat.trn(0f, 0.40f, -0.28f);
        mpbArrow.setVertexTransform(coneMat);
        mpbArrow.cone(0.68f, 0.52f, 0.68f, 12);

        return modelBuilder.end();
    }

    public Model createArrowModel(Color arrowColor) {
        return createArrowBlockModel(arrowColor);
    }

    public Model createPlayerModel() {
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // 1. Obsidian & Steel Knight Armor Torso
        Material armorMat = MaterialFactory.createKnightArmorMaterial();
        MeshPartBuilder mpbTorso = modelBuilder.part("knight_torso", GL20.GL_TRIANGLES, attr, armorMat);
        mpbTorso.box(0f, 0.45f, 0f, 0.72f, 0.65f, 0.60f);

        // 2. Gold Belt & Buckle
        Material goldMat = MaterialFactory.createGoldTrimMaterial();
        MeshPartBuilder mpbBelt = modelBuilder.part("knight_belt", GL20.GL_TRIANGLES, attr, goldMat);
        mpbBelt.box(0f, 0.22f, 0f, 0.76f, 0.14f, 0.64f);

        // 3. 3D Shoulder Pauldrons (Left and Right)
        MeshPartBuilder mpbPauldrons = modelBuilder.part("knight_pauldrons", GL20.GL_TRIANGLES, attr, armorMat);
        Matrix4 leftP = new Matrix4().trn(-0.44f, 0.68f, 0f);
        mpbPauldrons.setVertexTransform(leftP);
        mpbPauldrons.sphere(0.32f, 0.28f, 0.32f, 8, 8);

        Matrix4 rightP = new Matrix4().trn(0.44f, 0.68f, 0f);
        mpbPauldrons.setVertexTransform(rightP);
        mpbPauldrons.sphere(0.32f, 0.28f, 0.32f, 8, 8);

        // 4. Knight Helmet / Head
        MeshPartBuilder mpbHead = modelBuilder.part("knight_helmet", GL20.GL_TRIANGLES, attr, armorMat);
        Matrix4 headMat = new Matrix4().trn(0f, 1.02f, 0f);
        mpbHead.setVertexTransform(headMat);
        mpbHead.sphere(0.52f, 0.52f, 0.52f, 12, 12);

        // 5. Glowing Cyan Visor Line
        Material visorMat = MaterialFactory.createVisorGlowMaterial();
        MeshPartBuilder mpbVisor = modelBuilder.part("knight_visor", GL20.GL_TRIANGLES, attr, visorMat);
        Matrix4 visorM = new Matrix4().trn(0f, 1.02f, -0.22f);
        mpbVisor.setVertexTransform(visorM);
        mpbVisor.box(0.38f, 0.12f, 0.16f);

        // 6. Floating Mana Halo / Compass Ring at feet
        MeshPartBuilder mpbHalo = modelBuilder.part("player_halo", GL20.GL_TRIANGLES, attr, visorMat);
        Matrix4 haloM = new Matrix4().trn(0f, 0.05f, 0f);
        mpbHalo.setVertexTransform(haloM);
        mpbHalo.cylinder(0.9f, 0.04f, 0.9f, 16);

        return modelBuilder.end();
    }

    public Model createDungeonDoorModel() {
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // 1. Stone Arch Pillars
        Material stoneMat = MaterialFactory.createDungeonStoneMaterial();
        MeshPartBuilder mpbArch = modelBuilder.part("stone_arch", GL20.GL_TRIANGLES, attr, stoneMat);
        // Left pillar
        mpbArch.box(-0.95f, 1.0f, 0f, 0.35f, 2.0f, 0.5f);
        // Right pillar
        mpbArch.box(0.95f, 1.0f, 0f, 0.35f, 2.0f, 0.5f);
        // Top lintel arch
        mpbArch.box(0f, 2.1f, 0f, 2.25f, 0.35f, 0.55f);

        // 2. Heavy Oak Wooden Door Planks
        Material woodMat = MaterialFactory.createWoodMaterial();
        MeshPartBuilder mpbWood = modelBuilder.part("door_planks", GL20.GL_TRIANGLES, attr, woodMat);
        mpbWood.box(0f, 0.95f, 0f, 1.6f, 1.9f, 0.2f);

        // 3. Iron Hinges & Crossbars
        Material ironMat = MaterialFactory.createIronMaterial();
        MeshPartBuilder mpbIron = modelBuilder.part("door_iron", GL20.GL_TRIANGLES, attr, ironMat);
        mpbIron.box(0f, 0.4f, 0.12f, 1.65f, 0.16f, 0.08f);
        mpbIron.box(0f, 1.5f, 0.12f, 1.65f, 0.16f, 0.08f);

        // 4. Illuminated Green "EXIT" Sign Plaque
        Material exitMat = MaterialFactory.createExitDoorGlowMaterial();
        MeshPartBuilder mpbExit = modelBuilder.part("door_exit_sign", GL20.GL_TRIANGLES, attr, exitMat);
        Matrix4 em = new Matrix4().trn(0f, 2.38f, 0.15f);
        mpbExit.setVertexTransform(em);
        mpbExit.box(1.2f, 0.32f, 0.18f);

        return modelBuilder.end();
    }

    public Model createTorchSconceModel() {
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // 1. Wrought Iron Wall Sconce Bracket
        Material ironMat = MaterialFactory.createIronMaterial();
        MeshPartBuilder mpbBracket = modelBuilder.part("torch_iron", GL20.GL_TRIANGLES, attr, ironMat);
        mpbBracket.box(0f, 0.4f, 0f, 0.14f, 0.8f, 0.14f);
        mpbBracket.cylinder(0.35f, 0.22f, 0.35f, 10);

        // 2. Glowing Flame Core
        Material flameMat = MaterialFactory.createFlameMaterial();
        MeshPartBuilder mpbFlame = modelBuilder.part("torch_flame", GL20.GL_TRIANGLES, attr, flameMat);
        Matrix4 fm = new Matrix4().trn(0f, 0.75f, 0f);
        mpbFlame.setVertexTransform(fm);
        mpbFlame.cone(0.36f, 0.55f, 0.36f, 10);

        return modelBuilder.end();
    }

    public Model createVortexPortalModel() {
        modelBuilder.begin();
        Material mat = MaterialFactory.createPortalMaterial();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        MeshPartBuilder mpb = modelBuilder.part("portal_ring", GL20.GL_TRIANGLES, attr, mat);

        // Vertical Ring Portal
        Matrix4 ringM = new Matrix4().setToRotation(Vector3.X, 90).trn(0, 0.85f, 0);
        mpb.setVertexTransform(ringM);
        mpb.cylinder(1.4f, 0.22f, 1.4f, 16);

        // Inner glowing core
        Material innerMat = MaterialFactory.createGoalMaterial();
        MeshPartBuilder mpbCore = modelBuilder.part("core", GL20.GL_TRIANGLES, attr, innerMat);
        Matrix4 coreM = new Matrix4().trn(0, 0.85f, 0);
        mpbCore.setVertexTransform(coreM);
        mpbCore.sphere(0.6f, 0.6f, 0.6f, 10, 10);

        return modelBuilder.end();
    }

    public Model createGoalModel() {
        return createVortexPortalModel();
    }

    public Model createSwitchModel() {
        Material mat = MaterialFactory.createArrowBlockMaterial(new Color(0x06 / 255f, 0xB6 / 255f, 0xD4 / 255f, 1f));
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        return modelBuilder.createCylinder(1.2f, 0.18f, 1.2f, 16, mat, attr);
    }

    public Model createGateModel(boolean isOpen) {
        Material mat = MaterialFactory.createGateMaterial(isOpen);
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        return modelBuilder.createBox(1.8f, isOpen ? 0.2f : 1.6f, 0.3f, mat, attr);
    }

    public Model createPortalModel() {
        return createVortexPortalModel();
    }

    public Model createFloatingIslandBaseModel(int width, int depth) {
        float size = Constants.GRID_CELL_SIZE;
        float totalW = width * size;
        float totalD = depth * size;
        modelBuilder.begin();
        long attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Material cliffMat = MaterialFactory.createCliffMaterial();
        MeshPartBuilder mpb = modelBuilder.part("cliff", GL20.GL_TRIANGLES, attr, cliffMat);

        // Layer 1 beneath grid (starts right below the 0.5f tile base)
        mpb.box(0, -1.0f, 0, totalW * 0.98f, 1.0f, totalD * 0.98f);
        // Layer 2 deeper cliff
        mpb.box(0, -2.0f, 0, totalW * 0.82f, 1.0f, totalD * 0.82f);
        // Layer 3 tapered bottom point
        mpb.box(0, -3.0f, 0, totalW * 0.58f, 1.0f, totalD * 0.58f);

        return modelBuilder.end();
    }
}

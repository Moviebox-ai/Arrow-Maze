package com.example.arrowmaze3d.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.arrowmaze3d.ui.CustomSkinFactory;
import com.example.arrowmaze3d.utilities.AppLogger;

public class GameAssetManager {
    private final AssetManager assetManager;
    private Skin uiSkin;

    public GameAssetManager() {
        this.assetManager = new AssetManager();
        this.uiSkin = CustomSkinFactory.createSkin();
    }

    public void update() {
        assetManager.update();
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        if (uiSkin != null) {
            uiSkin.dispose();
            uiSkin = null;
        }
        assetManager.dispose();
        AppLogger.d("GameAssetManager disposed");
    }
}

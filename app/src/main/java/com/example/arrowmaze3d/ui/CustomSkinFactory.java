package com.example.arrowmaze3d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CustomSkinFactory {

    public static Skin createSkin() {
        Skin skin = new Skin();

        // 1. Font
        BitmapFont font = new BitmapFont(); // Default LibGDX font
        font.getData().setScale(2.2f);
        skin.add("default-font", font);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3.5f);
        skin.add("title-font", titleFont);

        BitmapFont smallFont = new BitmapFont();
        smallFont.getData().setScale(1.6f);
        skin.add("small-font", smallFont);

        // 2. Color Palette
        Color primary = new Color(0x3A / 255f, 0x86 / 255f, 0xFF / 255f, 1f); // Electric Blue
        Color primaryDark = new Color(0x1D / 255f, 0x4E / 255f, 0xD8 / 255f, 1f);
        Color accentGold = new Color(0xFF / 255f, 0xD7 / 255f, 0x00 / 255f, 1f);
        Color surfaceDark = new Color(0x12 / 255f, 0x1E / 255f, 0x2A / 255f, 0.92f);
        Color cardDark = new Color(0x1E / 255f, 0x29 / 255f, 0x3B / 255f, 0.95f);
        Color textWhite = new Color(0xF8 / 255f, 0xFA / 255f, 0xFC / 255f, 1f);
        Color textMuted = new Color(0x94 / 255f, 0xA3 / 255f, 0xB8 / 255f, 1f);

        skin.add("primary", primary);
        skin.add("primaryDark", primaryDark);
        skin.add("accentGold", accentGold);
        skin.add("surfaceDark", surfaceDark);
        skin.add("cardDark", cardDark);

        // 3. Drawables
        Texture whiteTexture = createRoundedRectTexture(128, 64, 8, Color.WHITE);
        Texture primaryTexture = createRoundedRectTexture(128, 64, 12, primary);
        Texture primaryDarkTexture = createRoundedRectTexture(128, 64, 12, primaryDark);
        Texture cardTexture = createRoundedRectTexture(128, 128, 16, cardDark);
        Texture surfaceTexture = createRoundedRectTexture(128, 128, 16, surfaceDark);
        Texture goldTexture = createRoundedRectTexture(128, 64, 12, accentGold);

        skin.add("white", whiteTexture);
        skin.add("primaryDrawable", new TextureRegionDrawable(primaryTexture), com.badlogic.gdx.scenes.scene2d.utils.Drawable.class);
        skin.add("primaryDarkDrawable", new TextureRegionDrawable(primaryDarkTexture), com.badlogic.gdx.scenes.scene2d.utils.Drawable.class);
        skin.add("cardDrawable", new TextureRegionDrawable(cardTexture), com.badlogic.gdx.scenes.scene2d.utils.Drawable.class);
        skin.add("surfaceDrawable", new TextureRegionDrawable(surfaceTexture), com.badlogic.gdx.scenes.scene2d.utils.Drawable.class);
        skin.add("goldDrawable", new TextureRegionDrawable(goldTexture), com.badlogic.gdx.scenes.scene2d.utils.Drawable.class);

        // 4. Label Styles
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = textWhite;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        titleStyle.fontColor = accentGold;
        skin.add("title", titleStyle);

        Label.LabelStyle smallStyle = new Label.LabelStyle();
        smallStyle.font = smallFont;
        smallStyle.fontColor = textMuted;
        skin.add("small", smallStyle);

        Label.LabelStyle goldStyle = new Label.LabelStyle();
        goldStyle.font = font;
        goldStyle.fontColor = accentGold;
        skin.add("gold", goldStyle);

        Label.LabelStyle accentStyle = new Label.LabelStyle();
        accentStyle.font = font;
        accentStyle.fontColor = accentGold;
        skin.add("accent", accentStyle);

        // 5. Button Styles
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = textWhite;
        btnStyle.up = new TextureRegionDrawable(primaryTexture);
        btnStyle.down = new TextureRegionDrawable(primaryDarkTexture);
        btnStyle.over = new TextureRegionDrawable(primaryTexture);
        skin.add("default", btnStyle);

        TextButton.TextButtonStyle goldBtnStyle = new TextButton.TextButtonStyle();
        goldBtnStyle.font = font;
        goldBtnStyle.fontColor = Color.BLACK;
        goldBtnStyle.up = new TextureRegionDrawable(goldTexture);
        goldBtnStyle.down = new TextureRegionDrawable(primaryTexture);
        skin.add("gold", goldBtnStyle);

        // 6. Slider Styles
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(createRoundedRectTexture(100, 16, 4, surfaceDark));
        sliderStyle.knob = new TextureRegionDrawable(createRoundedRectTexture(24, 32, 6, primary));
        skin.add("default-horizontal", sliderStyle);

        return skin;
    }

    private static Texture createRoundedRectTexture(int width, int height, int cornerRadius, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        pixmap.setColor(color);
        pixmap.fillRectangle(cornerRadius, 0, width - 2 * cornerRadius, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - 2 * cornerRadius);
        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius, height - cornerRadius, cornerRadius);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}

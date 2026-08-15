package com.example.arrowmaze3d.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class VictoryDialog extends Table {
    private final Label starsLabel;
    private final Label statsLabel;

    public VictoryDialog(Skin skin, final Runnable onNextLevel, final Runnable onRestart) {
        setBackground(skin.getDrawable("surfaceDrawable"));
        pad(50);
        setSize(850, 750);
        setPosition(115, 585); // Centered on 1080x1920

        Label title = new Label("LEVEL COMPLETED!", skin, "title");
        starsLabel = new Label("* * *", skin, "title");
        statsLabel = new Label("Completed in 8 moves", skin, "default");

        TextButton replayBtn = new TextButton("REPLAY", skin, "default");
        replayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                if (onRestart != null) onRestart.run();
            }
        });

        TextButton nextBtn = new TextButton("NEXT LEVEL", skin, "gold");
        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                if (onNextLevel != null) onNextLevel.run();
            }
        });

        add(title).padBottom(30).row();
        add(starsLabel).padBottom(20).row();
        add(statsLabel).padBottom(40).row();

        Table btnTable = new Table();
        btnTable.add(replayBtn).width(280).height(110).padRight(30);
        btnTable.add(nextBtn).width(340).height(110);

        add(btnTable);
    }

    public void show(int stars, int moves) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i < stars) sb.append("* ");
            else sb.append("- ");
        }
        starsLabel.setText(sb.toString().trim());
        statsLabel.setText("Solved in " + moves + " moves");
        setVisible(true);
    }
}

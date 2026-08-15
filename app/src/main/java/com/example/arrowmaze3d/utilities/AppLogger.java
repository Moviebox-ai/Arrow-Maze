package com.example.arrowmaze3d.utilities;

import com.badlogic.gdx.Gdx;

public class AppLogger {
    private static final String TAG = "ArrowMaze3D";

    public static void d(String message) {
        if (Gdx.app != null) {
            Gdx.app.log(TAG, message);
        } else {
            System.out.println("[" + TAG + "] DEBUG: " + message);
        }
    }

    public static void e(String message, Throwable t) {
        if (Gdx.app != null) {
            Gdx.app.error(TAG, message, t);
        } else {
            System.err.println("[" + TAG + "] ERROR: " + message);
            if (t != null) t.printStackTrace();
        }
    }
}

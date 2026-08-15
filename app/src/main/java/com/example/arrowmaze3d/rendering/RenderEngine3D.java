package com.example.arrowmaze3d.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;

public class RenderEngine3D {
    private ModelBatch modelBatch;
    private final Environment environment;
    private final DirectionalLight sunLight;

    public RenderEngine3D() {
        this.modelBatch = new ModelBatch();
        this.environment = new Environment();

        // Environment Lighting
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.45f, 0.5f, 0.58f, 1.0f));

        sunLight = new DirectionalLight();
        sunLight.set(new Color(1.0f, 0.96f, 0.88f, 1.0f), -0.5f, -1.2f, -0.6f);
        environment.add(sunLight);
    }

    public void render(PerspectiveCamera camera, Array<ModelInstance> instances) {
        if (instances == null || instances.size == 0 || camera == null || modelBatch == null) return;

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

        modelBatch.begin(camera);
        for (int i = 0; i < instances.size; i++) {
            ModelInstance instance = instances.get(i);
            if (instance != null) {
                modelBatch.render(instance, environment);
            }
        }
        modelBatch.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void dispose() {
        if (modelBatch != null) {
            modelBatch.dispose();
            modelBatch = null;
        }
    }
}

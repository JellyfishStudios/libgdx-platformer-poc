package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by adunne on 2015/09/19.
 */
public class Image implements Component{
    public Image(TextureRegion tex) {
        texture = tex;
    }

    public Image(TextureRegion texture, float width, float height) {
        this(texture, width, height, null, 0, 0f);
    }

    public Image(TextureRegion tex, float width, float height, Color color, int filter, float opacity) {
        this.texture = tex;
        this.width = width;
        this.height = height;
        this.color = color;
        this.filter = filter;
        this.opacity = opacity;
    }

    public TextureRegion texture;

    public Color color = new Color();
    public int filter = 0;
    public float opacity = 1.0f;
    public float width = 0.0f;
    public float height = 0.0f;
}

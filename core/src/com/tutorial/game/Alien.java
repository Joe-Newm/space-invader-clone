package com.tutorial.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;

public class Alien {
    public Vector2 position;
    public Sprite sprite;

    public Alien(Texture img,Color color ) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(4);
        position = new Vector2((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
    }
    public void draw(SpriteBatch batch){
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
    }

}

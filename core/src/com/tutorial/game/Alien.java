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
    public ArrayList<Bullet> bullets;
    public Texture bulletTexture;

    public Alien(Texture img,Texture img_bullet, ArrayList<Bullet> bullets, Color color, float startX, float startY) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(7);
        position = new Vector2(startX, startY);
        bulletTexture = img_bullet;
        this.bullets = bullets;

        // create multiple
    }
    public void shoot() {
        bullets.add(new Bullet(bulletTexture, position.x + 3, position.y-15));
    }
    public void draw(SpriteBatch batch){
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);

        }


}

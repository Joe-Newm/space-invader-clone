package com.tutorial.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public Vector2 position;
    public Sprite sprite;
    public float speed_bullet = 5;

    public Bullet(Texture img_bullet, float startX, float startY) {
        sprite = new Sprite(img_bullet);
        sprite.setScale(4);
        position = new Vector2(startX, startY);
    }
    public void player_update(float deltaTime) {
        position.y += deltaTime * speed_bullet;
    }

    public void alien_update(float deltaTime) {
        position.y -= deltaTime * 500;

    }
    public void draw(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
    }

}

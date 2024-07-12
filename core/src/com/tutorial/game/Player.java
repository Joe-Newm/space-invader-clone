package com.tutorial.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.audio.Sound;

public class Player {
    public Vector2 position;
    public Sprite sprite;
    public float speed = 2.5f;
    public ArrayList<Bullet> bullets;
    public Texture bulletTexture;
    public float bullet_delay = 20;
    public Sound shootSound;

    public Player(Texture img,Texture img_bullet, Color color) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(8);
        position = new Vector2((float) Gdx.graphics.getWidth() /2,sprite.getScaleY()*sprite.getHeight()/2);
        bullets = new ArrayList<>();
        bulletTexture = img_bullet;

        // sound
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot-sound.wav"));

    }

    public void update(float deltaTime) {
        if(Gdx.input.isKeyPressed(Keys.LEFT)){
            position.x -= deltaTime * speed;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)){
            position.x += deltaTime * speed;
        }
        if(Gdx.input.isKeyPressed(Keys.SPACE) && bullet_delay < 0 && bullets.isEmpty()){
            bullets.add(new Bullet(bulletTexture, position.x + 3, position.y, 5));
            bullet_delay = 20;
            shootSound.play(0.1f);
        }
    }
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        update(3);

        // draw all bullets
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }

        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);

        // collision for the border of the screen
        if (position.x >= camera.viewportWidth - sprite.getWidth() * 6) {
            position.x = camera.viewportWidth - sprite.getWidth() *6;
        }
        if (position.x <= sprite.getWidth() * 7) {
            position.x = sprite.getWidth() * 7;
        }

        //Update all bullets
        bullet_delay -=1;
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.player_update(3);
            // remove bullets off the screen
            if (bullet.position.y > camera.viewportHeight) {
                iterator.remove();
            }
        }

    }
}

package com.space.invaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    public Vector2 position;
    public Sprite sprite;
    public float speed = 2.5f;
    public ArrayList<Bullet> bullets;
    public Texture bulletTexture;
    public float bullet_delay = 20;
    public Sound shootSound;
    public Sound deathSound;
    public Animation<TextureRegion> explosionAnimation;
    public float stateTime;
    private boolean exploding;
    public float SCALE = 8;
    private boolean visible = true;

    public Player(Texture img,Texture img_bullet, Color color) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(SCALE);
        position = new Vector2(sprite.getWidth()*7,sprite.getScaleY()*sprite.getHeight()/2 + 100);
        bullets = new ArrayList<>();
        bulletTexture = img_bullet;

        // sound
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot-sound.wav"));

        //create animation for explosion
        Texture explosionSheet = new Texture(Gdx.files.internal("sprites/explode.png"));
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, explosionSheet.getWidth() / 2, explosionSheet.getHeight());
        TextureRegion[] explosionFrames = new TextureRegion[2];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j< 2; j++) {
                explosionFrames[index++] = tmp[i][j];
            }
        }
        explosionAnimation = new Animation<TextureRegion>(0.1f, explosionFrames);
        stateTime = 0f;
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


    public void triggerExplosion() {
        exploding = true;
        stateTime = 0f;
    }
    public boolean isExploding() {
        return exploding;
    }

    public boolean isExplosionFinished() {
        return explosionAnimation.isAnimationFinished(stateTime);
    }



    public void respawn() {
        // Logic to respawn the player
        position.set(position);
        sprite.setPosition(position.x, position.y);
        visible = true;
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera, float delta) {
        update(3);

        // draw all bullets
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }

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
        if (exploding) {

            stateTime += delta;
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
            if (explosionAnimation.isAnimationFinished(stateTime)) {
                exploding = false; // Once the animation is done, set exploding to false
                visible = false;
            }
            float explosionWidth = currentFrame.getRegionWidth() * SCALE;
            float explosionHeight = currentFrame.getRegionHeight() * SCALE;
            float offsetX = (sprite.getWidth() * SCALE - explosionWidth) / 2 -23;
            float offsetY = (sprite.getHeight() * SCALE - explosionHeight) / 2 -23;
            batch.draw(currentFrame, position.x + offsetX, position.y + offsetY, explosionWidth, explosionHeight);
        } else if (visible) {
            sprite.setPosition(position.x, position.y);
            sprite.draw(batch);
            }
    }
}
package com.space.invaders;

import com.badlogic.gdx.Gdx;
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

public class Alien {

    public Vector2 position;
    public Sprite sprite;
    public ArrayList<Bullet> bullets;
    public static Texture bulletTexture;
    public Sound bulletSound;
    private boolean exploding = false;
    private float stateTime;
    private Animation<TextureRegion> explosionAnimation;
    private static final float SCALE = 8f;
    public static int ticks;

    private float normalize(int value) {
        return value / 255f;
    }

    public static boolean movingRight;

    public Alien(
        Texture img,
        ArrayList<Bullet> bullets,
        int r,
        int g,
        int b,
        int a,
        float startX,
        float startY
    ) {
        sprite = new Sprite(img);
        sprite.setColor(normalize(r), normalize(g), normalize(b), normalize(a));
        sprite.setScale(SCALE);
        position = new Vector2(startX, startY);
        bulletTexture = new Texture("sprites/bullet.png");
        this.bullets = bullets;
        bulletSound = Gdx.audio.newSound(
            Gdx.files.internal("sound/shoot-sound.wav")
        );
        ticks = 0;
        movingRight = true;

        //create animation for explosion
        Texture explosionSheet = new Texture(
            Gdx.files.internal("sprites/explode.png")
        );
        TextureRegion[][] tmp = TextureRegion.split(
            explosionSheet,
            explosionSheet.getWidth() / 2,
            explosionSheet.getHeight()
        );
        TextureRegion[] explosionFrames = new TextureRegion[2];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 2; j++) {
                explosionFrames[index++] = tmp[i][j];
            }
        }
        explosionAnimation = new Animation<TextureRegion>(
            0.1f,
            explosionFrames
        );
        stateTime = 0f;
    }

    public void shoot() {
        bullets.add(
            new Bullet(bulletTexture, position.x + 3, position.y - 15, 3)
        );
        bulletSound.play(0.1f, 0.6f, 0f);
    }

    public static ArrayList<Alien> createAliens(
        Texture alien_img,
        float VIRTUAL_WIDTH,
        float VIRTUAL_HEIGHT,
        ArrayList<Bullet> alien_bullets
    ) {
        ArrayList<Alien> aliens = new ArrayList<>();
        float alien_width = alien_img.getWidth() * 9;
        float alien_height = alien_img.getHeight() * 10;
        float startX = (float) VIRTUAL_WIDTH / 30f;
        float startY = (float) VIRTUAL_HEIGHT - 110;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 11; col++) {
                float x = startX + col * (alien_width + 10);
                float y = startY - row * (alien_height + 10);

                if (row == 0) {
                    aliens.add(
                        new Alien(
                            alien_img,
                            alien_bullets,
                            98,
                            222,
                            109,
                            255,
                            x,
                            y
                        )
                    );
                } else if (row == 1 || row == 2) {
                    aliens.add(
                        new Alien(
                            alien_img,
                            alien_bullets,
                            235,
                            223,
                            100,
                            255,
                            x,
                            y
                        )
                    );
                } else aliens.add(
                    new Alien(alien_img, alien_bullets, 248, 59, 58, 255, x, y)
                );
            }
        }
        return aliens;
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

    public static boolean moveAliens(
        ArrayList<Alien> aliens,
        OrthographicCamera camera
    ) {
        boolean hitBottom = false;

        float screenWidth = camera.viewportWidth;

        // Check if any alien hits the edge
        if (ticks == 12) {
            for (Alien alien : aliens) {
                alien.position.y -= 40;
            }
            movingRight = !movingRight;
            ticks = 0;
        } else {
            // Move aliens horizontally
            for (Alien alien : aliens) {
                if (movingRight) {
                    alien.position.x += 30;
                } else {
                    alien.position.x -= 30;
                }
                if (alien.position.y < 200) {
                    hitBottom = true;
                }
            }
        }
        ticks += 1;

        System.out.println(ticks);

        return hitBottom;
    }

    public void draw(SpriteBatch batch, float delta) {
        if (exploding) {
            stateTime += delta;
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(
                stateTime,
                false
            );
            if (explosionAnimation.isAnimationFinished(stateTime)) {
                exploding = false; // Once the animation is done, set exploding to false
            }
            float explosionWidth = currentFrame.getRegionWidth() * SCALE;
            float explosionHeight = currentFrame.getRegionHeight() * SCALE;
            float offsetX =
                (sprite.getWidth() * SCALE - explosionWidth) / 2 - 23;
            float offsetY =
                (sprite.getHeight() * SCALE - explosionHeight) / 2 - 23;
            batch.draw(
                currentFrame,
                position.x + offsetX,
                position.y + offsetY,
                explosionWidth,
                explosionHeight
            );
        } else {
            sprite.setPosition(position.x, position.y);
            sprite.draw(batch);
        }
    }
}

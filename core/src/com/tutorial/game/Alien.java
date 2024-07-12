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
import com.badlogic.gdx.audio.Sound;
import java.util.Iterator;

public class Alien {
    public Vector2 position;
    public Sprite sprite;
    public ArrayList<Bullet> bullets;
    public static Texture bulletTexture;
    public Sound bulletSound;

    public static boolean movingRight = true;

    public Alien(Texture img, ArrayList<Bullet> bullets, Color color, float startX, float startY) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(8);
        position = new Vector2(startX, startY);
        bulletTexture = new Texture("bullet.png");
        this.bullets = bullets;
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot-sound.wav"));

    }

    public void shoot() {
        bullets.add(new Bullet(bulletTexture, position.x + 3, position.y-15, 3));
        bulletSound.play(0.1f, 0.6f,0f);
    }

    public static ArrayList<Alien> createAliens(Texture alien_img, float VIRTUAL_WIDTH, float VIRTUAL_HEIGHT, ArrayList<Bullet> alien_bullets) {
        ArrayList<Alien> aliens = new ArrayList<>();
        float alien_width = alien_img.getWidth() * 9;
		float alien_height = alien_img.getHeight() * 10;
		float startX = (float) VIRTUAL_WIDTH / 5.5f;
		float startY = (float) VIRTUAL_HEIGHT - 110;

        for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 11; col++) {
				float x = startX + col * (alien_width + 10);
				float y = startY - row * (alien_height + 10);

				aliens.add(new Alien(alien_img, alien_bullets, Color.GREEN, x, y));
			}
		}
        return aliens;
    }

    public static void moveAliens(ArrayList<Alien> aliens, OrthographicCamera camera) {
        boolean hitEdge = false;
        float screenWidth = camera.viewportWidth;

        for (Alien alien : aliens) {

            if (movingRight) {
                alien.position.x += 0.5;
            } else {
                alien.position.x -= 0.5;
            }
            // check for hitEdge
            if (alien.position.x >= screenWidth - alien.sprite.getWidth()*7 || alien.position.x <= 0 + alien.sprite.getWidth()*7) {
                hitEdge = true;
            }
        }
        if (hitEdge) {
            movingRight = !movingRight;
            for (Alien alien : aliens) {
                alien.position.y -= 40;
            }
        }
    }

    public void draw(SpriteBatch batch){
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
        }
}
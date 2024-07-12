package com.tutorial.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
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
    public Texture bulletTexture;
    public Sound bulletSound;


    public Alien(Texture img,Texture img_bullet, ArrayList<Bullet> bullets, Color color, float startX, float startY) {
        sprite = new Sprite(img);
        sprite.setColor(color);
        sprite.setScale(7);
        position = new Vector2(startX, startY);
        bulletTexture = img_bullet;
        this.bullets = bullets;
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot-sound.wav"));
    }

    public void shoot() {
        bullets.add(new Bullet(bulletTexture, position.x + 3, position.y-15));
        bulletSound.play(0.1f, 0.6f,0f);
    }

    public static ArrayList<Alien> createAliens(Texture alien_img, float VIRTUAL_WIDTH, float VIRTUAL_HEIGHT, Texture img_bullet, ArrayList<Bullet> alien_bullets) {
        ArrayList<Alien> aliens = new ArrayList<>();
        float alien_width = alien_img.getWidth() * 8;
		float alien_height = alien_img.getHeight() * 9;
		float startX = (float) VIRTUAL_WIDTH / 5.5f;
		float startY = (float) VIRTUAL_HEIGHT - 90;

        for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 11; col++) {
				float x = startX + col * (alien_width + 10);
				float y = startY - row * (alien_height + 10);

				aliens.add(new Alien(alien_img, img_bullet, alien_bullets, Color.GREEN, x, y));
			}
		}
    return aliens;
    }

    public void draw(SpriteBatch batch){
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);

        }


}
package com.tutorial.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.*;
import java.util.ArrayList;

public class TutorialGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture player_img;
	Player player;
	Texture img_bullet;
	Texture alien_img;
	ArrayList<Alien> aliens;
	ArrayList<Bullet> bullets;
	boolean movingRight = true;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		player_img = new Texture("player.png");
		img_bullet = new Texture("bullet.png");
		alien_img = new Texture("alien.png");
		player = new Player(player_img,img_bullet, Color.GREEN);
		bullets = player.bullets;
		aliens = new ArrayList<>();
		createAliens();

		// set FPS
		Gdx.graphics.setForegroundFPS(60);
	}

	public void createAliens() {
		float alien_width = alien_img.getWidth() *4;
		float alien_height = alien_img.getHeight()*4;
		float startX = Gdx.graphics.getWidth()/5.5f;
		float startY = (float) Gdx.graphics.getHeight() - 50;

		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 11; col++){
				float x = startX + col * (alien_width + 10);
				float y = startY - row * (alien_height + 10);

				aliens.add(new Alien(alien_img, Color.GREEN, x, y));
			}
		}
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		player.draw(batch);
		for (Alien alien : aliens) {
			alien.draw(batch);
		}
		moveAliens();
		checkCollisions();
		batch.end();
	}

	public void checkCollisions() {
		ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
		ArrayList<Alien> aliensToRemove = new ArrayList<>();

		for(Bullet bullet : bullets) {
			for(Alien alien : aliens) {
				if (bullet.sprite.getBoundingRectangle().overlaps(alien.sprite.getBoundingRectangle())) {
					bulletsToRemove.add(bullet);
					aliensToRemove.add(alien);
					System.out.println("Bullet collided with " + bullet.sprite.getBoundingRectangle());
					break;
				}
			}
		}
		bullets.removeAll(bulletsToRemove);
		aliens.removeAll(aliensToRemove);
	}
	public void moveAliens() {
		boolean hitEdge = false;
		for(Alien alien : aliens) {
			if (movingRight) {
				alien.position.x += 1;
			} else {
				alien.position.x -= 1;
			}
			// check for hitEdge
			if (alien.position.x >= Gdx.graphics.getWidth() - alien.sprite.getWidth() || alien.position.x <= 0){
				hitEdge = true;
			}

		}
		if (hitEdge) {
			movingRight = !movingRight;
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player_img.dispose();
		img_bullet.dispose();
		alien_img.dispose();
	}
}

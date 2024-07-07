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
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		player_img = new Texture("player.png");
		img_bullet = new Texture("bullet.png");
		alien_img = new Texture("alien.png");
		player = new Player(player_img,img_bullet, Color.GREEN);

		aliens = new ArrayList<>();
		createAliens();

		// set FPS
		Gdx.graphics.setForegroundFPS(60);
	}

	public void createAliens() {
		float alien_width = alien_img.getWidth() *4;
		float alien_height = alien_img.getHeight()*4;
		float startX = (float) Gdx.graphics.getWidth()/5;
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
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player_img.dispose();
		img_bullet.dispose();
		alien_img.dispose();
	}
}

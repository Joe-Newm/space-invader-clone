package com.tutorial.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.*;

public class TutorialGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Player player;
	Texture img_bullet;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("player.png");
		img_bullet = new Texture("bullet.png");
		player = new Player(img,img_bullet, Color.GREEN);

		// set FPS
		Gdx.graphics.setForegroundFPS(60);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		player.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

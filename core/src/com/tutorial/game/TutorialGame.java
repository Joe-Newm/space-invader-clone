package com.tutorial.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class TutorialGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture player_img;
	Player player;
	Texture img_bullet;
	Texture alien_img;
	ArrayList<Alien> aliens;
	ArrayList<Bullet> player_bullets;
	ArrayList<Bullet> alien_bullets;
	float enemy_shoot_delay = 5f;
	boolean movingRight = true;
	private OrthographicCamera camera;
	private Viewport viewport;

	public final float VIRTUAL_WIDTH = 800;
	private final float VIRTUAL_HEIGHT = 600;

	@Override
	public void create () {
		batch = new SpriteBatch();
		player_img = new Texture("player.png");
		img_bullet = new Texture("bullet.png");
		alien_img = new Texture("alien.png");
		player = new Player(player_img,img_bullet, Color.GREEN);
		player_bullets = player.bullets;
		alien_bullets = new ArrayList<>();
		aliens = new ArrayList<>();
		createAliens();

		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		viewport.apply();

		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		// set FPS
		Gdx.graphics.setForegroundFPS(60);
	}

	public void createAliens() {
		float alien_width = alien_img.getWidth() *4;
		float alien_height = alien_img.getHeight()*4;
		float startX = Gdx.graphics.getWidth()/5.5f;
		float startY = (float) VIRTUAL_HEIGHT - 35;

		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 11; col++){
				float x = startX + col * (alien_width + 10);
				float y = startY - row * (alien_height + 10);

				aliens.add(new Alien(alien_img,img_bullet, alien_bullets, Color.GREEN, x, y));
			}
		}
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		player.draw(batch, camera);
		enemy_shoot_delay -= Gdx.graphics.getDeltaTime();

		// check if time for alien to shoot
		if (enemy_shoot_delay <= 0 && !aliens.isEmpty()){
			enemy_shoot_delay = random.nextFloat(1f,5f);

			int randomIndex = random.nextInt(aliens.size());
			Alien randomAlien = aliens.get(randomIndex);
			randomAlien.shoot();
		}

		for (Alien alien : aliens) {
			alien.draw(batch);
		}
		updateAlienBullets();
		moveAliens();
		checkCollisions();
		batch.end();
	}

	public void updateAlienBullets() {
		Iterator<Bullet> iter = alien_bullets.iterator();
		while (iter.hasNext()) {
			Bullet bullet = iter.next();
			bullet.alien_update(Gdx.graphics.getDeltaTime());

			if (bullet.position.y < 0) {
				iter.remove();
			}
			bullet.draw(batch);
		}
	}

	public void checkCollisions() {
		ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
		ArrayList<Alien> aliensToRemove = new ArrayList<>();

		for(Bullet bullet : player_bullets) {
			for(Alien alien : aliens) {
				if (bullet.sprite.getBoundingRectangle().overlaps(alien.sprite.getBoundingRectangle())) {
					bulletsToRemove.add(bullet);
					aliensToRemove.add(alien);
					System.out.println("Bullet collided with " + bullet.sprite.getBoundingRectangle());
					break;
				}
			}
		}
		player_bullets.removeAll(bulletsToRemove);
		aliens.removeAll(aliensToRemove);
	}
	public void moveAliens() {
		boolean hitEdge = false;
		float screenWidth = camera.viewportWidth;

		for(Alien alien : aliens) {

			if (movingRight) {
				alien.position.x += 0.5;
			} else {
				alien.position.x -= 0.5;
			}
			// check for hitEdge
		if (alien.position.x >= screenWidth - alien.sprite.getWidth() || alien.position.x <= 0){
				hitEdge = true;
			}

		}
		if (hitEdge) {
			movingRight = !movingRight;
		}
	}
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player_img.dispose();
		img_bullet.dispose();
		alien_img.dispose();
	}
}

package com.tutorial.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class TutorialGame extends Game {
	@Override
	public void create() {
		setScreen(new MenuScreen(this));
	}

	public void startGame() {
		GameScreen gameScreen = new GameScreen(this);
		setScreen(gameScreen);
		gameScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}

class GameScreen implements Screen {
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
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private int lives;
	private int score;

	private final float VIRTUAL_WIDTH = 1200;
	private final float VIRTUAL_HEIGHT = 1000;

	public final Game game;

	public GameScreen(Game game) {
		this.game = game;
		batch = new SpriteBatch();
		player_img = new Texture("player.png");
		img_bullet = new Texture("bullet.png");
		alien_img = new Texture("alien.png");
		player = new Player(player_img, img_bullet, Color.GREEN);
		player_bullets = player.bullets;
		alien_bullets = new ArrayList<>();
		aliens = new ArrayList<>();
		createAliens();
		shapeRenderer = new ShapeRenderer();
		lives = 3;
		score = 0;


		// add camera
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		// add font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixellari.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 30;
		parameter.color = Color.BLACK;
		font = generator.generateFont(parameter);
		generator.dispose();
		// set FPS
		Gdx.graphics.setForegroundFPS(60);

		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		camera.update();
	}


	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		shapeRenderer.rect(0, VIRTUAL_HEIGHT -40, VIRTUAL_WIDTH, 40);
		shapeRenderer.end();

		batch.begin();
		font.draw(batch, "LIVES: " + lives, 20, VIRTUAL_HEIGHT -10);
		font.draw(batch, "SCORE: " + score, 200, VIRTUAL_HEIGHT -10);
		player.draw(batch, camera);
		enemy_shoot_delay -= Gdx.graphics.getDeltaTime();

		// check if time for alien to shoot
		if (enemy_shoot_delay <= 0 && !aliens.isEmpty()) {
			enemy_shoot_delay = random.nextFloat(1f, 5f);

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
		playerCollisions();
		batch.end();
	}


	public void createAliens() {
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
	}

	public void moveAliens() {
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

		for (Bullet bullet : player_bullets) {
			for (Alien alien : aliens) {
				if (bullet.sprite.getBoundingRectangle().overlaps(alien.sprite.getBoundingRectangle())) {
					bulletsToRemove.add(bullet);
					aliensToRemove.add(alien);
					score += 10;
					System.out.println("Bullet collided with " + bullet.sprite.getBoundingRectangle());
					break;
				}
			}
		}
		player_bullets.removeAll(bulletsToRemove);
		aliens.removeAll(aliensToRemove);
	}

	public void playerCollisions() {
		ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

		for (Bullet bullet : alien_bullets) {
			if (bullet.sprite.getBoundingRectangle().overlaps(player.sprite.getBoundingRectangle())) {
				bulletsToRemove.add(bullet);
				System.out.println("Bullet collided with the player" + bullet.sprite.getBoundingRectangle());
				lives -= 1;
			}
		}
		alien_bullets.removeAll(bulletsToRemove);
	}


	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		player_img.dispose();
		img_bullet.dispose();
		alien_img.dispose();
	}
}


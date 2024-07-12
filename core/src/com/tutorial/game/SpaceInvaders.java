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


import java.util.ArrayList;
import java.util.Iterator;

import static com.badlogic.gdx.math.MathUtils.random;

public class SpaceInvaders extends Game {
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
		aliens = Alien.createAliens(alien_img, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, alien_bullets);
		shapeRenderer = new ShapeRenderer();
		lives = 3;
		score = 0;


		// add camera
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		// add font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pixellari.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 23;
		parameter.color = Color.BLACK;
		font = generator.generateFont(parameter);
		generator.dispose();

		// set FPS
		Gdx.graphics.setForegroundFPS(60);
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

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(0, 75, VIRTUAL_WIDTH, 4);
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

		for (Iterator<Alien> iter = aliens.iterator(); iter.hasNext(); ) {
			Alien alien = iter.next();
			alien.draw(batch, delta); // Pass delta time to the draw method
			if (alien.isExplosionFinished()) {
				iter.remove(); // Remove alien after explosion animation finishes
			}
		}
		updateAlienBullets();
		Alien.moveAliens( aliens, camera);
		checkCollisions();
		playerCollisions();
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

		for (Bullet bullet : player_bullets) {
			for (Alien alien : aliens) {
				if (bullet.sprite.getBoundingRectangle().overlaps(alien.sprite.getBoundingRectangle())) {
					bulletsToRemove.add(bullet);
					alien.triggerExplosion();

					score += 10;

					System.out.println("Bullet collided with " + bullet.sprite.getBoundingRectangle());
					break;
				}
			}
		}
		player_bullets.removeAll(bulletsToRemove);
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


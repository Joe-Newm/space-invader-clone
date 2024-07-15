package com.space.invaders;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Iterator;

import static com.badlogic.gdx.math.MathUtils.random;

public class SpaceInvaders extends Game {
	private boolean isFullScreenToggled = false;

	@Override
	public void create() {
		setScreen(new MenuScreen(this));
	}
	@Override
	public void render() {
		super.render();
		screenControl();
	}

	public void startGame() {
		GameScreen gameScreen = new GameScreen(this);
		setScreen(gameScreen);
		gameScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void screenControl() {
		if (Gdx.input.isKeyPressed(Input.Keys.F)) {
			if (!isFullScreenToggled) {
				isFullScreenToggled = true;
				if (Gdx.graphics.isFullscreen()) {
					Gdx.graphics.setWindowedMode(800, 600);
				} else {
					Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
					Gdx.graphics.setFullscreenMode(displayMode);
				}
			}
		} else {
			isFullScreenToggled = false;
		}
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
	private boolean isPaused = false;
	private float pauseDuration = 3.0f;
	private boolean waitingForExplosion = false;
	public Sound deathSound;
	public Sound gameOver;

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

		// death sound during pause
		deathSound = Gdx.audio.newSound(Gdx.files.internal("sound/518307__mrthenoronha__death-song-8-bit.wav"));
		gameOver = Gdx.audio.newSound(Gdx.files.internal("sound/game-over.wav"));

		// add camera
		camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

		// add font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/C&C Red Alert [INET].ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 38;
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
		if (isPaused) {
			// Skip game update logic while paused
			return;
		}
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

		player.draw(batch, camera, delta);
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
				player.triggerExplosion();
				System.out.println("Bullet collided with the player" + bullet.sprite.getBoundingRectangle());
				if (lives > 0 && !waitingForExplosion) {
					lives -= 1;
					waitingForExplosion = true;
					Timer.schedule(new Timer.Task() {
						@Override
						public void run() {
							waitingForExplosion = false;
							pauseGameForDuration(2.0f);
							deathSound.play(0.1f);
							Timer.schedule(new Timer.Task(){
								@Override
								public void run() {
									player.position =  new Vector2(player.sprite.getWidth()*7,player.sprite.getScaleY()*player.sprite.getHeight()/2 + 100);
									player.respawn();
								}
							},1.0f);
						}
					}, 0.25f); // Pause after explosion animation
				} else {
					gameOver.play(0.1f);
					player.triggerExplosion();
					Timer.schedule(new Timer.Task() {
						@Override
						public void run() {
							waitingForExplosion = false;
							isPaused = true;
						}
					}, 0.4f);
				}
			}
		}
		alien_bullets.removeAll(bulletsToRemove);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
	}

	private void pauseGame() {
		Gdx.app.log("MyGame", "Game is pausing for " + pauseDuration + " seconds");
		isPaused = true;
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				resumeGame();
			}
		}, pauseDuration);
	}

	private void resumeGame() {
		isPaused = false;
	}

	public void pauseGameForDuration(float duration) {
		this.pauseDuration = duration;
		pauseGame();
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


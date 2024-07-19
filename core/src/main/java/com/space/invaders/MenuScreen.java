package com.space.invaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    private final Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private final float VIRTUAL_WIDTH = 1200;
    private final float VIRTUAL_HEIGHT = 1000;

    public MenuScreen(Game game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        //font stuff
        //        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Pixellari.ttf"));
        //        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //        parameter.size = 40;
        //        parameter.color = Color.WHITE;
        //        font = generator.generateFont(parameter);
        //        generator.dispose();
        font = new BitmapFont(Gdx.files.internal("fonts/munro.fnt"));
        font.setColor(Color.WHITE);

        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(
            batch,
            "SPACE INVADERS CLONE\n  by Joseph Newman\n           V2.0",
            VIRTUAL_WIDTH / 3 - 50,
            VIRTUAL_HEIGHT - 400
        );

        font.setColor(Color.GREEN);
        font.draw(
            batch,
            "click to begin",
            VIRTUAL_WIDTH - 760,
            VIRTUAL_HEIGHT - 650
        );
        batch.end();

        if (
            Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)
        ) {
            ((SpaceInvaders) game).startGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(
            camera.viewportWidth / 2,
            camera.viewportHeight / 2,
            0
        );
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        //        font.dispose();
    }
}

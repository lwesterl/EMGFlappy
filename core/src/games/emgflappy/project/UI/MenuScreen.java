package games.emgflappy.project.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuScreen extends AbstractScreen {

    private static final int SpriteAmount = 4;
    private static final float ZoomInterval = 0.02f; // 20 ms
    private static final float ImageChangeInterval = 10.f;
    private SpriteBatch batch;
    private Sprite[] sprites;
    private Texture[] textures;
    private OrthographicCamera camera;
    private float elapsed;
    private float totalTime;
    private int currentSprite;

    /**
     * Constructor for MenuScreen
     */
    public MenuScreen() {
        super();
        batch = new SpriteBatch();
        camera = new OrthographicCamera(AbstractScreen.ViewportWidth, AbstractScreen.ViewportHeight);
        camera.position.set(AbstractScreen.ViewportWidth * 0.5f, AbstractScreen.ViewportHeight * 0.5f, 0.f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        sprites = new Sprite[MenuScreen.SpriteAmount];
        textures = new Texture[MenuScreen.SpriteAmount];
        for (int i = 0; i < SpriteAmount; i++) {
            textures[i] = new Texture(Gdx.files.internal("gameplay/gameplay" + (i + 1) + ".png"));
            sprites[i] = new Sprite(textures[i]);
            sprites[i].setSize(AbstractScreen.ViewportWidth, AbstractScreen.ViewportHeight);
            sprites[i].setPosition(0.f, 0.f);
        }
        elapsed = 0.f;
        currentSprite = 0;
    }

    /**
     * Show MenuScreen
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table(skin);
        Label title = new Label("EMGFlappy", skin, "bigLabel");
        title.setPosition((AbstractScreen.ViewportWidth - title.getPrefWidth()) * 0.5f, AbstractScreen.ViewportHeight - title.getPrefHeight() * 2.f);
        stage.addActor(title);
        TextButton startButton = new TextButton("Start", skin, "pauseButton");
        startButton.setColor(Color.WHITE);
        TextButton quitButton = new TextButton("Quit", skin, "pauseButton");
        quitButton.setColor(Color.WHITE);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.startGame();
            }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.quitGame();
            }
        });
        table.add(startButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight).padBottom(100.f).padTop(100.f);
        table.row();
        table.add(quitButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);
        table.setPosition(AbstractScreen.ViewportWidth * 0.5f, AbstractScreen.ViewportHeight * 0.5f);
        stage.addActor(table);
    }

    /**
     * Render MenuScreen: uses camera zoom to create more interactive feeling menu
     * @param delta Time elapsed since previous render cycle (in seconds)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.f, 0.f, 0.f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsed += delta;
        totalTime += delta;
        if (elapsed >= MenuScreen.ZoomInterval) {
            elapsed -= MenuScreen.ZoomInterval;
            camera.zoom -= 0.0005f;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
        }
        if (totalTime >= MenuScreen.ImageChangeInterval) {
            totalTime -= MenuScreen.ImageChangeInterval;
            camera.zoom = 1.f;
            if (currentSprite < MenuScreen.SpriteAmount - 1) currentSprite++;
            else currentSprite = 0;
        }
        batch.begin();
        sprites[currentSprite].draw(batch);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Dispose all resources
     */
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        for (Texture texture : textures) texture.dispose();
    }

}

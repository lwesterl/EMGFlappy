package games.emgflappy.project.EMGflappy.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import games.emgflappy.project.EMGflappy.EMGflappy;
import games.emgflappy.project.EMGflappy.FlappyTextures;


/**
 * Screen shown when the game is over
 */
public class EndScreen extends AbstractScreen {

    private static final float FrameDuration = 0.3f; // How duration of each animation frame
    private static final Color BackgroundColor = new Color(0.f, 0.f, 0.15f, 1.f);
    private static final float SpriteWidth = AbstractScreen.ViewportWidth / 2.f; // Width of the Flappy sprites drawn
    private static final float SpriteHeight = AbstractScreen.ViewportHeight / 2.f; // Height of the Flappy sprites drawn

    private SpriteBatch batch;
    private Sprite[] sprites;
    private float elapsedTime;
    private int currentFrame;

    /**
     * Constructor for EndScreen
     */
    public EndScreen() {
        super();
        batch = new SpriteBatch();
        Camera camera = new OrthographicCamera(AbstractScreen.ViewportWidth, AbstractScreen.ViewportHeight);
        camera.position.set(AbstractScreen.ViewportWidth / 2.f, AbstractScreen.ViewportHeight / 2.f, 0.f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        sprites = new Sprite[]{ new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame1)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame2)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame3)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame4))};
        for (Sprite sprite : sprites) {
            sprite.setSize(EndScreen.SpriteWidth, EndScreen.SpriteHeight);
            sprite.setPosition((AbstractScreen.ViewportWidth - EndScreen.SpriteWidth) * 0.5f, (AbstractScreen.ViewportHeight - EndScreen.SpriteHeight) * 0.5f);
        }
        elapsedTime = 0.f;
        currentFrame = 0;
    }

    /**
     * Show EndScreen
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table(skin);

        Label label = new Label("Workout completed", skin, "bigLabel");
        label.setPosition(AbstractScreen.ViewportWidth * 0.5f - label.getPrefWidth() * 0.5f,
                AbstractScreen.ViewportHeight - label.getPrefHeight());

        stage.addActor(label);

        TextButton mainMenuButton = new TextButton("Main menu", skin, "pauseButton");
        mainMenuButton.setColor(Color.BLUE);
        TextButton restartButton = new TextButton("Restart", skin, "pauseButton");
        restartButton.setColor(Color.BLUE);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.quitGame();
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.restartGame();
            }
        });

        table.add(mainMenuButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);
        table.add(restartButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);
        table.setPosition(AbstractScreen.ViewportWidth * 0.5f, AbstractScreen.ButtonHeight);

        stage.addActor(table);

    }

    /**
     * Draw EndScreen
     * @param delta Time elapsed since previous render cycle (in seconds)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BackgroundColor.r, BackgroundColor.g, BackgroundColor.b, BackgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        elapsedTime += delta;
        if (elapsedTime >= EndScreen.FrameDuration) {
            elapsedTime -= EndScreen.FrameDuration;
            currentFrame = currentFrame + 1 < sprites.length ? currentFrame + 1 : 0;
        }
        sprites[currentFrame].draw(batch);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}

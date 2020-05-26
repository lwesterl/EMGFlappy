package games.emgflappy.project.EMGflappy.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;


/**
 * Abstract class for implementing Screen
 */
public abstract class AbstractScreen implements Screen {

    protected static int ButtonWidth = 400;
    protected static int ButtonHeight = 100;
    protected static float ViewportWidth = 1600.f;
    protected static float ViewportHeight = 1000.f;

    protected Stage stage;
    protected Skin skin;
    protected TextureAtlas atlas;

    /**
     * Constructor, call this from subclasses
     */
    protected AbstractScreen() {
        super();
        stage = new Stage(new StretchViewport(ViewportWidth, ViewportHeight));
        skin = new Skin(Gdx.files.internal("resources/UIskin.json"));
        atlas = new TextureAtlas(Gdx.files.internal("resources/UIskin.atlas"));
        skin.addRegions(atlas);
    }

    /**
     * Resize PauseScreen after the viewport has changed
     * @param width New viewport width
     * @param height New viewport height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Each subclass of AbstractScreen needs to implement show method
     */
    @Override
    public abstract void show();

    /**
     * Each subclass of AbstractScreen needs to implement render method
     * @param delta Time elapsed since previous render cycle (in seconds)
     */
    @Override
    public abstract void render(float delta);

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    /**
     * Dispose memory used after the Screen is not anymore needed
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }

}

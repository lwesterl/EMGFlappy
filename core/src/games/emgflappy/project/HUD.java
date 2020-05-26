package games.emgflappy.project;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * Class which creates a heads-up display (HUD) on the screen
 * HUD is horizontally aligned and it consists of a small flappy image and HP bar which show much
 * HP Flappy has still left
 */
public class HUD implements Disposable {

    private static float HorizontalSplit = 0.5f; // How much horizontal space is received for the image when compared to the HP bar (must be in the range of [0.f, 1.f])
    private static float HorizontalEmptySpace = 0.03f; // How much space is between the end of image and HP bar (must be in the range of [0.f, 1.f - HorizontalSlip])
    private static float HPBarVerticalSize = 0.1f; // How much space the HP bar takes vertically when compared to HUD vertical size (must be in the range of [0.f, 1.f])

    private Camera camera;
    private float x;
    private float y;
    private float width;
    private float height;
    private NinePatch whiteHP;
    private NinePatch greenHP;
    private NinePatch redHP;
    private Sprite sprite;


    /**
     * Create a HUD
     * @param position Vector2 which specifies HUD position in x and y dimensions (origin is
     *                 the left bottom edge of the screen)
     * @param size Vector2 which specifies HUD size (width and height)
     * @param ViewPortSizeX EMGflappy ViewPortSizeX used to set correct camera viewports
     * @param ViewPortSizeY EMGflappy ViewPortSizeY used to set correct camera viewports
     */
    public HUD(Vector2 position, Vector2 size, float ViewPortSizeX, float ViewPortSizeY) {
        x = position.x;
        y = position.y;
        width = size.x;
        height = size.y;
        camera = new OrthographicCamera(ViewPortSizeX, ViewPortSizeY);
        camera.position.set(ViewPortSizeX / 2.f, ViewPortSizeY / 2.f, 0.f);
        camera.update();
        whiteHP = new NinePatch(EMGflappy.getTexture(FlappyTextures.HPBarWhite), 0, 0, 0, 0);
        greenHP = new NinePatch(EMGflappy.getTexture(FlappyTextures.HPBarGreen), 0, 0, 0, 0);
        redHP = new NinePatch(EMGflappy.getTexture(FlappyTextures.HPBarRed), 0, 0, 0, 0);
        sprite = new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyForward));
        sprite.setSize(width * HUD.HorizontalSplit, height);
        sprite.setPosition(x, y);
    }

    /**
     * Dispose HUD, call this when the game is finished
     */
    @Override
    public void dispose() {}

    /**
     * Draw HUD (HUD should be drawn last so that it's on top of everything else)
     * @param batch Where HUD is drawn to
     * @param HP Flappy's current HP
     * @param maxHP Flappy max HP
     */
    public void draw(SpriteBatch batch, float HP, float maxHP) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        sprite.draw(batch);
        float barX = x + width * (HorizontalSplit + HorizontalEmptySpace);
        float barY =  y + (1.f - HPBarVerticalSize) * 0.5f * height;
        float barWidth = (1.f - HorizontalSplit - HorizontalEmptySpace) * width;
        float HPBarWidth = barWidth * HP / maxHP;
        float barHeight = height * HPBarVerticalSize;
        whiteHP.draw(batch, barX, barY, barWidth, barHeight);
        if (HP < maxHP / 2.f && HP >= 0.f) redHP.draw(batch, barX, barY, HPBarWidth, barHeight);
        else if (HP >= 0.f) greenHP.draw(batch, barX, barY, HPBarWidth, barHeight);
        batch.end();
    }

    /**
     * Set new viewport, call this from EMGflappy after a viewport has changed
     * @param ViewPortSizeX New EMGflappy ViewPortSizeX
     * @param ViewPortSizeY New EMGflappy ViewPortSizeY
     */
    public void onViewPortChanged(float ViewPortSizeX, float ViewPortSizeY) {
        camera.viewportWidth = ViewPortSizeX;
        camera.viewportHeight = ViewPortSizeY;
        camera.position.set(ViewPortSizeX / 2.f, ViewPortSizeY / 2.f, 0.f);
        camera.update();
    }
}

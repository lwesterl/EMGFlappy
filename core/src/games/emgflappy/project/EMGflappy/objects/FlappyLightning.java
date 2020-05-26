package games.emgflappy.project.EMGflappy.objects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import games.emgflappy.project.EMGflappy.audio.AudioType;
import games.emgflappy.project.EMGflappy.audio.FlappyAudio;

/**
 * FlappyObstacles that use lightning texture and which are Tunnels in ObstacleType
 */
public class FlappyLightning extends FlappyObstacle {

    private static float LightningStrikePeriod = 2.f; // How often lightning strikes in seconds

    private float elapsedTime;
    private boolean showLightning;
    private boolean visible;

    /**
     * Constructor for FlappyLightning
     * @param x Position of left edge coordinate
     * @param y Position of bottom edge coordinate
     * @param height Height of the object (in world units)
     * @param ViewPortSizeX EMGflappy ViewPortSizeX used to set correct size for the object
     */
    public FlappyLightning(float x, float y, float height, float ViewPortSizeX) {
        super(ObstacleType.Tunnel, x, y, height, ViewPortSizeX, false);
        fixtureDef.isSensor = true;
        type = ObjectType.FlappyLightning;
        showLightning = false;
        visible = false;
    }


    /**
     * Draw lightning periodically
     * @param batch On which the object is drawn to
     * @param elapsedTime Time elapsed since previous render cycle in seconds (not used)
     * @return false FlappyLightning cannot be destroyed
     */
    @Override
    public boolean draw(SpriteBatch batch, float elapsedTime) {
        if (visible) {
            this.elapsedTime += elapsedTime;
            if (this.elapsedTime >= FlappyLightning.LightningStrikePeriod) {
                showLightning = !showLightning;
                if (showLightning) FlappyAudio.playSound(AudioType.ThunderAudio);
                this.elapsedTime = 0.f;
            }
            if (showLightning) sprite.draw(batch);
        }
        return false;
    }

    /**
     * Set object visible or hidden
     * Call this method each game update cycle
     * @param flappyStartX Left edge of the Flappy (FlappyObject coordinate system)
     * @param flappyEndX Right edge of the Flappy (FlappyObject coordinate system)
     * @param ViewPortSizeX EMGflappy ViewPortSizeX used to detect whether Flappy can see the object
     */
    public void setVisible(float flappyStartX, float flappyEndX, float ViewPortSizeX) {
        visible = (flappyEndX + ViewPortSizeX / 2.f >= x && flappyStartX <= getEndPosition().x + ViewPortSizeX / 2.f);
    }

    /**
     * Check whether the object can cause damage  or not
     * @return true if the object is visible and it should cause damage, otherwise false
     */
    @Override
    public boolean canDamage() {
        return visible && showLightning;
    }
}

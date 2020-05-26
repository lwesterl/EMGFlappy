package games.emgflappy.project.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import games.emgflappy.project.EMGflappy;
import games.emgflappy.project.FlappyTextures;

import static games.emgflappy.project.objects.ObstacleType.getObstacleWidthScale;


/**
 * Class for static obstacles in the game
 */
public class FlappyObstacle extends FlappyObject {

    protected Sprite sprite;
    private PolygonShape shape;
    private ObstacleType obstacleType;
    private float height;

    /**
     * Constructor for FlappyObstacle
     * @param obstacleType Specifies the type of obstacle according to ObstacleType
     * @param x Position of left edge coordinate
     * @param y Position of bottom edge coordinate
     * @param height Height of the object (in world units)
     * @param ViewPortSizeX EMGflappy ViewPortSizeX used to set correct size for the object
     * @param flipY Whether to flip texture in y dimension or not
     */
    public FlappyObstacle(ObstacleType obstacleType,
                          float x, float y, float height, float ViewPortSizeX, boolean flipY) {
        super(x, y, BodyDef.BodyType.StaticBody);
        type = ObjectType.FlappyObstacle;
        this.obstacleType = obstacleType;
        this.height = height;
        shape = new PolygonShape();
        float width = ViewPortSizeX * getObstacleWidthScale(obstacleType);
        shape.setAsBox(width * 0.5f, height * 0.5f);
        fixtureDef.shape = shape;
        if (obstacleType == ObstacleType.Normal) {
            sprite = flipY ? new Sprite(EMGflappy.getTexture(FlappyTextures.NormalObstacleFlipped)) : new Sprite(EMGflappy.getTexture(FlappyTextures.NormalObstacle));
        }
        else {
            sprite = new Sprite(EMGflappy.getTexture(FlappyTextures.Lightning));
            sprite.flip(false, flipY);
        }
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        setPositionFlappyCoordinates(new Vector2(x, y));
    }

    /**
     * Free reserved resources
     */
    @Override
    public void dispose() {
        shape.dispose();
    }

    /**
     * Transform FlappyObject coordinates to Box2D coordinates
     * @param pos Position in FlappyObject coordinates (left bottom edge)
     * @return Position of the center of the object (Box2D coordinates)
     */
    @Override
    public Vector2 coordinateTransform(Vector2 pos) {
        return new Vector2(pos.x + sprite.getWidth() * 0.5f, pos.y + sprite.getHeight() * 0.5f);
    }

    /**
     * Transform Box2D coordinates (center of the object) to FlappyObject coordinate system
     * (left bottom edge)
     * @param pos Position in Box2D coordinates (center of the object)
     * @return Position of the left bottom edge (FlappyObject coordinates)
     */
    @Override
    public Vector2 inverseCoordinateTransform(Vector2 pos) {
        return new Vector2(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);
    }

    /**
     * Get object end position (right top edge)
     * @return Position of the right top edge
     */
    @Override
    public Vector2 getEndPosition() {
        return new Vector2(x + sprite.getWidth(), y + sprite.getHeight());
    }

    /**
     * Draw object
     * @param batch On which the object is drawn to
     * @param elapsedTime Time elapsed since previous render cycle in seconds (not used)
     * @return false, FlappyObstacle cannot be destroyed
     */
    @Override
    public boolean draw(SpriteBatch batch, float elapsedTime) {
        sprite.draw(batch);
        return false;
    }

    /**
     * Call this method after ViewPortSize has changed
     * @param ViewPortSizeX New EMGflappy ViewPortSizeX
     * @param ViewPortSizeY New EMGflappy ViewPortSizeY
     */
    @Override
    public void onViewPortChanged(float ViewPortSizeX, float ViewPortSizeY) {
        float width = ViewPortSizeX * getObstacleWidthScale(obstacleType);
        shape.setAsBox(width * 0.5f, height * 0.5f);
        boolean sensor = fixtureDef.isSensor;
        body.destroyFixture(body.getFixtureList().first());
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = sensor;
        body.createFixture(fixtureDef);
        sprite.setSize(width, height);
        body.setTransform(coordinateTransform(new Vector2(x, y)), body.getAngle());
    }

    /**
     * Write FlappyObstacle to json (write also object height)
     * @param json Where options are written to
     */
    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("height", height);
    }

    /**
     * Whether the FlappyObstacle can cause damage or not
     * @return true, by default FlappyObstacles can cause damage
     */
    public boolean canDamage() { return true; }
}

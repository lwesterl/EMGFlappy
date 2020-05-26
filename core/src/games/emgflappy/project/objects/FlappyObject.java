package games.emgflappy.project.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Abstract class for drawable object in FlappyWorld
 */
public abstract class FlappyObject implements Disposable, Json.Serializable {

    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;
    protected float x; /** FlappyObject coordinate system, left edge, Box2D counterpart uses different coordinate system */
    protected float y; /** FlappyObject coordinate system, bottom edge, Box2D counterpart uses different coordinate system */
    protected Body body;
    protected ObjectType type;

    /**
     * Create FlappyObject, call this from inherited class constructors
     * Note: set correct shape for fixtureDef and bodyDef.position in inherited classes
     * @param x Object position in x dimension (left edge)
     * @param y Object position in y dimension (bottom edge)
     * @param bodyType BodyType of the object to be created
     */
    protected FlappyObject(float x, float y, BodyDef.BodyType bodyType) {
        this.x = x;
        this.y = y;
        bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.fixedRotation = true;
        fixtureDef = new FixtureDef();
        type = ObjectType.FlappyObject; // Set this in lower classes to more specific type
    }

    /**
     * Write Options to json
     * @param json Where options are written to
     */
    @Override
    public void write(Json json) {
        json.writeValue("type", type);
        json.writeValue("x", x);
        json.writeValue("y", y);
    }

    /**
     * Needed to implement Json.Serializable but DO NOT call this (rather, do processing in
     * FlappyWorld)
     * @param json Not used
     * @param jsonData Not used
     */
    @Override
    public void read(Json json, JsonValue jsonData) {}

    /**
     * Coordinate transform from FlappyObject coordinates to Box2D physics coordinates
     * (Box2d coordinates are fixed to FlappyObject center whereas FlappyObject coordinates are
     * fixed to left bottom edge)
     * @param pos Position in FlappyObject coordinates (left bottom edge)
     * @return Matching center coordinates, x and y
     */
    public abstract Vector2 coordinateTransform(Vector2 pos);

    /**
     * Coordinate transform from Box2D coordinates (FlappyObject center) to FlappyObject coordinates
     * which are fixed to bottom left edge
     * @param pos Position in Box2D coordinates (center of the object)
     * @return Bottom edge coordinates, x and y
     */
    public abstract Vector2 inverseCoordinateTransform(Vector2 pos);

    /**
     * Draw object on a SpriteBatch
     * @param batch On which the object is drawn to
     * @param elapsedTime Time elapsed since previous render cycle in seconds
     * @return true if object is destroyed, otherwise false
     */
    public abstract boolean draw(SpriteBatch batch, float elapsedTime);

    /**
     * Update object size after ViewPortSize has changed
     * @param ViewPortSizeX New EMGflappy ViewPortSizeX
     * @param ViewPortSizeY New EMGflappy ViewPortSizeY
     */
    public abstract void onViewPortChanged(float ViewPortSizeX, float ViewPortSizeY);

    /**
     * Get object position in x dimension (FlappyObject coordinate system, left edge)
     * @return x (left edge)
     */
    public float getPositionX() {
        return x;
    }

    /**
     * Get object position in y dimension (FlappyObject coordinate system, bottom edge)
     * @return y (bottom edge)
     */
    public float getPositionY() {
        return y;
    }

    /**
     * Get object center position (this is the position in Box2D coordinate system)
     * @return Center position as a Vector
     */
    public Vector2 getCenterPosition() {
        return coordinateTransform(new Vector2(x, y));
    }

    /**
     * Get object end position
     * @return Vector2 specifying the object right top edge
     */
    public abstract Vector2 getEndPosition();

    /**
     * Set object position in FlappyObject coordinate system
     * Note: do not call this from FlappyWord, use setPositionPhysicsCoordinates
     * @param pos New position in FlappyObject coordinate system (left bottom edge)
     */
    public void setPositionFlappyCoordinates(Vector2 pos) {
        x = pos.x;
        y = pos.y;
        bodyDef.position.set(coordinateTransform(pos));
    }

    /**
     * Set object position in Box2D coordinate system (object center coordinates)
     * Note: call this from FlappyWorld to update FlappyObject position
     * @param pos New position in Box2D coordinate system (center of the object)
     */
    public void setPositionPhysicsCoordinates(Vector2 pos) {
        Vector2 flappyPos = inverseCoordinateTransform(pos);
        x = flappyPos.x;
        y = flappyPos.y;
    }

    /**
     * Get FixtureDef of the FlappyObject
     * @return fixtureDef
     */
    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    /**
     * Get BodyDef of the FlappyObject
     * @return bodyDef
     */
    public BodyDef getBodyDef() {
        return bodyDef;
    }

    /**
     * Get Box2D body of the object
     * @return Box2D Body (this might be null)
     */
    public Body getBody() {
        return body;
    }

    /**
     * Set Box2D body for the object
     * @param body Physical counterpart to the FlappyObject in Box2D World
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Get FlappyObject ObjectType
     * @return type
     */
    public ObjectType getType() { return type; }
}

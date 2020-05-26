package games.emgflappy.project.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

/**
 * Class creating FlappyWorld static boundaries: these object are not drawn
 */
public class BoundaryObject implements Disposable {

    private PolygonShape shape;
    private BodyDef bodyDef;

    /**
     * Create FlappyWorld boundary
     * @param x BoundaryObject left edge
     * @param y BoundaryObject bottom edge
     * @param width BoundaryObject width
     * @param height BoundaryObject height
     */
    public BoundaryObject(float x, float y, float width, float height) {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        shape = new PolygonShape();
        shape.setAsBox(width / 2.f, height / 2.f);
        bodyDef.position.set(x + width / 2.f, y + height / 2.f);
    }

    /**
     * Free allocated memory
     */
    @Override
    public void dispose() {
        shape.dispose();
    }

    /**
     * Get BodyDef of the BoundaryObject
     * @return bodyDef used for adding the object to Box2D world
     */
    public BodyDef getBodyDef() {
        return bodyDef;
    }

    /**
     * Get BoundaryObject shape
     * @return shape
     */
    public Shape getShape() {
        return shape;
    }

}

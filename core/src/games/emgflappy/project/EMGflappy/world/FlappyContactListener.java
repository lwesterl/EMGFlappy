package games.emgflappy.project.EMGflappy.world;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import games.emgflappy.project.EMGflappy.objects.Flappy;
import games.emgflappy.project.EMGflappy.objects.FlappyObject;
import games.emgflappy.project.EMGflappy.objects.FlappyObstacle;
import games.emgflappy.project.EMGflappy.objects.ObjectType;
import games.emgflappy.project.EMGflappy.utils.Pair;

/**
 * Contact listener for FlappyWorld
 */
public class FlappyContactListener implements ContactListener {

    /**
     * Listener for contact start events
     * @param contact Between two FlappyObjects (Flappy and some FlappyObstacle)
     */
    @Override
    public void beginContact(Contact contact) {
        Pair<Flappy, FlappyObstacle> objects = getContactedObjects(contact);
        Flappy flappy = objects.getFirst();
        FlappyObstacle obstacle = objects.getSecond();
        if (flappy != null && obstacle != null) {
            flappy.setColliding(true, obstacle);

        }
    }

    /**
     * Listener for contact end events
     * @param contact Between two FlappyObjects (Flappy and some FlappyObstacle)
     */
    @Override
    public void endContact(Contact contact) {
        Pair<Flappy, FlappyObstacle> objects = getContactedObjects(contact);
        Flappy flappy = objects.getFirst();
        FlappyObstacle obstacle = objects.getSecond();
        if (flappy != null && obstacle != null) {
            flappy.setColliding(false, null);
        }
    }


    /**
     * Get FlappyObjects from Contact
     * @param contact Contact information which contains two collided objects
     * @return Pair of Flappy and FlappyObstacle or nulls if the contact was between a BoundaryObject
     */
    private Pair<Flappy, FlappyObstacle> getContactedObjects(Contact contact) {
        Flappy flappy = null;
        FlappyObstacle opponent = null;
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        Object data1 = body1.getUserData();
        Object data2 = body2.getUserData();
        if (data1.getClass() == Pair.class && data2.getClass() == Pair.class) {
            Pair<ObjectType, Object> content1 = (Pair<ObjectType, Object>) data1;
            Pair<ObjectType, Object> content2 = (Pair<ObjectType, Object>) data2;
            if (content1.getFirst() == ObjectType.FlappyObject && content2.getFirst() == ObjectType.FlappyObject) {
                FlappyObject flappyObject1 = (FlappyObject) content1.getSecond();
                FlappyObject flappyObject2 = (FlappyObject) content2.getSecond();
                if (flappyObject1.getType() == ObjectType.Flappy) {
                    flappy = (Flappy) flappyObject1;
                    opponent = (FlappyObstacle) flappyObject2;
                }
                else if (flappyObject2.getType() == ObjectType.Flappy) {
                    flappy = (Flappy) flappyObject2;
                    opponent = (FlappyObstacle) flappyObject1;
                }
            }
        }
        return new Pair<>(flappy, opponent);
    }


    /* Not implemented but needed to implement ContactListener interface */

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}

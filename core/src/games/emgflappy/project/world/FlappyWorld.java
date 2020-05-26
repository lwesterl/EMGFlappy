package games.emgflappy.project.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import games.emgflappy.project.objects.BoundaryObject;
import games.emgflappy.project.objects.Flappy;
import games.emgflappy.project.objects.FlappyLightning;
import games.emgflappy.project.objects.FlappyObject;
import games.emgflappy.project.objects.FlappyObstacle;
import games.emgflappy.project.objects.ObjectType;
import games.emgflappy.project.objects.ObstacleType;
import games.emgflappy.project.utils.Options;
import games.emgflappy.project.utils.Pair;

import java.util.Random;

/**
 * Class used to create and manage Box2D physics world for EMGFlappy
 */
public class FlappyWorld implements Disposable {

    // See for reference: https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_hello.html
    private static final float TIME_STEP = 1/60f; // Box2D time step
    private static final float FLAPPY_UPDATE_PERIOD = 1/60f; // How often forces are applied to Flappy
    private static final int VELOCITY_ITERATIONS = 6; // Box2D velocity iterations
    private static final int POSITION_ITERATIONS = 2; // Box2D position iterations
    private static final float BOUNDARY_WIDTH = 10000000.f; // Used to create map level boundaries, this needs to be very large value but larger values seem to break something in Box2D collision detection
    private static final String WorldSaveFile = "EMGflappy_world_save.json";
    private static final float GraphSizeCorrection = Flappy.getFlappySize() / 2.f + 0.001f; // To account for Flappy's size when calculation FlappyPath upper and lower obstacle sizes are increased (set this value to 0.f to disable the size correction)

    private static float GravityY = -10f; // Gravity in y dimension (- downwards, + upwards)
    private static float GravityX = 0.f; // Gravity in x dimension (- left, + right)
    private static float TunnelProbability = 0.15f; // How probable (0.0 - 1.0) it is that a Tunnel is created
    private static float SpaceBetweenObjects = 0.3f; // How much space there is between objects in relation to ViewPortSizeX
    private static float VerticalGapBetweenObstacles = 0.25f; // How much space there is between Normal Obstacles in y dimension, ([0.15, 0.30] is a good range but this depends on FlappySize, and to disable upper Obstacles set value > 1.0)
    private static int MinObstaclesBeforeFirstLightning = 8; // How many Normal obstacles there are before the first lightning at minimum (Note: Normal obstacles are always created in pairs)
    private World world;
    private float accumulatedTime;

    private Array<Body> bodies;
    private Flappy flappy; // Movable character
    private Array<FlappyObject> flappyObjects; // Rest of the FlappyObjects
    private Array<BoundaryObject> worldBoundaries; // Invisible world boundaries
    private Timer timer;
    private Random rand;
    private float startPosition;



    /**
     * Constructor for FlappyWorld: create Box2D world
     * @param ViewPortSizeX EMGflappy ViewPortSizeX
     * @param ViewPortSizeY EMGflappy ViewPortSizeY, fixed dimension of the game world
     * @param startPosition Position in x dimension where the first FlappyObstacle is created
     * @param worldWidth Target width of the world
     * @param tryLoad Whether try to load previously stored FlappyWorld
     */
    public FlappyWorld(float ViewPortSizeX, float ViewPortSizeY, float startPosition, float worldWidth, boolean tryLoad) {
        this.startPosition = startPosition;
        Box2D.init();
        world = new World(new Vector2(GravityX, GravityY), true);
        world.setContactListener(new FlappyContactListener());
        accumulatedTime = 0.f;
        rand = new Random(System.currentTimeMillis());
        boolean init = true;
        if (tryLoad) init = !load(ViewPortSizeX, ViewPortSizeY); // When loading fails, init normally
        if (init) {
            initWorld(ViewPortSizeX, ViewPortSizeY);
            extendWorld(worldWidth, ViewPortSizeX, ViewPortSizeY);
        }
        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                flappy.applyForce();
            }
        }, FLAPPY_UPDATE_PERIOD, FLAPPY_UPDATE_PERIOD);
    }

    /**
     * Dispose allocated memory
     */
    @Override
    public void dispose() {
        timer.clear();
        timer.stop();
        for (FlappyObject flappyObject : flappyObjects) {
            flappyObject.dispose();
        }
        for (BoundaryObject boundaryObject : worldBoundaries) {
            boundaryObject.dispose();
        }
        flappy.dispose();
    }

    /**
     * Do a physics update step
     * @param deltaTime Real time elapsed since last call of this method in seconds
     * @return Flappy position in x dimension which can be used to set camera to follow Flappy
     * (this is the center of the Flappy in x dimension)
     */
    public float physicsStep(float deltaTime) {
        world.setGravity(new Vector2(GravityX, Options.getInstance().Difficulty.getGravityScale() * GravityY));
        float frameTime = Math.min(deltaTime, 0.25f); // Set max value to avoid possible spiral of death on slow devices
        accumulatedTime += frameTime;
        while (accumulatedTime >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulatedTime -= TIME_STEP;
        }
        updateFlappyObjects();
        flappy.applyForce();
        return flappy.getCenterPosition().x;
    }

    /**
     * Draw FlappyObjects
     * @param batch SpriteBatch where the object should be drawn to
     * @param deltaTime Time elapsed since last render cycle in seconds
     * @param ViewPortSizeX EMGflappy ViewPortSizeX
     */
    public void draw(SpriteBatch batch, float deltaTime, float ViewPortSizeX) {
        // Draw all objects
        for (FlappyObject flappyObject : flappyObjects) {
            if (flappyObject.getType() == ObjectType.FlappyLightning) {
                FlappyLightning flappyLightning = (FlappyLightning) flappyObject;
                flappyLightning.setVisible(flappy.getPositionX(), flappy.getEndPosition().x, ViewPortSizeX);
            }
            flappyObject.draw(batch, deltaTime);
        }
        flappy.draw(batch, deltaTime);
    }

    /**
     * Call this method after EMGflappy ViewPort has changed, updated all object sizes to match the
     * new ViewPort
     * @param ViewPortSizeX New EMGflappy ViewPortSizeX
     * @param ViewPortSizeY New EMGflappy ViewPortSizeY
     */
    public void onViewPortChanged(float ViewPortSizeX, float ViewPortSizeY) {
        for (FlappyObject flappyObject : flappyObjects) {
            flappyObject.onViewPortChanged(ViewPortSizeX, ViewPortSizeY);
        }
        flappy.onViewPortChanged(ViewPortSizeX, ViewPortSizeY);
    }

    /**
     * Extend world (create FlappyObstacles)
     * @param worldWidth How many world units the obstacles should cover
     * @param ViewPortSizeX EMGflappy.ViewPortSizeX
     * @param ViewPortSizeY EMGflappy.ViewPortSizeY
     */
    public void extendWorld(float worldWidth, float ViewPortSizeX, float ViewPortSizeY) {
        removeNotVisibleObjects(worldWidth);
        float endPosition = startPosition + worldWidth;
        do {
            float random = rand.nextFloat();
            ObstacleType type = ObstacleType.Normal;
            FlappyObject flappyObject;
            if (random < FlappyWorld.TunnelProbability && flappyObjects.size > FlappyWorld.MinObstaclesBeforeFirstLightning) {
                // Create one Tunnel obstacle
                float height = ViewPortSizeY * ObstacleType.TunnelObstacleHeight;
                float y = ViewPortSizeY - height;
                flappyObject = new FlappyLightning(startPosition, y, height, ViewPortSizeX);
            } else {
                // Create two Normal obstacles with identical x dimensional positions
                float rangeMin = 0.3f;
                float rangeMax = 0.7f;
                float heightScale = rangeMin + rand.nextFloat() * (rangeMax - rangeMin);
                float height = heightScale * ViewPortSizeY;
                float y2 = height + ViewPortSizeY * FlappyWorld.VerticalGapBetweenObstacles;
                float height2 = ViewPortSizeY - y2;
                flappyObject = new FlappyObstacle(type, startPosition, 0.f, height, ViewPortSizeX, false);
                FlappyObject flappyObjectUpper = new FlappyObstacle(type, startPosition, y2, height2, ViewPortSizeX, true);
                addBody(flappyObjectUpper);
                flappyObjects.add(flappyObjectUpper);
            }
            addBody(flappyObject);
            flappyObjects.add(flappyObject);
            startPosition = flappyObject.getEndPosition().x + FlappyWorld.SpaceBetweenObjects * ViewPortSizeX;
        } while (startPosition < endPosition);
        world.getBodies(bodies);
    }

    /**
     * Get Flappy's current HP
     * @return flappy's HP
     */
    public int getFlappyHP() {
        return flappy.getHP();
    }

    /**
     * Get Flappy's current x dimensional position
     * @return flappy's x coordinate
     */
    public float getFlappyPositionX() {
        return flappy.getPositionX();
    }

    /**
     * Wrapper for getting Flappy hits
     * @return Flappy's hits
     */
    public int getFlappyHits() {
        return flappy.getHits();
    }

    /**
     * Save FlappyWorld to a json file
     * (this will save all FlappyObstacle positions, Flappy position and startPosition)
     */
    public void save() {
        try {
            FileHandle file = Gdx.files.local(FlappyWorld.WorldSaveFile);
            Json json = new Json();
            String flappyStr = json.toJson(flappy);
            String objects = json.toJson(flappyObjects);
            String worldSave = "{startPosition:" + startPosition + ",Flappy:" + flappyStr + ",objects:" + objects + "}";
            file.writeString(worldSave, false);
        } catch (Exception e) {
            Gdx.app.log(FlappyWorld.class.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Load FlappyWorld from a json file
     * CAUTION: if the extendWorld implementation is changed this will broke
     * @param ViewPortSizeX EMGflappy ViewPortSizeX
     * @param ViewPortSizeY EMGflappy ViewPortSizeY
     * @return true if loading succeeded, otherwise false
     */
    private boolean load(float ViewPortSizeX, float ViewPortSizeY) {
        float startPosition_cpy = startPosition;
        try {
            FileHandle file = Gdx.files.local(FlappyWorld.WorldSaveFile);
            String save = file.readString();
            JsonValue jsonValue = new JsonReader().parse(save);
            startPosition = jsonValue.getFloat("startPosition");

            JsonValue flappyJSON = jsonValue.child.next;
            float flappyX = flappyJSON.getFloat("x");
            float flappyY = flappyJSON.getFloat("y");
            int HP = flappyJSON.getInt("HP");
            flappy = new Flappy(flappyX, flappyY, ViewPortSizeX, ViewPortSizeY);
            flappy.setHP(HP);
            addBody(flappy);

            flappyObjects = new Array<>();
            JsonValue objectsJSON = flappyJSON.next;
            JsonValue object = objectsJSON.child;
            while (object != null) {
                String type = object.getString("type");
                float x = object.getFloat("x");
                float y = object.getFloat("y");
                float height = object.getFloat("height");
                FlappyObject flappyObject;
                if (type.equals("FlappyObstacle")) {
                    flappyObject = new FlappyObstacle(ObstacleType.Normal, x, y, height, ViewPortSizeX, y != 0.f);
                    if (y == 0.f) {
                        // Add path only for the lower obstacles
                        float heightScale = height / ViewPortSizeY;
                    }
                } else {
                    flappyObject = new FlappyLightning(x, y, height, ViewPortSizeX);
                }
                addBody(flappyObject);
                flappyObjects.add(flappyObject);
                object = object.next;
            }
            createWorldBoundaries(ViewPortSizeY);
            bodies = new Array<>();
            world.getBodies(bodies);
            return true;
        } catch (Exception e) {
            Gdx.app.log(FlappyWorld.class.getSimpleName(), e.getMessage());
            startPosition = startPosition_cpy;
            return false;
        }
    }

    /**
     * Initialize FlappyWorld by creating initial objects
     * @param ViewPortSizeX Initial EMGflappy ViewPortSizeX
     * @param ViewPortSizeY Initial EMGflappy ViewPortSizeY, this is the fixed dimension
     */
    private void initWorld(float ViewPortSizeX, float ViewPortSizeY) {
        flappy = new Flappy(0.f, 46.f, ViewPortSizeX, ViewPortSizeY);
        addBody(flappy);
        // Create other object here
        flappyObjects = new Array<>();

        // Create invisible world boundaries
        createWorldBoundaries(ViewPortSizeY);

        bodies = new Array<>();
        world.getBodies(bodies);
    }

    /**
     * Add Body for FlappyObject
     * @param flappyObject FlappyObject for which the physical representation is created for
     */
    private void addBody(FlappyObject flappyObject) {
        BodyDef bodyDef = flappyObject.getBodyDef();
        Body body = world.createBody(bodyDef);
        body.setUserData(new Pair<ObjectType, Object>(ObjectType.FlappyObject, flappyObject));
        body.createFixture(flappyObject.getFixtureDef());
        flappyObject.setBody(body);
    }

    /**
     * Create world boundaries
     * @param ViewPortSize ViewPortSize, EMGflappy ViewPortSizeY
     */
    private void createWorldBoundaries(float ViewPortSize) {
        worldBoundaries = new Array<>();
        BoundaryObject lowerBoundary = new BoundaryObject(-BOUNDARY_WIDTH / 2.f, -0.1f, BOUNDARY_WIDTH, 0.1f);
        BoundaryObject upperBoundary = new BoundaryObject(-BOUNDARY_WIDTH / 2.f, ViewPortSize, BOUNDARY_WIDTH, 0.1f);
        Body body1 = world.createBody(lowerBoundary.getBodyDef());
        Body body2 = world.createBody(upperBoundary.getBodyDef());
        body1.setUserData(new Pair<ObjectType, Object>(ObjectType.BoundaryObject, lowerBoundary));
        body2.setUserData(new Pair<ObjectType, Object>(ObjectType.BoundaryObject, upperBoundary));
        body1.createFixture(lowerBoundary.getShape(), 0.f);
        body2.createFixture(upperBoundary.getShape(), 0.f);
        worldBoundaries.add(lowerBoundary);
        worldBoundaries.add(upperBoundary);
    }

    /**
     * Update FlappyObject positions after physics step
     */
    private void updateFlappyObjects() {
        for (Body body : bodies) {
            Object userData = body.getUserData();
            if (userData.getClass() == Pair.class) {
                Pair<ObjectType, Object> pair = (Pair<ObjectType, Object>) userData;
                if (pair.getFirst() == ObjectType.FlappyObject) {
                    FlappyObject flappyObject = (FlappyObject) pair.getSecond();
                    Vector2 pos = body.getPosition();
                    flappyObject.setPositionPhysicsCoordinates(pos);
                }
            }

        }
    }

    /**
     * Remove FlappyObjects that are not visible anymore
     * @param worldWidth Width of the game world, this is used to detect which objects are not visible
     */
    private void removeNotVisibleObjects(float worldWidth) {
        float flappyPosition = flappy.getPositionX();
        int i = 0;
        int removeEndIdx = 0;
        for (FlappyObject flappyObject : flappyObjects) {
            i++;
            if (flappyObject.getPositionX() + worldWidth < flappyPosition) {
                if (flappyObject.getBody() != null) world.destroyBody(flappyObject.getBody());
                removeEndIdx = i;
                flappyObject.dispose();
            }
        }
        if (removeEndIdx > 0) flappyObjects.removeRange(0, removeEndIdx);
    }
}

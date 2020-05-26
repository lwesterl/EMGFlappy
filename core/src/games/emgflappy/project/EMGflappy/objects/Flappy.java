package games.emgflappy.project.EMGflappy.objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import games.emgflappy.project.EMGflappy.EMGflappy;
import games.emgflappy.project.EMGflappy.FlappyTextures;
import games.emgflappy.project.EMGflappy.UI.ScreenManager;
import games.emgflappy.project.EMGflappy.audio.AudioType;
import games.emgflappy.project.EMGflappy.audio.FlappyAudio;

public class Flappy extends FlappyObject {

    private static float FlappyDensity = 0.5f;
    private static float FlappyFriction = 0.f;
    private static float FlappyRestitution = 0.f;
    private static float FlappyAccelerationY = 20.f; // How much acceleration is applied to Flappy in y dimension after it's touched each update cycle
    private static float FlappyVelocityX = 5.f; // Linear velocity for Flappy in x dimension
    private static float FlappyFrameTime = 0.25f; // How many seconds one Flappy animation frame takes @see setFlappyPressed

    private static boolean FlappyPressed = false; /** When FlappyPressed is set to true force is applied to Flappy */
    private static float FlappySize = 0.08f; /** Scale of the flappy texture (used for width and height) relating to ViewPortSize @see EMGFlappy */
    private static int FlappyHP = Integer.MAX_VALUE; // How many hit points Flappy has (set this to MAX_VALUE for 'never ending' game loop)
    private static int CollisionDamage = 10; // How much damage is caused colliding with another object
    private static float CollisionDamageInterval = 1.f; // How often (in seconds) further damage is caused when collision continues
    private static float AnimationLength = 0.33f * CollisionDamageInterval; // How long the animation is (generally good idea to keep it < CollisionDamageInterval)

    private PolygonShape shape;
    private Sprite[] sprites;
    private int currentSprite;
    private float frameTime;
    private boolean colliding;
    private int HP;
    private long collisionTime;
    private FlappyObstacle obstacle;
    private Animation<TextureRegion> featherAnimation;
    private float animationTime;
    private int hits;

    /**
     * Set FlappyPressed to enable/disable force applied on Flappy
     * Call this after user touch inputs starts/stops
     * @param pressed Whether the flappy is pressed/touched or not
     */
    public static void setFlappyPressed(boolean pressed) {
        if (pressed != Flappy.FlappyPressed) {
            Flappy.FlappyPressed = pressed;
            Flappy.FlappyFrameTime = pressed ? 0.10f : 0.25f;
            FlappyAudio.playLooping(AudioType.WingFlappingAudio, pressed);
        }
    }

    /**
     * Get Flappy CollisionDamageInterval (GameApplication needs this for computing score)
     * @return Flappy.CollisionDamageInterval
     */
    public static float getCollisionDamageInterval() {
        return CollisionDamageInterval;
    }

    /**
     * Get maximum HP for Flappy
     * @return FlappyHP
     */
    public static int getMaxFlappyHP() { return Flappy.FlappyHP; }

    /**
     * Get Flappy size in relation to viewport sizes (this size is used for both width and height)
     * @return FlappySize
     */
    public static float getFlappySize() {
        return Flappy.FlappySize;
    }


    /**
     * Constructor for Flappy
     * @param x Initial position in x dimension
     * @param y Initial position in y dimension
     * @param ViewPortSizeX EMGflappy ViewPortSizeX used for creating correct size object
     * @param ViewPortSizeY EMGflappy ViewPortSizeY This implementation relies that this is the fixed
     *                      viewport size: game height is fixed
     */
    public Flappy(float x, float y, float ViewPortSizeX, float ViewPortSizeY) {
        super(x, y, BodyDef.BodyType.DynamicBody);
        type = ObjectType.Flappy;
        shape = new PolygonShape();
        float width = ViewPortSizeX * Flappy.FlappySize;
        float height = ViewPortSizeY * Flappy.FlappySize;
        shape.setAsBox(width * 0.5f, height * 0.5f);
        // fixtureDef is created in super class constructor
        fixtureDef.shape = shape;
        fixtureDef.density = Flappy.FlappyDensity;
        fixtureDef.friction = Flappy.FlappyFriction;
        fixtureDef.restitution = Flappy.FlappyRestitution;

        sprites = new Sprite[]{ new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame1)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame2)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame3)),
                                new Sprite(EMGflappy.getTexture(FlappyTextures.FlappyFrame4))};
        for (Sprite sprite : sprites) {
            sprite.setSize(width, height);
        }
        currentSprite = 0;
        frameTime = 0.f;
        HP = Flappy.FlappyHP;
        obstacle = null;
        animationTime = Flappy.AnimationLength + 1.f;
        hits = 0;
        setPositionFlappyCoordinates(new Vector2(x, y));
        createAnimation();
    }

    /**
     * Dispose object resources
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
        return new Vector2(pos.x + sprites[currentSprite].getWidth() * 0.5f, pos.y + sprites[currentSprite].getHeight() * 0.5f);
    }

    /**
     * Transform Box2D coordinates (center of the object) to FlappyObject coordinate system
     * (left bottom edge)
     * @param pos Position in Box2D coordinates (center of the object)
     * @return Position of the left bottom edge (FlappyObject coordinates)
     */
    @Override
    public Vector2 inverseCoordinateTransform(Vector2 pos) {
        return new Vector2(pos.x - sprites[currentSprite].getWidth() * 0.5f, pos.y - sprites[currentSprite].getHeight() * 0.5f);
    }

    /**
     * Get object end position (right top edge)
     * @return Position of the right top edge
     */
    @Override
    public Vector2 getEndPosition() {
        return new Vector2(x + sprites[currentSprite].getWidth(), y + sprites[currentSprite].getHeight());
    }

    /**
     * Draw object
     * @param batch On which the object is drawn to
     * @param elapsedTime Time elapsed since previous render cycle in seconds
     * @return true if Flappy is destroyed (game should end), otherwise false
     */
    @Override
    public boolean draw(SpriteBatch batch, float elapsedTime) {
        // Draw Flappy
        frameTime += elapsedTime;
        if (frameTime >= Flappy.FlappyFrameTime) {
            currentSprite = currentSprite + 1 < sprites.length ? currentSprite + 1 : 0;
            frameTime = 0.f;
        }
        sprites[currentSprite].setPosition(getPositionX(), getPositionY());
        sprites[currentSprite].draw(batch);

        animationTime += elapsedTime;

        // Cause damage if colliding
        if (colliding) {
            if (ScreenManager.isPaused()) collisionTime = System.nanoTime(); // do not damage Flappy if the game is paused (this is approximately correct, it will reset current damage cycle)
            float damageTime = (float) (System.nanoTime() - collisionTime) / 1000000000.f; // to s
            while (damageTime > Flappy.CollisionDamageInterval) {
                damageFlappy();
                damageTime -= Flappy.CollisionDamageInterval;
            }
        }
        // Draw animation (damageFlappy call needs to be before this one)
        if (animationTime < Flappy.AnimationLength) {
            TextureRegion currentAnimationFrame = featherAnimation.getKeyFrame(animationTime, false);
            Sprite animationSprite = new Sprite(currentAnimationFrame);
            animationSprite.setSize(sprites[currentSprite].getWidth(), sprites[currentSprite].getHeight());
            animationSprite.setPosition(x, y);
            animationSprite.draw(batch);
        }
        return HP > 0;
    }

    /**
     * Call this method after the ViewPortSize has changed
     * @param ViewPortSizeX New EMGflappy ViewPortSizeX New EMGflappy ViewPortSizeX
     * @param ViewPortSizeY New EMGflappy ViewPortSizeY New EMGflappy ViewPortSizeY (assumes that
     *                      this is the fixed dimension)
     */
    @Override
    public void onViewPortChanged(float ViewPortSizeX, float ViewPortSizeY) {
        float width = ViewPortSizeX * Flappy.FlappySize;
        float height = ViewPortSizeY * Flappy.FlappySize;
        shape.setAsBox(width * 0.5f, height * 0.5f);
        for (Sprite sprite : sprites) sprite.setSize(width, height);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = Flappy.FlappyDensity;
        fixtureDef.friction = Flappy.FlappyFriction;
        fixtureDef.restitution = Flappy.FlappyRestitution;
        body.destroyFixture(body.getFixtureList().first());
        body.createFixture(fixtureDef);
        body.setTransform(coordinateTransform(new Vector2(x, y)), body.getAngle());
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("HP", HP);
    }

    /**
     * Apply force to the Flappy
     * Note: Force are applied to center of Flappy so that it doesn't start rotating
     * to rotate
     */
    public void applyForce() {
        if (body != null) {
            float mass = body.getMass();
            if (Flappy.FlappyPressed) {
                body.applyForceToCenter(0.f, FlappyAccelerationY * mass, true);
            }
            Vector2 velocity = body.getLinearVelocity().cpy();
            velocity.set(FlappyVelocityX, velocity.y);
            body.setLinearVelocity(velocity);
        }
    }

    /**
     * Set Flappy colliding, call this after flappy has started or stopped colliding with some other
     * FlappyObject
     * @param colliding true when flappy is started colliding, false when contact has stopped
     * @param obstacle Which Flappy is colliding or null if collision over
     */
    public void setColliding(boolean colliding, FlappyObstacle obstacle) {
        this.colliding = colliding;
        this.obstacle = obstacle;
        if (colliding) damageFlappy();
    }

    /**
     * Get Flappy HP
     * @return HP
     */
    public int getHP() {
        return HP;
    }

    /**
     * Directly set Flappy's HP
     * @param HP New HP value for Flappy
     */
    public void setHP(int HP) {
        this.HP = HP;
    }

    /**
     * Get how many times Flappy has collided with object and taken damage
     * @return hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * Cause damage to Flappy
     * Call this in draw before animation is drawn because this resets the animationTime
     */
    private void damageFlappy() {
        if (obstacle != null && obstacle.canDamage()) {
            HP -= Flappy.CollisionDamage;
            hits++;
            if (animationTime >= Flappy.AnimationLength) animationTime = 0.f;
        }
        collisionTime = System.nanoTime();
    }

    /**
     * Create feather animation
     */
    private void createAnimation() {
        Texture texture = EMGflappy.getTexture(FlappyTextures.FeatherAnimation);
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / FlappyTextures.FeatherAnimationSpriteSheetColumns,
                texture.getHeight() / FlappyTextures.FeatherAnimationSpriteSheetRows);
        int size = FlappyTextures.FeatherAnimationSpriteSheetRows * FlappyTextures.FeatherAnimationSpriteSheetColumns;
        TextureRegion[] animationRegion = new TextureRegion[size];
        int index = 0;
        for (int i = 0; i < FlappyTextures.FeatherAnimationSpriteSheetRows; i++) {
            for (int j = 0; j < FlappyTextures.FeatherAnimationSpriteSheetColumns; j++) {
                animationRegion[index++] = tmp[i][j];
            }
        }
        featherAnimation = new Animation<>(Flappy.AnimationLength / size, animationRegion);
    }
}

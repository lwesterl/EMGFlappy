package games.emgflappy.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import games.emgflappy.project.UI.ScreenManager;
import games.emgflappy.project.audio.FlappyAudio;
import games.emgflappy.project.objects.Flappy;
import games.emgflappy.project.utils.Options;
import games.emgflappy.project.world.FlappyWorld;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Rehabilitation game made with libGDX
 */
public class EMGflappy extends Game implements InputProcessor {

	public static boolean LoadGame = false; // Set this to true in GameApplication to load old game save

	private static final int WorldWidth = 500; // World width
	private static final int WorldHeight = 50; // World height
	private static final String TAG = EMGflappy.class.getSimpleName();
	private static Vector2 HUDSize = new Vector2(8, 4); // HUD size in world units
	private static Vector2 HUDPosition = new Vector2(0.f, 0.f); // HUD position in visible viewport (0.f, 0.f is the left bottom edge)
	private static Map<FlappyTextures, Texture> textures;
	private static float FirstObstaclePositionX = 30.f; // X dimensional position where the first FlappyObstacle is created to
	private static float ViewPortSizeXWhenFixed = 80.f; // X dimension viewport which is used to fix object sizes when FixViewPortSizes == true
	private static float BackgroundMovementSpeed = 0.2f; // How fast background is moved (0.f -> no movement, > 0.f moves from right to left)

	private float ViewPortSizeX;
	private float ViewPortSizeY;
	private HUD hud;
	private Camera camera;
	private SpriteBatch batch;
	private FlappyWorld flappyWorld;
	private long time;
	private Deque<Sprite> backgroundSprites;
	private int worldCounter;


	/**
	 * Load game textures
	 * Call this when the game is created
	 */
	private static void loadTextures() {
		textures = new HashMap<>();
		try {
			for (Map.Entry<FlappyTextures, String> entry : FlappyTextures.getTexturesPathMap().entrySet()) {
				textures.put(entry.getKey(), new Texture(Gdx.files.internal(entry.getValue())));
			}
		} catch (RuntimeException e) {
			Gdx.app.exit();
		}
	}

	/**
	 * Dispose game textures
	 * Call this from dispose
	 */
	private static void disposeTextures() {
		Collection<Texture> loadedTextures = textures.values();
		for (Texture texture : loadedTextures) {
			texture.dispose();
		}
	}

	/**
	 * Get Texture speficied by FlappyTextures enum value
	 * @param flappyTexture FlappyTextures enum
	 * @return Texture matching the enum value
	 */
	public static Texture getTexture(FlappyTextures flappyTexture) {
		return textures.get(flappyTexture);
	}


	/*
	 	----------Viewport-------------------------------------------
	 	|	The whole world is shown in Y dimension					|
	 	|	X dimension should be scaled based on the aspect ratio	|
		-------------------------------------------------------------
	 */

	/**
	 * Method called when application is created
	 * Load all textures etc.
	 */
	@Override
	public void create() {
		Options.getInstance().readOptions();
		FlappyAudio.loadAudioFiles();
		ScreenManager.init(this);
		float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		ViewPortSizeX = aspectRatio * WorldHeight;
		ViewPortSizeY = WorldHeight;
		camera = new OrthographicCamera(ViewPortSizeX, ViewPortSizeY);
		batch = new SpriteBatch();
		EMGflappy.loadTextures();
		hud = new HUD(HUDPosition, HUDSize, ViewPortSizeX, ViewPortSizeY);
		if (Options.getInstance().FixViewPortSizes) ViewPortSizeX = EMGflappy.ViewPortSizeXWhenFixed;
		flappyWorld =  new FlappyWorld(ViewPortSizeX, ViewPortSizeY, EMGflappy.FirstObstaclePositionX, WorldWidth, EMGflappy.LoadGame);
		Gdx.input.setInputProcessor(this);
		time = System.currentTimeMillis();
		worldCounter = 1;
		float flappyX = flappyWorld.getFlappyPositionX();
		while(flappyX >= (float) (worldCounter * WorldWidth) - ViewPortSizeX) worldCounter++; // This is only needed when the world was loaded
		initBackground();
		camera.position.set(flappyX, WorldHeight / 2.f, camera.position.z);
		camera.update();
		if (EMGflappy.LoadGame) {
			ScreenManager.showPauseScreen();
			EMGflappy.LoadGame = false;
		} else {
			ScreenManager.showMenuScreen(false);
		}
	}

	/**
	 * Restart game
	 */
	public void restart() {
		flappyWorld.dispose();
		Flappy.setFlappyPressed(false);
		flappyWorld = new FlappyWorld(ViewPortSizeX, ViewPortSizeY, EMGflappy.FirstObstaclePositionX, WorldWidth, false);
		flappyWorld.extendWorld(WorldWidth, ViewPortSizeX, ViewPortSizeY);
		backgroundSprites.clear();
		time = System.currentTimeMillis();
		worldCounter = 1;
		initBackground();
	}

	/**
	 * Finish game session
	 */
	public void finishGame() {
		Gdx.app.exit();
	}

	/**
	 * Pause game (save game state)
	 */
	@Override
	public void pause() {
		flappyWorld.save();
		Options.getInstance().writeOptions();
		if (getScreen() == null) ScreenManager.showPauseScreen();
	}

	/**
	 * Resume game after pausing
	 */
	@Override
	public void resume() {
		time = System.currentTimeMillis();
	}

	/**
	 * Set input focus on the game itself. Call this from ScreenManager after switching back to
	 * the game
	 */
	public void setInputFocus() {
		Gdx.input.setInputProcessor(this);
	}

	/**
	 * Method called when the screen is resized
	 * @param width New screen width
	 * @param height New screen height
	 */
	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;
		ViewPortSizeX = aspectRatio * WorldHeight;
		if (Options.getInstance().FixViewPortSizes) ViewPortSizeX = EMGflappy.ViewPortSizeXWhenFixed;
		camera.viewportWidth = ViewPortSizeX;
		camera.update();
		flappyWorld.onViewPortChanged(ViewPortSizeX, ViewPortSizeY);
		hud.onViewPortChanged(ViewPortSizeX, ViewPortSizeY);
		if (getScreen() != null) screen.resize(width, height);
	}

	/**
	 * Method called by the game loop every time rendering should be performed
	 * Game logic updates and rendering
	 * (There is no explicit main loop in libGDX)
	 */
	@Override
	public void render() {
		// Input processing
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (Sprite sprite : backgroundSprites) {
			sprite.draw(batch);
		}
		long timeNow = System.currentTimeMillis();
		float elapsedTime = ((float) (timeNow - time)) / 1000.f; // millis to s
		time = timeNow;
		if (!ScreenManager.updatePhysics()) elapsedTime = 0.f;

		flappyWorld.draw(batch, elapsedTime, ViewPortSizeX);
		batch.end();

		if (ScreenManager.updatePhysics()) {
			// Update physics when not showing a menu Screen
			float flappyX = flappyWorld.physicsStep(elapsedTime);

			camera.position.set(flappyX, camera.position.y, camera.position.z);
			camera.update();
			moveBackground(elapsedTime);
			if (extendWorld(flappyX))
				flappyWorld.extendWorld(WorldWidth, ViewPortSizeX, ViewPortSizeY);
		} else Flappy.setFlappyPressed(false);
		int hp = flappyWorld.getFlappyHP();
		if (hp <= 0 && getScreen() == null) ScreenManager.showEndScreen();
		hud.draw(batch, hp, Flappy.getMaxFlappyHP());
		ScreenManager.renderScreen();
	}

	/**
	 * Method called when the application is destroyed
	 * Dispose textures etc.
	 * Store game state
	 */
	@Override
	public void dispose () {
		hud.dispose();
		batch.dispose();
		flappyWorld.dispose();
		EMGflappy.disposeTextures();
		FlappyAudio.disposeAudio();
	}

	// Input processing

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Flappy.setFlappyPressed(true);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Flappy.setFlappyPressed(false);
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACKSPACE) {
			pause();
			return true;
		}
		return false;
	}


	/** Not implemented but must be overridden */

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}


	// Background textures handling

	/**
	 * Create background images
	 * Note: there needs to be two images created so that the images fill the whole screen
	 */
	private void initBackground() {
		backgroundSprites = new LinkedList<>();
		Sprite background = new Sprite(getTexture(FlappyTextures.BackGround));
		background.setSize(WorldWidth, WorldHeight);
		Sprite backgroundContinues = new Sprite(background);
		background.setPosition(-WorldWidth, 0.f);
		backgroundContinues.setPosition(0.f, 0.f);
		backgroundSprites.addLast(background);
		backgroundSprites.addLast(backgroundContinues);
	}

	/**
	 * Method which checks whether the world needs to be extended and modifies background when
	 * needed. Call this each time in render
	 * @param flappyX X coordinate position of the Flappy
	 * @return true if world needs to be extended, otherwise false
	 */
	private boolean extendWorld(float flappyX) {
		float backgroundEndX = backgroundSprites.getLast().getX() + backgroundSprites.getLast().getWidth();
		if (flappyX >= backgroundEndX - ViewPortSizeX) {
			Sprite background = new Sprite(getTexture(FlappyTextures.BackGround));
			background.setSize(WorldWidth, WorldHeight);
			background.setPosition(backgroundEndX, 0.f);
			backgroundSprites.removeFirst(); // This isn't needed anymore, it's well outside of the visible region
			backgroundSprites.addLast(background);
		}
		if (flappyX >= (float) (worldCounter * WorldWidth) - ViewPortSizeX) {
			worldCounter++;
			return true;
		}
		return false;
	}

	/**
	 * Move background textures periodically
	 * @param elapsedTime How much time has elapsed since last update
	 */
	private void moveBackground(float elapsedTime) {
		for (Sprite background : backgroundSprites) {
			background.setX(background.getX() - elapsedTime * EMGflappy.BackgroundMovementSpeed);
		}
	}
}

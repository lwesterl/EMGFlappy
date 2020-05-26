package games.emgflappy.project.EMGflappy.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import games.emgflappy.project.EMGflappy.EMGflappy;

/**
 * A static class which handles communication between different screens
 */
public final class ScreenManager {

    private static EMGflappy game;
    private static Screen screen;

    private ScreenManager() {}


    /**
     * Init ScreenManager, call this when EMGflappy is created
     * @param game Instance of EMGflappy
     */
    public static void init(EMGflappy game) {
        ScreenManager.game = game;
    }

    /**
     * Render current Screen (renders nothing if no Screen has been set to EMGflappy)
     * Call this at the end of an EMGflappy render cycle
     */
    public static void renderScreen() {
        if (ScreenManager.game != null && ScreenManager.game.getScreen() != null) {
            ScreenManager.game.getScreen().render(Gdx.graphics.getDeltaTime());
        }
    }

    /**
     * Quit the game
     * This will dispose EMGflappy and all Screens
     */
    public static void quitGame() {
        Gdx.app.exit();
    }

    /**
     * Force resize on the game
     */
    public static void resizeGame() {
        if (game != null) {
            game.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    /**
     * Resume the game (EMGflappy)
     * Call this from PauseScreen when resume button is clicked
     */
    public static void resumeGame() {
        if (game != null) {
            game.setInputFocus();
            game.setScreen(null);
            if (ScreenManager.screen != null) ScreenManager.screen.dispose();
            ScreenManager.screen = null;
        }
    }

    /**
     * Restart game (EMGflappy)
     */
    public static void restartGame() {
        if (game != null) {
            game.setInputFocus();
            game.setScreen(null);
            if (ScreenManager.screen != null) ScreenManager.screen.dispose();
            ScreenManager.screen = null;
            game.restart();
        }
    }

    /**
     * Check whether the game is paused or not
     * @return true if the game is paused, otherwise false
     */
    public static boolean isPaused() {
        return ScreenManager.screen != null;
    }

    /**
     * Show PauseScreen
     */
    public static void showPauseScreen() {
        if (game != null) {
            if (ScreenManager.screen != null) ScreenManager.screen.dispose();
            ScreenManager.screen = new PauseScreen();
            game.setScreen(ScreenManager.screen);
        }
    }

    /**
     * Show OptionsScreen
     */
    public static void showOptionsScreen() {
        if (game != null) {
            if (ScreenManager.screen != null) ScreenManager.screen.dispose();
            ScreenManager.screen = new OptionsScreen();
            game.setScreen(ScreenManager.screen);
        }
    }

    /**
     * Show EndScreen after game is over
     */
    public static void showEndScreen() {
        if (game != null) {
            if (ScreenManager.screen != null) ScreenManager.screen.dispose();
            ScreenManager.screen = new EndScreen();
            game.setScreen(ScreenManager.screen);
            game.finishGame();
        }
    }

    /**
     * Check whether to update physics or not in EMGflappy
     * @return true if physics need to be updated and false if only EMGflappy needs to be rendered
     */
    public static boolean updatePhysics() {
        return ScreenManager.game != null && ScreenManager.game.getScreen() == null;
    }

}

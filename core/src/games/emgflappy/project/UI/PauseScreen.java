package games.emgflappy.project.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


/**
 * Screen shown after the game is paused
 */
public class PauseScreen extends AbstractScreen {

    /**
     * Constructor
     */
    public PauseScreen() {
        super();
    }

    /**
     * Show PauseScreen (renders menu buttons and sets input focus)
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table(skin);
        table.setFillParent(true);
        table.setBackground("dialogDim");

        TextButton resumeButton = new TextButton("Resume", skin, "pauseButton");
        resumeButton.setColor(Color.BLUE);
        TextButton optionsButton = new TextButton("Options", skin, "pauseButton");
        optionsButton.setColor(Color.BLUE);
        TextButton quitButton = new TextButton("Quit", skin, "pauseButton");
        quitButton.setColor(Color.BLUE);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.quitGame();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.showOptionsScreen();
            }
        });

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.resumeGame();
            }
        });

        table.add(resumeButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);
        table.row();
        table.add(optionsButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);
        table.row();
        table.add(quitButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight);

        stage.addActor(table);
    }

    /**
     * Render PauseScreen with transparent background
     * @param delta Time elapsed since last render cycle
     */
    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        stage.act(delta);
        stage.draw();
    }
}

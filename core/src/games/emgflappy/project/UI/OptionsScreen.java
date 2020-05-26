package games.emgflappy.project.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import games.emgflappy.project.utils.DifficultySetting;
import games.emgflappy.project.utils.Options;

/**
 * Screen for showing an options menu
 */
public class OptionsScreen extends AbstractScreen {


    /**
     * Constructor
     */
    public OptionsScreen() {
        super();
    }

    /**
     * Show Options menu screen
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table(skin);
        table.setBackground("dialogDim");
        table.setFillParent(true);

        Label title = new Label("Options", skin, "bigLabel");
        Label volumeLabel = new Label("Volume", skin, "normalLabel");
        Label soundLabel = new Label("Sound effects", skin);
        Label minLabel = new Label("Min", skin);
        Label maxLabel = new Label("Max", skin);
        Label graphicsLabel = new Label("Advanced", skin, "normalLabel");
        Label viewPortLabel = new Label("Viewports", skin);
        Label fixedLabel = new Label("Fixed", skin);
        Label freeLabel = new Label("Free", skin);
        Label generalLabel = new Label("General", skin, "normalLabel");
        Label difficultyLabel = new Label("Difficulty", skin);
        Label easyLabel = new Label("Easy", skin);
        Label hardLabel = new Label("Hard", skin);

        final Slider volumeSlider = new Slider(Options.getInstance().MinVolume, Options.getInstance().MaxVolume, 0.1f, false, skin);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Options.getInstance().setVolume(volumeSlider.getValue());
            }
        });
        volumeSlider.setValue(Options.getInstance().getVolume());
        Container<Slider> volumeSliderContainer = createContainer(volumeSlider, 2.f);

        final Slider viewportSlider = new Slider(0.f, 1.f, 1.f, false, skin);
        viewportSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Options.getInstance().FixViewPortSizes = viewportSlider.getValue() == 1.f;
                ScreenManager.resizeGame();
            }
        });
        viewportSlider.setValue(Options.getInstance().FixViewPortSizes ? 1.f : 0.f);
        Container<Slider> viewPortSliderContainer = createContainer(viewportSlider, 2.f);

        final Slider difficultySlider = new Slider(DifficultySetting.getEasiestDifficulty(), DifficultySetting.getHardestDifficulty(), DifficultySetting.getStepSize(), false, skin);
        difficultySlider.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 Options.getInstance().Difficulty.setDifficulty((int) difficultySlider.getValue());
             }
         });
        difficultySlider.setValue(Options.getInstance().Difficulty.getDifficulty());
        Container < Slider > difficultySliderContainer = createContainer(difficultySlider, 2.f);

        Table volumeTable = new Table(skin);
        volumeTable.add(minLabel).padRight(5.f);
        volumeTable.add(volumeSliderContainer).width(volumeSliderContainer.getWidth()).height(volumeSliderContainer.getHeight());
        volumeTable.add(maxLabel).padLeft(5.f);

        Table viewportTable = new Table(skin);
        viewportTable.add(freeLabel).padRight(5.f);
        viewportTable.add(viewPortSliderContainer).width(viewPortSliderContainer.getWidth()).height(viewPortSliderContainer.getHeight());
        viewportTable.add(fixedLabel).padLeft(5.f);

        Table difficultyTable = new Table(skin);
        difficultyTable.add(easyLabel).padRight(5.f);
        difficultyTable.add(difficultySliderContainer).width(difficultySliderContainer.getWidth()).height(difficultySliderContainer.getHeight());
        difficultyTable.add(hardLabel).padLeft(5.f);

        // Buttons
        TextButton resumeButton = new TextButton("Resume", skin, "pauseButton");
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.resumeGame();
            }
        });
        resumeButton.setColor(Color.BLUE);

        TextButton quitButton = new TextButton("Quit", skin, "pauseButton");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ScreenManager.quitGame();
            }
        });
        quitButton.setColor(Color.BLUE);

        table.setWidth(2.f * AbstractScreen.ButtonWidth);
        table.add(title).center().padLeft(AbstractScreen.ButtonWidth);
        table.row().padTop(100.f);
        table.add(volumeLabel);
        table.add(generalLabel);
        table.row().padTop(50.f);
        table.add(soundLabel);
        table.add(difficultyLabel);
        table.row();
        table.add(volumeTable);
        table.add(difficultyTable);
        table.row().padTop(100.f);
        table.add(graphicsLabel);
        table.row().padTop(50.f);
        table.add(viewPortLabel);
        table.row();
        table.add(viewportTable);
        table.row().padTop(100.f);
        table.add(resumeButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight).padTop(200.f);
        table.add(quitButton).size(AbstractScreen.ButtonWidth, AbstractScreen.ButtonHeight).padTop(200.f);
        table.pack();
        stage.addActor(table);
    }

    /**
     * Render options menu
     * @param delta Time elapsed since previous render cycle (in seconds)
     */
    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Create new Container for a Slider which scales the slider
     * @param slider Slider instance
     * @param scale Specifies how the Slider is scaled
     * @return Container for the slider which is
     */
    private Container<Slider> createContainer(Slider slider, float scale) {
        Container<Slider> container = new Container<>(slider);
        container.setTransform(true);
        container.setSize(slider.getWidth(), slider.getHeight());
        container.setOrigin(slider.getWidth() / 2.f, slider.getHeight() / 2.f);
        container.setScale(scale);
        container.setOrigin(slider.getWidth(), slider.getHeight());
        container.setSize(scale * slider.getWidth(), scale * slider.getHeight());
        return container;
    }
}

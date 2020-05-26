package games.emgflappy.project.EMGflappy.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Singleton class containing all options for EMGflappy
 */
public final class Options implements Json.Serializable {

    private static Options options;
    private static String OptionsFile = "EMGflappy_options.json";

    public final float MaxVolume = 1.f; // Max volume for libGDX audio playback
    public final float MinVolume = 0.f;  // Min volume for libGDX audio playback
    public boolean FixViewPortSizes = false; // Whether to fix viewport size also in x dimension: this will make all the objects have equal sizes on every device (physics work nicer) but it also tends to look rougher
    public DifficultySetting Difficulty = DifficultySetting.Easy; // Difficulty of the game

    private float Volume = 0.1f; // Current audio playback volume

    /**
     * Singleton constructor
     */
    private Options() {}

    /**
     * Write Options to json
     * @param json Where options are written to
     */
    @Override
    public void write(Json json) {
        json.writeValue("Volume", Volume);
        json.writeValue("FixViewPortSize", FixViewPortSizes);
        json.writeValue("Difficulty", Difficulty.getDifficulty());
    }

    /**
     * Read options from json
     * @param json Handle to the json
     * @param jsonData Contains Options in json format
     */
    @Override
    public void read(Json json, JsonValue jsonData) {
        Volume = jsonData.getFloat("Volume", 0.5f);
        FixViewPortSizes = jsonData.getBoolean("FixViewPortSize", false);
        Difficulty.setDifficulty(jsonData.getInt("Difficulty", DifficultySetting.Easy.getDifficulty()));
    }

    /**
     * Get Singleton instance
     * @return Instance of Options class
     */
    public static Options getInstance() {
        if (options == null) options = new Options();
        return options;
    }

    /**
     * Get current volume level
     * @return Volume
     */
    public float getVolume() {
        return Volume;
    }

    /**
     * Set volume level
     * @param volume New value for Volume
     */
    public void setVolume(float volume) {
        if (volume >= MinVolume && volume <= MaxVolume) {
            Volume = volume;
        }
    }

    /**
     * Read Options when the game is started
     */
    public void readOptions() {
        try {
            FileHandle file = Gdx.files.local(Options.OptionsFile);
            String fileOptions = file.readString();
            Json json = new Json();
            options = json.fromJson(Options.class, fileOptions);
        } catch (Exception e) {
            Gdx.app.log(Options.class.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Write Options to a file when the game is finished
     */
    public void writeOptions() {
        try {
            FileHandle file = Gdx.files.local(Options.OptionsFile);
            Json json = new Json();
            String content = json.toJson(this, Options.class);
            file.writeString(content, false);
        } catch (Exception e) {
            Gdx.app.log(Options.class.getSimpleName(), e.getMessage());
        }

    }

}

package games.emgflappy.project.audio;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import games.emgflappy.project.utils.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * Static class which handles audio playback in EMGflappy
 */
public final class FlappyAudio {

    private static final String TAG = FlappyAudio.class.getSimpleName();
    private static Map<AudioType, Sound> audioSounds;

    /**
     * Load audio files to Sounds
     * Call this once when EMGflappy is launched
     */
    public static void loadAudioFiles() {
        audioSounds = new HashMap<>();
        for (AudioType type : AudioType.values()) {
            audioSounds.put(type, Gdx.audio.newSound(Gdx.files.internal(AudioType.getAudioPath(type))));
        }
    }

    /**
     * Dispose audio Sounds
     * Call this once before EMGflappy is destroyed
     */
    public static void disposeAudio() {
        for (Sound sound : audioSounds.values()) {
            sound.dispose();
        }
    }

    /**
     * Play sound
     * @param audioType Specifies which audio is played
     */
    public static void playSound(AudioType audioType) {
        try {
            audioSounds.get(audioType).play(Options.getInstance().getVolume());
        } catch (NullPointerException e) {
            Gdx.app.log(TAG, e.getMessage());
        }
    }

    /**
     * Play looping audio or stop looping audio
     * @param audioType Specifies which audio is looped or stopped
     * @param looping true to start looping an audio track or false to stop all audio playbacks of
     *                the audio track
     */
    public static void playLooping(AudioType audioType, boolean looping) {
        try {
            if (looping) audioSounds.get(audioType).loop(Options.getInstance().getVolume());
            else audioSounds.get(audioType).stop();
        } catch (NullPointerException e) {
            Gdx.app.log(TAG, e.getMessage());
        }
    }
}

package games.emgflappy.project.audio;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for different audio files
 */
public enum AudioType {

    ThunderAudio, // Audio used for LightningObjects
    WingFlappingAudio; // Audio used for Flappy

    private static final Map<AudioType, String> audioPaths = createAudioPaths();


    /**
     * Create a Map between AudioType enum constants and matching audio file paths on internal
     * storage
     * @return Map between AudioType and String file path
     */
    private static Map<AudioType, String> createAudioPaths() {
        Map<AudioType, String> map = new HashMap<>();
        map.put(ThunderAudio, "audio/thunder.wav");
        map.put(WingFlappingAudio, "audio/wing_flapping.wav");
        return map;
    }

    /**
     * Get path to an audio file
     * @param audioType Constant AudioType enum value
     * @return Path on internal storage
     */
    public static String getAudioPath(AudioType audioType) { return audioPaths.get(audioType); }
}

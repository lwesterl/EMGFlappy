package games.emgflappy.project.EMGflappy;

import java.util.HashMap;
import java.util.Map;

/**
 * EMGflappy textures
 */
public enum  FlappyTextures {

    FlappyFrame1, // Flappy texture frame 1
    FlappyFrame2, // Flappy texture frame 2
    FlappyFrame3, // Flappy texture frame 3
    FlappyFrame4, // Flappy texture frame 4
    BackGround, // Background texture
    Lightning, // Lightning texture, used for Tunnel FlappyObstacles
    NormalObstacle, // Texture used for Normal FlappyObstacles
    NormalObstacleFlipped, // Texture used for Normal FlappyObstacles, flipped vertically
    FeatherAnimation, // Texture used for Flappy feather animation
    HPBarWhite, // Texture for white HP bar, @see HUD
    HPBarGreen, // Texture for green HP bar, @see HUD
    HPBarRed, // Texture for red HP bar, @see HUD
    FlappyForward; // Flappy viewed from forward angle, @see HUD

    public static final int FeatherAnimationSpriteSheetColumns = 2; // This must match amount of columns in the spritesheet
    public static final int FeatherAnimationSpriteSheetRows = 3;  // This must mach amount of rows in the spritesheet

    private static final Map<FlappyTextures, String> texturesPathMap = createTexturePathMap();

    /**
     * Create Map between enums and texture paths (these paths must be valid)
     * @return Map between texture enums and their paths
     */
    private static Map<FlappyTextures, String> createTexturePathMap() {
        Map<FlappyTextures, String> map = new HashMap<>();
        // Texture paths need to be relative to the assets folder
        map.put(FlappyFrame1, "flappy_textures/flappy_frame1.png");
        map.put(FlappyFrame2, "flappy_textures/flappy_frame2.png");
        map.put(FlappyFrame3, "flappy_textures/flappy_frame3.png");
        map.put(FlappyFrame4, "flappy_textures/flappy_frame4.png");
        map.put(BackGround, "flappy_textures/night_view.png");
        map.put(Lightning, "flappy_textures/lightning.png");
        map.put(NormalObstacle, "flappy_textures/obstacle.png");
        map.put(NormalObstacleFlipped, "flappy_textures/obstacle_flipped.png");
        map.put(FeatherAnimation, "flappy_textures/flappy_feather_spritesheet.png");
        map.put(HPBarWhite, "flappy_textures/HP_white.png");
        map.put(HPBarGreen, "flappy_textures/HP_green.png");
        map.put(HPBarRed, "flappy_textures/HP_red.png");
        map.put(FlappyForward, "flappy_textures/flappy_forward.png");
        return map;
    }


    /**
     * Getter for Map between flappy texture enums and texture paths
     * @return texturesPathMap which can be used for loading textures by libGDX
     */
    public static final Map<FlappyTextures, String> getTexturesPathMap () { return texturesPathMap; }

}

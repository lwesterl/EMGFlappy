package games.emgflappy.project.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for different types of obstacles used in EMGflappy
 */
public enum ObstacleType {
    Normal,
    Tunnel;

    public static float TunnelObstacleHeight = 0.8f; // Height of FlappyObstacles of type Tunnel
    private static float ObstacleWidth = 0.1f; // High obstacle width in relation to ViewPortSizeX
    private static float TunnelObstacleWidth = 0.7f; // Tunnel obstacle width in relation to ViewPortSizeX

    private final static Map<ObstacleType, Float> obstacleWidthScales = createObstacleWidthScales();

    /**
     * Create Map between ObstacleType and width scales
     * @return Map where obstacle width scales are stored
     */
    private static Map<ObstacleType, Float> createObstacleWidthScales() {
        Map<ObstacleType, Float> map = new HashMap<>();
        map.put(ObstacleType.Normal, ObstacleWidth);
        map.put(ObstacleType.Tunnel, TunnelObstacleWidth);
        return map;
    }

    /**
     * Get width scale for specific ObstacleType
     * @param type Specifies obstacle
     * @return Width scale which should be multiplied with EMGflappy.ViewPortSizeX to get object width
     */
    public static float getObstacleWidthScale(ObstacleType type) {
        try {
            return obstacleWidthScales.get(type);
        } catch (NullPointerException e) {
            return 0.f;
        }
    }
}

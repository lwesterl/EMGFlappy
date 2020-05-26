package games.emgflappy.project.utils;

/**
 * Class which maps Flappy's y position and obstacle y positions (basically a 3D vector)
 */
public class PathMap {

    private float flappyY;
    private float lowerObstacleY;
    private float upperObstacleY;

    /**
     * Constructor for PathMap
     * @param flappyY Scaled Flappy's y coordinate (center coordinate, in the range of [0.f, 1.f])
     * @param lowerObstacleY Scaled y coordinate of the lower obstacle (in the range of [0.f, 1.f])
     * @param upperObstacleY Scaled y coordinate of the upper obstacle (in the range of [0.f, 1.f])
     */
    public PathMap(float flappyY, float lowerObstacleY, float upperObstacleY) {
        this.flappyY = flappyY;
        this.lowerObstacleY = lowerObstacleY;
        this.upperObstacleY = upperObstacleY;
    }

    /**
     * Get Flappy y coordinate
     * @return flappyY
     */
    public float getFlappyY() {
        return flappyY;
    }

    /**
     * Get lower obstacle scaled y coordinate
     * @return lowerObstacleY
     */
    public float getLowerObstacleY() {
        return lowerObstacleY;
    }

    /**
     * Get upper obstacle scale y coordinate
     * @return upperObstacleY
     */
    public float getUpperObstacleY() {
        return upperObstacleY;
    }
}

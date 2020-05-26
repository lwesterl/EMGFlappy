package games.emgflappy.project.utils;

/**
 * Enum which holds game difficulty setting (mapping between enum values, ints and floats)
 */
public enum DifficultySetting {

    Easy(1), // Easy difficulty
    Medium(2), // Medium difficulty
    Hard(3); // Hard difficulty

    private int difficulty;

    /**
     * Step size between difficulty value int constants (needed in OptionsScreen)
     * @return Enum constant step size
     */
    public static int getStepSize() {
        return 1;
    }

    /**
     * Get int constant matching the easiest difficulty
     * @return Easy.difficulty
     */
    public static int getEasiestDifficulty() {
        return 1;
    }

    /**
     * Get int constant matching the hardest difficulty
     * @return Hard.difficulty
     */
    public static int getHardestDifficulty() {
        return 3;
    }

    /**
     * Constructor (private)
     * @param difficulty int difficulty constant
     */
    DifficultySetting(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Get difficulty as an int
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Set difficulty, this basically changes the enum value
     * @param difficulty New difficulty
     */
    public void setDifficulty(int difficulty) {
        if (difficulty < Easy.difficulty) this.difficulty = Easy.difficulty;
        else if (difficulty > Hard.difficulty) this.difficulty = Hard.difficulty;
        this.difficulty = difficulty;
    }

    /**
     * Get gravity scale that matches DifficultySetting
     * @return Gravity scale used in y dimension in FlappyWorld
     */
    public float getGravityScale() {
        return (float) difficulty;
    }

}

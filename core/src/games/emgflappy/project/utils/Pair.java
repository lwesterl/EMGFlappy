package games.emgflappy.project.utils;

/**
 * Pair class which holds two Objects
 * @param <T1> Object of type T1
 * @param <T2> Object of type T2
 */
public class Pair<T1, T2> {

    private T1 first;
    private T2 second;

    /**
     * Template constructor for Pair
     * @param first The first Object
     * @param second The second Object
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get first element
     * @return first
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Get second element
     * @return second
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Update contents of the Pair
     * @param first New first element
     * @param second New second element
     */
    public void update(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}

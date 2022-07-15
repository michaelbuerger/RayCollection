package main.math;

public class MathUtils {
    /** 
     * Inclusively clamps integer within range, undefined if low > high
     * @param x number to be clamped
     * @param low lowest possible return value
     * @param high highest possible return value
     * @return x between [low, high]
     */
    public static int clampInt(int x, int low, int high) {
        if(x < low) return low;
        if(x > high) return high;
        return x;
    }

    /** 
     * Inclusively clamps double within range
     * @param x number to be clamped
     * @param low lowest possible return value
     * @param high highest possible return value
     * @return x between [low, high]
     */
    public static double clampDouble(double x, double low, double high) {
        if(x < low) return low;
        if(x > high) return high;
        return x;
    }

    /**
     * Averages array of vectors
     * @param vecs array of vectors
     * @return average of vectors
     */
    public static Vec3 avgVecs(Vec3[] vecs) {
        Vec3 sum = new Vec3(0);
        int vecCount = vecs.length;

        // get sum
        for(int i=0; i < vecCount; i++) {
            sum = sum.add(vecs[i]);
        }

        // average
        return sum.mult(1.0 / vecCount);
    }
}

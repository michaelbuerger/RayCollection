public class Utils {
    /**
     * Encodes an integer with RGBA color data
     * @param rgb rgb color channels, these get clamped between 0 and 255
     * @param a alpha color channel, clamped between 0 and 255
     * @return encoded integer
     */
    public static int encodeColor(Vec3 rgb, int a) {
        // all 8 bits (greatest to least significance by shifting respectively and ORing into one num)
        // a --> r --> g --> b
        // b31-24 b23-16 b15-8 b7-0
        return (clampInt((int)a, 0, 255)<<24) | clampInt((int)rgb.x, 0, 255)<<16 | clampInt((int)rgb.y, 0, 255)<<8 | clampInt((int)rgb.z, 0, 255);
    }

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
}

package main.engine;

import main.math.Vec3;
import main.math.MathUtils;

public class Utils {
    /** 
     * Converts any number of seconds to shorthand format
     * @param seconds number of seconds
     * @return minutes and seconds formatted as such: "#m#s"
     */
    public static String secondsToShorthand(Integer seconds) {
        if(seconds < 0)
            return "0s";
        if(seconds < 60)
            return seconds.toString() + "s";

        int sec = seconds % 60;
        int min = (int) Math.floor(seconds / 60.0);

        return min + "m" + sec + "s";
    }

    /**
     * Figures out a reasonable number of threads for render process at runtime 
     * @return power of 2 that is less than number of available processors
     */
    public static int getNumReasonableThreads() {
        int available =  Runtime.getRuntime().availableProcessors();

        int numThreads = 1;

        while(numThreads < available) // find 2^n number that is less than available num of processors
            numThreads *= 2;

        return numThreads;
    }

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
        return (MathUtils.clampInt((int)a, 0, 255)<<24) | MathUtils.clampInt((int)rgb.x, 0, 255)<<16 | MathUtils.clampInt((int)rgb.y, 0, 255)<<8 | MathUtils.clampInt((int)rgb.z, 0, 255);
    }

    /**
     * Combines integers in a reproducible way to use as a key for hashing
     * @param x first integer
     * @param y second integer
     * @return "xy"
     */ 
    public static String keyFromTwoIntegers(Integer x, Integer y) {
        return x.toString() + y.toString();
    }

    /**
     * Color function XY1
     * @param brightness brightness of pixel
     * @param x x-coord of pixel
     * @param y y-coord of pixel
     * @param DIM resolution size
     * @return encoded color based on brightness and pixel coords, to make things interesting
     */ 
    public static int colorFunctionXY1(double brightness, int x, int y, int DIM) {
        int r, g, b;
        r = (int)((double)x/(DIM-1)*255);
        g = (int)((double)y/(DIM-1)*255);
        b = (int)((((double)x+y)/2)/(DIM-1)*255);
        return encodeColor(new Vec3(r, g, b).mult(brightness), 255);
    }
}

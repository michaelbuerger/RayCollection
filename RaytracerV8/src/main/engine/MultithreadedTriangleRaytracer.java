package main.engine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.math.Triangle;
import main.math.Vec3;

public class MultithreadedTriangleRaytracer {
    public static final int NUM_THREADS = 4; // please make this number power of 2, greater than 16 doesn't seem to make a difference
    public static final boolean FORCE_THREAD_COUNT = false;

    private static ArrayList<Triangle> triangles;

    // global variables
    private static int resolution;
    private static Vec3 sensorPosition, sensorDirection;
    private static double sensorWidth, outrageousRange;
    private static int backgroundColor; // encoded
    private static double wiggle;
    private static Vec3 lightPosition;
    private static double luminosity;

    private static BufferedImage img; // output image
    private static int numThreads; // actual num of threads to be used

    // setup, return num of threads to be used
    public static int setup(ArrayList<Triangle> tris, int res, Vec3 sensPos, Vec3 sensDir, double sensWidth, double outrageousDist, int bgColor, double wigglePer, Vec3 lightPos, double lumin) {
        triangles = tris;

        resolution = res;
        sensorPosition = sensPos;
        sensorDirection = sensDir;
        sensorWidth = sensWidth;
        outrageousRange = outrageousDist;
        backgroundColor = bgColor;
        wiggle = wigglePer;
        lightPosition = lightPos;
        luminosity = lumin;

        img = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);

        numThreads = FORCE_THREAD_COUNT ? NUM_THREADS : Utils.getNumReasonableThreads();
        return numThreads;
    }

    public static BufferedImage run() {
        // example resolution = 32 --> image is 32x32 pixels
        // divide resolution by NUM_THREADS (e.g. 4)
        // 32 / 4 = 8
        // create 4 threads where they process 8 whole horizontal rows (8*32 = 256/1024 pixels each) = numRows
        // so, minX = 0, maxX = resolution - 1
        // minY = i * numRows, maxY = ((i+1) * numRows) - 1 (0-7, 8-15, etc)

        int numRows = resolution / numThreads;

        System.out.println("Starting " + numThreads + " threads...");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i=0; i < numThreads; i++) {
            PixelProcessor processor = new PixelProcessor(0, resolution - 1, i * numRows, ((i+1) * numRows) - 1);

            executorService.execute(processor);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS); // await all threads to finish, terminates after 24 hours
        } catch (InterruptedException e) {
            throw new RuntimeException("A thread was interrupted, check for errors...");
        }
        System.out.println("Finished all threads!");

        return img;
    }

    private static class PixelProcessor implements Runnable {
        private final int minX;
        private final int maxX;
        private final int minY;
        private final int maxY;

        public PixelProcessor(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        public void run()
        {
            try {
                for(int x=minX; x <= maxX; x++) {
                    for(int y=minY; y <= maxY; y++) {
                        img.setRGB(x, resolution - (y + 1),
                                raycastPointClosestHit(x, y));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in thread: " + e);
            }
        }

        private int raycastPointClosestHit(int x, int y) {
            ColorDepthPoint closestHitData = new ColorDepthPoint(backgroundColor, outrageousRange);
            for(Triangle tri : triangles) {
                ColorDepthPoint cdp =
                        renderPointSpecTriangle(tri, x, y, sensorPosition,
                                                        sensorWidth, sensorDirection,
                                                        resolution, backgroundColor,
                                wiggle, lightPosition, luminosity, outrageousRange);

                if(cdp.depth < closestHitData.depth)
                    closestHitData = cdp;
            }

            return closestHitData.color;
        }
    }

    /* 
     * Raycasts one single triangle at one single pixel point and returns color + depth info 
     */
    private static ColorDepthPoint renderPointSpecTriangle(Triangle tri, int x, int y, Vec3 sensorPosition, double sensorWidth, Vec3 sensorDirection, int resolution, int backgroundColor, double wiggle, Vec3 lightPosition, double luminosity, double outrageousRange) {
        // x and y go from 1 --> resolution e.g. 1-32
        Vec3 mappedSensorPos = sensorPosition.add(new Vec3(sensorWidth / 2, sensorWidth / 2, 0.0).negative());
        mappedSensorPos = mappedSensorPos.add(new Vec3(((double) x / resolution) * sensorWidth, ((double) y / resolution) * sensorWidth, 0.0));

        HitResult hitResult = Collision.raycastTriangle(tri, mappedSensorPos, sensorDirection, wiggle);
        // dog 1.1-1.12 wiggle is good

        if (hitResult.hit) { // did hit, assign color + depth
            double brightness = calcBrightnessPoint(hitResult, lightPosition, luminosity);
            brightness = Math.max(brightness, 0.17); // ambient/minimum light, shouldn't be hardcoded

            brightness = Math.min(1.0, brightness); // clip brightness at 1.0

            return new ColorDepthPoint(Utils.colorFunctionXY1(brightness, x, y, resolution), hitResult.distance);
        } else { // didn't hit, just assign outrageous depth and background color
            return new ColorDepthPoint(backgroundColor, outrageousRange);
        }
    }

    // lighting
    private static double calcBrightnessDirectional(HitResult hitResult, Vec3 lightDir) {
        return Math.abs(Math.pow(lightDir.normalized().dot(hitResult.hitNormal.normalized().negative()), 1));
    }

    // lighting
    private static double calcBrightnessPoint(HitResult hitResult, Vec3 lightPosition, double luminosity) {
        double brightnessRaw = luminosity/(4*Math.PI*Math.pow(hitResult.distance, 2));

        double angleFactor = calcBrightnessDirectional(hitResult, hitResult.hitPos.add(lightPosition.negative()));

        return angleFactor * brightnessRaw;
    }
}

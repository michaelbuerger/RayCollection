import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultithreadedTriangleRaycaster {
    public static final int NUM_THREADS = 4; // please make this number power of 2, greater than 16 doesn't seem to make a difference
    public static final boolean FORCE_THREAD_COUNT = false;

    private static ArrayList<Triangle> triangles;

    // glob variables
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

    public static int Setup(ArrayList<Triangle> tris, int res, Vec3 sensPos, Vec3 sensDir, double sensWidth, double outrageousDist, int bgColor, double wigglePer, Vec3 lightPos, double lumin) {
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

        numThreads = FORCE_THREAD_COUNT ? NUM_THREADS : Engine.GetNumReasonableThreads();
        return numThreads;
    }

    public static BufferedImage Run() {
        // example resolution = 32 --> image is 32x32 pixels
        // divide resolution by NUM_THREADS (e.g. 4)
        // 32 / 4 = 8
        // create 4 threads where they process 8 whole horizontal rows (8*32 = 256/1024 pixels each) = numRows
        // so, minX = 0, maxX = resolution - 1
        // minY = i * numRows, maxY = ((i+1) * numRows) - 1 (0-7, 8-15, etc)

        int numRows = resolution / numThreads;

        System.out.println("Starting " + numThreads + " threads...");
        ExecutorService es = Executors.newCachedThreadPool();

        for(int i=0; i < numThreads; i++) {
            PixelProcessor processor = new PixelProcessor(0, resolution - 1, i * numRows, ((i+1) * numRows) - 1);

            es.execute(processor);
        }
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.DAYS); // await all threads to finish, terminates after 24 hours
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
            //System.out.println(minX + "-" + maxX + ", " + minY + "-" + maxY);
            try {
                for(int x=minX; x <= maxX; x++) {
                    for(int y=minY; y <= maxY; y++) {
                        img.setRGB(x, resolution - (y + 1),
                                RaycastPointClosestHit(x, y));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in thread: " + e);
            }
        }

        private int RaycastPointClosestHit(int x, int y) {
            ColorDepthPoint closestHitData = new ColorDepthPoint(backgroundColor, outrageousRange);
            for(Triangle tri : triangles) {
                ColorDepthPoint cdp =
                        Engine.RaycastPointSpecTriangle(tri, x, y, sensorPosition,
                                                        sensorWidth, sensorDirection,
                                                        resolution, backgroundColor,
                                wiggle, lightPosition, luminosity, outrageousRange);

                if(cdp.depth < closestHitData.depth)
                    closestHitData = cdp;
            }

            return closestHitData.color;
        }
    }
}
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultithreadedTriangleRaycaster {
    // NUM_THREADS is used iff FORCE_THREAD_COUNT = true
    public static final int NUM_THREADS = 4; // please make this number power of 2, using more threads than logical processors will probably not make a difference
    public static final boolean FORCE_THREAD_COUNT = false;

    private static ArrayList<Triangle> triangles;

    private static int resolution;
    private static Vec3 sensorPosition, sensorDirection;
    private static double sensorWidth, outrageousRange;
    private static int backgroundColor; // encoded
    private static double wiggle;
    private static Vec3 lightPosition;
    private static double luminosity;
    private static double shininess;
    private static double diffuseMultiplier;
    private static double specularMultiplier;

    private static BufferedImage img; // output image
    private static int numThreads; // actual num of threads to be used

    /* Setup members, return num of threads that will be used */
    public static int Setup(ArrayList<Triangle> tris, int res, Vec3 sensPos, Vec3 sensDir, double sensWidth, double outrageousDist, int bgColor, double wigglePer,
                            Vec3 lightPos, double lumin, double shininess, double diffuseMult, double specularMult) {
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
        MultithreadedTriangleRaycaster.shininess = shininess;
        diffuseMultiplier = diffuseMult;
        specularMultiplier = specularMult;

        img = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);

        // use forced number of threads (if enabled) OR a reasonable power of 2 for the given machine
        numThreads = FORCE_THREAD_COUNT ? NUM_THREADS : Engine.GetNumReasonableThreads();
        return numThreads;
    }

    /* Dispatches PixelProcessor(s) to render the image square-by-square */
    public static BufferedImage Run() {
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

    /* Unit responsible for the processing/rendering of one single square of pixels (and corresponding outgoing rays) */
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
                                RaycastPointClosestHit(x, y)); // this returns an RGB value stored as an int (ARGB - 1 channel per byte)
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
                                                        sensorWidth, sensorDirection, resolution, backgroundColor, wiggle, 
                                                        sensorDirection, lightPosition, luminosity, shininess, diffuseMultiplier, specularMultiplier, outrageousRange);

                if(cdp.depth < closestHitData.depth)
                    closestHitData = cdp;
            }

            return closestHitData.color;
        }
    }
}
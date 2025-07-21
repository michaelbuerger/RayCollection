public class Engine {
    public static double ambientLight = 0.17;

    /* Generates hashable key from two integers */
    public static String KeyFromTwoIntegers(Integer x, Integer y) {
        return x.toString() + y.toString();
    }

    /* Returns short format of time duration rep. with minutes and seconds */
    public static String SecondsToShortTimeString(Integer seconds) {
        if(seconds < 60)
            return seconds.toString() + "s";

        int sec = seconds % 60;
        int min = (int) Math.floor(seconds / 60.0);

        return min + "m" + sec + "s";
    }

    /* Calculates triangle area using Heron's formula */
    public static double TriangleArea(Triangle triangle) {
        // side lengths
        double a = triangle.v1.Distance(triangle.v2);
        double b = triangle.v2.Distance(triangle.v3);
        double c = triangle.v3.Distance(triangle.v1);

        // semi-perimeter
        double s = (a + b + c) / 2;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    /* Encode integer with ARGB color data, expects 0-255, doesn't filter */
    public static int EncodeColor(Vec3 rgb) {
        // all 8 bits (greatest to least significance by shifting respectively and ORing into one num)
        // a --> r --> g --> b (a is hardcoded to 255)
        // b31-24 b23-16 b15-8 b7-0
        return (255<<24) | ((int)rgb.x<<16) | ((int)rgb.y<<8) | (int)rgb.z;
    }

    /* Figure out a reasonable number of threads for render process at runtime */
    public static int GetNumReasonableThreads() {
        int available =  Runtime.getRuntime().availableProcessors();

        int numThreads = 1;

        while(numThreads < available) // find 2^n number that is less than available num of processors
            numThreads *= 2;

        return numThreads;
    }

    /* Raycasts one single triangle at one single pixel point and returns color + depth info */
    public static ColorDepthPoint RaycastPointSpecTriangle(Triangle tri, int x, int y, Vec3 sensorPosition, double sensorWidth, Vec3 sensorDirection, int resolution, int backgroundColor, double wiggle, 
                                                           Vec3 viewDirection, Vec3 lightPosition, double luminosity, double shininess, double diffuseMultiplier, double specularMultiplier, double outrageousRange) {
        // x and y go from 1 --> resolution e.g. 1-32
        Vec3 mappedSensorPos = sensorPosition.Add(new Vec3(sensorWidth / 2, sensorWidth / 2, 0.0).Negative());
        mappedSensorPos = mappedSensorPos.Add(new Vec3(((double) x / resolution) * sensorWidth, ((double) y / resolution) * sensorWidth, 0.0));

        HitResult hitResult = Engine.RaycastTriangle(tri, mappedSensorPos, sensorDirection, wiggle);
        // dog 1.1-1.12 wiggle is good

        if (hitResult.hit) { // did hit, assign color + depth
            double brightness = Engine.CalcBrightnessOfPoint(hitResult, viewDirection, lightPosition, luminosity, shininess, diffuseMultiplier, specularMultiplier);
            brightness = Math.max(brightness, ambientLight); // ambient/minimum light

            brightness = Math.min(1.0, brightness); // clip brightness at 1.0

            return new ColorDepthPoint(Engine.colorFunctionXY1(brightness, x, y, resolution), hitResult.distance);
        } else { // didn't hit, just assign outrageous depth and background color
            return new ColorDepthPoint(backgroundColor, outrageousRange);
        }
    }

    /* Raycast and checks if ray hit a specific triangle */
    public static HitResult RaycastTriangle(Triangle triangle, Vec3 linePoint, Vec3 lineVec, double wiggle) {
        // check if hit should even be possible
        if(!ValidHitPossible(triangle.normal, lineVec))
            return new HitResult(false, null, null, 0);

        // calculate hit point on plane parallel to triangle
        Vec3 hitPoint = CalcPlaneLineIntersect(triangle.v1, triangle.normal, linePoint, lineVec);

        // check if this hit point is roughly within the specific triangle
        if(!PointWithinTriangle(triangle, hitPoint, wiggle)) {
            // point is not roughly within the triangle
            return new HitResult(false, null, null, 0);
        }

        // point is roughly within the triangle
        return new HitResult(true, hitPoint, triangle.normal, hitPoint.Distance(linePoint));
    }

    /* Checks if hit is even possible, meaning inverse surface normal and ray direction are less than orthogonal*/
    public static boolean ValidHitPossible(Vec3 planeNormal, Vec3 lineVec) {
        return planeNormal.Dot(lineVec.Negative()) > 0;
    }

    /* ============================================================================================================================== */

    /* Calculates intersection point between plane and line. Can result in infinite/NaN if invalid hits are not filtered. */
    private static Vec3 CalcPlaneLineIntersect(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineVec) {
        Vec3 c, n, x0, v, w;
        c = planePoint;
        n = planeNormal.Normalized();
        x0 = linePoint;
        v = lineVec.Normalized();
        w = c.Add(x0.Negative()); // w goes from x0 to c

        return x0.Add(v.Mult(w.Dot(n) / v.Dot(n)));
    }

    /* Returns whether or not point is within triangle, set wiggle=1 to allow no room for error */
    private static boolean PointWithinTriangle(Triangle triangle, Vec3 point, double wiggle) {
        double triArea0 = TriangleArea(triangle);

        // substitute point for triangle points, if at any point total is greater than
        // triangle's original area (+ wiggle), then the point is effectively outside
        double triAreaSum = TriangleArea(new Triangle(point, triangle.v2, triangle.v3));
        if(triAreaSum > triArea0*wiggle)
            return false;

        triAreaSum += TriangleArea(new Triangle(triangle.v1, point, triangle.v3));
        if(triAreaSum > triArea0*wiggle)
            return false;

        triAreaSum += TriangleArea(new Triangle(triangle.v1, triangle.v2, point));
        if(triAreaSum > triArea0*wiggle)
            return false;

        // passed all tests, point is effectively within triangle
        return true;
    }

    /* Cheap substitution for an actual texture */
    private static int colorFunctionXY1(double brightness, int x, int y, int DIM) {
        int r, g, b;
        r = (int)((double)x/(DIM-1)*255);
        g = (int)((double)y/(DIM-1)*255);
        b = (int)((((double)x+y)/2)/(DIM-1)*255);
        return EncodeColor(new Vec3(r, g, b).Mult(brightness));
    }

    /* Calculates brightness of a point accounting for lighting */
    private static double CalcBrightnessOfPoint(HitResult hitResult, Vec3 viewDirection, Vec3 lightPosition, double luminosity, double shininess, double diffuseMultiplier, double specularMultiplier) {
        // brightness reversely proportional to distance --> proportional to intensity (since light spreads out over larger area)
        double attenuationFactor = luminosity/(4*Math.PI*Math.pow(hitResult.distance, 2));

        Vec3 lightDir = lightPosition.Add(hitResult.hitPos.Negative()).Normalized();
        Vec3 hitNormal = hitResult.hitNormal.Normalized();
        Vec3 viewDir = viewDirection.Normalized();

        double diffuse = Math.max(0.0, hitNormal.Dot(lightDir)) * diffuseMultiplier;

        Vec3 reflectDir = lightDir.Negative().ReflectedAcross(hitNormal);
        double specular = Math.pow(viewDir.Dot(reflectDir), shininess) * specularMultiplier;

        double ambient = 0.1;
        return Math.min(1.0, ambient + (diffuse + specular) * attenuationFactor);
    }

}

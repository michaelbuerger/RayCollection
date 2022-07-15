package main.math;

public class CollisionMath {
    
    /**
     * Checks if plane-ray intersect is even remotely possible
     * @param planeNormal normal vector of plane, doesn't need to be normalized
     * @param lineVec 
     * @return true if rayDirection can possibly hit plane from the front
     */
    public static boolean validPlaneRayIntersect(Vec3 planeNormal, Vec3 rayDirection) {
        return planeNormal.dot(rayDirection) < 0;
    }

    /**
     * Calculates intersection point between a plane and a ray (line with primary direction).
     * Can result in infinite/NaN if invalid hits are not filtered via ValidPlaneRayIntersect.
     * Filtering should be handled prior to the method, as there is extra runtime overhead 
     * to handle it within the function and then express an invalid case.
     * @param planePoint some point on the plane
     * @param planeNormal normal vector of plane, doesn't need to be normalized
     * @param rayPoint some point on the ray
     * @param rayDirection direction the ray points
     * @return coordinates of intersection
     */
    public static Vec3 calcPlaneRayIntersect(Vec3 planePoint, Vec3 planeNormal, Vec3 rayPoint, Vec3 rayDirection) {
        Vec3 c, n, x0, v, w;
        c = planePoint;
        n = planeNormal.normalized();
        x0 = rayPoint;
        v = rayDirection.normalized();
        w = c.add(x0.negative()); // w goes from x0 to c

        return x0.add(v.mult(w.dot(n) / v.dot(n)));
    }

    /**
     * @param triangle triangle, normal is not used/does not need to be defined
     * @param point some point to test
     * @param wiggle wiggle room for floating-point calculations, 1 == no wiggle room, 1.1 == 10% wiggle room
     * @return whether or not point is within triangle
     */
    public static boolean isPointWithinTriangle(Triangle triangle, Vec3 point, double wiggle) {
        double triArea0 = triangle.area();
        if(wiggle < 1) wiggle = 1;

        // substitute point for triangle points, if at any point total is greater than
        // triangle's original area (+ wiggle), then the point is effectively outside
        double triAreaSum = new Triangle(point, triangle.v2, triangle.v3).area();
        if(triAreaSum > triArea0*wiggle)
            return false;

        triAreaSum += new Triangle(triangle.v1, point, triangle.v3).area();
        if(triAreaSum > triArea0*wiggle)
            return false;

        triAreaSum += new Triangle(triangle.v1, triangle.v2, point).area();
        if(triAreaSum > triArea0*wiggle)
            return false;

        // passed all tests, point is effectively within triangle
        return true;
    }
}

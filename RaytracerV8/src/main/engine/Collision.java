package main.engine;

import main.math.Vec3;
import main.math.Triangle;
import main.math.CollisionMath;

public class Collision {

    /** 
     * Raycast and check if ray hits single triangle (outer face)
     * @param triangle triangle with defined normal
     * @param linePoint ray starting point
     * @param lineVec ray direction vector
     * @param wiggle wiggle factor for PointWithinTriangle call
     * @return HitResult
     */
    public static HitResult raycastTriangle(Triangle triangle, Vec3 linePoint, Vec3 lineVec, double wiggle) {
        // check if hit should even be possible
        if(!CollisionMath.validPlaneRayIntersect(triangle.normal, lineVec))
            return new HitResult(false, null, null, 0);

        // calculate hit point between plane parallel to triangle and ray's line
        Vec3 hitPoint = CollisionMath.calcPlaneRayIntersect(triangle.v1, triangle.normal, linePoint, lineVec);

        // check if this hit point is roughly within the specific triangle
        if(!CollisionMath.isPointWithinTriangle(triangle, hitPoint, wiggle)) {
            // point is not roughly within the triangle
            return new HitResult(false, null, null, 0);
        }

        // point is roughly within the triangle
        return new HitResult(true, hitPoint, triangle.normal, hitPoint.distance(linePoint));
    }
}

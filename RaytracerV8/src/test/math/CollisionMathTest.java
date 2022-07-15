package test.math;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import main.math.CollisionMath;
import main.math.Vec3;

public class CollisionMathTest {
    Vec3 a, b;
    
    @Test
    public void testValidPlaneRayIntersectReturnsTrue() {
        a = new Vec3(4, 4, 0);
        b = new Vec3(0, -4, 0);
        assertEquals(CollisionMath.validPlaneRayIntersect(a, b), true);
    }

    @Test
    public void testValidPlaneRayIntersectReturnsFalse() {
        a = new Vec3(4, 4, 0);
        b = new Vec3(2, 1, 0);
        assertEquals(CollisionMath.validPlaneRayIntersect(a, b), false);
    }

    /*
     * calcPlaneRayIntersect and isPointWithinTriangle are not directly tested
     * as their functionality is best tested in practice and they do work
     * issues with accuracy come from the fact that a wiggle factor is
     * required for the current method
     */

}

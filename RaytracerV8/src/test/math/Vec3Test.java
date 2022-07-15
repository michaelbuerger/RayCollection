package test.math;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import main.math.Vec3;

public class Vec3Test {
    private double x = 10, y = 5, z = 20;
    private double magnitude = Math.sqrt(x * x + y * y + z * z); // for use with normalize tests
    private double epsilon = 0.001; // for use with double comparisons

    private Vec3 vec;
    private Vec3 vecA = new Vec3(10, 10, 10), vecB = new Vec3(5, 5, 5);
    private Vec3 vecC = new Vec3(25, 7, 1);

    @Test
    public void testConstructorWithNoNormalize() {
        vec = new Vec3(x, y, z);
        assertEquals(vec.x, x, epsilon);
        assertEquals(vec.y, y, epsilon);
        assertEquals(vec.z, z, epsilon);
    }

    @Test
    public void testConstructorWithNormalizeTrue() {
        vec = new Vec3(x, y, z, true);
        assertEquals(vec.x, x / magnitude, epsilon);
        assertEquals(vec.y, y / magnitude, epsilon);
        assertEquals(vec.z, z / magnitude, epsilon);
    }

    @Test
    public void testConstructorWithNormalizeFalse() {
        vec = new Vec3(x, y, z, false);
        assertEquals(vec.x, x, epsilon);
        assertEquals(vec.y, y, epsilon);
        assertEquals(vec.z, z, epsilon);
    }

    @Test
    public void testConstructorWithSingleValue() {
        vec = new Vec3(x);
        assertEquals(vec.x, x, epsilon);
        assertEquals(vec.y, x, epsilon);
        assertEquals(vec.z, x, epsilon);
    }

    @Test
    public void testConstructorWithOtherVector() {
        vec = new Vec3(new Vec3(x, y, z));
        assertEquals(vec.x, x, epsilon);
        assertEquals(vec.y, y, epsilon);
        assertEquals(vec.z, z, epsilon);
    }

    @Test
    public void testAdd() {
        vec = vecA.add(vecB);
        assertEquals(vec.x, vecA.x + vecB.x, epsilon);
        assertEquals(vec.y, vecA.y + vecB.y, epsilon);
        assertEquals(vec.z, vecA.z + vecB.z, epsilon);
    }

    @Test
    public void testSub() {
        vec = vecA.sub(vecB);
        assertEquals(vec.x, vecA.x - vecB.x, epsilon);
        assertEquals(vec.y, vecA.y - vecB.y, epsilon);
        assertEquals(vec.z, vecA.z - vecB.z, epsilon);
    }

    @Test
    public void testDivByVec3() {
        vec = vecA.div(vecB);
        assertEquals(vec.x, vecA.x / vecB.x, epsilon);
        assertEquals(vec.y, vecA.y / vecB.y, epsilon);
        assertEquals(vec.z, vecA.z / vecB.z, epsilon);
    }

    @Test
    public void testDivByDouble() {
        vec = vecA.div(2);
        assertEquals(vec.x, vecA.x / 2, epsilon);
        assertEquals(vec.y, vecA.y / 2, epsilon);
        assertEquals(vec.z, vecA.z / 2, epsilon);
    }

    @Test
    public void testDistance() {
        double distance = Math.sqrt(Math.pow(vecA.x - vecB.x, 2) + Math.pow(vecA.y - vecB.y, 2) + Math.pow(vecA.z - vecB.z, 2));
        assertEquals(vecA.distance(vecB), distance, epsilon);
    }

    @Test
    public void testDot() {
        double dot = vecA.x * vecB.x + vecA.y * vecB.y + vecA.z * vecB.z;
        assertEquals(dot, vecA.dot(vecB), epsilon);
    }

    @Test
    public void testACrossB() {
        Vec3 actualCross = vecA.cross(vecB);
        Vec3 expectedCross = new Vec3(0, 0, 0);

        assertEquals(actualCross.x, expectedCross.x, epsilon);
        assertEquals(actualCross.y, expectedCross.y, epsilon);
        assertEquals(actualCross.z, expectedCross.z, epsilon);
    }

    @Test
    public void testACrossC() {
        Vec3 actualCross = vecA.cross(vecC);
        Vec3 expectedCross = new Vec3(-60, 240, -180);

        assertEquals(actualCross.x, expectedCross.x, epsilon);
        assertEquals(actualCross.y, expectedCross.y, epsilon);
        assertEquals(actualCross.z, expectedCross.z, epsilon);
    }

    @Test
    public void testCCrossB() {
        Vec3 actualCross = vecC.cross(vecB);
        Vec3 expectedCross = new Vec3(30, -120, 90);

        assertEquals(actualCross.x, expectedCross.x, epsilon);
        assertEquals(actualCross.y, expectedCross.y, epsilon);
        assertEquals(actualCross.z, expectedCross.z, epsilon);
    }

    @Test
    public void testMagnitude() {
        vec = new Vec3(x, y, z);
        assertEquals(vec.magnitude(), magnitude, epsilon);
    }

    @Test
    public void testMultVecByVec() {
        vec = new Vec3(x, y, z);
        vec = vec.mult(vec); // effectively square each component
        assertEquals(vec.x, x * x, epsilon);
        assertEquals(vec.y, y * y, epsilon);
        assertEquals(vec.z, z * z, epsilon);
    }

    @Test
    public void testMultVecByDouble() {
        vec = new Vec3(x, y, z);
        vec = vec.mult(2);
        assertEquals(vec.x, x * 2, epsilon);
        assertEquals(vec.y, y * 2, epsilon);
        assertEquals(vec.z, z * 2, epsilon);
    }

    @Test
    public void testNegative() {
        vec = new Vec3(x, y, z);
        vec = vec.negative();
        assertEquals(vec.x, -x, epsilon);
        assertEquals(vec.y, -y, epsilon);
        assertEquals(vec.z, -z, epsilon);
    }

    @Test
    public void testNormalize() {
        vec = new Vec3(x, y, z);
        vec.normalize();
        assertEquals(vec.x, x/magnitude, epsilon);
        assertEquals(vec.y, y/magnitude, epsilon);
        assertEquals(vec.z, z/magnitude, epsilon);
    }

    @Test
    public void testNormalized() {
        vec = new Vec3(x, y, z);
        vec = vec.normalized();
        assertEquals(vec.x, x/magnitude, epsilon);
        assertEquals(vec.y, y/magnitude, epsilon);
        assertEquals(vec.z, z/magnitude, epsilon);
    }

    @Test
    public void testToString() {
        vec = new Vec3(x, y, z);
        assertEquals(vec.toString(), "[" + x + ", " + y + ", " + z + "]");
    }
}

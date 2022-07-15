package test.math;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import main.math.Vec3;
import main.math.Triangle;

public class TriangleTest {
    private Vec3 v1 = new Vec3(10, 10, 10), v2 = new Vec3(15, 102, 11), v3 = new Vec3(1, 10, 50);
    private Vec3 normal = new Vec3(0, 0, 1);
    private double area = 1888.893;
    private Triangle triangle;
    private double epsilon = 0.001; // for use with double comparisons

    @Test public void testConstructorWithNormal() {
        triangle = new Triangle(v1, v2, v3, normal);
        assertEquals(triangle.v1, v1);
        assertEquals(triangle.v2, v2);
        assertEquals(triangle.v3, v3);
        assertEquals(triangle.normal, normal);
    }

    @Test public void testConstructorNoNormal() {
        triangle = new Triangle(v1, v2, v3);
        assertEquals(triangle.v1, v1);
        assertEquals(triangle.v2, v2);
        assertEquals(triangle.v3, v3);
        assertNotNull(triangle.normal);
    }

    @Test public void testAreaSelf() {
        triangle = new Triangle(v1, v2, v3);
        assertEquals(triangle.area(), area, epsilon);
    }

    @Test public void testAreaOther() {
        triangle = new Triangle(v1, v2, v3);
        assertEquals(Triangle.area(triangle), area, epsilon);
    }
}
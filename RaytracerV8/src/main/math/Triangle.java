package main.math;

public class Triangle {
    public Vec3 v1, v2, v3;
    public Vec3 normal;

    // Note: does not calculate normal, intended for simple point storage
    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.normal = new Vec3(0);
    }

    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 normal) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.normal = normal;
    }

    /**
     * Calculates triangle area using Heron's formula
     * @return triangle's area
     */
    public double area() {
        // side lengths
        double a = v1.distance(v2);
        double b = v2.distance(v3);
        double c = v3.distance(v1);

        // semi-perimeter
        double s = (a + b + c) / 2;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    /**
     * Calculates triangle area using Heron's formula 
     * @param triangle triangle
     * @return triangle's area
     */
    public static double area(Triangle triangle) {
        // side lengths
        double a = triangle.v1.distance(triangle.v2);
        double b = triangle.v2.distance(triangle.v3);
        double c = triangle.v3.distance(triangle.v1);

        // semi-perimeter
        double s = (a + b + c) / 2;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}

public class Collision {

    /**
     * Checks if ray hits sphere at least once
     * @param sphere
     * @param ray
     * @return hits > 0
     */
    public static boolean rayHitsSphere(Sphere sphere, Ray ray) {
        Vec3 oc = ray.origin.sub(sphere.center);
        double a = ray.direction.dot(ray.direction);
        double b = 2.0 * oc.dot(ray.direction);
        double c = oc.dot(oc) - sphere.radius * sphere.radius;
        double discriminant = b * b - 4 * a * c;

        return discriminant > 0; // using quadratic formula, check number of roots, should be at least 1 if hit
    }
}

public class Ray {
    public Vec3 origin, direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * @param t parameter
     * @return some point along line, positive-t is same direction as ray, negative-t is opposite
     */
    public Vec3 at(double t) {
        return origin.add(direction.mult(t));
    }
}

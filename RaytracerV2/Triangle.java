public class Triangle {
    public Vec3 v1, v2, v3;
    public Vec3 normal;

    // Note: does not calculate normal for you in this case
    // instead used for case where triangle just reps 3 points
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
}

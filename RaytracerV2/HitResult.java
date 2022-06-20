public class HitResult {
	public boolean hit; // if hit is valid or not

	public double distance; // distance from ray start point to hit
	public Vec3 hitPos; // actual position of intersection
	public Vec3 hitNormal; // normal of surface at point that was hit

	public HitResult(boolean hit, Vec3 hitPos, Vec3 hitNormal, double distance) {
		this.hit = hit;
		this.hitPos = hitPos;
		this.hitNormal = hitNormal;
		this.distance = distance;
	}
}
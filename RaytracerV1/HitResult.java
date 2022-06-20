public class HitResult {
	public double distance;
	public Vec3 hitPosition;
	public boolean hit;

	public HitResult(double distance, Vec3 hitPosition, boolean hit) {
		this.distance = distance;
		this.hitPosition = hitPosition;
		this.hit = hit;
	}
}
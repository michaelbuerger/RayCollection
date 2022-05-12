import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;

import static java.lang.System.currentTimeMillis;

public class Raycaster
{
	static final int DIM = 64;
	static final Vec3 SENSOR_NORMAL = new Vec3(0, 0, 1);

	static final Vec3 LIGHT_DIR = new Vec3(0.0, 0.0, -1, true);
	static final double HIT_POINT_VALID_RANGE = 100;

	static final double SENSOR_SQUARE_WIDTH = 7.0; // 12 good for dog, 7 good for frog
	static final Vec3 SENSOR_OFFSET = new Vec3(0.0, 0.0, 0.0); // 0, 2, 0 good for dog, 0, 0, 0 for frog

	static final int P_0 = (255<<24); // black

	public static void main(String[] args) throws IOException
	{
		BufferedImage image = null; // create buffered image object
		image = new BufferedImage(DIM, DIM, BufferedImage.TYPE_INT_ARGB);

		File file = null; // create file object;

        /* For getting specific time for filenames so no overwrites */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
        LocalDateTime now = LocalDateTime.now();  
        String nowStr = dtf.format(now);
        /*                  
		
		/* Read in triangles from model */
		ArrayList<Triangle> trianglesList = OBJLoader.LoadOBJIndexed("models/lil-frog.obj");
		Triangle[] triangles = trianglesList.toArray(new Triangle[trianglesList.size()]);

		/* Loop variables */
		Vec3 color, mappedSensorPos;
		HitResult hitResult;
		double brightness;
		int p;
		HashMap<String, Double> depthMap = new HashMap<String, Double>();

		for (int x = 0; x < DIM; x++)
			{
				for (int y = 0; y < DIM; y++)
				{
					image.setRGB(x, y, P_0);
					depthMap.put(DepthMapKey(x, y), HIT_POINT_VALID_RANGE + 1);
				}
			}

		int currentTriangle = 0;
		double deltaTriTime = 0;
		double sumDeltaTriTime = 0;
		double avgDeltaTriTime = 0;
		double lastTriFinish = 0;
		double currentTriStart = currentTimeMillis();
		double totalTimeEstimatedSeconds = 0;

		double startTime = currentTimeMillis();
		double elapsedTimeSeconds = 0;
		double totalTimeLeftSeconds = 0;

		// for each triangle
		for (Triangle tri : triangles) {
			// set values pixel by pixel, for each triangle

			currentTriangle++;

			lastTriFinish = currentTriStart;
			currentTriStart = currentTimeMillis();

			deltaTriTime = (currentTriStart - lastTriFinish);
			sumDeltaTriTime += deltaTriTime;
			avgDeltaTriTime = sumDeltaTriTime / currentTriangle;
			totalTimeEstimatedSeconds = (avgDeltaTriTime * triangles.length) / 1000;

			elapsedTimeSeconds = (currentTimeMillis() - startTime) / 1000;
			totalTimeLeftSeconds = Math.max(totalTimeEstimatedSeconds - elapsedTimeSeconds, 0);

			System.out.print(currentTriangle + "/" + triangles.length + " tris | ");

			if(totalTimeLeftSeconds <= 60)
				System.out.println((int)totalTimeLeftSeconds + " seconds left");
			if(totalTimeLeftSeconds >= 60) {
				System.out.println((int)Math.floor(totalTimeLeftSeconds / 60.0) + " minutes " + (int) totalTimeLeftSeconds % 60 + " seconds left");
			}

			for (int x = 0; x < DIM; x++)
			{
				for (int y = 0; y < DIM; y++)
				{
					Vec3 sensorPos = new Vec3(-SENSOR_SQUARE_WIDTH/2, -SENSOR_SQUARE_WIDTH/2, 10);
					mappedSensorPos = sensorPos.Add(new Vec3((double)(SENSOR_SQUARE_WIDTH)*(x+1)/(DIM), (double)(SENSOR_SQUARE_WIDTH)*(y+1)/(DIM), 0));
					mappedSensorPos = mappedSensorPos.Add(SENSOR_OFFSET);

					hitResult = RaycastTriangle(tri, mappedSensorPos, SENSOR_NORMAL, 1.11);
					// dog 1.1-1.12 wiggle is good

					String depthMapKey = DepthMapKey(x, DIM - (y + 1));

					if(hitResult.hit) {
						// make sure a closer hit hasn't taken place
						if(depthMap.get(depthMapKey) > hitResult.distance) {
							brightness = CalcBrightnessPoint(hitResult, new Vec3(0, 0, 6), 500); // -2, 4, -5, 750 good for dog
							brightness += 0.1; // ambient

							brightness = Math.min(1.0, brightness);

							color = colorFunctionXY1(brightness, x, y);

							// all 8 bits (greatest to least significance by shifting respectively and ORing into one num)
							// a --> r --> g --> b (a is hardcoded to 255)
							// b31-24 b23-16 b15-8 b7-0 
							p = (255<<24) | ((int)color.x<<16) | ((int)color.y<<8) | (int)color.z;

							image.setRGB(x, DIM - (y + 1), p);
							depthMap.put(depthMapKey, hitResult.distance);
						}
					}
				}
			}
		}

		// write image to file
		try
		{
			file = new File("./out/" + nowStr + ".png");
			ImageIO.write(image, "png", file);
		}
		catch(IOException e)
		{
			System.out.println("Error: " + e);
		}

		if(elapsedTimeSeconds <= 60)
			System.out.println("Render took " + (int)elapsedTimeSeconds + " seconds");
		if(elapsedTimeSeconds >= 60) {
			System.out.println("Render took " + (int)Math.floor(elapsedTimeSeconds / 60.0) + " minutes " + (int) totalTimeLeftSeconds % 60 + " seconds");
		}
	}

	private static Vec3 colorFunctionXY1(double brightness, int x, int y) {
		int r, g, b;
		r = (int)((double)x/(DIM-1)*255);
		g = (int)((double)y/(DIM-1)*255);
		b = (int)((((double)x+y)/2)/(DIM-1)*255);
		return new Vec3(r, g, b).Mult(brightness);
	}

	private static double CalcBrightnessDirectional(HitResult hitResult, Vec3 lightDir) {
		return Math.pow(lightDir.Normalized().Dot(hitResult.hitNormal.Normalized().Negative()), 2);
	}

	private static double CalcBrightnessPoint(HitResult hitResult, Vec3 lightPosition, double luminosity) {
		double brightnessRaw = luminosity/(4*Math.PI*Math.pow(hitResult.distance, 2));

		double angleFactor = CalcBrightnessDirectional(hitResult, hitResult.hitPos.Add(lightPosition.Negative()));

		return angleFactor * brightnessRaw;
	}

	private static String DepthMapKey(Integer x, Integer y) {
		return x.toString() + y.toString();
	}

	// raycasts triangle, returns HitResult depicting what happened
	private static HitResult RaycastTriangle(Triangle triangle, Vec3 linePoint, Vec3 lineVec, double wiggle) {
		// General process:
		// 1. check point of intersection on infinite plane with arbitrary point = triangle vertex 1 and the same normal
		// 2. check if this point is within triangle (via arbitrary triangle area comparisons) within some wiggle room
		//   center == to triangle
		// 2.x if it is not roughly within the triangle return false hit result
		// 3 if it is roughly within the triangle, return valid hit result

		if(!ValidHitPossible(triangle.normal, lineVec)) {
			return new HitResult(false, null, null, 0);
		}
		// 1
		Vec3 hitPoint = CalcPlaneLineIntersect(triangle.v1, triangle.normal, linePoint, lineVec);
		
		// 2
		if(!PointWithinTriangle(triangle, hitPoint, wiggle)) {
			// 2.x
			return new HitResult(false, null, null, 0);
		}

		// 3
		return new HitResult(true, hitPoint, triangle.normal, hitPoint.Distance(linePoint));		
	}

	private static boolean ValidPoint(Vec3 vec) {
		return ValidNum(vec.x) && ValidNum(vec.y) && ValidNum(vec.z);
	}

	private static boolean ValidNum(double num) {
		return num >= -HIT_POINT_VALID_RANGE && num <= HIT_POINT_VALID_RANGE;
	}

	private static boolean ValidHitPossible(Vec3 planeNormal, Vec3 lineVec) {
		return planeNormal.Dot(lineVec.Negative()) > 0;
	}
	private static Vec3 CalcPlaneLineIntersect(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineVec) {
		Vec3 c, n, x0, v, w;
		c = planePoint;
		n = planeNormal.Normalized();
		x0 = linePoint;
		v = lineVec.Normalized();
		w = c.Add(x0.Negative()); // w goes from x0 to c

		return x0.Add(v.Mult(w.Dot(n) / v.Dot(n)));
	}

	// returns whether or not point is within triangle, set wiggle=1 to allow no room for error
	private static boolean PointWithinTriangle(Triangle triangle, Vec3 point, double wiggle) {
		double triArea0 = TriangleArea(triangle);

		// substitute point for triangle points, if at any point total is greater than
		// triangle's original area (+ wiggle), then the point is effectively outside
		double triAreaSum = TriangleArea(new Triangle(point, triangle.v2, triangle.v3));
		if(triAreaSum > triArea0*wiggle)
			return false;
		
		triAreaSum += TriangleArea(new Triangle(triangle.v1, point, triangle.v3));
		if(triAreaSum > triArea0*wiggle)
			return false;

		triAreaSum += TriangleArea(new Triangle(triangle.v1, triangle.v2, point));
		if(triAreaSum > triArea0*wiggle)
			return false;

		// passed all tests, point is effectively within triangle
		return true;
	}

	// Calculates triangle area using Heron's formula
	private static double TriangleArea(Triangle triangle) {
		// side lengths
		double a = triangle.v1.Distance(triangle.v2);
		double b = triangle.v2.Distance(triangle.v3);
		double c = triangle.v3.Distance(triangle.v1);

		// semi-perimeter
		double s = (a + b + c) / 2;

		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}
}
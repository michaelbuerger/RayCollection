import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

public class Raycaster
{
	static final int DIM = 256;
	static final Vec3 SENSOR_NORMAL = new Vec3(0, 0, 1);
	static final Vec3 SPHERE_POS = new Vec3(0.5, 0.5, 0.5);
	static final double SPHERE_RADIUS = 0.5;
	static final double STEP_LENGTH = 0.0001;
	static final Vec3 LIGHT_DIR = new Vec3(0.0, 0.5, 1, true);

	public static void main(String args[]) throws IOException
	{
		// effectively defines distance from sensors to farthest bound of "camera" view, should fully contain visible portions of objects
		// will also determine brightness of pixels as normalization is based on this number (0 brightness == dist. eq. of MAX_DISTANCE)
		final double MAX_DISTANCE = 1.0;

		BufferedImage image = null; // create buffered image object
		image = new BufferedImage(DIM, DIM, BufferedImage.TYPE_INT_ARGB);

		File file = null; // create file object;

        /* For getting specific time for filenames so no overwrites */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
        LocalDateTime now = LocalDateTime.now();  
        String nowStr = dtf.format(now);
        /*                                                          */

		/* Loop variables */
		Vec3 color, mappedSensorPos;
		HitResult hitResult;
		double distance, brightness;
		int p;

        // set values pixel by pixel
		for (int x = 0; x < DIM; x++)
		{
			for (int y = 0; y < DIM; y++)
			{
				mappedSensorPos = new Vec3((double)x/(DIM-1), (double)y/(DIM-1), 0);

				hitResult = castSphere(mappedSensorPos, SENSOR_NORMAL, SPHERE_POS, SPHERE_RADIUS, STEP_LENGTH, MAX_DISTANCE);
				Vec3 sphereNorm = sphereNormal(SPHERE_POS, hitResult.hitPosition);

				brightness = 0;
				if(hitResult.hit) {
					brightness = Math.pow((MAX_DISTANCE - hitResult.distance)/MAX_DISTANCE, 3);
					double brightness2 = sphereNorm.Dot(LIGHT_DIR);
					if(brightness2 < 0)
						brightness = -brightness2; // opposing normal and light vector = more negative dot = brighter
					else
						brightness = 0;
				}

				brightness = Math.pow(brightness, 1);

				color = colorFunction(brightness, x, y);

				// all 8 bits (greatest to least significance by shifting respectively and ORing into one num)
                // a --> r --> g --> b (a is hardcoded to 255)
                // b31-24 b23-16 b15-8 b7-0 
				p = (255<<24) | ((int)color.x<<16) | ((int)color.y<<8) | (int)color.z;

				image.setRGB(x, y, p);
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
	}

	/* THIS ASSUMES THE NORMAL VECTOR IS NORMALIZED */
	private static HitResult castSphere(Vec3 sensorPos, Vec3 sensorNormal, Vec3 spherePos, double sphereRadius, double stepLength, double maxDistance) {
		double distance = 0.0;
		Vec3 walkingSensorPos = sensorPos;
		HitResult hitResult = new HitResult(0, new Vec3(0), false);

		while(distance <= maxDistance) {
			if(walkingSensorPos.Distance(spherePos) <= sphereRadius) {
				hitResult.distance = distance;
				hitResult.hitPosition = walkingSensorPos;
				hitResult.hit = true;
				return hitResult;
			}

			distance += stepLength;
			walkingSensorPos = walkingSensorPos.Add(sensorNormal.Mult(stepLength));
		}

		hitResult.hit = false;

		return hitResult;
	}
	
	private static Vec3 sphereNormal(Vec3 spherePos, Vec3 hitPosition) {
		// return normalized vector from: spherePos to hitPosition
		Vec3 diff = hitPosition.Add(spherePos.Negative());

		return diff.Normalized();
	}

	private static Vec3 colorFunction(double brightness, int x, int y) {
		return colorFunctionXY1(brightness, x, y);
	}

	private static Vec3 colorFunctionGray(double brightness) {
		return new Vec3(brightness*255);
	}

	private static Vec3 colorFunctionRed(double brightness) {
		return new Vec3(brightness*255, 0, 0);
	}

	private static Vec3 colorFunctionGreen(double brightness) {
		return new Vec3(0, brightness*255, 0);
	}

	private static Vec3 colorFunctionBlue(double brightness) {
		return new Vec3(0, 0, brightness*255);
	}

	private static Vec3 colorFunctionStatic(double brightness) {
		int c = (int)(brightness*Math.random()*255);
		return new Vec3(c);
	}

	private static Vec3 colorFunctionRand(double brightness) {
		// note, if alt. constructor is used (one that takes in one double), psuedorandom values will not be generated 3 times
		// this will lead to a TV static effect (random grayscale) as opposed to random colors
		return new Vec3((brightness*Math.random()*255), (brightness*Math.random()*255), (brightness*Math.random()*255));
	}

	private static Vec3 colorFunctionXY1(double brightness, int x, int y) {
		int r, g, b;
		r = (int)((double)x/(DIM-1)*255);
		g = (int)((double)y/(DIM-1)*255);
		b = (int)((((double)x+y)/2)/(DIM-1)*255);
		return new Vec3(r, g, b).Mult(brightness);
	}
}
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDateTime;

import static java.lang.System.currentTimeMillis;

public class Raytracer
{

	/* Variables just to mess with */
	static final Vec3 SENSOR_DIRECTION = new Vec3(0, 0, 1);
	static final double SENSOR_WIDTH = 2.0;
	static final Vec3 SENSOR_POSITION = new Vec3(0.0, 0.0, -5.0);
	static final Vec3 BACKGROUND_COLOR = new Vec3(255, 128, 0);
	static final double WIGGLE = 1.0001; // dog = 1.12, frog = 1.1, sphere = 1.0001
	static final Vec3 LIGHT_POSITION = SENSOR_POSITION.Add(new Vec3(-2.0, 1.0, 0.0));
	static final double LUMINOSITY = 250;

	/* Data that will be logged */
	static final String MODEL_FILE_PATH = "models/smoothsphere.obj";
	static final int RESOLUTION = 128;

	/* etc */
	static final double HYPOTHETICALLY_OUTRAGEOUS_RANGE = 100;
	static final int P_0 = Engine.EncodeColor(BACKGROUND_COLOR);

	public static void main(String[] args) throws IOException
	{
		File file = null; // create file object;

        /* For getting specific time for filenames so no overwrites */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
        LocalDateTime now = LocalDateTime.now();  
        String nowStr = dtf.format(now);
		
		/* Read in triangles from model */
		ArrayList<Triangle> trianglesList = OBJLoader.LoadOBJIndexed(MODEL_FILE_PATH);

		/* For tracking render time */
		double startTime = currentTimeMillis();

		/* Render */
		int numThreads = MultithreadedTriangleRaycaster.Setup(trianglesList, RESOLUTION, SENSOR_POSITION, SENSOR_DIRECTION,
											SENSOR_WIDTH, HYPOTHETICALLY_OUTRAGEOUS_RANGE, P_0, WIGGLE, LIGHT_POSITION, LUMINOSITY);
		BufferedImage image = MultithreadedTriangleRaycaster.Run();

		double endTime = currentTimeMillis();
		int timeElapsed = (int) (endTime - startTime) / 1000;

		// write image to file
		try
		{
			file = new File("./out/img/" + nowStr + "-" + Engine.SecondsToShortTimeString(timeElapsed) + ".png");
			ImageIO.write(image, "png", file);
		} catch(IOException e)
		{
			System.out.println("Error: " + e);
		}

		// write log file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("./out/log.txt", true));

			bw.write("Render '" + MODEL_FILE_PATH + "' "
					+ RESOLUTION + "x" + RESOLUTION + " "
					+ Engine.SecondsToShortTimeString(timeElapsed) + " "
					+ numThreads + " threads\n");
			bw.close();
		} catch(IOException e)
		{
			System.out.println("Error: " + e);
		}
	}
}
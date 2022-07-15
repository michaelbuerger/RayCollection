import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ImageWriter {
    /**
     * Writes image to png file
     * @param image image data as BufferedImage
     * @param filepath filepath excluding extension
     */
    public static void WriteBufferedImageToPng(BufferedImage image, String filepath) {
        try
        {
            File file = new File(filepath + ".png");
            ImageIO.write(image, "png", file);
        } catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
}

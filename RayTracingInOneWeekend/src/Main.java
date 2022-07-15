import java.awt.image.BufferedImage;

public class Main
{

    public static void main(String[] args) {
        BufferedImage image = render();

        ImageWriter.WriteBufferedImageToPng(image, "./out/image");
    }

    private static BufferedImage render() {
        // Image
        double aspectRatio = 16.0 / 9.0;
        int imageWidth = 400;
        int imageHeight = (int) (imageWidth / aspectRatio);
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Camera
        double viewportHeight = 2.0, viewportWidth = aspectRatio * viewportHeight;
        double focalLength = 1.0;

        Vec3 origin = new Vec3(0, 0, 0);
        Vec3 horizontal = new Vec3(viewportWidth, 0, 0);
        Vec3 vertical = new Vec3(0, viewportHeight, 0);
        Vec3 lowerLeftCorner = origin.sub(horizontal.div(2)).sub(vertical.div(2)).sub(new Vec3(0, 0, focalLength));

        // Render
        for(int j = imageHeight - 1; j >= 0; j--) { // vertical bottom -> top
            for(int i = 0; i < imageWidth; i++) { // horizontal left -> right
                double u = (double) i / (imageWidth - 1); // left -> right from 0.0-1.0
                double v = (double) j / (imageHeight - 1); // bottom -> top from 0.0-1.0

                // create ray starting at origin pointing to "pixel" on viewport
                Ray ray = new Ray(origin, lowerLeftCorner.add(horizontal.mult(u)).add(vertical.mult(v)).sub(origin));
                int color = colorFromRay(ray);

                // flip vertical again
                image.setRGB(i, imageHeight - (j + 1), color);
            }
        }

        return image;
    }

    // ray-wise render logic
    private static int colorFromRay(Ray ray) {
        Sphere sphere = new Sphere(new Vec3(0, 0, -1), 0.5);
        if(Collision.rayHitsSphere(sphere, ray)) // check for collision with hard-coded sphere
            return Utils.encodeColor(new Vec3(255, 0, 0), 255);

        Vec3 unitDirection = ray.direction.normalized();
        double t = 0.5 * (unitDirection.y + 1.0);
        Vec3 color = new Vec3(1.0, 1.0, 1.0).mult(1.0 - t).add(new Vec3(0.5, 0.7, 1.0).mult(t));
        color = color.mult(255);

        return Utils.encodeColor(color, 255);
    }

}
package core.loading;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageFormatHelper {

    public static BufferedImage improveFormat(BufferedImage image) {
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        var graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        return newImage;
    }

    public static BufferedImage newImage(int width, int height) {
        var env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var device = env.getDefaultScreenDevice();
        var config = device.getDefaultConfiguration();

        return config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public static BufferedImage keyOut(BufferedImage image, Color color) {
        for(var x = 0; x < image.getWidth(); x++) {
            for(var y = 0; y < image.getHeight(); y++) {
                if(image.getRGB(x, y) == color.getRGB()) {
                    image.setRGB(x, y, Color.TRANSLUCENT);
                }
            }
        }
        return image;
    }

}

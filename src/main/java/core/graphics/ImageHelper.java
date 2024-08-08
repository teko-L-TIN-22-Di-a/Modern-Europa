package core.graphics;

import core.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class ImageHelper {

    public static BufferedImage drawNoise(BufferedImage image) {
        var bytesPerPixel = 4L;

        IntStream.range(0, image.getHeight()).parallel().forEach(y -> {
            var pixelData = new SplittableRandom()
                    .ints(bytesPerPixel * image.getWidth(), 0, 256)
                    .toArray();
            image.getRaster().setPixels(0, y, image.getWidth(), 1, pixelData);
        });

        return image;
    }

    public static BufferedImage drawWhiteNoise(BufferedImage image, int lowerBounds, int upperBounds) {
        var bytesPerPixel = 4L;

        IntStream.range(0, image.getHeight()).parallel().forEach(y -> {
            var random = new Random();
            int currBrightness;

            var pixelData = new int[(int) (bytesPerPixel * image.getWidth())];
            for(var x = 0; x < image.getWidth(); x++) {
                currBrightness = random.nextInt(lowerBounds, upperBounds);

                var i = x*4;

                pixelData[i] = currBrightness;
                pixelData[i+1] = currBrightness;
                pixelData[i+2] = currBrightness;
                pixelData[i+3] = 255;
            }

            image.getRaster().setPixels(0, y, image.getWidth(), 1, pixelData);
        });

        return image;
    }

    public static BufferedImage improveFormat(BufferedImage image) {
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        var graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        return newImage;
    }

    public static BufferedImage newImage(Vector2f size) {
        return newImage((int) size.x(), (int) size.y());
    }

    public static BufferedImage newImage(int width, int height) {
        var env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var device = env.getDefaultScreenDevice();
        var config = device.getDefaultConfiguration();

        return config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public static BufferedImage keyOut(BufferedImage image, Color color) {
        return keyOut(image, color, Color.TRANSLUCENT);
    }

    public static BufferedImage keyOut(BufferedImage image, Color color, int newColor) {

        for(var x = 0; x < image.getWidth(); x++) {
            for(var y = 0; y < image.getHeight(); y++) {
                if(image.getRGB(x, y) == color.getRGB()) {
                    image.setRGB(x, y, newColor);
                }
            }
        }
        return image;
    }

    public static BufferedImage cleanup(BufferedImage image) {
        return cleanup(image, Color.TRANSLUCENT);
    }

    public static BufferedImage cleanup(BufferedImage image, int cleanupColor) {
        var raster = image.getRaster();
        var bytesPerPixel = 4;
        var data = new int[image.getWidth() * image.getHeight() * bytesPerPixel];

        Arrays.fill(data, cleanupColor);
        raster.setPixels(0, 0, image.getWidth(), image.getHeight(), data);

        return image;
    }

    public static BufferedImage clone(BufferedImage image) {
        var cm = image.getColorModel();
        var isAlphaPremultiplied = cm.isAlphaPremultiplied();
        var raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}

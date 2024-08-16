package core.graphics;

import core.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

/**
 * Provides static functions for working with compatible images.
 */
public class ImageHelper {

    /**
     * Efficiently draws random noise.
     * @param image The image that will be modified.
     * @return The same image reference passed.
     */
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

    /**
     * Efficiently draws white noise with bounds to passed image.
     * @param image The image that will be modified.
     * @param lowerBounds Lower bounds used for random generated colors.
     * @param upperBounds Upper bounds used for random generated colors.
     * @return The same image reference passed.
     */
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

    /**
     * Generates a new compatible image using passed image.
     * @param image The image that will be used as base for the new image.
     * @return The new compatible image that was generated.
     */
    public static BufferedImage improveFormat(BufferedImage image) {
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        var graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        return newImage;
    }

    /**
     * Creates a new compatible image which can improve performance on this machine.
     * @param size Image size.
     * @return New compatible image.
     */
    public static BufferedImage newImage(Vector2f size) {
        return newImage((int) size.x(), (int) size.y());
    }

    /**
     * Creates a new compatible image which can improve performance on this machine.
     * @param width Image width.
     * @param height Image height.
     * @return New compatible image.
     */
    public static BufferedImage newImage(int width, int height) {
        var env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var device = env.getDefaultScreenDevice();
        var config = device.getDefaultConfiguration();

        return config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    /**
     * Calls keyOut with the newColor parameter set to Color.TRANSLUCENT.
     * @param image The image that will be modified.
     * @param color The color that will be made transparent.
     * @return The same image reference as the one passed.
     */
    public static BufferedImage keyOut(BufferedImage image, Color color) {
        return keyOut(image, color, Color.TRANSLUCENT);
    }

    /**
     * Sets all pixels with a certain color to another on the image passed.
     * @param image The image that will be modified.
     * @param color The target color that will be replaced by the newColor.
     * @param newColor The new color that will be drawn.
     * @return The same image reference as the one passed.
     */
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

    /**
     * Calls cleanup with cleanupColor Color.TRANSLUCENT.
     * @param image image that will be modified.
     * @return The same image as the one passed as parameter.
     */
    public static BufferedImage cleanup(BufferedImage image) {
        return cleanup(image, Color.TRANSLUCENT);
    }

    /**
     * Resets all pixels of image to cleanupColor
     * @param image This image will be modified
     * @param cleanupColor Color used for every pixel
     * @return The same image reference that was input will be returned for chaining.
     */
    public static BufferedImage cleanup(BufferedImage image, int cleanupColor) {
        var raster = image.getRaster();
        var bytesPerPixel = 4;
        var data = new int[image.getWidth() * image.getHeight() * bytesPerPixel];

        Arrays.fill(data, cleanupColor);
        raster.setPixels(0, 0, image.getWidth(), image.getHeight(), data);

        return image;
    }

    /**
     * Performs a deep clone on the image.
     * @param image Image to clone.
     * @return The new BufferedImage with the same data.
     */
    public static BufferedImage clone(BufferedImage image) {
        var cm = image.getColorModel();
        var isAlphaPremultiplied = cm.isAlphaPremultiplied();
        var raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}

package core;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        System.out.println("Start");

        uiTest();

        System.out.println("Done!");
    }

    public static void uiTest() {
        FlatLightLaf.setup();

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        var frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        var panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                var graphics = (Graphics2D) g;

                var image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
                var random = new Random();

                for(int y = 0; y < image.getHeight(); y++) {
                    for(int x = 0; x < image.getWidth(); x++) {
                        int color = random.nextInt(255);
                        image.setRGB(x, y, new Color(color, color, color).getRGB());
                    }
                }

                graphics.drawImage(image, 0, 0, null);

                var pos = frame.getMousePosition();
                if(pos != null) {
                    graphics.drawString("X: " + pos.x + ", Y: " + pos.y, 10, 10);
                }
                super.paint(g);
            }
        };
        frame.add(panel);
        panel.setOpaque(false);

        var btn = new JButton("Click Me");
        panel.add(btn);

        var btn2 = new JButton("Click Me");
        panel.add(btn2);
    }

}
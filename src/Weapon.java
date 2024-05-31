import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Weapon {
    private final double VELOCITY = 0.5;
    private BufferedImage laser; // make a separate attack/weapon class
    private BufferedImage missile;
    private double x;
    private double y;

    public Weapon(Player player, String attack, double x, double y) {
        try {
            laser = ImageIO.read(new File(attack));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void setY(double newY) {
        y = newY;
    }

    public BufferedImage getLaserImage() {
        return laser;
    }

    public void shoot() {
        while (y >= 0) {
            y -= VELOCITY;
            System.out.println("y = " + y);
        }
    }

    public Rectangle shootRect(int x, int y) {
        Rectangle rectangle = new Rectangle(x, y, laser.getWidth(), laser.getHeight());
        return rectangle;
    }
}
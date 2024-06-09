import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Asteroid {
    private BufferedImage asteroid;
    private double x;
    private double y;
    private int health;
    private String direction;

    public Asteroid(double x, double y) {
        try {
            asteroid = ImageIO.read(new File("assets/asteroid.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.x = x;
        this.y = y;
        health = 2;
        int randomDirection = (int) (Math.random() * 3) + 1;

        if (randomDirection == 1) {
            direction = "left";
        } else if (randomDirection == 2) {
            direction = "down";
        } else {
            direction = "right";
        }
    }

    public BufferedImage getAsteroidImage() {
        return asteroid;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void setX(double newX) {
        x = newX;
    }

    public void setY(double newY) {
        y = newY;
    }

    public String getDirection() {
        return direction;
    }

    public void leftDirection() {
        x -= 1;
        y += 1;
    }

    public void downDirection() {
        y += 1;
    }

    public void rightDirection() {
        x += 1;
        y += 1;
    }

    public Rectangle asteroidRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, asteroid.getWidth(), asteroid.getHeight());
        return rectangle;
    }
}

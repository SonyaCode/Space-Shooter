import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {
    private BufferedImage bullet;
    private double x;
    private double y;
    private boolean fromPlayer;


    public Bullet(String attack, double x, double y, boolean fromPlayer) { // add a from which player
        try {
            bullet = ImageIO.read(new File(attack));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        this.x = x;
        this.y = y;
        this.fromPlayer = fromPlayer;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void playerShoot() {
        y -= 1.5;
    }

    public void monsterShoot() {
        y += 1;
    }

    public BufferedImage getBulletImage() {
        return bullet;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    public Rectangle bulletRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, bullet.getWidth(), bullet.getHeight());
        return rectangle;
    }
}

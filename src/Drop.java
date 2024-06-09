import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drop {
    private BufferedImage drop;
    private String type;
    private double x;
    private double y;
    private int upgradeAmount;

    public Drop(Player player, Monster monster) {
        int random = (int) (Math.random() * 50) + 1;

        // if the monster is going to drop something, the drop rate for upgrade is 70%
        if (random <= 7) {
            try {
                drop = ImageIO.read(new File("assets/" + player.getColor() + "-upgrade.png"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            type = "upgrade";
        } else {
            // if the monster is going to drop something, the drop rate for upgrade is 30%
            try {
                drop = ImageIO.read(new File("assets/" + player.getColor() + "-shield-drop.png"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            type = "shield";
        }

        x = monster.getX();
        y = monster.getY();

        if (monster.getName().equals("Insect")) {
            upgradeAmount = 1;
        } else if (monster.getName().equals("Bug")) {
            upgradeAmount = 2;
        } else if (monster.getName().equals("Green Horn Monster")) {
            upgradeAmount = 3;
        } else {
            upgradeAmount = 4;
        }
    }

    public BufferedImage getDropImage() {
        return drop;
    }

    public String getType() {
        return type;
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

    public void fall() {
        y += 0.1;
    }
    public void dropUpgrade(Player player) {
        player.increaseDamage(upgradeAmount);
    }

    public Rectangle dropRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, drop.getWidth(), drop.getHeight());
        return rectangle;
    }




}

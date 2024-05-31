import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Player {
    private BufferedImage player;
    private final double MOVE_AMT = 0.5;
    private double x;
    private double y;
    private boolean isAlive;
    private int damage;
    private boolean shield;


    public Player(String spaceship) {
        try {
            player = ImageIO.read(new File(spaceship));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        x = 100;
        y = 550;
        isAlive = true;
        damage = 1;
        shield = false;
    }

    public BufferedImage getPlayerImage() {
        return player;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public boolean getHealth() {
        return isAlive;
    }

    public int getDamage() {
        return damage;
    }

    public void setxCoord(int newX) {
        x = newX;
    }

    public void setyCoord(int newY) {
        y = newY;
    }

    public void died() {
        isAlive = false;
    }

    public void moveUp() {
        if (y - MOVE_AMT >= 0) {
            y -= MOVE_AMT;
        }
    }

    public void moveDown() {
        if (y + MOVE_AMT <= 610) {
            y += MOVE_AMT;
        }
    }

    public void moveRight() {
        if (x + MOVE_AMT <= 1075) {
            x += MOVE_AMT;
        }
    }

    public void moveLeft() {
        if (x - MOVE_AMT >= 0) {
            x -= MOVE_AMT;
        }
    }


    public void upgradeDamage(int addDamage) {
        damage += addDamage;
    }

    public Rectangle playerRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, player.getWidth(), player.getHeight());
        return rectangle;
    }

}
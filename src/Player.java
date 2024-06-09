
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
    private BufferedImage shield;
    private String color;
    private final double MOVE_AMT = 1; // school desktop: 0.3
    private double x;
    private double y;
    private boolean isAlive;
    private int level;
    private int damage;
    private boolean hasShield;


    public Player(String spaceshipColor) {
        try {
            player = ImageIO.read(new File("assets/" + spaceshipColor + "-spaceship.png"));
            shield = ImageIO.read(new File("assets/shield.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.color = spaceshipColor;
        x = 100;
        y = 550;
        isAlive = true;
        level = 1;
        damage = 20;
        hasShield = false;
    }

    public BufferedImage getPlayerImage() {
        return player;
    }

    public BufferedImage getShieldImage() {
        return shield;
    }

    public String getColor() {
        return color;
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

    public boolean hasShield() {
        return hasShield;
    }

    public void setxCoord(int newX) {
        x = newX;
    }

    public void setyCoord(int newY) {
        y = newY;
    }

    public void died() {
        isAlive = false;
        hasShield = false;
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

    public void increaseDamage(int addDamage) {
        damage += addDamage;
    }

    public void addShield() {
        hasShield = true;
    }

    public void removeShield() {
        hasShield = false;
    }

    public Rectangle playerRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, player.getWidth(), player.getHeight());
        return rectangle;
    }

}
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
    private int x;
    private int y;
    private boolean isAlive;
    private int damage;


    public Player(String spaceship) {
        try {
            player = ImageIO.read(new File(spaceship));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        x = 10;
        y = 300;
        isAlive = true;
        damage = 1;
    }

    public BufferedImage getPlayerImage() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public void upgradeDamage(int addDamage) {
        damage += addDamage;
    }

}
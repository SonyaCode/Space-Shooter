import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Monster {
    private BufferedImage monster;
    private String enemyName;
    private double x;
    private double y;
    private int stopPosY;
    private double moveAmt;
    private int health;
    private boolean isAlive;

    public Monster(Player player) {
        // random spawn
        int randomPick = 0;
        if (player.getDamage() >= 10) {
            randomPick = (int) (Math.random() * 4) + 1;
        } else if (player.getDamage() >= 8) {
            randomPick = (int) (Math.random() * 3) + 1;
        } else {
            randomPick = (int) (Math.random() * 2) + 1;
        }

        if (player.getDamage() >= 20 && randomPick == 4) { // unlock when damage = 20
            try {
                monster = ImageIO.read(new File("assets/boss-monster.png"));
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            enemyName = "Boss";
            health = 50;

        } else if (player.getDamage() >= 8 && randomPick == 3) { // unlock when damage = 8
            try {
                monster = ImageIO.read(new File("assets/green-horn-monster.png"));
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            enemyName = "Green Horn Monster";
            health = 15;

        } else if (player.getDamage() >= 3 && randomPick == 2) { // unlock when damage = 3
            try {
                monster = ImageIO.read(new File("assets/bug-monster.png"));
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            enemyName = "Bug";
            health = 5;

        } else {
            try {
                monster = ImageIO.read(new File("assets/insect-monster.png"));
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            enemyName = "Insect";
            health = 1;
        }


        if (enemyName.equals("Green Horn Monster")) {
            moveAmt = Math.random() * 0.15 + 0.05; // randomize the speed of each green horn monster
            x = 0;
            y = (int) (Math.random() * 251); // spawn at random y-coordinate
        } else {
            moveAmt = 0.15;
            x = (int) (Math.random() * 951); // spawn at random x-coordinate
            y = 0;
        }

        if (enemyName.equals("Boss")) {
            stopPosY = (int) (Math.random() * 200);
        }

        isAlive = true;

    }

    public BufferedImage getMonsterImage() {
        return monster;
    }

    public String getName() {
        return enemyName;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealth(int subtract) {
        health -= subtract;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    public void moveDown() {
        y += moveAmt;
    }

    public void moveRight() {
        x += moveAmt;
    }

    public int getStopPosY() {
        return stopPosY;
    }

    public Rectangle monsterRect() {
        Rectangle rectangle = new Rectangle((int) x, (int) y, monster.getWidth(), monster.getHeight());
        return rectangle;
    }


}

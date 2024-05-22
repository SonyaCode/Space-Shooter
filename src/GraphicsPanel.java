import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel { // REMEMBER TO IMPORT THE OTHERS LATER
    private BufferedImage spaceBackground;
    private int numOfPlayers;
    private Player player1;
    private Player player2;
    private boolean[] pressedKeys;
    private Timer timer;
    private int time;
    private JButton pause;
    private boolean isPause;

    public GraphicsPanel(int numOfPlayers) {
        try {
            spaceBackground = ImageIO.read(new File("game-background.png"));
            player1 = new Player("blue-spaceship.png");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (numOfPlayers == 2) {
            player2 = new Player("red-spaceship.png");
        }

        this.numOfPlayers = numOfPlayers;

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(spaceBackground, 0, 0, null);
        g.drawImage(player1.getPlayerImage(), player1.getX(), player1.getY(), null);

        if (numOfPlayers == 2) {
            player2.setxCoord(player1.getX() + 100);
            g.drawImage(player2.getPlayerImage(), player2.getX(), player2.getY(), null);
        }
    }
}
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private BufferedImage spaceBackground;
    private int numOfPlayers;
    private Player player1;
    private Player player2;
    private ArrayList<Weapon> bullets;
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
            player2.setxCoord(player2.getX() + 900);
        }

        bullets = new ArrayList<Weapon>();

        this.numOfPlayers = numOfPlayers;
        pressedKeys = new boolean[128]; // 128 keys

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(spaceBackground, 0, 0, null);
        g.drawImage(player1.getPlayerImage(), player1.getX(), player1.getY(), null);

        if (numOfPlayers == 2) {
            g.drawImage(player2.getPlayerImage(), player2.getX(), player2.getY(), null);
        }

        g.drawRect((int) player1.getX(), (int) player1.getY(), player1.getPlayerImage().getWidth(), player1.getPlayerImage().getHeight() - 10);
        g.drawRect((int) player2.getX(), (int) player2.getY(), player2.getPlayerImage().getWidth(), player2.getPlayerImage().getHeight());

        // ----- player 1 movement -----
        // player 1 moves up [W]
        if (pressedKeys[87]) {
            player1.moveUp();
        }

        // player 1 moves left [A]
        if (pressedKeys[65]) {
            player1.moveLeft();
        }

        // player 1 moves down [S]
        if (pressedKeys[83]) {
            player1.moveDown();
        }

        // player 1 moves right [D]
        if (pressedKeys[68]) {
            player1.moveRight();
        }


        // ----- player 2 movement -----
        if (numOfPlayers == 2) {
            // player 2 moves up [up arrow]
            if (pressedKeys[38]) {
                player2.moveUp();
            }

            // player 2 moves left [left arrow]
            if (pressedKeys[37]) {
                player2.moveLeft();
            }

            // player 2 moves down [down arrow]
            if (pressedKeys[40]) {
                player2.moveDown();
            }

            // player 2 moves right [right arrow]
            if (pressedKeys[39]) {
                player2.moveRight();
            }

        }

        for (int i = 0; i < bullets.size(); i++) {
            Weapon bullet = bullets.get(i);
            if (bullet.getX() >= 0) {
                bullet.setY(bullet.getY() - 1);
            }
            g.drawImage(bullet.getLaserImage(), bullet.getX(), bullet.getY(), null);
        }
    }

    // KeyListener Interface Method
    public void keyTyped(KeyEvent e) { }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;

        // shoot the bullet
        if (key == KeyEvent.VK_E) {
            Weapon bullet = new Weapon(player1, "laser.png", player1.getX() + 57, player1.getY() - 40);
            bullets.add(bullet);
        }
    }

    // MouseListener Interface Methods (most likely don't need them)
    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { } // unimplemented

    public void mouseExited(MouseEvent e) { } // unimplemented

    // ACTIONLISTENER INTERFACE METHODS (buttons and timer)
    public void actionPerformed(ActionEvent e) { }
}
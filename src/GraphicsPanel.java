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
    private ArrayList<Bullet> bullets;
    private ArrayList<Monster> monsters;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Drop> drops;
    private boolean[] pressedKeys;
    private Timer timer;
    private int time;
    private int startTime;
    private int endTime;
    private JButton pause;
    private boolean isPause;
    private boolean coolDown;
    private int startCoolDown;
    private int endCoolDown;

    public GraphicsPanel(int numOfPlayers) {
        try {
            spaceBackground = ImageIO.read(new File("assets/game-background.png"));
            player1 = new Player("blue");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (numOfPlayers == 2) {
            player2 = new Player("red");
            player2.setxCoord(player2.getX() + 900);
        }

        bullets = new ArrayList<Bullet>();
        monsters = new ArrayList<Monster>();
        asteroids = new ArrayList<Asteroid>();
        drops = new ArrayList<Drop>();

        // timer
        time = 0;
        timer = new Timer(1000, this);
        timer.start();

        this.numOfPlayers = numOfPlayers;
        pressedKeys = new boolean[128]; // 128 keys

        coolDown = false;

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(spaceBackground, 0, 0, null);

        if (player1.getHealth()) {
            g.drawImage(player1.getPlayerImage(), player1.getX(), player1.getY(), null);
        }

        if (numOfPlayers == 2 && player2.getHealth()) {
            g.drawImage(player2.getPlayerImage(), player2.getX(), player2.getY(), null);
        }

        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.setColor(Color.WHITE);

        g.drawString("Time: " + time, 20, 40);
        timer.start();

        // if there is no monster and the timer is up, spawn 1 - 5 monsters
        if (monsters.size() == 0 && endTime == time) {
            int numOfMonsters = (int) (Math.random() * 5 ) + 1;

            for (int i = 0; i < numOfMonsters; i++) {
                Monster monster = new Monster(player1);
                monsters.add(monster);
            }

        }

        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            g.drawImage(monster.getMonsterImage(), monster.getX(), monster.getY(), null);

            // insect monsters moving down
            if (monster.getName().equals("Insect") && monster.getY() < 743) {
                monster.moveDown();
            } else if (monster.getName().equals("Insect") && monster.getY() >= 743) {
                monster.setY(0);
            }

            // bug monsters moving midway down
            if (monster.getName().equals("Bug") && monster.getY() < 200) {
                monster.moveDown();
            }

            // green horn monster moving horizontally
            if (monster.getName().equals("Green Horn Monster") && monster.getX() < 1214) {
                monster.moveRight();
            } else if (monster.getName().equals("Green Horn Monster") && monster.getX() >= 1214){
                monster.setX(0);
            }

            // boss monster moving forward and stop randomly between y = 0 and y = 200
            if (monster.getName().equals("Boss")) {
                if (monster.getY() < monster.getStopPosY()) {
                    monster.moveDown();
                }
            }


            // if the monster touches the player, the player died
            if (monster.monsterRect().intersects(player1.playerRect())) {
                breakShield(player1);
            }

            if (numOfPlayers == 2) {
                if (monster.monsterRect().intersects(player2.playerRect())) {
                    breakShield(player2);
                }
            }

            for (int j = 0; j < bullets.size() ; j++) {
                Bullet bullet = bullets.get(j);
                if (bullet.isFromPlayer() && monster.monsterRect().intersects(bullet.bulletRect())) {
                    monster.decreaseHealth(player1.getDamage());
                    if (monster.getHealth() <= 0) {
                        monsters.remove(i);
                        i--;

                        // drop rate for anything: 20%
                        int random = (int) (Math.random() * 5) + 1;
                        if (random == 1) {
                            // drop rate for upgrade: 14%
                            // drop rate for shield: 6%
                            System.out.println("Supposed to drop something");
                            Drop drop;
                            if (numOfPlayers == 1) {
                                drop = new Drop(player1, monster);
                            } else {
                                int randomColor = (int) (Math.random() * 2) + 1;
                                if (randomColor == 1) {
                                    drop = new Drop(player1, monster);
                                } else {
                                    drop = new Drop(player2, monster);
                                }
                            }

                            drops.add(drop);
                        }

                    }
                    bullets.remove(j);
                    j--;
                } else if (!bullet.isFromPlayer() && player1.playerRect().intersects(bullet.bulletRect())) {
                    breakShield(player1);
                } else if (!bullet.isFromPlayer() && player2.playerRect().intersects(bullet.bulletRect())) {
                    breakShield(player2);
                }
            }


            // wait for 3 seconds to spawn new monsters
            if (monsters.size() == 0) {
                startTime = time;
                endTime = startTime + 3;
            }
        }

        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroid = asteroids.get(i);
            System.out.println("player1: " + player1.getX() + ", " + player1.getY());
            System.out.println("asteroid: " + asteroid.getX() + ", " + asteroid.getY());
            if (player1.playerRect().intersects(asteroid.asteroidRect())) {
                player1.died();
                System.out.println("Got hit");
            } else if (player2.playerRect().intersects(asteroid.asteroidRect())) {
                player2.died();
            }

            for (int b = 0; b < bullets.size(); b++) {
                Bullet bullet = bullets.get(b);
                if (bullet.bulletRect().intersects(asteroid.asteroidRect())) {
                    bullets.remove(b);
                    b--;
                    asteroids.remove(i);
                    i--;
                }
            }
        }
        g.drawRect(player1.getX(), player1.getY(), player1.getPlayerImage().getWidth(), player1.getPlayerImage().getHeight() - 10);
        g.drawRect(player2.getX(), player2.getY(), player2.getPlayerImage().getWidth(), player2.getPlayerImage().getHeight());

        // ----- player 1 movement -----
        // player 1 moves up [W]
        if (player1.getHealth()) {
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
        }

        // ----- player 2 movement -----
        if (numOfPlayers == 2 && player2.getHealth()) {
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
            Bullet bullet = bullets.get(i);
            if (bullet.getY() > 0 && bullet.isFromPlayer()) {
                bullet.setY(bullet.getY() - 1.2); // school desktop: 0.8. home laptop: 1.2
            } else if (bullet.getY() < 743 && !bullet.isFromPlayer()) {
                bullet.setY(bullet.getY() + 1);
            } else {
                bullets.remove(i);
                i--;
            }
            g.drawImage(bullet.getBulletImage(), bullet.getX(), bullet.getY(), null);
        }

        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroid = asteroids.get(i);
            if (asteroid.getDirection().equals("left")) {
                asteroid.leftDirection();
            } else if (asteroid.getDirection().equals("down")) {
                asteroid.downDirection();
            } else {
                asteroid.rightDirection();
            }
            if (asteroid.getX() <= 0 || asteroid.getX() >= 1214 || asteroid.getY() >= 743) {
                asteroids.remove(i);
                i--;
            }

            g.drawRect(asteroid.getX(), asteroid.getY(), asteroid.getAsteroidImage().getWidth(), asteroid.getAsteroidImage().getHeight());
            g.drawImage(asteroid.getAsteroidImage(), asteroid.getX(), asteroid.getY(), null);
        }

        for (int i = 0; i < drops.size(); i++) {
            Drop drop = drops.get(i);

            if (drop.getY() < 745) {
                drop.fall();
            } else {
                drops.remove(i);
                i--;
            }

            if (drop.dropRect().intersects(player1.playerRect())) {
                if (drop.getType().equals("upgrade")) {
                    drop.dropUpgrade(player1);
                } else {
                    player1.addShield();
                }

                drops.remove(i);
                System.out.println("Player 1: " + player1.getDamage());
            }
            if (numOfPlayers == 2) {
                if (drop.dropRect().intersects(player2.playerRect())) {
                    if (drop.getType().equals("upgrade")) {
                        drop.dropUpgrade(player2);
                    } else {
                        player2.addShield();
                    }

                    drops.remove(i);
                    System.out.println("Player 2: " + player2.getDamage());
                }
            }
            g.drawImage(drop.getDropImage(), drop.getX(), drop.getY(), null);
        }


        if (player1.hasShield()) {
            g.drawImage(player1.getShieldImage(), player1.getX() - 40, player1.getY() - 70, null);
        }
        if (numOfPlayers == 2) {
            if (player2.hasShield()) {
                g.drawImage(player2.getShieldImage(), player2.getX() - 40, player2.getY() - 70, null);
            }
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
        if (key == KeyEvent.VK_E && player1.getHealth()) {
            Bullet bullet;
            if (player1.getDamage() >= 10) {
                bullet = new Bullet("assets/blue-missile.png", player1.getX() + 30, player1.getY() - 40, true);
            } else {
                bullet = new Bullet("assets/laser.png", player1.getX() + 57, player1.getY() - 40, true);
            }
            bullets.add(bullet);
        }

        if (key == KeyEvent.VK_ENTER && player2.getHealth()) {
            Bullet bullet;
            if (player2.getDamage() >= 10) {
                bullet = new Bullet("assets/red-missile.png", player2.getX() + 30, player2.getY() - 40, true);
            } else {
                bullet = new Bullet("assets/laser.png", player2.getX() + 57, player2.getY() - 40, true);
            }
            bullets.add(bullet);
        }
    }

    public void breakShield(Player player) {
        if (player.hasShield()) {
            startCoolDown = time;
            endCoolDown = startCoolDown + 2;
            coolDown = true;
            player.removeShield();
        }

        if (coolDown && time > endCoolDown) {
            player.died();
            coolDown = false;
        } else if (!coolDown) {
            player.died();
        }
    }

    // MouseListener Interface Methods (most likely don't need them)
    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { } // unimplemented

    public void mouseExited(MouseEvent e) { } // unimplemented

    // ACTIONLISTENER INTERFACE METHODS (buttons and timer)
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer) {
            time++;

            // every 3 seconds, there will be 40% chance a bug monster or green horn monster will shoot out a laser
            if (time % 3 == 0) {
                for (Monster monster : monsters) {
                    int random = (int) (Math.random() * 5) + 1;
                    if ((monster.getName().equals("Bug") || monster.getName().equals("Green Horn Monster")) && random >= 2) {
                        Bullet monsterLaser = new Bullet("assets/monster-laser.png", monster.getX() + 25, monster.getY() + 64, false);
                        bullets.add(monsterLaser);
                    } else if (monster.getName().equals("Boss") && random >= 2) {
                        Asteroid asteroid = new Asteroid(monster.getX() + 60, monster.getY() + 90);
                        asteroids.add(asteroid);
                    }

                }

            }
        }

    }
}

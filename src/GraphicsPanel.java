import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private JFrame enclosingFrame;
    private BufferedImage spaceBackground;
    private BufferedImage gameOverBackground;
    private int numOfPlayers;
    private Player player1;
    private Player player2;
    private ArrayList<Bullet> bullets;
    private ArrayList<Monster> monsters;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Drop> drops;
    private int highScore;
    private int currentScore;
    private boolean[] pressedKeys;
    private Timer timer;
    private int time;
    private int startTime;
    private int endTime;
    private JButton playAgain;
    private boolean coolDown;
    private int startCoolDown;
    private int endCoolDown;
    private boolean run;
    private Clip song;
    private Clip laserSound;
    private Clip missileSound;
    private Clip pickUpSound;
    private Clip destroyShieldSound;
    private Clip playerDeathSound;

    public GraphicsPanel(JFrame frame, int numOfPlayers) {
        enclosingFrame = frame;
        try {
            spaceBackground = ImageIO.read(new File("assets/game-background.png"));
            gameOverBackground = ImageIO.read(new File("assets/game-over-background.jpg"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        player1 = new Player("blue");

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

        // high score
        readData();

        this.numOfPlayers = numOfPlayers;
        pressedKeys = new boolean[128]; // 128 keys

        coolDown = false;
        run = true;

        playAgain = new JButton("Play Again");

        playAgain.setFocusable(true);
        playAgain.addActionListener(this);

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // music
        playSong();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (run) {
            g.drawImage(spaceBackground, 0, 0, null);

            if (player1.isAlive()) {
                g.drawImage(player1.getPlayerImage(), player1.getX(), player1.getY(), null);
            }

            if (numOfPlayers == 2 && player2.isAlive()) {
                g.drawImage(player2.getPlayerImage(), player2.getX(), player2.getY(), null);
            }

            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.setColor(Color.WHITE);

            g.drawString("Time: " + time + "s", 20, 40);
            timer.start();
            g.drawString("High Score: " + highScore, 20, 70);
            g.drawString("Current Score: " + currentScore, 20, 100);

            // if there is no monster and the timer is up, spawn 1 - 5 monsters
            if (monsters.size() == 0 && endTime == time) {
                int numOfMonsters = (int) (Math.random() * 5) + 1;

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
                } else if (monster.getName().equals("Green Horn Monster") && monster.getX() >= 1214) {
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
                } else if (numOfPlayers == 2 && monster.monsterRect().intersects(player2.playerRect())) {
                    breakShield(player2);
                }

                for (int j = 0; j < bullets.size(); j++) {
                    Bullet bullet = bullets.get(j);

                    // if the bullet from the player hit the monster, monster decreases HP
                    if (bullet.isFromPlayer() && monster.monsterRect().intersects(bullet.bulletRect())) {
                        monster.decreaseHealth(player1.getDamage());
                        if (monster.getHealth() <= 0) {
                            String monsterName = monster.getName();

                            // if the monster died, the score goes up depending on the type of the monster being killed
                            if (monsterName.equals("Insect")) {
                                currentScore++;
                            } else if (monsterName.equals("Bug")) {
                                currentScore += 2;
                            } else if (monsterName.equals("Green Horn Monster")) {
                                currentScore += 3;
                            } else {
                                currentScore += 4;
                            }
                            monsters.remove(i);
                            i--;

                            // drop rate for anything: 20%
                            int random = (int) (Math.random() * 5) + 1;
                            if (random == 1) {
                                // drop rate for upgrade: 14%
                                // drop rate for shield: 6%
                                Drop drop;
                                if (numOfPlayers == 1) {
                                    drop = new Drop(player1, monster);
                                } else {
                                    // random color between blue and red
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

                        /* if bullet from the monster hit the player, check if the player has shield
                        if the player has shield, break shield
                        if not, the player died */
                    } else if (!bullet.isFromPlayer() && player1.playerRect().intersects(bullet.bulletRect())) {
                        breakShield(player1);
                    } else if (numOfPlayers == 2 && !bullet.isFromPlayer() && player2.playerRect().intersects(bullet.bulletRect())) {
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
                // if player touches the asteroid, the player dies
                if (player1.playerRect().intersects(asteroid.asteroidRect())) {
                    player1.died();
                    playPlayerDeathSound();
                } else if (numOfPlayers == 2 && player2.playerRect().intersects(asteroid.asteroidRect())) {
                    player2.died();
                    playPlayerDeathSound();
                }

                for (int b = 0; b < bullets.size(); b++) {
                    Bullet bullet = bullets.get(b);
                    // if the player shoots at the asteroid, the asteroid disappears
                    if (bullet.isFromPlayer() && bullet.bulletRect().intersects(asteroid.asteroidRect())) {
                        bullets.remove(b);
                        b--;
                        asteroids.remove(i);
                        i--;
                    }
                }
            }

            // ----- player 1 movement -----
            // player 1 moves up [W]
            if (player1.isAlive()) {
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
            if (numOfPlayers == 2 && player2.isAlive()) {
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

            // bullet movement
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if (bullet.getY() > 0 && bullet.isFromPlayer()) {
                    bullet.playerShoot();
                } else if (bullet.getY() < 743 && !bullet.isFromPlayer()) {
                    bullet.monsterShoot();
                } else {
                    bullets.remove(i);
                    i--;
                }
                g.drawImage(bullet.getBulletImage(), bullet.getX(), bullet.getY(), null);
            }

            // asteroid movement
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

                // if the player intersects with upgrade drop, damage increases
                // if the player intersects with the shield drop, the player received a shield
                if (drop.dropRect().intersects(player1.playerRect())) {
                    if (drop.getType().equals("upgrade")) {
                        drop.dropUpgrade(player1);
                        System.out.println("Player 1's Damage: " + player1.getDamage());
                    } else {
                        player1.addShield();
                        System.out.println("Player 1 received a shield");
                    }

                    drops.remove(i);
                    playPickUpSound();
                } else if (numOfPlayers == 2 && drop.dropRect().intersects(player2.playerRect())) {
                    if (drop.getType().equals("upgrade")) {
                        drop.dropUpgrade(player2);
                        System.out.println("Player 2's Damage: " + player2.getDamage());
                    } else {
                        player2.addShield();
                        System.out.println("Player 2 received a shield");
                    }

                    drops.remove(i);
                    playPickUpSound();
                }

                g.drawImage(drop.getDropImage(), drop.getX(), drop.getY(), null);
            }


            if (player1.hasShield()) {
                g.drawImage(player1.getShieldImage(), player1.getX() - 40, player1.getY() - 70, null);
            }
            if (numOfPlayers == 2 && player2.hasShield()) {
                g.drawImage(player2.getShieldImage(), player2.getX() - 40, player2.getY() - 70, null);
            }
        }

        // game over screen
        if ((numOfPlayers == 1 && !player1.isAlive()) || (numOfPlayers == 2 && !player1.isAlive() && !player2.isAlive())) {
            run = false;
            song.stop(); // stop song
            song.close();

            g.drawImage(gameOverBackground, 0, 0, null);
            g.setFont(new Font("Courier New", Font.BOLD, 36));

            // if the current score is greater than the high score, the current score becomes the new high score
            if (currentScore > highScore) {
                updateHighScore();
                g.setColor(Color.GREEN);
                g.drawString("*NEW HIGH SCORE*" , 650, 400);
            }

            g.setColor(Color.WHITE);
            g.drawString("Survival Time: " + time + "s", 420, 300);
            g.drawString("High Score: " + highScore, 420, 350);
            g.drawString("Score: " + currentScore, 420, 400);

            // play again button
            add(playAgain);
            playAgain.setFont(new Font("Courier New", Font.BOLD, 24));
            playAgain.setLocation(500, 450);
        }

    }


    public void breakShield(Player player) {
        if (player.hasShield()) {
            startCoolDown = time;
            endCoolDown = startCoolDown + 2;
            coolDown = true;
            player.removeShield();
            playShieldBreakSound();
        }

        if (coolDown && time > endCoolDown) {
            player.died();
            playPlayerDeathSound();
            coolDown = false;
        } else if (!coolDown) {
            player.died();
            playPlayerDeathSound();
        }
    }


    // access the high score file
    private void readData() {
        try {
            File highestScoreFile = new File("src/high-score.txt");
            Scanner scanData = new Scanner(highestScoreFile);
            highScore = scanData.nextInt();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // update new high score
    private void updateHighScore() {
        try {
            PrintWriter writer = new PrintWriter("src/high-score.txt");
            writer.write(String.valueOf(currentScore));
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----- play music clips method -----
    private void playSong() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/SkyFire.wav").getAbsoluteFile());
            song = AudioSystem.getClip();
            song.open(audioInputStream);
            song.loop(Clip.LOOP_CONTINUOUSLY);
            song.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playLaserSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/laser-sound.wav").getAbsoluteFile());
            laserSound = AudioSystem.getClip();
            laserSound.open(audioInputStream);
            laserSound.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playMissileSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/missile-sound.wav").getAbsoluteFile());
            missileSound = AudioSystem.getClip();
            missileSound.open(audioInputStream);
            missileSound.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playPickUpSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/pickup-sound.wav").getAbsoluteFile());
            pickUpSound = AudioSystem.getClip();
            pickUpSound.open(audioInputStream);
            pickUpSound.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playShieldBreakSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/destroy-shield-sound.wav").getAbsoluteFile());
            destroyShieldSound = AudioSystem.getClip();
            destroyShieldSound.open(audioInputStream);
            destroyShieldSound.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playPlayerDeathSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("assets/player-death-sound.wav").getAbsoluteFile());
            playerDeathSound = AudioSystem.getClip();
            playerDeathSound.open(audioInputStream);
            playerDeathSound.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ----- INTERFACE METHODS -----
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
        if (key == KeyEvent.VK_E && player1.isAlive()) {
            Bullet bullet;
            if (player1.getDamage() >= 10) {
                bullet = new Bullet("assets/blue-missile.png", player1.getX() + 30, player1.getY() - 40, true);
                playMissileSound();
            } else {
                bullet = new Bullet("assets/laser.png", player1.getX() + 57, player1.getY() - 40, true);
                playLaserSound();
            }
            bullets.add(bullet);
        }

        if (key == KeyEvent.VK_ENTER && player2.isAlive()) {
            Bullet bullet;
            if (player2.getDamage() >= 10) {
                bullet = new Bullet("assets/red-missile.png", player2.getX() + 30, player2.getY() - 40, true);
                playMissileSound();
            } else {
                bullet = new Bullet("assets/laser.png", player2.getX() + 57, player2.getY() - 40, true);
                playLaserSound();
            }
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer && run) {
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

        if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();

            if (button == playAgain) {
                MenuFrame menuFrame = new MenuFrame();
                enclosingFrame.setVisible(false);
            }
        }

    }
}

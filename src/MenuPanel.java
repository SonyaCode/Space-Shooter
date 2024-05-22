import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPanel extends JPanel implements ActionListener {
    private BufferedImage menuBackground;
    private BufferedImage title;
    private JButton singleplayer;
    private JButton multiplayer;
    private JFrame enclosngFrame;

    public MenuPanel(JFrame frame) {
        enclosngFrame = frame;
        try {
            menuBackground = ImageIO.read(new File("menu-background.png"));
            title = ImageIO.read(new File("title.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        singleplayer = new JButton("Singleplayer");
        multiplayer = new JButton("Multiplayer ");
        add(singleplayer);
        add(multiplayer);
        singleplayer.setFocusable(false);
        multiplayer.setFocusable(false);
        singleplayer.addActionListener(this);
        multiplayer.addActionListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.setColor(Color.BLACK);

        g.drawImage(menuBackground, 0, 0, null);
        g.drawImage(title, 156, 100, null);

        singleplayer.setFont(new Font("Courier New", Font.BOLD, 24));
        singleplayer.setLocation(300, 180);
        multiplayer.setFont(new Font("Courier New", Font.BOLD, 24));
        multiplayer.setLocation(300, 240);

    }

    // one player if clicked singleplayer and two player if clicked multiplayers
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();

            if (button == singleplayer) {
                MainFrame f = new MainFrame(1);
                enclosngFrame.setVisible(false); // close this window
            } else {
                MainFrame f = new MainFrame(2);
                enclosngFrame.setVisible(false); // close this window
            }
        }

    }

}
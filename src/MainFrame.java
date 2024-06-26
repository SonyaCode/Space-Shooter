import javax.swing.*;

public class MainFrame implements Runnable {
    private GraphicsPanel panel;

    public MainFrame(int numOfPlayers) {
        JFrame frame = new JFrame("Space Shooter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1214, 743);
        frame.setLocationRelativeTo(null);

        panel = new GraphicsPanel(frame, numOfPlayers);
        frame.add(panel);

        frame.setVisible(true);

        // animation
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (true) {
            panel.repaint();
        }
    }
}
import javax.swing.*;
import java.awt.*;

public class MenuFrame {
    private MenuPanel panel;

    public MenuFrame() {
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(830, 518);
        frame.setLocationRelativeTo(null); // auto-centers frame in screen

        // create and add panel
        panel = new MenuPanel(frame);
        frame.add(panel);

        // display the frame
        frame.setVisible(true);
    }
}
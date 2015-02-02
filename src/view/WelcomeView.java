package view;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Welcome screen.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @copyright Public Domain
 */
public class WelcomeView extends JPanel {

    public static final File ICON = new File("./images/icon - save my cheese.png");
    private static final int PREF_WIDTH = 400;
    private static final int PREF_HEIGHT = 200;
    private static WelcomeView instance;
    private static final JFrame frame = new JFrame("Welcome - Save My Cheese");
    private static final JButton jbStart = new JButton("Start");
    private static final JButton jbAbout = new JButton("About");
    private static final JButton jbExit = new JButton("Exit");
    private static final java.awt.Font BUTTON_FONT = new java.awt.Font("Tahoma", 1, 24);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public static void createAndShowGUI() {
        if (instance == null) {
            try {
                instance = new WelcomeView();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setIconImage(ImageIO.read(ICON));
                frame.getContentPane().add(instance);
                frame.pack();
                frame.setLocationRelativeTo(null);                
            } catch (IOException ex) {
                Logger.getLogger(WelcomeView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        frame.setVisible(true);
    }

    private WelcomeView() {
        setLayout(new java.awt.GridLayout(3, 0));
        add(jbStart);
        add(jbAbout);
        add(jbExit);
        
        jbStart.setFont(BUTTON_FONT);
        jbAbout.setFont(BUTTON_FONT);
        jbExit.setFont(BUTTON_FONT);
        
        jbStart.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("start");
            GameView.createAndShowGUI();
        });
        jbAbout.addActionListener((java.awt.event.ActionEvent evt) -> {
            AboutView.createAndShowGUI();
        });
        jbExit.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("exit");
        });
    }
}

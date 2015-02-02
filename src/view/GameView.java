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
 * Game screen.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @copyright Public Domain
 */
public class GameView extends JPanel {

    private static final int PREF_WIDTH = 400;
    private static final int PREF_HEIGHT = 200;
    private static GameView instance;
    private static final JFrame frame = new JFrame("Game - Save My Cheese");
    private static final JButton jbStartPause = new JButton("Pause");
    private static final JButton jbBack = new JButton("Back");
    private static final JPanel jpCanvas = new JPanel();
    private static final JPanel jpButtons = new JPanel();
    private static final java.awt.Font BUTTON_FONT = new java.awt.Font("Tahoma", 1, 12);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public static void createAndShowGUI() {
        if (instance == null) {
            try {
                instance = new GameView();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setIconImage(ImageIO.read(WelcomeView.ICON));
                frame.getContentPane().add(instance);
                frame.pack();
                frame.setLocationRelativeTo(null);                
            } catch (IOException ex) {
                Logger.getLogger(WelcomeView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        frame.setVisible(true);
    }

    private GameView() {
        setLayout(null);
        add(jpCanvas);    
        add(jpButtons);
        
        
        jpCanvas.setBounds(0, 0, 200, 100);
        jpButtons.setBounds(280, 10, 100, 50);
        jpButtons.setLayout(new java.awt.GridLayout(2, 0));
        jpButtons.add(jbStartPause);
        jpButtons.add(jbBack);
        
        jbStartPause.setFont(BUTTON_FONT);
        jbBack.setFont(BUTTON_FONT);
        
        jbStartPause.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("start");
        });
        
        jbBack.addActionListener((java.awt.event.ActionEvent evt) -> {
            frame.setVisible(false);
        });
    }
}

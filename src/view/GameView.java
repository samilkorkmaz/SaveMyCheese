package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Game screen.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @copyright Public Domain
 */
public class GameView extends JPanel {

    private static final int PREF_WIDTH = 800;
    private static final int PREF_HEIGHT = 600;
    private static final int BUTTON_PANEL_WIDTH = 100;
    private static GameView instance;
    private static final JFrame frame = new JFrame("Game - Save My Cheese");
    private static final JButton jbStartPause = new JButton("Pause");
    private static final JButton jbBack = new JButton("Back");
    private static final JButton jbNext = new JButton("Next");
    private static final JPanel jpCanvas = new CanvasPanel();
    private static final JPanel jpButtons = new JPanel();
    private static final java.awt.Font BUTTON_FONT = new java.awt.Font("Tahoma", 1, 12);
    private static final java.awt.Font LEVEL_SUCCESS_FONT = new java.awt.Font("Tahoma", 1, 12);

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

        jpButtons.setBounds((int) getPreferredSize().getWidth() - (BUTTON_PANEL_WIDTH + 10), 10, BUTTON_PANEL_WIDTH, 75);
        jpCanvas.setBounds(0, 0, (int) getPreferredSize().getWidth() - (BUTTON_PANEL_WIDTH + 10), (int) getPreferredSize().getHeight());

        jpButtons.setLayout(new java.awt.GridLayout(3, 0));
        jpButtons.add(jbStartPause);
        jpButtons.add(jbBack);
        jpButtons.add(jbNext);

        jbStartPause.setFont(BUTTON_FONT);
        jbBack.setFont(BUTTON_FONT);
        jbNext.setFont(BUTTON_FONT);
        jbNext.setEnabled(false);

        jbStartPause.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("start");
        });

        jbBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                makeVisible(false);
                WelcomeView.makeVisible(true);
            }
        });
        
        jbNext.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("next");
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WelcomeView.makeVisible(true);
            }
        });
    }

    public static void makeVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    public static void setLevelSuccess() {
        JLabel jlSuccess = new JLabel("Level completed successfully");
        jlSuccess.setFont(LEVEL_SUCCESS_FONT);
        jlSuccess.setForeground(Color.BLUE);
        jlSuccess.setBounds(10, 10, 200, 50);
        jpCanvas.add(jlSuccess);
        jbNext.setEnabled(true);
    }
}

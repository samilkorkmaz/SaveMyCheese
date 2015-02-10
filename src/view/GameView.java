package view;

import controller.GameController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
    private static final JButton jbRestart = new JButton("Restart");
    private static final JButton jbNext = new JButton("Next");
    private static final JButton jbBack = new JButton("Back");
    private static JPanel jpCanvas;
    private static final JPanel jpButtons = new JPanel();
    private static final java.awt.Font BUTTON_FONT = new java.awt.Font("Tahoma", 1, 12);
    private static final java.awt.Font LEVEL_SUCCESS_FONT = new java.awt.Font("Tahoma", 1, 24);
    private static final String SUCCESS_TEXT = "Level completed successfully!";
    private static final String FAIL_TEXT = "Game over: Mouse ate the cheese!";
    private static final Color SUCCESS_COLOR = Color.BLUE;
    private static final Color FAIL_COLOR = Color.RED;
    private static final JLabel jlSuccessFail = new JLabel();
    private static boolean isPause = true;

    @Override
    public final Dimension getPreferredSize() {
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
        jpCanvas = CanvasPanel.create(0, 0, (int) getPreferredSize().getWidth() - (BUTTON_PANEL_WIDTH + 10), (int) getPreferredSize().getHeight());
        jpCanvas.add(jlSuccessFail);
        jlSuccessFail.setBounds(10, 10, 450, 50);
        jlSuccessFail.setFont(LEVEL_SUCCESS_FONT);
        jlSuccessFail.setForeground(Color.BLUE);

        add(jpCanvas);
        add(jpButtons);

        jpButtons.add(jbStartPause);
        jpButtons.add(jbRestart);
        jpButtons.add(jbNext);
        jpButtons.add(jbBack);
        jpButtons.setLayout(new java.awt.GridLayout(jpButtons.getComponentCount(), 0));
        jpButtons.setBounds((int) getPreferredSize().getWidth() - (BUTTON_PANEL_WIDTH + 10), 10, BUTTON_PANEL_WIDTH, 
                25 * jpButtons.getComponentCount());

        jbStartPause.setFont(BUTTON_FONT);
        jbRestart.setFont(BUTTON_FONT);
        jbNext.setFont(BUTTON_FONT);
        jbNext.setEnabled(false);
        jbBack.setFont(BUTTON_FONT);

        jbStartPause.addActionListener((java.awt.event.ActionEvent evt) -> {
            if(isPause) {
                GameController.pause();
            } else {
                GameController.continueGame();
            }
            isPause = !isPause;
            if(isPause) {
                jbStartPause.setText("Pause");
            } else {
                jbStartPause.setText("Continue");
            }
        });

        jbRestart.addActionListener((ActionEvent ae) -> {
            GameController.start();
            jlSuccessFail.setText("");
            jpCanvas.repaint();
            jbNext.setEnabled(false);
            CanvasPanel.resetMap();
        });

        jbNext.addActionListener((java.awt.event.ActionEvent evt) -> {
            System.out.println("next");
        });

        jbBack.addActionListener((ActionEvent ae) -> {
            makeVisible(false);
            WelcomeView.makeVisible(true);
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
        jlSuccessFail.setForeground(SUCCESS_COLOR);
        jlSuccessFail.setText(SUCCESS_TEXT);
        jbNext.setEnabled(true);
    }
    
    public static void setLevelFail() {
        jlSuccessFail.setForeground(FAIL_COLOR);
        jlSuccessFail.setText(FAIL_TEXT);
        jbNext.setEnabled(true);
    }
}

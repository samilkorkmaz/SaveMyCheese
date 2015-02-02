package view;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * About screen.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @copyright Public Domain
 */
public class AboutView extends JPanel {

    private static final int PREF_WIDTH = 400;
    private static final int PREF_HEIGHT = 200;
    private static AboutView instance;
    private static final JFrame frame = new JFrame("About - Save My Cheese");
    private static final JTextArea jtaInfo = new JTextArea("Save My Cheese version 1.0."
            + "\nProgrammed by Samil Korkmaz, February 2015"
            + "\nhttp://samilkorkmaz.blogspot.com/");
    private static final JButton jbBack = new JButton("Back");
    private static final JPanel jpButtons = new JPanel();
    private static final java.awt.Font BUTTON_FONT = new java.awt.Font("Tahoma", 1, 12);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }

    public static void createAndShowGUI() {
        if (instance == null) {
            try {
                instance = new AboutView();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setIconImage(ImageIO.read(WelcomeView.ICON));
                frame.getContentPane().add(instance);
                frame.pack();
                frame.setLocationRelativeTo(null);
            } catch (IOException ex) {
                Logger.getLogger(AboutView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        frame.setVisible(true);
    }

    private AboutView() {
        setLayout(new java.awt.GridLayout(2, 0));
        add(jtaInfo);
        add(jpButtons);
        jpButtons.setBackground(Color.WHITE);
        jpButtons.add(jbBack);

        jtaInfo.setEditable(false);
        jtaInfo.setLineWrap(true);
        jbBack.setFont(BUTTON_FONT);

        jbBack.addActionListener((java.awt.event.ActionEvent evt) -> {
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
}

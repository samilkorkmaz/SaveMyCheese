package savemycheese;

import controller.GameController;
import view.WelcomeView;

/**
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class Main {

    public static void main(String[] args) {
        GameController.start();
        WelcomeView.createAndShowGUI();
    }
    
}

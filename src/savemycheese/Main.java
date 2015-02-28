package savemycheese;

import controller.GameController;
import java.io.IOException;
import java.net.URISyntaxException;
import view.WelcomeView;

/**
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        GameController.start();
        WelcomeView.createAndShowGUI();
    }

    

}

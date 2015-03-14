package savemycheese;

import java.io.IOException;
import java.net.URISyntaxException;
import view.WelcomeView;

/**
 * Main class of Save my Cheese game.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        WelcomeView.createAndShowGUI();
    }

}

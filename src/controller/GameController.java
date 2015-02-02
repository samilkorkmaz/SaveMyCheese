package controller;

import java.awt.Polygon;
import java.util.List;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.MyPolygon;
import view.GameView;
import view.WelcomeView;

/**
 * Game controller.
 *
 * @author Samil Korkmaz
 */
public class GameController {

    private static final List<MyPolygon> polygonList = new ArrayList<>();
    private static final List<MyPolygon> snapPolygonList = new ArrayList<>();

    public static List<MyPolygon> getSnapPolygonList() {
        return snapPolygonList;
    }

    public static List<MyPolygon> getPolygonList() {
        return polygonList;
    }

    /**
     * Initialize game (create puzzle pieces etc.).
     */
    public static void init() {
        for (int i = 0; i < 3; i++) {
            int x0 = i * 50;
            int y0 = i * 50;
            int[] xCoords = {x0 + 0, x0 + 60, x0 + 120, x0 + 100, x0 + 20};
            int[] yCoords = {y0 + 50, y0 + 0, y0 + 50, y0 + 125, y0 + 125};
            MyPolygon myPolygon = new MyPolygon(xCoords, yCoords, xCoords.length);
            polygonList.add(myPolygon);

            int[] xSnapCoords = new int[xCoords.length];
            int[] ySnapCoords = new int[xCoords.length];
            int xSnap = 200;
            int ySnap = 20;
            for (int j = 0; j < xCoords.length; j++) {
                xSnapCoords[j] = xSnap + xCoords[j];
                ySnapCoords[j] = ySnap + yCoords[j];
            }
            MyPolygon snapPolygon = new MyPolygon(xSnapCoords, ySnapCoords, xSnapCoords.length);
            snapPolygonList.add(snapPolygon);
        }
    }

}

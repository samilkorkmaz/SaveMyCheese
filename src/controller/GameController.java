package controller;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import model.MyPolygon;

/**
 * Game controller.
 *
 * @author Samil Korkmaz
 */
public class GameController {

    private static final List<MyPolygon> polygonList = new ArrayList<>();
    private static final List<MyPolygon> snapPolygonList = new ArrayList<>();
    private static MyPolygon selectedPolygon = null;
    private static MyPolygon selectedSnapPolygon = null;
    private static int prevMouseX;
    private static int prevMouseY;

    public static void toggleSelectedShape() {
        if (selectedPolygon != null) {
            selectedPolygon = null;
        }
    }

    public static void setSelectedShape(int mouseX, int mouseY) {
        if (GameController.getShapeList().size() > 0) {
            for (int i = GameController.getShapeList().size() - 1; i >= 0; i--) {
                if (GameController.getShapeList().get(i).contains(new Point(mouseX, mouseY))) { //if there is a polygon at clicked point
                    selectedPolygon = GameController.getShapeList().get(i);

                    //move the selected polygon to the end of list so that it will be drawn last (i.e. on top) in paintComponent and checked first for mouse click:
                    GameController.getShapeList().remove(selectedPolygon);
                    GameController.getShapeList().add(GameController.getShapeList().size(), selectedPolygon);

                    selectedSnapPolygon = GameController.getSnapShapeList().get(i);
                    GameController.getSnapShapeList().remove(selectedSnapPolygon);
                    GameController.getSnapShapeList().add(GameController.getSnapShapeList().size(), selectedSnapPolygon);

                    prevMouseX = mouseX;
                    prevMouseY = mouseY;

                    break;
                }
            }
        }
    }

    public static void moveShape(int mouseX, int mouseY) {
        if (selectedPolygon != null && !selectedPolygon.isSnapped()) {
            selectedPolygon.translate(mouseX - prevMouseX, mouseY - prevMouseY);
            prevMouseX = mouseX;
            prevMouseY = mouseY;
            selectedPolygon.setIsSnapped(selectedPolygon.isCloseTo(selectedSnapPolygon));

        } else {
            selectedPolygon = null;
            selectedSnapPolygon = null;
        }
    }

    public static List<MyPolygon> getSnapShapeList() {
        return snapPolygonList;
    }

    public static List<MyPolygon> getShapeList() {
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

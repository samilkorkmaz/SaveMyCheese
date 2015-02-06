package controller;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import model.MyPolygon;
import view.CanvasPanel;
import view.GameView;

/**
 * Game controller.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class GameController {

    private static final List<MyPolygon> polygonList = new ArrayList<>();
    private static final List<MyPolygon> snapPolygonList = new ArrayList<>();
    private static MyPolygon selectedPolygon = null;
    private static MyPolygon selectedSnapPolygon = null;
    private static int prevMouseX;
    private static int prevMouseY;
    private static boolean isAllSnapped;

    public static void start() {
        init();
    }

    public static void deselectShape() {
        selectedPolygon = null;
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

    public static boolean isAllSnapped() {
        return isAllSnapped;
    }

    private static void checkAllSnapped() {
        isAllSnapped = true;
        for (MyPolygon myPolygon : polygonList) {
            if (!myPolygon.isSnapped()) {
                isAllSnapped = false;
                break;
            }
        }
        if (isAllSnapped) {
            GameView.setLevelSuccess();
        }
    }

    public static void moveShape(int mouseX, int mouseY) {
        if (selectedPolygon != null && !selectedPolygon.isSnapped()) {
            selectedPolygon.translate(mouseX - prevMouseX, mouseY - prevMouseY);
            prevMouseX = mouseX;
            prevMouseY = mouseY;
            boolean isSnapped = selectedPolygon.isCloseTo(selectedSnapPolygon);
            selectedPolygon.setIsSnapped(isSnapped);
            if (isSnapped) {
                checkAllSnapped();
                CanvasPanel.updateMapAndPaths(selectedPolygon);
            }
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
    private static void init() {
        polygonList.clear();
        snapPolygonList.clear();
        /*for (int i = 0; i < 3; i++) {
         int[] xCoords = {0, 60, 120, 100, 20};
         int[] yCoords = {50, 0, 50, 125, 125};
         MyPolygon myPolygon = new MyPolygon(xCoords, yCoords, xCoords.length);
         myPolygon.translate(i * 50, i * 50);
         polygonList.add(myPolygon);

         MyPolygon snapPolygon = new MyPolygon(xCoords, yCoords, xCoords.length);
         snapPolygon.translate(200 + i * 50, 20 + i * 50);
         snapPolygonList.add(snapPolygon);
         }*/
        addToList(new int[]{110, 157, 174, 157, 110, 127}, new int[]{90, 90, 126, 162, 162, 126}, 351, 67 + 120);
        addToList(new int[]{110, 134, 215, 238}, new int[]{90, 43 + 94, 43 + 94, 90}, 270, 139 + 120);
        addToList(new int[]{110, 134, 215, 238}, new int[]{90, 43, 43, 90}, 270, 20 + 120);
        addToList(new int[]{110, 157, 140, 157, 110, 93}, new int[]{90, 90, 126, 162, 162, 126}, 253, 67 + 120);
    }

    private static void addToList(int[] xCoords, int[] yCoords, int xSnap, int ySnap) {
        MyPolygon myPolygon = new MyPolygon(xCoords, yCoords, xCoords.length);
        myPolygon.translate(10, 350);
        polygonList.add(myPolygon);

        MyPolygon snapPolygon = new MyPolygon(xCoords, yCoords, xCoords.length);
        snapPolygon.translate(xSnap, ySnap);
        snapPolygonList.add(snapPolygon);
    }

}

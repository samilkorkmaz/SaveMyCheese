package controller;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.MyPolygon;
import view.CanvasPanel;
import view.GameView;
import view.WelcomeView;

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
    private static int level = 1;
    private static final int nbOfLevels = FileUtils.getNbOfLevelFiles();

    public static boolean isNotLastLevel() {
        return level < nbOfLevels;
    }
    
    public static int getLevel() {
        return level;
    }

    public static void incLevel() {
        level++;
    }
    
    public static String getPolygonFileForCurrentLevel() {
        String pf = "";
        switch (level) {
            case 1 :
                pf = "level1.txt";
                break;
            case 2: 
                pf = "level2.txt";
                break;
            default :
                throw new IllegalArgumentException("Unknown level: " + level);
        }
        return pf;
    }

    public static void start() {
        CanvasPanel.setGameOver(false);
        polygonList.clear();
        snapPolygonList.clear();

        /*addToList(new int[]{110, 157, 174, 157, 110, 127}, new int[]{90, 90, 126, 162, 162, 126}, 351, 67 + 120, 1);
         addToList(new int[]{110, 134, 215, 238}, new int[]{90, 43 + 94, 43 + 94, 90}, 270, 139 + 120, 1);
         addToList(new int[]{110, 134, 215, 238}, new int[]{90, 43, 43, 90}, 270, 20 + 120, 1);
         addToList(new int[]{110, 157, 140, 157, 110, 93}, new int[]{90, 90, 126, 162, 162, 126}, 253, 67 + 120, 1);*/
        
        /*addToList(new int[]{310, 309, 125, 197, 244, 199}, new int[]{486, 533, 674, 446, 434, 568}, 241, 258, 0.5);
        addToList(new int[]{376, 422, 495, 310, 309, 420}, new int[]{434, 446, 674, 533, 486, 568}, 333, 258, 0.5);
        addToList(new int[]{244, 197, 10, 239, 268, 133}, new int[]{434, 446, 306, 306, 351, 348}, 184, 194, 0.5);
        addToList(new int[]{352, 380, 607, 422, 375, 486}, new int[]{351, 307, 305, 446, 434, 349}, 355, 194, 0.5);
        addToList(new int[]{268, 240, 309, 380, 350, 309}, new int[]{351, 306, 78, 306, 351, 216}, 300, 80, 0.5);*/

        List<PolygonData> pdList = getPolygonDataFromFile(getPolygonFileForCurrentLevel());
        for (PolygonData pd : pdList) {
            addToList(pd.xArray, pd.yArray, pd.xStart, pd.yStart, pd.scale);
        }
    }

    public static void pause() {
        CanvasPanel.pauseAllThreads();
    }

    public static void continueGame() {
        CanvasPanel.continueAllThreads();
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

    private static class PolygonData {

        int[] xArray;
        int[] yArray;
        int xStart;
        int yStart;
        double scale;
    }
    
    private static List<PolygonData> getPolygonDataFromFile(String fileName) {
        //read file as String list:
        InputStream is = GameController.class.getClassLoader().getResourceAsStream(WelcomeView.POLYGON_DIR + fileName);
        List<String> dataStrList = getInputStreamAsStringList(is);
        try {
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //extract polygon data from strings
        List<PolygonData> pdList = new ArrayList<>();
        for (int i = 0; i < dataStrList.size(); i++) {
            if (i % 5 == 0) {
                PolygonData pd = new PolygonData();
                pd.xArray = getCoordArray(dataStrList.get(i));
                pd.yArray = getCoordArray(dataStrList.get(i+1));
                pd.xStart = getStart(dataStrList.get(i+2));
                pd.yStart = getStart(dataStrList.get(i+3));
                pd.scale = getScale(dataStrList.get(i+4));
                pdList.add(pd);
            }
        }
        return pdList;
    }
    
    private static int getStart(String dataStr) {
        String[] str = dataStr.split("=");
        return Integer.parseInt(str[1].trim());
    }
    
    private static double getScale(String dataStr) {
        String[] str = dataStr.split("=");
        return Double.parseDouble(str[1].trim());
    }
    
    private static int[] getCoordArray(String dataStr) {
        String[] str1 = dataStr.split("=");
        String[] str2 = str1[1].split(",");
        int[] coordArray = new int[str2.length];
        for (int i = 0; i < str2.length; i++) {
            coordArray[i] = Integer.parseInt(str2[i].trim());
        }
        return coordArray;
    }

    private static List<String> getInputStreamAsStringList(final InputStream is) {
        String COMMENT_STRING_START = "//";
        ArrayList<String> fileAsStringList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        try {
            while ((s = br.readLine()) != null) {
                boolean isNotCommentLine = !s.trim().startsWith(COMMENT_STRING_START);
                boolean isNotEmptyLine = !s.trim().isEmpty();
                if (isNotCommentLine && isNotEmptyLine) {
                    fileAsStringList.add(s);
                }
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileAsStringList;
    }

    private static void addToList(int[] xCoords, int[] yCoords, int xSnap, int ySnap, double scale) {
        int[] scaledXCoords = multiplyArray(xCoords, scale);
        int[] scaledYCoords = multiplyArray(yCoords, scale);
        MyPolygon myPolygon = new MyPolygon(scaledXCoords, scaledYCoords, scaledXCoords.length);
        myPolygon.translate(10, 350);
        polygonList.add(myPolygon);

        MyPolygon snapPolygon = new MyPolygon(scaledXCoords, scaledYCoords, scaledXCoords.length);
        snapPolygon.translate(xSnap, ySnap);
        snapPolygonList.add(snapPolygon);
    }

    private static int[] multiplyArray(int[] array, double c) {
        int[] newArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = (int) Math.round(array[i] * c);
        }
        return newArray;
    }

}

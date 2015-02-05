/*
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
package model;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.CanvasPanel;

/**
 * Mouse operations.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class MouseThread extends Thread implements Runnable {
    
    private boolean keepRunning = true;
    public static final int N_MAP_ROWS = 25;
    public static final int N_MAP_COLS = 30;
    private List<Node> path; //starts from endNode and ends at startNode
    private static int[][] mapArray2D;
    private static final List<MyRectangle> mapCellList = new ArrayList<>();
    private int iActiveRow = 0;
    private int iActiveCol = 0;
    private final Object myLock = new Object();
    private final int iThread;
    
    
    public static List<MyRectangle> getMapCellList() {
        return mapCellList;
    }

    public List<Node> getPath() {
        return path;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public MouseThread(int iThread) {
        super();
        this.iThread = iThread;
        System.out.println(iThread + ". mouse thread created.");
    }

    public void setActivePoint(int iRow, int iCol) {
        iActiveRow = iRow;
        iActiveCol = iCol;
        CanvasPanel.refreshDrawing();
    }

    public void setActivePoint(RectRowCol rc) {
        setActivePoint(rc.rowIndex, rc.colIndex);
    }

    public RectRowCol getActivePoint() {
        RectRowCol rc = new RectRowCol();
        rc.rowIndex = iActiveRow;
        rc.colIndex = iActiveCol;
        return rc;
    }

    public void updatePath() {
        Node startNode = new Node(null, iActiveRow, iActiveCol);
        Node endNode = new Node(null, CanvasPanel.CHEESE_IROW, CanvasPanel.CHEESE_ICOL);
        setActivePoint(startNode.getRowIndex(), startNode.getColIndex());
        AStarPathFinder as = new AStarPathFinder();
        this.path = as.calcPath(mapArray2D, startNode, endNode);
    }

    public static void createMap(int width, int height) {
        mapArray2D = Map2D.create(N_MAP_ROWS, N_MAP_COLS);
        int rectHeight = height / N_MAP_ROWS;
        int rectWidth = width / N_MAP_COLS;
        for (int iRow = 0; iRow < N_MAP_ROWS; iRow++) {
            for (int iCol = 0; iCol < N_MAP_COLS; iCol++) {
                MyRectangle cell = new MyRectangle(iCol * rectWidth, iRow * rectHeight, rectWidth, rectHeight, mapArray2D[iRow][iCol]);
                mapCellList.add(cell);
            }
        }
    }

    public static void resetMap() {
        for (int iRow = 0; iRow < N_MAP_ROWS; iRow++) {
            for (int iCol = 0; iCol < N_MAP_COLS; iCol++) {
                mapArray2D[iRow][iCol] = Map2D.OPEN;
                mapCellList.get(iRow * N_MAP_ROWS + iCol).setPathType(Map2D.OPEN);
            }
        }
    }

    /**
     * Turn the map cells under the shape to walls.
     *
     * @param shape
     */
    public static void updateMap(Shape shape) {
        for (MyRectangle cell : mapCellList) {
            if (cell.isInShape(shape)) {
                RectRowCol rectRowCol = getRowCol(cell);
                mapArray2D[rectRowCol.rowIndex][rectRowCol.colIndex] = Map2D.WALL;
                cell.setPathType(Map2D.WALL);
            }
        }
    }

    public static RectRowCol getRowCol(MyRectangle rect) {
        RectRowCol rectRowCol = new RectRowCol();
        int i1D = mapCellList.indexOf(rect);
        rectRowCol.colIndex = i1D % N_MAP_COLS;
        rectRowCol.rowIndex = (i1D - rectRowCol.colIndex) / N_MAP_COLS;
        return rectRowCol;
    }

    public static class RectRowCol {

        public int rowIndex;
        public int colIndex;
    }

    @Override
    public void run() {
        for (int i = path.size() - 1; i >= 0; i--) {
            if (!keepRunning) {                
                break;
            }
            Node currentNode = path.get(i);
            setActivePoint(currentNode.getRowIndex(), currentNode.getColIndex());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MouseThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("END: " + iThread + ". mouse thread ended.");
    }

}

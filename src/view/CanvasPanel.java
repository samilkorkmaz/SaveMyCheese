package view;

import controller.GameController;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import model.AStarPathFinder;
import model.Map2D;
import model.MyRectangle;
import model.Node;

/**
 *
 * @author Samil Korkmaz
 */
public class CanvasPanel extends JPanel {

    private static CanvasPanel instance;
    private static final Stroke SHAPE_STROKE = new BasicStroke(3f);
    private static final Stroke SNAP_SHAPE_STROKE = new BasicStroke(1f);
    private static final Color SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color SHAPE_FILL_COLOR = Color.BLUE;
    private static final Color SNAP_SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.GREEN;
    private static int[][] mapArray2D;
    private static final List<MyRectangle> mapRectList = new ArrayList<>();
    private static final Color OPEN_PATH_COLOR = BACKGROUND_COLOR;
    private static final Color WALL_COLOR = new Color(0, 0, 0, 255); //alpha = 0: Transparent, 255: Opaque
    private static final Color MAP_GRID_COLOR = Color.LIGHT_GRAY;
    private static final Color CHEESE_COLOR = Color.YELLOW;
    private static final Color PATH_COLOR = Color.ORANGE;
    private final int height;
    private final int width;
    private static final int N_MAP_ROWS = 25;
    private static final int N_MAP_COLS = 30;
    private static final int cheeseIRow = 9;
    private static final int cheeseICol = 14;
    private static List<Node> path;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawMap(g2);
        for (Shape snapShape : GameController.getSnapShapeList()) {
            g2.setStroke(SNAP_SHAPE_STROKE);
            g2.setColor(SNAP_SHAPE_LINE_COLOR);
            g2.draw(snapShape);
        }
        for (Shape shape : GameController.getShapeList()) {
            g2.setColor(SHAPE_FILL_COLOR);
            g2.fill(shape);
            g2.setStroke(SHAPE_STROKE);
            g2.setColor(SHAPE_LINE_COLOR);
            g2.draw(shape);
        }
        drawPath(g2);
    }
    
    private void drawPath(Graphics2D g2) {
        for(Node node : path) {
            MyRectangle rect = mapRectList.get(get1DIndex(node.getRowIndex(), node.getColIndex()));
            g2.setColor(PATH_COLOR);
            g2.draw(rect);
        }
    }
    
    private int get1DIndex(int iRow, int iCol) {
        return iRow*N_MAP_COLS + iCol;
    }

    private void drawMap(Graphics2D g2) {
        for (MyRectangle rect : mapRectList) {
            if (rect.getPathType() == Map2D.OPEN) {
                g2.setColor(OPEN_PATH_COLOR);
            } else {
                g2.setColor(WALL_COLOR);
            }
            RectRowCol rc = getRowCol(rect);
            if (rc.rowIndex == cheeseIRow && rc.colIndex == cheeseICol) {
                g2.setColor(CHEESE_COLOR);
            }
            g2.fill(rect);
            g2.setColor(MAP_GRID_COLOR);
            g2.draw(rect);
        }
    }

    public static CanvasPanel create(int x, int y, int width, int height) {
        if (instance == null) {
            instance = new CanvasPanel(x, y, width, height);
        }        
        return instance;
    }

    private CanvasPanel(int x, int y, int width, int height) {
        super();
        setBounds(x, y, width, height);
        this.height = height;
        this.width = width;
        setLayout(null);
        setBackground(BACKGROUND_COLOR);
        createMap();
        updatePath();
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
    }

    private void createMap() {
        mapArray2D = Map2D.create(N_MAP_ROWS, N_MAP_COLS);
        int rectHeight = height / N_MAP_ROWS;
        int rectWidth = width / N_MAP_COLS;
        for (int iRow = 0; iRow < N_MAP_ROWS; iRow++) {
            for (int iCol = 0; iCol < N_MAP_COLS; iCol++) {
                MyRectangle rect = new MyRectangle(iCol * rectWidth, iRow * rectHeight, rectWidth, rectHeight, mapArray2D[iRow][iCol]);
                mapRectList.add(rect);
            }
        }
    }

    public static void resetMap() {
        for (int iRow = 0; iRow < N_MAP_ROWS; iRow++) {
            for (int iCol = 0; iCol < N_MAP_COLS; iCol++) {
                mapArray2D[iRow][iCol] = Map2D.OPEN;
                mapRectList.get(iRow * N_MAP_ROWS + iCol).setPathType(Map2D.OPEN);
            }
        }
        updatePath();
    }

    public static void updateMap(Shape shape) {
        for (MyRectangle rect : mapRectList) {
            if (rect.isInShape(shape)) {
                RectRowCol rectRowCol = getRowCol(rect);
                mapArray2D[rectRowCol.rowIndex][rectRowCol.colIndex] = Map2D.WALL;
                rect.setPathType(Map2D.WALL);
            }
        }
        updatePath();
        instance.repaint();
    }
    
    private static void updatePath() {
        Node startNode = new Node(null, 0, 0);
        Node endNode = new Node(null, cheeseIRow, cheeseICol);
        path = AStarPathFinder.calcPath(mapArray2D, startNode, endNode);
    }

    private static class RectRowCol {

        int rowIndex;
        int colIndex;
    }

    private static RectRowCol getRowCol(MyRectangle rect) {
        RectRowCol rectRowCol = new RectRowCol();
        int i1D = mapRectList.indexOf(rect);
        rectRowCol.colIndex = i1D % N_MAP_COLS;
        rectRowCol.rowIndex = (i1D - rectRowCol.colIndex) / N_MAP_COLS;
        return rectRowCol;
    }

    private class MyMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                GameController.setSelectedShape(evt.getX(), evt.getY());
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent evt) {
            if (mouseIsInCanvas(evt)) {
                GameController.moveShape(evt.getX(), evt.getY());
            }
            repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            GameController.deselectShape();
            repaint();
        }
    }

    private boolean mouseIsInCanvas(MouseEvent evt) {
        return evt.getX() >= this.getX() && evt.getX() <= this.getX() + this.getWidth()
                && evt.getY() >= this.getY() && evt.getY() <= this.getY() + this.getHeight();
    }

}

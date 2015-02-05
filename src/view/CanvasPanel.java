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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import model.Map2D;
import model.MouseThread;
import model.MyRectangle;
import model.Node;

/**
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class CanvasPanel extends JPanel {

    private static CanvasPanel instance;
    private static final Stroke SHAPE_STROKE = new BasicStroke(3f);
    private static final Stroke SNAP_SHAPE_STROKE = new BasicStroke(1f);
    private static final Color SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color SHAPE_FILL_COLOR = Color.BLUE;
    private static final Color SNAP_SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.GREEN;

    private static final Color OPEN_PATH_COLOR = BACKGROUND_COLOR;
    private static final Color WALL_COLOR = new Color(0, 0, 0, 0); //alpha = 0: Transparent, 255: Opaque
    private static final Color MAP_GRID_COLOR = Color.LIGHT_GRAY;
    private static final Color CHEESE_COLOR = Color.YELLOW;
    private static final Color PATH_COLOR = Color.ORANGE;
    private static final Color CURRENT_NODE_COLOR = Color.RED;

    public static final int CHEESE_IROW = 9;
    public static final int CHEESE_ICOL = 14;

    private static final int N_MOUSE_THREAD = 3;
    private static int counterThread = 0;
    private static final List<MouseThread> mouseThreadList = new ArrayList<>();

    public static void refreshDrawing() {
        if (instance != null) {
            instance.repaint();
        }
    }

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
        drawPaths(g2);
    }

    private void drawPaths(Graphics2D g2) {
        for (int i = 0; i < mouseThreadList.size(); i++) {
            MouseThread mouseThread = mouseThreadList.get(i);
            for (Node node : mouseThread.getPath()) {
                MyRectangle cell = MouseThread.getMapCellList().get(get1DIndex(node.getRowIndex(), node.getColIndex()));
                MouseThread.RectRowCol activePoint = mouseThread.getActivePoint();
                //System.out.println("i = " + i + ", path.size() = " + mouseThread.getPath().size() + ", activePoint.rowIndex = "
                //        + activePoint.rowIndex + ", colIndex = " + activePoint.colIndex);
                if (node.getRowIndex() == activePoint.rowIndex && node.getColIndex() == activePoint.colIndex) {
                    g2.setColor(CURRENT_NODE_COLOR);
                } else {
                    g2.setColor(PATH_COLOR);
                }
                g2.draw(cell);
            }
        }
    }

    private int get1DIndex(int iRow, int iCol) {
        return iRow * MouseThread.N_MAP_COLS + iCol;
    }

    private void drawMap(Graphics2D g2) {
        for (MyRectangle rect : MouseThread.getMapCellList()) {
            if (rect.getPathType() == Map2D.OPEN) {
                g2.setColor(OPEN_PATH_COLOR);
            } else {
                g2.setColor(WALL_COLOR);
            }
            MouseThread.RectRowCol rc = MouseThread.getRowCol(rect);
            if (rc.rowIndex == CHEESE_IROW && rc.colIndex == CHEESE_ICOL) {
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
    
    public static void updateMap(Shape shape) {
        MouseThread.updateMap(shape);
        List<MouseThread.RectRowCol> prevActivePointList = new ArrayList<>();
        for (MouseThread mouseThread : mouseThreadList) {
            MouseThread.RectRowCol prevActivePoint = mouseThread.getActivePoint();
            prevActivePointList.add(prevActivePoint);
            mouseThread.setKeepRunning(false); //kill thread
        }
        mouseThreadList.clear();
        for (int i=0; i < N_MOUSE_THREAD; i++) {
            MouseThread.RectRowCol prevActivePoint = prevActivePointList.get(i);
            MouseThread mouseThread = new MouseThread(counterThread++);
            mouseThreadList.add(mouseThread);
            mouseThread.setActivePoint(prevActivePoint);
            mouseThread.updatePath();
            mouseThread.start();
        }
        instance.repaint();
    }

    public static void resetMap() {
        MouseThread.resetMap();
        for (MouseThread mouseThread : mouseThreadList) {
            mouseThread.setKeepRunning(false); //kill thread
        }
        mouseThreadList.clear();
        for (int i = 0; i < N_MOUSE_THREAD; i++) {
            MouseThread mouseThread = new MouseThread(counterThread++);
            mouseThreadList.add(mouseThread);
            mouseThread.setActivePoint(0, i * 10); //TODO
            mouseThread.updatePath();
            mouseThread.start();
        }
        instance.repaint();
    }

    private static void createMouseList(int width, int height) {
        MouseThread.createMap(width, height);
        for (int i = 0; i < N_MOUSE_THREAD; i++) {
            MouseThread mt = new MouseThread(counterThread++);
            mouseThreadList.add(mt);
            mt.setActivePoint(0, i * 10); //TODO
            mt.updatePath();
            mt.start();
        }
    }

    private CanvasPanel(int x, int y, int width, int height) {
        super();
        createMouseList(width, height);
        setBounds(x, y, width, height);
        setLayout(null);
        setBackground(BACKGROUND_COLOR);
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
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

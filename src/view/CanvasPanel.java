package view;

import controller.GameController;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public static final int CHEESE_IROW = 24;//18;
    public static final int CHEESE_ICOL = 30;//14;

    private static final int N_MOUSE_THREAD = 3;
    private static int counterThread = 0;
    private static final List<MouseThread> mouseThreadList = new ArrayList<>();
    private static Image mouseImage;
    private static int mouseImageHalfWidth;
    private static int mouseImageHalfHeight;
    private static boolean isGameOver = false;
    private static final List<MouseThread.RectRowCol> startPointList = new ArrayList<>();

    public static void setGameOver(boolean inIsGameOver) {
        isGameOver = inIsGameOver;
    }

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
            for (Node pathNode : mouseThread.getPath()) {
                MyRectangle pathCell = MouseThread.getMapCellList().get(get1DIndex(pathNode.getRowIndex(), pathNode.getColIndex()));
                MouseThread.RectRowCol activePathPoint = mouseThread.getActivePoint();
                if (pathNode.getRowIndex() == activePathPoint.getRowIndex() && pathNode.getColIndex() == activePathPoint.getColIndex()) {
                    g2.setColor(CURRENT_NODE_COLOR);
                } else {
                    g2.setColor(PATH_COLOR);
                }
                g2.draw(pathCell);
            }
            Point ap = mouseThread.getActivePointXY();

            // Rotation information
            double locationX = mouseImageHalfWidth;
            double locationY = mouseImageHalfHeight;
            AffineTransform tx = AffineTransform.getRotateInstance(mouseThread.getImageRotation_rad(), locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

            // Drawing the rotated image at the required drawing locations
            g2.drawImage(op.filter(toBufferedImage(mouseImage), null), ap.x - mouseImageHalfWidth + MouseThread.getRectWidth() / 2,
                    ap.y - mouseImageHalfHeight + MouseThread.getRectHeight() / 2, null);
        }
    }

    /**
     * Converts a given Image into a BufferedImage.<br/>
     * Reference: http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
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
            if (rc.getRowIndex() == CHEESE_IROW && rc.getColIndex() == CHEESE_ICOL) {
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

    public static void pauseAllThreads() {
        MouseThread.setIsBusy(true);
    }

    public static void continueAllThreads() {
        MouseThread.setIsBusy(false);
        for (MouseThread mouseThread : mouseThreadList) {
            synchronized (mouseThread.getLock()) {
                mouseThread.getLock().notify();
            }
        }
    }

    private static void killAllThreads() {
        for (MouseThread mouseThread : mouseThreadList) {
            mouseThread.setKeepRunning(false); //kill thread
        }
    }

    public static void updateMapAndPaths(Shape shape) {
        killAllThreads();
        if (!GameController.isAllSnapped()) { //do not do the following if all is snapped because it causes the success message to lag
            MouseThread.updateMap(shape);
            List<MouseThread.RectRowCol> prevActivePointList = new ArrayList<>();
            List<Double> prevImageRotationList_rad = new ArrayList<>();
            for (MouseThread mouseThread : mouseThreadList) {
                MouseThread.RectRowCol prevActivePoint = mouseThread.getActivePoint();
                prevActivePointList.add(prevActivePoint);
                double prevImageRotation_rad = mouseThread.getImageRotation_rad();
                prevImageRotationList_rad.add(prevImageRotation_rad);
            }
            mouseThreadList.clear();
            for (int i = 0; i < N_MOUSE_THREAD; i++) {
                MouseThread mouseThread = new MouseThread(counterThread++);
                mouseThreadList.add(mouseThread);
                MouseThread.RectRowCol prevActivePoint = prevActivePointList.get(i);
                mouseThread.setActivePoint(prevActivePoint);
                double prevImageRotation_rad = prevImageRotationList_rad.get(i);
                mouseThread.setPrevImageRotation_rad(prevImageRotation_rad);

                mouseThread.updatePath();
                mouseThread.start();
            }
        }
        instance.repaint();
    }

    public static void onMouseReachedCheese() {
        for (MouseThread mouseThread : mouseThreadList) {
            mouseThread.setKeepRunning(false); //kill thread
        }
        setGameOver(true);
        GameView.setLevelFail();
    }

    public static void reset() {
        startPointList.clear();
        MouseThread.resetMap();
        for (MouseThread mouseThread : mouseThreadList) {
            mouseThread.setKeepRunning(false); //kill thread
        }
        mouseThreadList.clear();
        for (int i = 0; i < N_MOUSE_THREAD; i++) {
            MouseThread mouseThread = new MouseThread(counterThread++);
            mouseThreadList.add(mouseThread);
            mouseThread.setActivePoint(getRandomCell());
            mouseThread.updatePath();
            mouseThread.start();
        }
        instance.repaint();
    }

    private static boolean isInStartPointList(MouseThread.RectRowCol rc) {
        boolean isInStartPointList = false;
        for (MouseThread.RectRowCol rcInList : startPointList) {
            if (rcInList.getRowIndex() == rc.getRowIndex() && rcInList.getColIndex() == rc.getColIndex()) {
                isInStartPointList = true;
                break;
            }
        }
        return isInStartPointList;
    }

    private static MouseThread.RectRowCol getRandomCell() {
        MouseThread.RectRowCol rc;
        int radius = Math.min(CHEESE_IROW, CHEESE_ICOL);
        int x0 = CHEESE_ICOL;
        int y0 = CHEESE_IROW;
        int counter = 0;
        while (true) {
            double angle_rad = Math.toRadians(30 * new Random().nextInt(12));
            int iRow = y0 + (int) Math.round(radius * Math.cos(angle_rad));
            int iCol = x0 + (int) Math.round(radius * Math.sin(angle_rad));
            rc = new MouseThread.RectRowCol(iRow, iCol);
            if (!isInStartPointList(rc)) {
                startPointList.add(rc);
                break;
            }
            if (counter++ > 100) { //infinite loop prevention
                throw new RuntimeException("while loop taking too many iterations!");
            }
        }
        //rc = new MouseThread.RectRowCol(5, 5);
        //rc = new MouseThread.RectRowCol(35, 35);
        return rc;
    }

    private static void createMouseListAndStart(int width, int height) {
        MouseThread.createMap(width, height);
        for (int i = 0; i < N_MOUSE_THREAD; i++) {
            MouseThread mt = new MouseThread(counterThread++);
            mouseThreadList.add(mt);
            mt.setActivePoint(getRandomCell());
            mt.updatePath();
            mt.start();
        }
    }

    private CanvasPanel(int x, int y, int width, int height) {
        super();
        mouseImage = MouseThread.getMouseImage();
        mouseImageHalfWidth = mouseImage.getWidth(null) / 2;
        mouseImageHalfHeight = mouseImage.getHeight(null) / 2;
        createMouseListAndStart(width, height);
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
            if (!GameController.isPaused()) { //prevent puzzle piece movement when game is paused
                if (!isGameOver && mouseIsInCanvas(evt)) {
                    GameController.moveShape(evt.getX(), evt.getY());
                }
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

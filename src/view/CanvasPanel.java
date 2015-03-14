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
import javax.swing.JPanel;
import model.Map2D;
import model.MouseThread;
import model.MyRectangle;
import model.Node;

/**
 * Canvas on which map, cheese, puzzle pieces and mice are drawn. It is a component of GameView.
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

    private static Image mouseImage;
    private static int mouseImageHalfWidth;
    private static int mouseImageHalfHeight;

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
        for (int i = 0; i < GameController.getMouseThreadList().size(); i++) {
            MouseThread mouseThread = GameController.getMouseThreadList().get(i);
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
            if (rc.getRowIndex() == GameController.CHEESE_IROW && rc.getColIndex() == GameController.CHEESE_ICOL) {
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
    
    public static int getPanelWidth() {
        return instance.getBounds().width;
    }
    
    public static int getPanelHeight() {
        return instance.getBounds().height;
    }

    private CanvasPanel(int x, int y, int width, int height) {
        super();
        mouseImage = MouseThread.getMouseImage();
        mouseImageHalfWidth = mouseImage.getWidth(null) / 2;
        mouseImageHalfHeight = mouseImage.getHeight(null) / 2;
        GameController.createMouseListAndStart(width, height);
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
            //if (true) {
                if (!GameController.isGameOver() && mouseIsInCanvas(evt)) {
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

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
import model.Map2D;
import model.MyRectangle;

/**
 *
 * @author Samil Korkmaz
 */
public class CanvasPanel extends JPanel {

    private static final Stroke SHAPE_STROKE = new BasicStroke(3f);
    private static final Stroke SNAP_SHAPE_STROKE = new BasicStroke(1f);
    private static final Color SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color SHAPE_FILL_COLOR = Color.BLUE;
    private static final Color SNAP_SHAPE_LINE_COLOR = Color.BLACK;
    private static final Color SNAP_SHAPE_FILL_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = Color.GREEN;
    private int[][] mapArray2D;
    private final List<MyRectangle> mapRectList = new ArrayList<>();
    private static final Color OPEN_PATH_COLOR = BACKGROUND_COLOR;
    private static final Color WALL_COLOR = Color.BLACK;
    private static final Color MAP_GRID_COLOR = Color.LIGHT_GRAY;
    private final int height;
    private final int width;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawMap(g2);
        for (Shape snapShape : GameController.getSnapShapeList()) {
            g2.setColor(SNAP_SHAPE_FILL_COLOR);
            g2.fill(snapShape);
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
    }

    private void drawMap(Graphics2D g2) {
        for (MyRectangle rect : mapRectList) {
            if (rect.getPathType() == Map2D.OPEN_PATH) {
                g2.setColor(OPEN_PATH_COLOR);
            } else {
                g2.setColor(WALL_COLOR);
            }
            g2.fill(rect);
            g2.setColor(MAP_GRID_COLOR);
            g2.draw(rect);
        }
    }

    public CanvasPanel(int x, int y, int width, int height) {
        super();
        setBounds(x, y, width, height);
        this.height = height;
        this.width = width;
        setLayout(null);
        setBackground(BACKGROUND_COLOR);
        createMap();
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
    }

    private void createMap() {
        int nRows = 25;
        int nCols = 30;
        mapArray2D = Map2D.create(nRows, nCols);
        int rectHeight = height / nRows;
        int rectWidth = width / nCols;
        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                MyRectangle rect = new MyRectangle(iCol * rectWidth, iRow * rectHeight, rectWidth, rectHeight,
                        mapArray2D[iRow][iCol]);
                mapRectList.add(rect);
            }
        }
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
            GameController.toggleSelectedShape();
            repaint();
        }
    }

    private boolean mouseIsInCanvas(MouseEvent evt) {
        return evt.getX() >= this.getX() && evt.getX() <= this.getX() + this.getWidth()
                && evt.getY() >= this.getY() && evt.getY() <= this.getY() + this.getHeight();
    }

}

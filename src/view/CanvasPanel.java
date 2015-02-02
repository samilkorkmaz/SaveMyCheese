package view;

import controller.GameController;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import model.MyPolygon;

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
    private int prevMouseX;
    private int prevMouseY;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Shape snapShape : GameController.getSnapPolygonList()) {
            g2.setColor(SNAP_SHAPE_FILL_COLOR);
            g2.fill(snapShape);
            g2.setStroke(SNAP_SHAPE_STROKE);
            g2.setColor(SNAP_SHAPE_LINE_COLOR);
            g2.draw(snapShape);
        }
        for (Shape shape : GameController.getPolygonList()) {
            g2.setColor(SHAPE_FILL_COLOR);
            g2.fill(shape);
            g2.setStroke(SHAPE_STROKE);
            g2.setColor(SHAPE_LINE_COLOR);
            g2.draw(shape);
        }
    }

    public CanvasPanel() {
        super();
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
    }

    private class MyMouseAdapter extends MouseAdapter {

        private MyPolygon selectedPolygon = null;
        private MyPolygon selectedSnapPolygon = null;

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (GameController.getPolygonList().size() > 0) {
                    for (int i = GameController.getPolygonList().size() - 1; i >= 0; i--) {
                        if (GameController.getPolygonList().get(i).contains(evt.getPoint())) { //if there is a polygon at clicked point
                            selectedPolygon = GameController.getPolygonList().get(i);

                            //move the selected polygon to the end of list so that it will be drawn last (i.e. on top) in paintComponent and checked first for mouse click:
                            GameController.getPolygonList().remove(selectedPolygon);
                            GameController.getPolygonList().add(GameController.getPolygonList().size(), selectedPolygon);

                            selectedSnapPolygon = GameController.getSnapPolygonList().get(i);
                            GameController.getSnapPolygonList().remove(selectedSnapPolygon);
                            GameController.getSnapPolygonList().add(GameController.getSnapPolygonList().size(), selectedSnapPolygon);

                            prevMouseX = evt.getX();
                            prevMouseY = evt.getY();
                            repaint();
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent evt) {
            if (selectedPolygon != null) {
                if (mouseIsInCanvas(evt) && !selectedPolygon.isSnapped()) {
                    selectedPolygon.translate(evt.getX() - prevMouseX, evt.getY() - prevMouseY);
                    prevMouseX = evt.getX();
                    prevMouseY = evt.getY();
                    selectedPolygon.setIsSnapped(selectedPolygon.isCloseTo(selectedSnapPolygon));

                } else {
                    selectedPolygon = null;
                    selectedSnapPolygon = null;
                }
            }
            repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedPolygon != null) {
                repaint();
                selectedPolygon = null;
            }
        }
    }

    private boolean mouseIsInCanvas(MouseEvent evt) {
        return evt.getX() >= this.getX() && evt.getX() <= this.getX() + this.getWidth()
                && evt.getY() >= this.getY() && evt.getY() <= this.getY() + this.getHeight();
    }

}

package model;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Rectangle that knows if it is inside a given polygon.
 *
 * @author Samil Korkmaz
 * @date February 2015
 * @license Public Domain
 */
public class MyRectangle extends Rectangle {

    private final int pathType;
    
    public MyRectangle(int x, int y, int width, int height, int pathType) {
        super(x, y, width, height);
        this.pathType = pathType;
    }
    
    public int getPathType() {
        return pathType;
    }

    public boolean isInPolygon(Polygon polygon) {
        return polygon.contains(calcCenterPoint());
    }

    private Point calcCenterPoint() {
        return new Point(this.x + this.width / 2, this.y + this.height / 2);
    }
}

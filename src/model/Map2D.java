package model;

/**
 * Two dimensional map with rectangular cells.
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class Map2D {
    public static final int OPEN = 1;
    public static final int WALL = 0;
    
    public static int[][] create(int nRows, int nCols) {
        int[][] map = new int[nRows][nCols];
        for (int iRow = 0; iRow < nRows; iRow++) {
            for (int iCol = 0; iCol < nCols; iCol++) {
                map[iRow][iCol] = OPEN;
            }
        }
        return map;
    }

}

/*
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
package model;

import java.util.logging.Level;
import java.util.logging.Logger;
import view.CanvasPanel;

/**
 *
 * @author Samil Korkmaz
 * @date January 2015
 * @license Public Domain
 */
public class MouseThread extends Thread {
    
    private static final Object pathLock = new Object();
    private static Node currentNode;
    private volatile boolean keepRunning = true;
    
    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
    
    public MouseThread() {
        super();
        System.out.println("New mouse thread started.");
    }
    

    @Override
    public void run() {
        for (int i = CanvasPanel.getPath().size() - 1; i >= 0; i--) {
            if (!keepRunning) {
                System.out.println("Mouse thread ended.");
                break;
            }
            currentNode = CanvasPanel.getPath().get(i);
            CanvasPanel.setActivePoint(currentNode.getRowIndex(), currentNode.getColIndex());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MouseThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    

}

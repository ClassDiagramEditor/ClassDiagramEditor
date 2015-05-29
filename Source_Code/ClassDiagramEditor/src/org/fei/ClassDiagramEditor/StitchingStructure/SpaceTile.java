/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.StitchingStructure;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import org.fei.ClassDiagramEditor.drawing.Point;


/**
 *
 * @author Mr.Phoenixxx
 */
public class SpaceTile extends Tile implements Comparable<SpaceTile> {
    final private int MAX_DISTANCE = 1000000;   // Distance pre hľadanie voľných dlaždíc
    
    private int sourceDistance = MAX_DISTANCE;
    private int distance = MAX_DISTANCE;
    private Point nearestTilePoint = null;
    
    public SpaceTile(Point start, Point end) {
        super(start, end);
    }
    
    public void clearDistances() {
        sourceDistance = MAX_DISTANCE;
        distance = MAX_DISTANCE;  
        nearestTilePoint = null;
    }
    
    public int getSourceDistance() {
        return sourceDistance;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public Point getNearestTilePoint() {
        return nearestTilePoint;
    }
    
    public void setNearestTilePoint(Point nearestTilePoint) {
        this.nearestTilePoint = nearestTilePoint;
    }  
    
    public boolean isNewDistanceLower(int distance) {
        return (this.distance > distance);
    }
    
    public void setNewDistances(int sourceDistance, int distance, Point newNearestTilePoint) {
        this.sourceDistance = sourceDistance;
        this.distance = distance;
        this.nearestTilePoint = newNearestTilePoint;
    }   
   
    @Override
    public int compareTo(SpaceTile o) {
        if(distance == o.getDistance()){
            if(sourceDistance == o.getSourceDistance()) return 0;
            if(sourceDistance < o.getSourceDistance()) return -1;
                else return 1;
        }
        if(distance > o.getDistance()) return 1;
        else return -1;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        Random r = new Random();
        g2d.setColor(Color.getHSBColor(r.nextFloat(), r.nextFloat(), r.nextFloat()));
        g2d.fillRect(start.getX(), start.getY(), end.getX()-start.getX(), end.getY()-start.getY());
           
        // Draw sourceDistance & distance
        if(distance != MAX_DISTANCE) {
            Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            g2d.setFont(f);
            g2d.setColor(Color.red);
            g2d.drawString(String.valueOf(sourceDistance)+" | "+String.valueOf(distance), start.getX()+10, start.getY()+10);
        }
    } 
    
}

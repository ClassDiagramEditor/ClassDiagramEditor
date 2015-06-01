/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Router;

import java.awt.Graphics;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
/**
 *
 * @author Mr.Phoenixxx
 */
public class Route {
    private ArrayList<Point> waypoints = new ArrayList<Point>();
    
    private Class child;
    private Class parent;

    public Route(Class child, Class parent) {
        this.child = child;
        this.parent = parent;
    }
    
    public ArrayList<Point> addWayoint(Point waypoint) {
        waypoints.add(waypoint);
        return waypoints;
    }
    
    public ArrayList<Point> getWaypoints() {
        return waypoints;
    }
    
    public ArrayList<SubLine> generateSubLines() {
        ArrayList<SubLine> subLines = new ArrayList<SubLine>();
        for(int i=0; i<waypoints.size()-1; i++) {
            subLines.add(new SubLine(waypoints.get(i), waypoints.get(i+1), child, parent));   
        }
        return subLines;
    }
    
    public Point getLastWaypoint() {
        if(waypoints.isEmpty()) return null;
        return waypoints.get(waypoints.size()-1); 
    }
    
    public void makeOrthogonal(){
        int i=0;
        while(i < waypoints.size()-1) {    
            Point p1 = waypoints.get(i);
            Point p2 = waypoints.get(i+1);
            
            if(!p1.isInVerticalLine(p2)) { 
                waypoints.add(i+1, new Point(p1.getX(), p2.getY()));              
                i=i+2;
            }else {
                i++;
            }
        }
    }
        
    public SubLine setSubLineY(SubLine subLine, int Y) {
        Point p1 = new Point(subLine.getStart().getX(), Y);
        Point p2 = new Point(subLine.getEnd().getX(), Y);
        waypoints.add(waypoints.indexOf(subLine.getStart())+1, p1);
        waypoints.add(waypoints.indexOf(subLine.getEnd()), p2);  
        return new SubLine(p1, p2, child, parent);
    }
    
    public void draw(Graphics g) {
        for(SubLine subLine : generateSubLines()) {
            subLine.draw(g);
        }
    }  
}

package org.fei.ClassDiagramEditor.StitchingStructure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.org.apache.xpath.internal.functions.FuncBoolean;
import java.awt.Rectangle;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Router.SubLine;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;

/**
 *
 * @author Mr.Phoenixxx
 */
public class Tile {
    protected Point start;
    protected Point end;
    
    private SubLine topSubLine;
    private SubLine rightSubLine;  
    private SubLine bottomSubLine;
    private SubLine leftSubLine;  
    
    public Tile(Point start, Point end) {
        this.start = start;
        this.end = end;        
        topSubLine = new SubLine(new Point(start.getX(), start.getY()), new Point(end.getX(), start.getY()), null, null);
        rightSubLine = new SubLine(new Point(end.getX(), start.getY()), new Point(end.getX(), end.getY()), null, null);
        bottomSubLine = new SubLine(new Point(end.getX(), end.getY()), new Point(start.getX(), end.getY()), null, null);
        leftSubLine = new SubLine(new Point(start.getX(), end.getY()), new Point(start.getX(), start.getY()), null, null);
    } 
    
    public Point getStart() {
        return start;
    }
    
    public Point getEnd() {
        return end;
    }
    
    public Point getTopLeft() {
        return start;
    }
    
    public Point getTopRight() {
        return new Point(end.getX(), start.getY());
    }
    
    public Point getBottomLeft() {
        return new Point(start.getX(), end.getY());
    }
    public Point getBottomRight() {
        return end;
    }    
    
    public SubLine getTopSubLine() {
        return topSubLine;
    }
    
    public SubLine getRightSubLine() {
        return rightSubLine;
    }
    
    public SubLine getBottomSubLine() {
        return bottomSubLine;
    }
    
    public SubLine getLeftSubLine() {
        return leftSubLine;
    }
    
    public ArrayList<SubLine> getIntersectSubLines(Line line) {
        ArrayList<SubLine> subLines = new ArrayList<SubLine>();
        if(line.intersects(topSubLine)) subLines.add(topSubLine);
        if(line.intersects(rightSubLine)) subLines.add(rightSubLine);
        if(line.intersects(bottomSubLine)) subLines.add(bottomSubLine);
        if(line.intersects(leftSubLine)) subLines.add(leftSubLine);
        return subLines;
    }
    
    public Tile getInsideTile() {
        return new Tile(new Point(start.getX()+1, start.getY()+1), new Point(end.getX()-1, end.getY()-1));
    }
    
    public Rectangle getRectangleRepresentation() {
        return new Rectangle(start.getX(), start.getY(), getWidth(), getHeight());
    } 
    
    public boolean intersects(Line line) {
        return getRectangleRepresentation().intersectsLine(line.getLine2DRepresentation());
    }
    
    public boolean intersects(Tile tile) {        
        return tile.intersects(topSubLine) ||
               tile.intersects(rightSubLine) ||
               tile.intersects(bottomSubLine) ||
               tile.intersects(leftSubLine);
    }  
    
    public boolean contains(Point point) {
        if((point.getX() >= start.getX()) && (point.getX() <= end.getX()) && (point.getY() >= start.getY()) && (point.getY() <= end.getY()))
            return true;
        else
            return false;
    }
    
    public boolean contains(Tile tile) {
        return getRectangleRepresentation().contains(tile.getRectangleRepresentation());
    }
    
    public ArrayList<? extends Tile> findNeighboringTiles(ArrayList<? extends Tile> tiles) {
        ArrayList<Tile> neighboringTiles = new ArrayList<Tile>();
        for (Tile tile : tiles) {
            if(this == tile) continue;
            if (this.intersects(tile)) neighboringTiles.add(tile);
        }              
        return neighboringTiles;
    }
    
    // Nájde bod patriaci dlaždici, ktorý je najbližšie k zadanému bodu
    public Point findNewNearestTilePoint(Point point) {
        if ((start.getX() <= point.getX())&&(end.getX() >= point.getX())) {
            if(start.getY() >= point.getY()) return new Point(point.getX(), start.getY());
            else return new Point(point.getX(), end.getY()); 
        }
        
        if ((start.getY() <= point.getY())&&(end.getY() >= point.getY())) {
            if(start.getX() >= point.getX()) return new Point(start.getX(), point.getY());
            else return new Point(end.getX(), point.getY()); 
        } 
        
        if ((start.getX() >= point.getX())&&(start.getY() >= point.getY())) return new Point(start);
        if ((start.getX() <= point.getX())&&(start.getY() >= point.getY())) return new Point(end.getX(), start.getY());
        if ((end.getX() <= point.getX())&&(end.getY() <= point.getY())) return new Point(end);
        if ((start.getX() >= point.getX())&&(end.getY() <= point.getY())) return new Point(start.getX(), end.getY());
        
        return null;
    }
    
    public int getWidth() {
        return Math.abs(start.getX()-end.getX());
    }
    
    public int getHeight() {
        return Math.abs(start.getY()-end.getY());
    }
    
    @Override
    public boolean equals(Object obj) {
        Tile o = (Tile)obj;
        return this.contains(o);
    }
}

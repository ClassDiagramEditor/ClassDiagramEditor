/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.drawing.Lines;

import java.awt.Graphics;
import java.awt.geom.Line2D;
import org.fei.ClassDiagramEditor.Router.RouterService;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstrakna trieda pre vykreslovanie ciary.
 * 
 * @author Tomas & Mr.Phoenixxx
 */
abstract public class Line implements Cloneable {
    
    protected Point start;
    protected Point end;
    
    protected Class child;
    protected Class parent;
    
    protected static int fontSize = 12;

    public Line(Point start, Point end, Class child, Class parent) {
        this.start = start;
        this.end = end;
        this.child = child;
        this.parent = parent;
    }
    
    abstract public void draw(Graphics g);
    public void draw(Graphics g, RouterService routerService) {
        draw(g);
    };
    
    public static void setFontSize(int fontSize) {
        Line.fontSize = fontSize;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }
    
    public Line2D.Float getLine2DRepresentation() {
        return new Line2D.Float(start.getX(), start.getY(), end.getX(), end.getY());
    }
    
    public boolean intersects(Line line) {
        return getLine2DRepresentation().intersectsLine(line.getLine2DRepresentation());       
    }
    
    public Point findCommonPoint(Line line) {
        if(end.equals(line.getStart())) return new Point(end);
        if(end.equals(line.getEnd())) return new Point(end);
        if(start.equals(line.getStart())) return new Point(start);
        if(start.equals(line.getEnd())) return new Point(start);
        return null;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        Line o = (Line)obj;
        if(((o.getStart().equals(start))&&(o.getEnd().equals(end)))||((o.getStart().equals(end))&&(o.getEnd().equals(start)))) {
            return true;
        }else {
            return false;
        }
    }
    
    public boolean containsClass(Class c) {
        
        if (getChild() == null || getParent() == null)
            return false;
        
        if (getChild() == c || getParent() == c)
            return true;
        else
            return false;
    }

    /**
     * @return the child
     */
    public Class getChild() {
        return child;
    }

    /**
     * @return the parent
     */
    public Class getParent() {
        return parent;
    }
    
    public abstract Element xmiCreateRelationElement(Document document);
}

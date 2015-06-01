/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Router;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Sipka pre vztah dedicnosti medzi dvoma triedami. 
 * 
 * @author Tomas
 */
public class SubLine extends Line {

    public SubLine(Point start, Point end, Class chlid, Class parent) {
        super(start, end, chlid, parent);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        g2d.setColor(Color.black);
        g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }  

    @Override
    public Element xmiCreateRelationElement(Document document) {
        return null;
    }
}

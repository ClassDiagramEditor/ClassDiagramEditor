/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.StitchingStructure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Mr.Phoenixxx
 */
public class StitchingLine extends Line implements Comparable<StitchingLine> {

    public StitchingLine(Point start, Point end) {
        super(start, end, null, null);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        g2d.setColor(Color.ORANGE);
        g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    @Override
    public int compareTo(StitchingLine o) {
        if (start.getY() == o.getStart().getY()) {
            return 0;
        }else{
            if(start.getY() > o.getStart().getY()) return 1;
                else return -1;
        }
    }

    @Override
    public Element xmiCreateRelationElement(Document document) {
        return null;
    }
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.drawing.Lines;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.fei.ClassDiagramEditor.Router.Route;
import org.fei.ClassDiagramEditor.Router.RouterService;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sipka pre vztah dedicnosti medzi dvoma triedami. 
 * 
 * @author Tomas
 */
public class InheritanceArrow extends Line {

    public InheritanceArrow(Point start, Point end, Class child, Class parent) {
        super(start, end, child, parent);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }
    
    @Override
    public void draw(Graphics g, RouterService routerService) {              
        Route route = routerService.createRoute(this);        
        route.draw(g);    
    }

    @Override
    public Element xmiCreateRelationElement(Document document) {
    
        Element generalization = document.createElement("UML:Generalization");
        Element childElement = document.createElement("UML:Generalization.child");
        Element parentElement = document.createElement("UML:Generalization.parent");
        Element class1 = document.createElement("UML:Class");
        Element class2 = document.createElement("UML:Class");
        
        generalization.setAttribute("xmi.id", XmiIdFactory.getId());
        generalization.setAttribute("isSpecification", "false");
        
        class1.setAttribute("xmi.idref", this.child.getClassData().getXmiid());
        childElement.appendChild(class1);
        
        class2.setAttribute("xmi.idref", this.parent.getClassData().getXmiid());
        parentElement.appendChild(class2);
        
        generalization.appendChild(childElement);
        generalization.appendChild(parentElement);
        
        return generalization;
    }
    
}

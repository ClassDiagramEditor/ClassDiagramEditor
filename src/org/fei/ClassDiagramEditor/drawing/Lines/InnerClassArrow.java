/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.drawing.Lines;

import org.fei.ClassDiagramEditor.drawing.Lines.Line;
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
 * Operator ktory znazornuje vztah medzi vnorenou triedou a vonkajsou triedou z pohladu vnorenej.
 * 
 * @author Tomas
 */
public class InnerClassArrow extends Line {

    public InnerClassArrow(Point start, Point end, Class child, Class parent) {
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
                
        Element class1 = document.createElement("UML:Class");
        Element class2 = document.createElement("UML:Class");
        Element participant1 = document.createElement("UML:AssociationEnd.participant");
        Element participant2 = document.createElement("UML:AssociationEnd.participant");
        Element end1 = document.createElement("UML:AssociationEnd");
        Element end2 = document.createElement("UML:AssociationEnd");
        Element connection = document.createElement("UML:Association.connection");
        Element association = document.createElement("UML:Association");
        
        association.setAttribute("xmi.id", XmiIdFactory.getId());
        association.setAttribute("name", "inner class");
        association.setAttribute("isSpecification", "false");
        association.setAttribute("isAbstract", "false");
        
        // child
        end1.setAttribute("xmi.id", XmiIdFactory.getId());
        end1.setAttribute("visibility", "public");
        end1.setAttribute("isSpecification", "false");
        end1.setAttribute("isNavigable", "false");
        end1.setAttribute("ordering", "unordered");
        end1.setAttribute("aggregation", "none");
        end1.setAttribute("targetScope", "instance");
        end1.setAttribute("changeability", "changeable");
        
        class1.setAttribute("xmi.idref", this.child.getClassData().getXmiid());
        participant1.appendChild(class1);
        end1.appendChild(participant1);
        
        // parent
        end2.setAttribute("xmi.id", XmiIdFactory.getId());
        end2.setAttribute("visibility", "public");
        end2.setAttribute("isSpecification", "false");
        end2.setAttribute("isNavigable", "false");
        end2.setAttribute("ordering", "unordered");
        end2.setAttribute("aggregation", "none");
        end2.setAttribute("targetScope", "instance");
        end2.setAttribute("changeability", "changeable");
        
        class2.setAttribute("xmi.idref", this.parent.getClassData().getXmiid());
        participant2.appendChild(class2);
        end2.appendChild(participant2);
        
        connection.appendChild(end1);
        connection.appendChild(end2);
        
        association.appendChild(connection);
        
        return association;
    }
}

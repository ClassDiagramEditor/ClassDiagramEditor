/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.drawing.Lines;

import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import org.fei.ClassDiagramEditor.Router.Route;
import org.fei.ClassDiagramEditor.Router.RouterService;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sipka ktora znazornuje vztah medzi triedou a rozhrahnim, ktore dana trieda implementuje.
 * @author Tomas
 */
public class InterfaceArrow extends Line {

    public InterfaceArrow(Point start, Point end, Class child, Class parent) {
        super(start, end, child, parent);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        Stroke old = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        g2d.setStroke(old);
    }
    
    @Override
    public void draw(Graphics g, RouterService routerService) {        
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        Stroke old = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        
        Route route = routerService.createRoute(this);        
        route.draw(g);    
        
        g2d.setStroke(old);
    }

    /*
            <UML:Abstraction xmi.id = '-64--88-1-118-2eb09b07:144bb29716a:-8000:0000000000000868'
          isSpecification = 'false'>
          <UML:Dependency.client>
            <UML:Class xmi.idref = '-64--88-1-118-2eb09b07:144bb29716a:-8000:0000000000000866'/>
          </UML:Dependency.client>
          <UML:Dependency.supplier>
            <UML:Interface xmi.idref = '-64--88-1-118-2eb09b07:144bb29716a:-8000:0000000000000867'/>
          </UML:Dependency.supplier>
        </UML:Abstraction>
    */
    @Override
    public Element xmiCreateRelationElement(Document document) {
               
        Element abstraction = document.createElement("UML:Abstraction");
        Element client = document.createElement("UML:Dependency.client");
        Element supplier = document.createElement("UML:Dependency.supplier");
        Element class1 = document.createElement("UML:Class");
        Element class2 = document.createElement("UML:Class");
        
        abstraction.setAttribute("xmi.id", XmiIdFactory.getId());
        abstraction.setAttribute("isSpecification", "false");
        
        class1.setAttribute("xmi.idref", this.child.getClassData().getXmiid());
        client.appendChild(class1);
        
        class2.setAttribute("xmi.idref", this.parent.getClassData().getXmiid());
        supplier.appendChild(class2);
        
        abstraction.appendChild(client);
        abstraction.appendChild(supplier);
        
        return abstraction;
    }
}
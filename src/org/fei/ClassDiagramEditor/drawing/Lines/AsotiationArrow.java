package org.fei.ClassDiagramEditor.drawing.Lines;

import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.VariableData;
import org.fei.ClassDiagramEditor.Router.Route;
import org.fei.ClassDiagramEditor.Router.RouterService;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.fei.ClassDiagramEditor.drawing.Point;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sipka pre vzhtah asocie medzi dvoma triedami
 * 
 * @author Tomas
 */
public class AsotiationArrow extends Line {
    private VariableData var;
    // premenna zavedena koli asociaciam, pocita kolko sa ich vztahuje k danej triede
    // na zaklade toho ich mozem vykreslovat pod sebou
    private int usages;
    private boolean isFirst;
    private ClassData classData;
    
    // na zobrazenie asociacii
    private static boolean showAsociationName = true;
    public AsotiationArrow(ClassData classData, Point start, Point end, VariableData var, int usages, boolean isFirst, Class child, Class parent) {
        super(start, end, child, parent);
        this.var = var;
        this.usages = usages;
        this.isFirst = isFirst;
        this.classData = classData;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        if (AsotiationArrow.showAsociationName) {
            String name = new String();
            name += var.accessToString();
            name += var.getName();
            name += " {" + classData.getFullName() + "}";
            Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
            g2d.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            g2d.drawString(name, end.getX()+ 3, end.getY() + 3 + (fm.getHeight()*this.usages-1));
        }
        
        if (isFirst)
            g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    @Override
    public void draw(Graphics g, RouterService routerService) {    
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        
        if (AsotiationArrow.showAsociationName) {
            String name = new String();
            name += var.accessToString();
            name += var.getName();
            name += " {" + classData.getFullName() + "}";
            Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
            g2d.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            g2d.drawString(name, end.getX()+ 3, end.getY() + 3 + (fm.getHeight()*this.usages-1));
        }
        
        //if (isFirst) {
            Route route = routerService.createRoute(this);        
            route.draw(g);    
        //}
    }
    
    public static void setShowAsociationName(boolean showAsociationName) {
        AsotiationArrow.showAsociationName = showAsociationName;
    }

    /**
     * @return the var
     */
    public VariableData getVariableData() {
        return var;
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
        association.setAttribute("name", this.var.getName());
        association.setAttribute("isSpecification", "false");
        association.setAttribute("isAbstract", "false");
        
        // child
        end1.setAttribute("xmi.id", XmiIdFactory.getId());
        end1.setAttribute("visibility", var.accessToFullString());
        end1.setAttribute("isSpecification", "false");
        end1.setAttribute("isNavigable", "false");
        end1.setAttribute("ordering", "unordered");
        end1.setAttribute("aggregation", "none");
        end1.setAttribute("targetScope", var.isStaticMember() ? "classifier" : "instance");
        end1.setAttribute("changeability", "changeable");
        
        class1.setAttribute("xmi.idref", this.child.getClassData().getXmiid());
        participant1.appendChild(class1);
        end1.appendChild(participant1);
        
        // parent
        end2.setAttribute("xmi.id", XmiIdFactory.getId());
        end2.setAttribute("visibility", var.accessToFullString());
        end2.setAttribute("isSpecification", "false");
        end2.setAttribute("isNavigable", "true");
        end2.setAttribute("ordering", "unordered");
        end2.setAttribute("aggregation", "none");
        end2.setAttribute("targetScope", var.isStaticMember() ? "classifier" : "instance");
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
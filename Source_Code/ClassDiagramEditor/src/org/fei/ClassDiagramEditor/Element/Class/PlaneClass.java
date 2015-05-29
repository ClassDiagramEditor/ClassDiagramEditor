/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;

/**
 * Obycajne a abstrakne triedy dedia z tejto triedy lebo nemaju nad nazvom nic.
 * 
 * @author Tomas
 */
public abstract class PlaneClass extends Class{
    
    public PlaneClass(int cornerX, int cornerY, ClassData classData, ArrayList<MethodLook> methodLook, ArrayList<VariableLook> variableLook) {
        super(cornerX, cornerY, 0, 0, classData, methodLook, variableLook, false, false);
    }
    /*
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        //->
        Font f = this.setClassNameFont();
        g2d.setFont(f);
        
        FontMetrics fm = g.getFontMetrics();
        // zistenie max sirky
        int maxWidth = fm.stringWidth(classFile.getName().toString());
        // potupne naratavanie vysky
        int actualHeight = 18;
        //->
        // pozicia pre nazov triedy
        int namePosition = actualHeight;
        
        actualHeight += fm.getAscent()/2 + 2;
        int nameEnd = actualHeight;
        actualHeight += 14;
//->
        for (VariableLook v : variables) {
            v.draw(g2d, cornerX + 5, cornerY + actualHeight, false, fontSize);
            maxWidth = (maxWidth < v.getStringWidth()) ? v.getStringWidth() : maxWidth;
            actualHeight += v.getStringHeight();
        }
        actualHeight -= fm.getAscent()/2;
        int varEnd = actualHeight;
        actualHeight += 14;
        for (MethodLook m : methods) {
            m.draw(g2d, cornerX + 5, cornerY + actualHeight, false, fontSize);
            maxWidth = (maxWidth < m.getStringWidth()) ? m.getStringWidth() : maxWidth;
            actualHeight += m.getStringHeight();
        }
        //->
        actualHeight -= fm.getAscent()/2;
        // nastavenie sirky vysky obdlznika
        width = maxWidth + 10;
        height = actualHeight;
        // nastavenie bodov na spajanie tried
        this.topPoint.setX(cornerX + width/2);
        this.topPoint.setY(cornerY);
        this.bottomPoint.setX(topPoint.getX());
        this.bottomPoint.setY(cornerY + height);
        this.rightPoint.setX(cornerX + width);
        this.rightPoint.setY(cornerY + height/2);
        this.leftPoint.setX(cornerX);
        this.leftPoint.setY(rightPoint.getY());
        // ak je rodicovska trieda vykresli sa sipocka
        if (this.isSuperclass)
            this.drawInheritanceArrow(g2d);
        
        // ak je tato trieda vo vztahu asociacie vykresli sa sipka
        if (this.isAsotiationClass)
            this.drawLeftAnotationArrow(g2d);
        // obdlznik triedy
        g2d.drawRect(cornerX, cornerY, width, height);
        // meno triedy v strede
        f = this.setClassNameFont();
        g2d.setFont(f);
        g2d.drawString(classFile.getName().toString(), cornerX+width/2 - fm.stringWidth(classFile.getName().toString())/2, cornerY+namePosition);
        // oddelenie mena
        g2d.drawLine(cornerX, cornerY + nameEnd, cornerX + width, cornerY + nameEnd);
        // oddelenie atributov od metod
        g2d.drawLine(cornerX, cornerY + varEnd, cornerX + width, cornerY + varEnd);
    }*/

    @Override
    protected int getClassNameWidth(Graphics g, String className) {
        Font f = this.setClassNameFont();
        g.setFont(f);
        
        FontMetrics fm = g.getFontMetrics();
        String name = className + this.generic;
        // zistenie max sirky
        return fm.stringWidth(name);
    }

    @Override
    protected int setLabelNamePosition(int actualHeight, FontMetrics fm) {
        namePosition = actualHeight;   
        actualHeight += fm.getAscent()/2 + 2;
        nameEnd = actualHeight;
        actualHeight += 14;
        
        return actualHeight;
    }
    
    abstract protected Font setClassNameFont();
    
    
}

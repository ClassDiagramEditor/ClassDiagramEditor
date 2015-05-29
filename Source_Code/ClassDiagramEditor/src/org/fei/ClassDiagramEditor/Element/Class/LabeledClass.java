/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;

/**
 * Rozhrania a enum dedia od tejto triedy, lebo maju nad nazvom tzv. "label". 
 * 
 * @author Tomas
 */
public class LabeledClass extends Class {

    private String label;
    private boolean isEnum;
    private boolean isInterface;
    
    public LabeledClass(int cornerX, int cornerY, ClassData classData, ArrayList<MethodLook> methodLook, ArrayList<VariableLook> variableLook, String label, boolean isEnum, boolean isInterface) {
        super(cornerX, cornerY, 0, 0, classData, methodLook, variableLook, isEnum, isInterface);
        this.label = label;
        this.isEnum = isEnum;
        this.isInterface = isInterface;
    }
    /*
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        // ->
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
        g2d.setFont(f);
        
        FontMetrics fm = g.getFontMetrics();
        // zistenie max sirky
        int maxWidth = fm.stringWidth(classFile.getName().toString());
        maxWidth = (maxWidth < fm.stringWidth(label)) ? fm.stringWidth(label) : maxWidth;
        // potupne naratavanie vysky
        int actualHeight = 18;
        //->
        // pozicia pre nazov triedy
        int labelPosition = actualHeight;
        actualHeight += fm.getAscent();
        int namePosition = actualHeight;
        actualHeight += fm.getAscent()/2 + 2;
        int nameEnd = actualHeight;
        actualHeight += 14;
        //->
        for (VariableLook v : variables) {
            v.draw(g2d, cornerX + 5, cornerY + actualHeight, isEnum, fontSize);
            maxWidth = (maxWidth < v.getStringWidth()) ? v.getStringWidth() : maxWidth;
            actualHeight += v.getStringHeight();
        }
        //->
        actualHeight -= fm.getAscent()/2;
        int varEnd = actualHeight;
        actualHeight += 14;
        for (MethodLook m : methods) {
            m.draw(g2d, cornerX + 5, cornerY + actualHeight, isInterface, fontSize);
            maxWidth = (maxWidth < m.getStringWidth()) ? m.getStringWidth() : maxWidth;
            actualHeight += m.getStringHeight();
        }
        actualHeight -= fm.getAscent()/2;
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
        // label pre triedu
        f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        g2d.setFont(f);
        g2d.drawString(label, cornerX+width/2 - fm.stringWidth(label)/2, cornerY+labelPosition);
        // meno triedy v strede
        f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
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
        int maxWidth = fm.stringWidth(name);
        return (maxWidth < fm.stringWidth(label)) ? fm.stringWidth(label) : maxWidth;
    }

    @Override
    protected Font setClassNameFont() {
        return new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
    }

    @Override
    protected int setLabelNamePosition(int actualHeight, FontMetrics fm) {
        labelPosition = actualHeight;
        actualHeight += fm.getAscent();
        namePosition = actualHeight;
        actualHeight += fm.getAscent()/2 + 2;
        nameEnd = actualHeight;
        actualHeight += 14;
        
        return actualHeight;
    }

    @Override
    protected void drawLabel(Graphics2D g2d) {     
        // label pre triedu
        Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setFont(f);
        g2d.drawString(label, cornerX+width/2 - fm.stringWidth(label)/2, cornerY+labelPosition);
    }
}

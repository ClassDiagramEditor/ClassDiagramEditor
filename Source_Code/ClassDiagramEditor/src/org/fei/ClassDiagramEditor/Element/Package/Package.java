/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Package;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSelectListener;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.WindowComponents.Diagram;

/**
 * Reprezentácia balíku
 * 
 * @author Mr.Phoenixxx
 */
public class Package implements PackageSelectListener, PackageSafeDeleteListener {
    
    private PackageData data;
    
    private int x;
    private int y;
    private int width;
    private int height;
    private Color classesBackgroundColor;
    
    private int min_width = 215;
    private int min_height = 50;
    private int padding = 5;
    private int fontSize = 12;
    
    private Rectangle labelRectangle;
    private Rectangle resizerRectangle;
    private Rectangle packageRectangle;
    
    private ArrayList<Class> classes;
    private ArrayList<Package> subPackages;
    private Package parent;
    private boolean assigned = false; // urcuje nam ci pri modifikacii zdrojovych suborou bola nova trieda priradena k starej
    
    private boolean isClicked = false;
     

    public Package(PackageData data) {
        this.data = data;
        this.classes = new ArrayList<Class>();
        this.subPackages = new ArrayList<Package>();
        this.parent = null;
        this.classesBackgroundColor = null;
        this.packageRectangle = null;
        RefactoringEvents.addPackageSafeDeleteListener(this);
    }
    
    public Package(PackageData data, Color classesBackgroundColor) {
        this.data = data;
        this.classes = new ArrayList<Class>();
        this.subPackages = new ArrayList<Package>();
        this.parent = null;
        this.classesBackgroundColor = classesBackgroundColor;
        this.packageRectangle = null;
        RefactoringEvents.addPackageSafeDeleteListener(this);
    }
    
    public void setMetrics(int x, int y, int width, int height) {
        this.setPosition(x, y);
        this.width = width;
        this.height = height;
    }
    
    public void setPosition(int x, int y) {
        int deltaX = x - this.x;
        int deltaY = y - this.y;
        
        for (Class classObj : this.getClasses()) {
            if(this.contains(classObj))
                classObj.setPosition(classObj.getCornerX()+deltaX, classObj.getCornerY()+deltaY);
        }
        
        for (Package packageObj : this.subPackages) {      
            if(this.contains(packageObj)) 
                packageObj.setPosition(packageObj.getX()+deltaX, packageObj.getY()+deltaY);
        }
        
        this.x = x;
        this.y = y;  
    }
    
    public void setPositionOnlyPackage(int x, int y){
        this.x = x;
        this.y = y;  
    }
    
    public void setParent(Package parent) {
        this.parent = parent;
        if (parent != null)
            this.data.setParent(parent.getData());
    }
    
    public Package getParent() {
        return this.parent;
    }
    
    public ArrayList<Package> addSubPackage(Package subPackage) {
        if (this.subPackages == null) this.subPackages = new ArrayList<Package>();
        this.subPackages.add(subPackage);
        this.data.addSubPackage(subPackage.getData());
        return this.subPackages;
    }
    
    public ArrayList<Package> getSubPackages() {
        return this.subPackages;
    }
    
    public ArrayList<Class> addClass(Class c) {
        c.setBackgroundColor(this.getClassesBackgroundColor());
        this.getClasses().add(c);
        this.data.addClass(c.getClassData());
        return this.getClasses();
    }
    
    public ArrayList<Class> getClasses() {
        return this.classes;
    }
    
    public String getName() {
        return data.getName();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void draw(Graphics g, ArrayList<Package> packages, ArrayList<Class> allClasses,  boolean includeClasses) {
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh); 
        
        this.createLabelRectangle(g);
        this.createResizerRectangle();
        this.packageRectangle = new Rectangle(x, y, width, height);
               
        Color previousColor = g2d.getColor();
        if (Diagram.coloredDiagram) g2d.setColor(this.getClassesBackgroundColor());
            else g2d.setColor(new Color(247, 247, 238));
        
                // bolo na triedu kliknute
        if (this.isClicked)
            g2d.setStroke(new BasicStroke(3));
        else
            g2d.setStroke(new BasicStroke(1));
        
        g.fillRoundRect(x, y, width, height, 10, 10);    
        g.fillRect(labelRectangle.x, labelRectangle.y, labelRectangle.width, labelRectangle.height);   
       
        g2d.setPaint(new Color(120, 120, 120));
        g2d.drawRoundRect(x, y, width, height, 10, 10);    
        g2d.drawRect(labelRectangle.x, labelRectangle.y, labelRectangle.width, labelRectangle.height);
        g2d.drawRect(resizerRectangle.x, resizerRectangle.y, resizerRectangle.width, resizerRectangle.height);  // Resizer        
        g2d.setStroke(new BasicStroke(1));
        
        g2d.setPaint(previousColor);     
        Font oldFont = g.getFont();
        
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        g.drawString(data.getName(), x + 10, y - 10);
        
        g.setFont(oldFont);
    
        if (includeClasses) {
            for (Class classObj : getClasses()) {
                classObj.draw(g, allClasses);
            }
        }
        
        for (Package subPackage : subPackages) {
            subPackage.draw(g, packages, allClasses, includeClasses);
        }
    }
    
    private void createLabelRectangle(Graphics g) {    
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(this.data.getName());
        this.min_width = stringWidth + 20;
        this.labelRectangle = new Rectangle(this.x + 3, this.y - 25, stringWidth + 15, 25);
    }
    
    private void createResizerRectangle() {
        this.resizerRectangle = new Rectangle(this.x+this.width-10, this.y+this.height-10, 10, 10);
    } 
    
    public boolean isOverLabel(int x, int y) {
        return this.labelRectangle.contains(x, y);   
    }
    
    public boolean isOverResizer(int x, int y) {
        return this.resizerRectangle.contains(x, y);
    }
    
    public boolean isOverPackage(int x, int y) {
        if (packageRectangle != null) {
            for (Package p : this.subPackages) {
                if (p.isOverPackage(x, y)) {
                    return false;
                }
            }
            return this.packageRectangle.contains(x, y);
        }
        return false;
    }
    
    public Rectangle getRectangleRepresentation() {
        return new Rectangle(x, y, width, height);
    } 
    
    public boolean intersects(ArrayList<Package> packages) {
        for (Package p : packages) {
                if(this == p) continue;
                if(p.getRectangleRepresentation().intersects(getRectangleRepresentation())) return true;
            }
        return false;
    }
    
    public boolean intersects(Package packageObj) {
        return packageObj.getRectangleRepresentation().intersects(getRectangleRepresentation());
    }
    
    public boolean intersects(Class classObj) {
        return classObj.getRectangleRepresentation().intersects(getRectangleRepresentation());
    }
    
    public boolean contains(Class classObj) {
        return getRectangleRepresentation().contains(classObj.getRectangleRepresentation());
    }
    
    public boolean contains(Package packageObj) {
        return getRectangleRepresentation().contains(packageObj.getRectangleRepresentation());
    }   

    public boolean resize(int width, int height) {
        if((this.min_width > width)||(this.min_height > height)) return false;
        
        //Pokus o posunutie prvkov v balíku
        Rectangle rectangleRep = this.getRectangleRepresentation();
        rectangleRep.grow(-padding, -padding);
        
        ArrayList<Class> insidePackageClasses = this.getInsidePackageClasses(rectangleRep);
        ArrayList<Package> insideSubPackages = this.getInsideSubPackages(rectangleRep);
        Rectangle rect = new Rectangle(this.x, this.y, width-padding, height-padding);
        
        if((this.getInsidePackageClasses(rect).size() < insidePackageClasses.size())
          ||(this.getInsideSubPackages(rect).size() < insideSubPackages.size())) {    
            rect = new Rectangle(this.x+(this.width-width)+padding, this.y+(this.height-height)+padding, width-padding, height-padding);
            if((this.getInsidePackageClasses(rect).size() == insidePackageClasses.size())
              &&(this.getInsideSubPackages(rect).size() == insideSubPackages.size())) {
                this.moveInsideElements(width-this.width, height-this.height);
            }
        }
         
        this.width = width;
        this.height = height;
        return true;
    }
    
    private ArrayList<Class> getInsidePackageClasses(Rectangle rectangle) {
        ArrayList<Class> insideClasses = new ArrayList<Class>();
        for (Class classObj : getClasses()) {
            if(rectangle.contains(classObj.getRectangleRepresentation())) insideClasses.add(classObj);
        }
        return insideClasses;
    }
    
    private ArrayList<Package> getInsideSubPackages(Rectangle rectangle) {
        ArrayList<Package> insideSubPackages = new ArrayList<Package>();
        for (Package packageObj : subPackages) {
            if(rectangle.contains(packageObj.getRectangleRepresentation())) insideSubPackages.add(packageObj);
        }
        return insideSubPackages;
    }
    
    private void moveInsideElements(int deltaX, int deltaY) {
        for (Class classObj : getClasses()) {
            if(this.contains(classObj)) classObj.setPosition(classObj.getCornerX()+deltaX, classObj.getCornerY()+deltaY);
        }
        for (Package packageObj : subPackages) {
            if(this.contains(packageObj)) packageObj.setPosition(packageObj.getX()+deltaX, packageObj.getY()+deltaY);
        }     
    }

    @Override
    public void packageSelect(PackageSelectEvent e) {
        if ((Package)e.getSource() == this)
            isClicked = true;
        else
            isClicked = false;
    }

    @Override
    public void packageUnselect() {
        isClicked = false;
    }

    /**
     * @return the data
     */
    public PackageData getData() {
        return data;
    }

    @Override
    public void onSafePackageDelete(PackageSelectEvent e) {
                
        Package pcgToRemove = null;
        
        for (Package p : subPackages) {
            if (p.getData() == e.getPackageData()) {
                pcgToRemove = p;     
            }
        }
        
        if (pcgToRemove != null) {
            subPackages.remove(pcgToRemove);           
        }
    }

    /**
     * @return the classesBackgroundColor
     */
    public Color getClassesBackgroundColor() {
        return classesBackgroundColor;
    }

    public void setClassesBackgroundColor(Color classesBackgroundColor) {
        this.classesBackgroundColor = classesBackgroundColor;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
    
}

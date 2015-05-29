/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

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
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Editor.events.ClassAttributeEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassMethodEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddMethodListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAttributeSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassChangeAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodChangeDeclarationListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSelectListener;
import org.fei.ClassDiagramEditor.Element.Package.Package;
import org.fei.ClassDiagramEditor.StitchingStructure.SolidTile;
import org.fei.ClassDiagramEditor.WindowComponents.Diagram;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;
import org.fei.ClassDiagramEditor.drawing.Point;

/**
 * Abstraknta trieda ktora zdruzuje vsetky rovnake vlastnosti pre vykreslovane
 * objekty (triedy, rozhrania...) diagramu tried.
 *
 * @author Tomas
 */
abstract public class Class implements ClassSelectListener, ClassMethodSafeDeleteListener,
        ClassAttributeSafeDeleteListener, ClassMethodChangeDeclarationListener, 
        ClassAddAttributeListener, ClassAddMethodListener, ClassChangeAttributeListener {

    final private int solidTilePadding = 8;
    protected SolidTile solidTile;
    protected ArrayList<MethodLook> methods;
    protected ArrayList<VariableLook> variables;
    protected ArrayList<Class> childrens;
    protected ArrayList<Class> innerClasses;
    protected ArrayList<Class> asotiationClasses;
    protected ArrayList<Class> asotiationChilds;
    private Class parent;
    protected ClassData classData;
    protected Package classPackage = null;
    protected Point topPoint;
    protected Point bottomPoint;
    protected Point rightPoint;
    protected Point leftPoint;
    protected Point innerClassesPoint;
    protected boolean isClicked;    // ak bola trieda oznacena kliknutim mysi, vykresli sa hrubsou ciarou
    protected int cornerX;
    protected int cornerY;
    protected int width;
    protected int height;
    protected int labelPosition;
    protected int namePosition;
    protected int varEnd;
    protected int nameEnd;
    private boolean assigned = false; // urcuje nam ci pri modifikacii zdrojovych suborou bola nova trieda priradena k starej
    private Color backgroundColor;
    protected static int fontSize = 12;
    protected boolean isSuperclass = false;
    protected boolean isAsotiationClass = false;
    protected boolean hasInnerClasses = false;
    protected boolean isAsociationChild = false;
    private boolean isInncerClassChild = false;
    private boolean isEnum;
    private boolean isInterface;
    //private String className;
    // premenna zavedena koli asociaciam, pocita kolko sa ich vztahuje k danej triede
    // na zaklade toho ich mozem vykreslovat pod sebou
    private int numberOfAsotiation = 0;
    // pomocne premenne pre h-layout
    //private int level;
    private int position;
    // premenne pre checkboxy
    private static boolean showAttributes = false;
    private static boolean showConstructors = false;
    // ak je trieda genericka zobrazi sa k nazvu aj vycet parametrov
    protected String generic = new String();

    public Class(int cornerX, int cornerY, int width, int height, ClassData classData, ArrayList<MethodLook> methodLook, ArrayList<VariableLook> variableLook, boolean isEnum, boolean isInterface) {
        this.cornerX = cornerX;
        this.cornerY = cornerY;
        this.width = width;
        this.height = height;
        //this.fontSize = 12;
        topPoint = new Point();
        bottomPoint = new Point();
        rightPoint = new Point();
        leftPoint = new Point();
        innerClassesPoint = new Point();
        this.methods = methodLook;
        this.variables = variableLook;
        this.classData = classData;

        this.isEnum = isEnum;
        this.isInterface = isInterface;

        this.parseGeneric();
        this.childrens = new ArrayList<Class>();
        this.innerClasses = new ArrayList<Class>();
        this.asotiationChilds = new ArrayList<Class>();
        this.asotiationClasses = new ArrayList<Class>();
        isClicked = false;
        
        RefactoringEvents.addClassMethodSafeDeleteListener(this);
        RefactoringEvents.addClassAttributeSafeDeleteListener(this);
        RefactoringEvents.addPackageRenameListener(this.getClassData());
        RefactoringEvents.addClassMethodChangeDeclarationListener(this);
        RefactoringEvents.addClassAddAttributeListener(this);
        RefactoringEvents.addClassAddMethodListener(this);
        RefactoringEvents.addClassChangeAttributeListener(this);
    }

    public Class getParent() {
        return this.parent;
    }

    public void setParent(Class parent) {
        this.parent = parent;
    }

    public ArrayList<Class> addChild(Class child) {
        this.childrens.add(child);
        this.isSuperclass = true;
        return this.childrens;
    }

    public ArrayList<Class> getChildrens() {
        return this.childrens;
    }
    
    public void removeChild(Class child) {
        this.childrens.remove(child);
        if (childrens.isEmpty()) {
            this.isSuperclass = false;
        }
    }
    
    public ArrayList<Class> getInnerClasses() {
        return this.innerClasses;
    }
    
    public void addInnerClass(Class inner) {
        this.innerClasses.add(inner);
        this.hasInnerClasses = true;
    }
    
    public void removeInnerClass(Class inner) {
        this.innerClasses.remove(inner);
        if (innerClasses.isEmpty()) {
            this.hasInnerClasses = false;
        }
    }
    
    public ArrayList<Class> getAsotiationClasses() {
        return this.asotiationClasses;
    }
    
    public void addAsotiationClass(Class a) {
        this.asotiationClasses.add(a);
        this.isAsociationChild = true;
    }
    
    public void removeAsotiationClass(Class a) {
        this.asotiationClasses.remove(a);
        if (this.asotiationClasses.isEmpty()) {
            this.isAsociationChild = false;
        }
    }
    
    public ArrayList<Class> getAsotiationChilds() {
        return this.asotiationChilds;
    }
    
    public void addAsotiationChild(Class child) {
        this.asotiationChilds.add(child);
        this.isAsotiationClass = true;
    }
    
    public void removeAsotiationChild(Class child) {
        this.asotiationChilds.remove(child);
        if (this.asotiationChilds.isEmpty()) {
            this.isAsotiationClass = false;
        }
    }

    public void setIsClicked(boolean clicked) {
        this.isClicked = clicked;
    }

    public ArrayList<Class> getChildrens(Package packageObj) {
        ArrayList<Class> list = new ArrayList<Class>();
        for (Class child : this.childrens) {
            if (child.getPackage() == packageObj) {
                list.add(child);
            }
        }
        return list;
    }

    public Rectangle getRectangleRepresentation() {
        return new Rectangle(cornerX, cornerY, width, height);
    }

    public boolean intersects(ArrayList<Class> classes) {
        for (Class c : classes) {
            if (this == c) {
                continue;
            }
            if (c.getSolidTile().getRectangleRepresentation().intersects(this.solidTile.getRectangleRepresentation())) {
                return true;
            }
        }
        return false;
    }

    public void calculateSize(Graphics g, boolean forcedPackageContains) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g.getFontMetrics();

        String className;
        if (forcedPackageContains) {
            className = classData.getSimpleName();
        } else {
            if (this.classPackage.contains(this)) {
                className = classData.getSimpleName();
            } else {
                className = classData.getFullName();
            }
        }


        int maxWidth = this.getClassNameWidth(g, className);
        int actualHeight = 18;
        actualHeight = this.setLabelNamePosition(actualHeight, fm);
        for (VariableLook v : variables) {
            if (!v.isIsAsotiation() || Class.showAttributes) {
                v.draw(g2d, cornerX + 5, cornerY + actualHeight, isEnum, fontSize);
                maxWidth = (maxWidth < v.getStringWidth()) ? v.getStringWidth() : maxWidth;
                actualHeight += v.getStringHeight();
            }
        }

        actualHeight -= fm.getAscent() / 2;
        varEnd = actualHeight;
        actualHeight += 14;

        for (MethodLook m : methods) {
            if (!(m.isConstructor() && !Class.showConstructors)) {
                m.draw(g2d, cornerX + 5, cornerY + actualHeight, isInterface, fontSize);
                maxWidth = (maxWidth < m.getStringWidth()) ? m.getStringWidth() : maxWidth;
                actualHeight += m.getStringHeight();
            }
        }

        actualHeight -= fm.getAscent() / 2;
        // nastavenie sirky vysky obdlznika
        width = maxWidth + 10;
        height = actualHeight;

        // nastavenie bodov na spajanie tried
        this.topPoint.setX(cornerX + width / 2);
        this.topPoint.setY(cornerY - solidTilePadding);
        this.bottomPoint.setX(topPoint.getX());
        this.bottomPoint.setY(cornerY + height + solidTilePadding);
        this.rightPoint.setX(cornerX + width + solidTilePadding);
        this.rightPoint.setY(cornerY + height / 2);
        this.innerClassesPoint.setX(cornerX + width + solidTilePadding);
        if (this.isAsotiationClass) // ak sa uz vykresluje asociacia vykresli sa vnorena trieda vyssie, inak sa vykresli vstrede
        {
            this.innerClassesPoint.setY(cornerY + height / 2 - height / 4);
        } else {
            this.innerClassesPoint.setY(cornerY + height / 2);
        }
        this.leftPoint.setX(cornerX - solidTilePadding);
        this.leftPoint.setY(rightPoint.getY());

        this.createSolidTile();
    }

    public void draw(Graphics g, ArrayList<Class> classes) {
        
        Graphics2D g2d = (Graphics2D) g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);


        this.calculateSize(g, false);

        // obdlznik triedy    
        Color previousColor = g2d.getColor();
        if (Diagram.coloredDiagram) {
            g2d.setColor(getBackgroundColor());
        } else {
            g2d.setColor(Color.white);
        }

        g.fillRoundRect(cornerX, cornerY, width, height, 5, 5);
        g2d.setColor(previousColor);

        // bolo na triedu kliknute
        if (this.isClicked) {
            g2d.setStroke(new BasicStroke(3));
        } else {
            g2d.setStroke(new BasicStroke(1));
        }

        g2d.drawRoundRect(cornerX, cornerY, width, height, 5, 5);
        g2d.setStroke(new BasicStroke(1));

        String className;
        if (this.classPackage.contains(this)) {
            className = classData.getSimpleName();
        } else {
            className = classData.getFullName();
        }


        //Font f = this.setClassNameFont();
        FontMetrics fm = g.getFontMetrics();
        // zistenie max sirky
        int maxWidth = this.getClassNameWidth(g, className);
        int actualHeight = 18;

        actualHeight = this.setLabelNamePosition(actualHeight, fm);

        for (VariableLook v : variables) {
            if (!v.isIsAsotiation() || Class.showAttributes) {
                v.draw(g2d, cornerX + 5, cornerY + actualHeight, isEnum, fontSize);
                maxWidth = (maxWidth < v.getStringWidth()) ? v.getStringWidth() : maxWidth;
                actualHeight += v.getStringHeight();
            }
        }

        actualHeight -= fm.getAscent() / 2;
        varEnd = actualHeight;
        actualHeight += 14;

        for (MethodLook m : methods) {
            if (!(m.isConstructor() && !Class.showConstructors)) {
                m.draw(g2d, cornerX + 5, cornerY + actualHeight, isInterface, fontSize);
                maxWidth = (maxWidth < m.getStringWidth()) ? m.getStringWidth() : maxWidth;
                actualHeight += m.getStringHeight();
            }
        }

        // ak je rodicovska trieda vykresli sa sipocka
        if (this.isSuperclass) {
            this.drawInheritanceArrow(g2d);
        }

        // ak je tato trieda vo vztahu asociacie vykresli sa sipka
        if (this.isAsotiationClass) {
            this.drawLeftAnotationArrow(g2d);
        }

        // ak tato trieda ma vnorene triedy vykresli sa sipka
        if (this.hasInnerClasses) {
            this.drawLeftInnerClassNest(g2d);
        }

        if (this.parent != null) {
            this.drawChildPort(g2d);
        }
        
        // asociacie a vnorene triedy, lavy port
        if (this.isAsociationChild || this.isInncerClassChild) {
            this.drawAsociationChildPort(g2d);
        }

        if (intersects(classes)) {
            g2d.setPaint(Color.red);
        } else {
            g2d.setPaint(Color.black);
        }

        this.drawLabel(g2d);

        // meno triedy v strede
        Font f = this.setClassNameFont();
        fm = g.getFontMetrics(f);
        g2d.setFont(f);
        g2d.drawString(className + generic/* + " (" + level + ", " + position + ")"*/, cornerX + width / 2 - (fm.stringWidth(className + generic) / 2), cornerY + namePosition);

        // oddelenie mena
        g2d.drawLine(cornerX, cornerY + nameEnd, cornerX + width, cornerY + nameEnd);

        // oddelenie atributov od metod
        g2d.drawLine(cornerX, cornerY + varEnd, cornerX + width, cornerY + varEnd);
        g2d.setPaint(Color.black);
    }

    abstract protected int getClassNameWidth(Graphics g, String className);

    abstract protected Font setClassNameFont();

    abstract protected int setLabelNamePosition(int actualHeight, FontMetrics fm);

    protected void drawLabel(Graphics2D g2d) {
    }

    protected void parseGeneric() {
        if (classData.getTypeSignature() == "") {
            return;
        }
     
        //Message.showMessage(types.toString());
        generic = classData.getTypeSignature();
        
    }

    public boolean isOver(int x, int y) {
        if (x > cornerX + width) {
            return false;
        }
        if (x < cornerX) {
            return false;
        }
        if (y > cornerY + height) {
            return false;
        }
        if (y < cornerY) {
            return false;
        }
        return true;
    }

    protected void drawInheritanceArrow(Graphics2D g) { //OK
        int x = bottomPoint.getX();
        int y = bottomPoint.getY() - solidTilePadding;
        g.drawLine(x, y, x - 5, y + 5);
        g.drawLine(x, y, x + 5, y + 5);
        g.drawLine(x + 5, y + 5, x - 5, y + 5);
        g.drawLine(x, y + 5, x, y + 8);
    }

    protected void drawLeftAnotationArrow(Graphics2D g) { //OK
        int x = rightPoint.getX() - solidTilePadding;
        int y = rightPoint.getY();
        g.drawLine(x, y, x + 8, y);
        g.drawLine(x, y, x + 6, y - 4);
        g.drawLine(x, y, x + 6, y + 4);
    }

    protected void drawLeftInnerClassNest(Graphics2D g) { //OK
        int x = innerClassesPoint.getX() - solidTilePadding;
        int y = innerClassesPoint.getY();
        g.drawLine(x, y, x + solidTilePadding, y);
        g.drawLine(x + 3, y - 3, x + 3, y + 3);
        g.drawOval(x, y - 3, 6, 6);
    }

    protected void drawChildPort(Graphics2D g) {
        g.drawLine(topPoint.getX(), topPoint.getY(), topPoint.getX(), topPoint.getY() + solidTilePadding);
    }
    
    protected void drawAsociationChildPort(Graphics2D g) {
        g.drawLine(leftPoint.getX(), leftPoint.getY(), leftPoint.getX() + solidTilePadding, leftPoint.getY());
    }

    public void setCornerX(int cornerX) {
        this.cornerX = cornerX;
    }

    public void setCornerY(int cornerY) {
        this.cornerY = cornerY;
    }

    public void setPosition(int x, int y) {
        this.cornerX = x;
        this.cornerY = y;
        
        this.topPoint.setX(cornerX + width / 2);
        this.topPoint.setY(cornerY - solidTilePadding);
        this.bottomPoint.setX(topPoint.getX());
        this.bottomPoint.setY(cornerY + height + solidTilePadding);
        this.rightPoint.setX(cornerX + width + solidTilePadding);
        this.rightPoint.setY(cornerY + height / 2);
        this.leftPoint.setX(cornerX - solidTilePadding);
        this.leftPoint.setY(rightPoint.getY());
    }

    public int getCornerX() {
        return cornerX;
    }

    public int getCornerY() {
        return cornerY;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fs) {
        fontSize = fs;
    }

    public Point getBottomPoint() {
        return bottomPoint;
    }

    public Point getTopPoint() {
        return topPoint;
    }

    public Point getLeftPoint() {
        return leftPoint;
    }

    public Point getRightPoint() {
        return rightPoint;
    }

    public Point getInnerClassesPoint() {
        return innerClassesPoint;
    }

    public void setIsSuperclass(boolean isSuperclass) {
        this.isSuperclass = isSuperclass;
    }

    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public void setIsAsotiationClass(boolean isAsotiationClass) {
        this.isAsotiationClass = isAsotiationClass;
    }

    public void setHasInnerClasses(boolean hasInnerClasses) {
        this.hasInnerClasses = hasInnerClasses;
    }

    public int getNumberOfAsotiation() {
        return this.asotiationChilds.size();
    }

    

    public ArrayList<VariableLook> getVariables() {
        return variables;
    }

    public static void setShowAttributes(boolean a) {
        Class.showAttributes = a;
    }

    public static void setShowConstructors(boolean showConstructors) {
        Class.showConstructors = showConstructors;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setPackage(Package classPackage) {
        this.classPackage = classPackage;
        this.classData.setPackageData(classPackage.getData());
    }

    public Package getPackage() {
        return this.classPackage;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void createSolidTile() {
        this.solidTile = new SolidTile(new Point(cornerX - solidTilePadding, cornerY - solidTilePadding), new Point(cornerX + width + solidTilePadding, cornerY + height + solidTilePadding));
    }

    public SolidTile getSolidTile() {
        return this.solidTile;
    }

    public ClassData getClassData() {
        return this.classData;
    }
    
    public void setIsAsociationChild(boolean ischild) {
        this.isAsociationChild = ischild;
    }

    @Override
    public void ClassSelect(ClassSelectEvent e) {
        if ((Class) e.getSource() == this) {
            this.isClicked = true;
        } else {
            this.isClicked = false;
        }
    }

    @Override
    public void ClassUnselect() {
        this.isClicked = false;
    }

    @Override
    public void onMethodSafeDelete(ClassMethodEvent e) {

        if (this.classData == e.getClassData()) {
            MethodLook methodToRemove = null;

            for (MethodLook m : methods) {
                if (m.getMethod() == e.getMethodData()) {
                    methodToRemove = m;
                    break;
                }
            }

            if (methodToRemove != null) {
                methods.remove(methodToRemove);
            }
        }
    }

    @Override
    public void onAttributeSafeDelete(ClassAttributeEvent e) {

        if (this.classData == e.getClassData()) {
            VariableLook variableToRemove = null;

            for (VariableLook v : variables) {
                if (v.getVar() == e.getVariableData()) {
                    variableToRemove = v;
                    break;
                }
            }

            if (variableToRemove != null) {
                variables.remove(variableToRemove);
            }
        }
    }

    @Override
    public void onMethodChangeDeclaration(ClassMethodEvent e) {
        
        if (this.classData == e.getClassData()) {

            for (MethodLook m : methods) {
                if (m.getMethod() == e.getMethodData()) {
                    m.initMethod();
                    break;
                }
            }

        }
    }

    @Override
    public void onAddAttribute(ClassAttributeEvent e) {
        
        if (this.classData == e.getClassData()) {
            variables.add(new VariableLook(e.getVariableData()));
        }
    }
    
    @Override
    public void onAddMethod(ClassMethodEvent e) {
    
        if (this.classData == e.getClassData())
            methods.add(new MethodLook(e.getMethodData(), e.getClassData()));
    }

    @Override
    public void onChangeAttribute(ClassAttributeEvent e) {
        
        if (this.classData == e.getClassData()) {
            for (VariableLook v : variables) {
                if (v.getVar() == e.getVariableData()) {
                    v.generateRepresent();
                    break;
                }
            }
        }
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param isInncerClassChild the isInncerClassChild to set
     */
    public void setIsInncerClassChild(boolean isInncerClassChild) {
        this.isInncerClassChild = isInncerClassChild;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
    
    
}

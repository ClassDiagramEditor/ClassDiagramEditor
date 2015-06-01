/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.WindowComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups.ClassPopup;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups.DeletePopup;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups.EmptySpacePopup;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups.PackagePopup;
import org.fei.ClassDiagramEditor.Editor.events.ClassAttributeEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassMethodEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassRenameEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSelectListener;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewClassListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewEnumListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewInnerClassListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewInterfaceListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewPackageListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddMethodListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAttributeSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassChangeAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodChangeDeclarationListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSelectListener;
import org.fei.ClassDiagramEditor.Element.Class.AbstractClassDrawing;
import org.fei.ClassDiagramEditor.Router.RouterService;
import org.fei.ClassDiagramEditor.StitchingStructure.StitchingStructure;
import org.fei.ClassDiagramEditor.drawing.Lines.AsotiationArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.Element.Class.ClassDrawing;
import org.fei.ClassDiagramEditor.Element.Class.EnumClass;
import org.fei.ClassDiagramEditor.Element.Class.InterfaceClass;
import org.fei.ClassDiagramEditor.Element.Package.Package;
import org.fei.ClassDiagramEditor.Element.Package.PackageService;
import org.fei.ClassDiagramEditor.JavaParser.JavaReaderAction;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;
import org.fei.ClassDiagramEditor.drawing.Lines.InnerClassArrow;

/**
 * Vykresli platno pre diagram tried. Je zdrojom udalosti ClassSelect
 *
 * @author Tomas
 */
public class Diagram extends JPanel implements MouseListener, MouseMotionListener, ItemListener,
        ClassRenameListener, PackageSafeDeleteListener, ClassSafeDeleteListener, ClassMethodSafeDeleteListener,
        ClassAttributeSafeDeleteListener, PackageRenameListener, ClassMethodChangeDeclarationListener,
        ClassAddAttributeListener, ClassAddMethodListener, ClassChangeAttributeListener, AddNewInnerClassListener,
        AddNewClassListener, AddNewInterfaceListener, AddNewEnumListener, AddNewPackageListener {

    private ArrayList<Class> classes;
    private ArrayList<Line> lines;
    private ArrayList<Package> packages;
    private Class draggedClass, clickedClass;
    private Package draggedPackage;
    private boolean draggedPackage_isEntireVisible;
    private Package resizedPackage;
    private int actual_origX, actual_origY;
    private int tmpX, tmpY;
    private boolean isAsociationEnabled = true;
    private boolean painted = false;
    // listeners
    private List<ClassSelectListener> classSelectEventlisteners = new ArrayList<ClassSelectListener>();
    private List<PackageSelectListener> packageSelectListeners = new ArrayList<PackageSelectListener>();
    public static boolean coloredDiagram = true;
    private PackageService packageService;

    public Diagram(ArrayList<Class> classes, ArrayList<Line> lines, ArrayList<Package> packages, Dimension diagramSize, PackageService service,boolean firstTime) {
        this.classes = classes;
        this.lines = lines;
        this.packages = packages;

        this.draggedClass = null;
        this.clickedClass = null;
        this.draggedPackage = null;
        this.resizedPackage = null;
        this.packageService = service;
        this.setBackground(Color.white);
        this.setPreferredSize(diagramSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        

        for (Class c : classes) {
            this.addClassSelectEventListener(c);
        }

        for (Package p : packages) {
            this.addPackageSelectEventListener(p);
        }

        if(firstTime){
            RefactoringEvents.addClassRenameEventListener(this);
            RefactoringEvents.addPackageSafeDeleteListener(this);
            RefactoringEvents.addClassSafeDeleteListener(this);
            RefactoringEvents.addClassMethodSafeDeleteListener(this);
            RefactoringEvents.addClassAttributeSafeDeleteListener(this);
            RefactoringEvents.addPackageRenameListener(this);
            RefactoringEvents.addClassMethodChangeDeclarationListener(this);
            RefactoringEvents.addClassAddAttributeListener(this);
            RefactoringEvents.addClassAddMethodListener(this);
            RefactoringEvents.addClassChangeAttributeListener(this);
            RefactoringEvents.addAddNewInnerClassListener(this);
            RefactoringEvents.addAddNewClassListener(this);
            RefactoringEvents.addAddNewInterfaceListener(this);
            RefactoringEvents.addAddNewEnumListener(this);
            RefactoringEvents.addAddNewPackageListener(this);
        }
    }

    @Override   
    public void paint(Graphics g) { // Vykreslovanie tried a čiar
        if(!JavaReaderAction.getRepaint()){
            super.paint(g);

            // Napojenie...
            for (Class d : getClasses()) {
                d.calculateSize(g, false);
            }

            for (Package packageObj : getPackages()) {
                packageObj.draw(g, getPackages(), getClasses(), false);
            }

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            Dimension stitchingStructSize = new Dimension(this.getPreferredSize().width + screenSize.width, this.getPreferredSize().height + screenSize.height);

            StitchingStructure stitchingStruct = new StitchingStructure(getClasses(), stitchingStructSize);
            RouterService routerService = new RouterService(stitchingStruct);


            for (Line l : getLines()) {
                if (l instanceof AsotiationArrow) {
                    if (this.isAsociationEnabled) {
                        l.draw(g, routerService);
                    }
                } else {
                    l.draw(g, routerService);
                }
            }

            for (Class d : getClasses()) {
                d.draw(g, getClasses());
            }
            g.dispose();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

        fireClassUnselectEvent();
        firePackageUnselectEvent();

        for (Class classObj : getClasses()) {

            if (classObj.isOver(e.getX(), e.getY())) {
                draggedClass = classObj;
                actual_origX = draggedClass.getCornerX();
                actual_origY = draggedClass.getCornerY();
                tmpX = (e.getX() - draggedClass.getCornerX());
                tmpY = (e.getY() - draggedClass.getCornerY());

                // fire event
                fireClassSelectEvent(classObj, classObj.getClassData());

                // popup menu
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (classObj instanceof ClassDrawing || classObj instanceof AbstractClassDrawing) {
                        ClassPopup popup = new ClassPopup(classObj.getClassData());
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                    if (classObj instanceof EnumClass || classObj instanceof InterfaceClass) {
                        DeletePopup popup = new DeletePopup();
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                this.repaint();
                return;
            }
        }
        Package pcgselected = null;
        for (Package packageObj : getPackages()) {
            if (packageObj.isOverLabel(e.getX(), e.getY())) {
                draggedPackage = packageObj;
                draggedPackage_isEntireVisible = this.getVisibleRect().contains(draggedPackage.getRectangleRepresentation());
                tmpX = (e.getX() - draggedPackage.getX());
                tmpY = (e.getY() - draggedPackage.getY());
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));

                if (e.getButton() == MouseEvent.BUTTON3) {
                    PackagePopup popup = new PackagePopup(packageObj.getData(), -1, -1);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }

                firePackageSelectEvent(packageObj);
                this.repaint();
                return;
            }

            if (packageObj.isOverResizer(e.getX(), e.getY())) {
                resizedPackage = packageObj;
                this.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                return;
            }
            
            if (packageObj.isOverPackage(e.getX(), e.getY())) {
                pcgselected = packageObj;
            }
        }
        
        if (pcgselected != null) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                    PackagePopup popup = new PackagePopup(pcgselected.getData(), e.getX(), e.getY());
                    popup.show(e.getComponent(), e.getX(), e.getY());
            }
            firePackageSelectEvent(pcgselected);
            this.repaint();
            return;
        }

        // bolo kliknute mimo nejaku triedu
        if (e.getButton() == MouseEvent.BUTTON3) {
            EmptySpacePopup popup = new EmptySpacePopup(e.getX(), e.getY());
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
        this.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedClass != null) {
            int x = 0;
            int y = 0;
            if (e.getX() - tmpX >= 0) {
                x = e.getX() - tmpX;
            }
            if (e.getY() - tmpY >= 0) {
                y = e.getY() - tmpY;
            }
            draggedClass.setCornerX(x);
            draggedClass.setCornerY(y);
            this.tryResize();
            this.scrollRectToVisible(draggedClass.getRectangleRepresentation());
            this.repaint();

        } else if (draggedPackage != null) {
            int x = 0;
            int y = 25;
            if (e.getX() - tmpX >= 0) {
                x = e.getX() - tmpX;
            }
            if (e.getY() - tmpY >= 25) {
                y = e.getY() - tmpY;
            }
            draggedPackage.setPosition(x, y);
            this.tryResize();
            if (draggedPackage_isEntireVisible) {
                this.scrollRectToVisible(draggedPackage.getRectangleRepresentation());
            }
            if (this.getVisibleRect().contains(draggedPackage.getRectangleRepresentation())) {
                draggedPackage_isEntireVisible = true;
            }
            this.repaint();

        } else if (resizedPackage != null) {
            resizedPackage.resize(e.getX() - resizedPackage.getX(), e.getY() - resizedPackage.getY());
            this.repaint();
        }
    }

    private void tryResize() {
        int maxX = 0;
        int maxY = 0;
        int x;
        int y;
        for (Class classObj : getClasses()) {
            x = classObj.getCornerX() + classObj.getWidth() + 50;
            y = classObj.getCornerY() + classObj.getHeight() + 50;
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        for (Package packageObj : getPackages()) {
            x = packageObj.getX() + packageObj.getWidth() + 50;
            y = packageObj.getY() + packageObj.getHeight() + 50;
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        this.setPreferredSize(new Dimension(maxX, maxY));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggedClass != null) {
            if (draggedClass.intersects(getClasses())) {
                draggedClass.setCornerX(actual_origX);
                draggedClass.setCornerY(actual_origY);
                //draggedClass = null;
                //this.repaint();
            }

            draggedClass = null;
            this.repaint();
        } else {
            draggedPackage = null;
            resizedPackage = null;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        if(JavaReaderAction.getRepaint()){
            for (Package packageObj : getPackages()) {
                if (packageObj.isOverLabel(e.getX(), e.getY())) {
                    this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    return;
                } else {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                if (packageObj.isOverResizer(e.getX(), e.getY())) {
                    this.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                    return;
                } else {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void itemStateChanged(ItemEvent e) { // Zmena checkboxu
        //Message.showMessage(String.valueOf(((JCheckBox)e.getItemSelectable()).isSelected()));
        this.isAsociationEnabled = ((JCheckBox) e.getItemSelectable()).isSelected();
        if (!isAsociationEnabled) {
            for (Class d : getClasses()) {
                d.setIsAsotiationClass(isAsociationEnabled);
                d.setIsAsociationChild(isAsociationEnabled);
            }
        } else {
            for (Class d : getClasses()) {
                for (Line l : getLines()) {
                    if (l instanceof AsotiationArrow) {
                        l.getParent().setIsAsotiationClass(isAsociationEnabled);
                        l.getChild().setIsAsociationChild(isAsociationEnabled);
                    }
                }
            }
        }
        //Message.showMessage(String.valueOf(this.isAsociationEnabled));
        this.repaint();
    }

    // class select event
    public synchronized void addClassSelectEventListener(ClassSelectListener listener) {
        this.classSelectEventlisteners.add(listener);
    }

    public synchronized void removeClassSelectEventListener(ClassSelectListener listener) {
        this.classSelectEventlisteners.remove(listener);
    }

    private synchronized void fireClassSelectEvent(Class source, ClassData data) {

        ClassSelectEvent event = new ClassSelectEvent(source, data);
        for (ClassSelectListener listener : this.classSelectEventlisteners) {
            listener.ClassSelect(event);
        }
    }

    public synchronized void fireClassUnselectEvent() {

        for (ClassSelectListener listener : this.classSelectEventlisteners) {
            listener.ClassUnselect();
        }
    }

    // package select event
    public synchronized void addPackageSelectEventListener(PackageSelectListener listener) {
        this.packageSelectListeners.add(listener);
    }

    public synchronized void removePackageSelectEventListener(PackageSelectListener listener) {
        this.packageSelectListeners.remove(listener);
    }

    private synchronized void firePackageSelectEvent(Package source) {

        PackageSelectEvent event = new PackageSelectEvent(source, source.getData());
        for (PackageSelectListener listener : this.packageSelectListeners) {
            listener.packageSelect(event);
        }
    }

    public synchronized void firePackageUnselectEvent() {

        for (PackageSelectListener listener : this.packageSelectListeners) {
            listener.packageUnselect();
        }
    }

    public void setDiagramSize(Dimension dimension) {
        this.setPreferredSize(dimension);
    }

    @Override
    public void ClassRenamed(ClassRenameEvent e) {
        this.repaint();
    }

    @Override
    public void onSafePackageDelete(PackageSelectEvent e) {

        Package pcgToRemove = null;

        for (Package p : getPackages()) {
            if (p.getData() == e.getPackageData()) {
                pcgToRemove = p;
            }
        }

        if (pcgToRemove != null) {
            getPackages().remove(pcgToRemove);

            // remove classes
            for (Class c : pcgToRemove.getClasses()) {
                removeClass(c);
            }

            firePackageUnselectEvent();
            fireClassUnselectEvent();

            this.repaint();
        }
    }

    @Override
    public void onSafeClassDelete(ClassSelectEvent e) {

        if (e.getClassData() == null) {
            return;
        }

        Class classtoRemove = null;

        boolean deleteFileObject = true;
        for (Class c : getClasses()) {
            if (c.getClassData() == e.getClassData()) {
                classtoRemove = c;
                continue;
            }
            if (c.getClassData().getSourceFileObject() == e.getClassData().getSourceFileObject()) {
                deleteFileObject = false;
            }
        }

        if (classtoRemove != null) {
            removeClass(classtoRemove);
        }
        
        if (deleteFileObject) {
            Refactoring.safeDelete(e.getClassData().getSourceFileObject());
        }

        firePackageUnselectEvent();
        fireClassUnselectEvent();

        this.repaint();
    }

    public void removeClass(Class c) {

        getClasses().remove(c);

        // odstranenie ciar
        ArrayList<Line> linesToRemove = new ArrayList<Line>();

        for (Line l : getLines()) {
            if (l.containsClass(c)) {
                linesToRemove.add(l);
            }
        }

        for (Line l : linesToRemove) {
            getLines().remove(l);
            if (l instanceof InnerClassArrow) { // vnorene triedy
                if (l.getChild() == c) {    // trieda ktora sa odobera ma vnorene triedy, musia sa odstranit aj tie
                    removeClass(l.getParent());
                }
                if (l.getParent() == c) {   // trieda ktora sa odobera je vnorena, odstrani sa
                    if (l.getChild().getInnerClasses().contains(c)) {
                        l.getChild().removeInnerClass(c);
                    }
                }
            }
            if (l instanceof AsotiationArrow) { // asociacie
                if (l.getChild() == c) {   
                    if (l.getParent().getAsotiationChilds().contains(c)) {
                        l.getParent().removeAsotiationChild(c);
                    }
                }
                if (l.getParent() == c) {   
                    if (l.getChild().getAsotiationClasses().contains(c)) {
                        l.getChild().removeAsotiationClass(c);
                    }
                }
            }
        }

        // odstranenie parentov a childov pre odstranenu triedu
        // koli vztahom
        for (Class c2 : getClasses()) {
            if (c2.getParent() == c) {
                c2.setParent(null);
            }
            if (c2.getChildrens().contains(c)) {
                c2.removeChild(c);
            }
        }
    }

    @Override
    public void onMethodSafeDelete(ClassMethodEvent e) {

        this.repaint();
    }

    @Override
    public void onAttributeSafeDelete(ClassAttributeEvent e) {

        Line lineToRemove = null;

        for (Line l : getLines()) {
            if (l instanceof AsotiationArrow) {
                if (((AsotiationArrow) l).getVariableData() == e.getVariableData()) {
                    lineToRemove = l;
                    break;
                }
            }
        }

        if (lineToRemove != null) {
            lineToRemove.getParent().setIsAsotiationClass(false);
            getLines().remove(lineToRemove);
        }

        this.repaint();
    }

    @Override
    public void onPackageRename(PackageSelectEvent e) {

        this.repaint();
    }

    @Override
    public void onMethodChangeDeclaration(ClassMethodEvent e) {

        this.repaint();
    }

    /**
     * @return the classes
     */
    public ArrayList<Class> getClasses() {
        return classes;
    }

    /**
     * @return the packages
     */
    public ArrayList<Package> getPackages() {
        return packages;
    }

    /**
     * @return the lines
     */
    public ArrayList<Line> getLines() {
        return lines;
    }

    @Override
    public void onAddAttribute(ClassAttributeEvent e) {

        this.repaint();
    }

    @Override
    public void onAddMethod(ClassMethodEvent e) {

        this.repaint();
    }

    @Override
    public void onChangeAttribute(ClassAttributeEvent e) {

        this.repaint();
    }

    @Override
    public void onAddNewInnerClass(ClassSelectEvent e) {

        Class orig = null;
        Package pcg = null;
        for (Class c : classes) {
            if (c.getClassData() == e.getSource()) {
                orig = c;
                break;
            }
        }
        for (Package p : packages) {
            if (p.getData() == e.getClassData().getPackageData()) {
                pcg = p;
                break;
            }
        }

        if (orig == null || pcg == null) {
            return;
        }

        ArrayList<MethodLook> emptyList1 = new ArrayList<MethodLook>();
        ArrayList<VariableLook> emptyList2 = new ArrayList<VariableLook>();
        ClassDrawing inner = new ClassDrawing(orig.getCornerX() + orig.getWidth() + 50, orig.getCornerY(), e.getClassData(), emptyList1, emptyList2);
        InnerClassArrow relation = new InnerClassArrow(inner.getLeftPoint(), orig.getInnerClassesPoint(), orig, inner);
        inner.setPackage(pcg);
        pcg.addClass(inner);
        this.addClassSelectEventListener(inner);
        orig.addInnerClass(inner);
        inner.setIsInncerClassChild(true);
        classes.add(inner);
        lines.add(relation);

        this.repaint();
    }

    @Override
    public void onAddNewClass(ClassSelectEvent e) {

        Package pcg = null;
        for (Package p : packages) {
            if (p.getData() == e.getSource()) {
                pcg = p;
                break;
            }
        }

        if (pcg == null) {
            return;
        }

        ArrayList<MethodLook> emptyList1 = new ArrayList<MethodLook>();
        ArrayList<VariableLook> emptyList2 = new ArrayList<VariableLook>();
        Class newClass;
        if (e.getClassData().isAbstractClass()) {
            if (e.getX() != -1 && e.getY() != -1)
                newClass = new AbstractClassDrawing(e.getX(), e.getY(), e.getClassData(), emptyList1, emptyList2);
            else
                newClass = new AbstractClassDrawing(pcg.getX() + 15, pcg.getY() + 15, e.getClassData(), emptyList1, emptyList2);
        } else {
            if (e.getX() != -1 && e.getY() != -1)
                newClass = new ClassDrawing(e.getX(), e.getY(), e.getClassData(), emptyList1, emptyList2);
            else
                newClass = new ClassDrawing(pcg.getX() + 15, pcg.getY() + 15, e.getClassData(), emptyList1, emptyList2);
        }
        newClass.setPackage(pcg);
        pcg.addClass(newClass);
        if (newClass != null) {
            this.addClassSelectEventListener(newClass);
            classes.add(newClass);
        }
        this.repaint();
    }

    @Override
    public void onAddNewInterface(ClassSelectEvent e) {

        Package pcg = null;
        for (Package p : packages) {
            if (p.getData() == e.getSource()) {
                pcg = p;
                break;
            }
        }

        if (pcg == null) {
            return;
        }

        ArrayList<MethodLook> emptyList1 = new ArrayList<MethodLook>();
        ArrayList<VariableLook> emptyList2 = new ArrayList<VariableLook>();

        InterfaceClass newInterface;
        if (e.getX() != -1 && e.getY() != -1)
            newInterface = new InterfaceClass(e.getX(), e.getY(), e.getClassData(), emptyList1, emptyList2);
        else
            newInterface = new InterfaceClass(pcg.getX() + 15, pcg.getY() + 15, e.getClassData(), emptyList1, emptyList2);
        newInterface.setPackage(pcg);
        pcg.addClass(newInterface);
        this.addClassSelectEventListener(newInterface);
        classes.add(newInterface);

        this.repaint();
    }

    @Override
    public void onAddNewEnum(ClassSelectEvent e) {

        Package pcg = null;
        for (Package p : packages) {
            if (p.getData() == e.getSource()) {
                pcg = p;
                break;
            }
        }

        if (pcg == null) {
            return;
        }

        ArrayList<MethodLook> emptyList1 = new ArrayList<MethodLook>();
        ArrayList<VariableLook> emptyList2 = new ArrayList<VariableLook>();

        EnumClass newEnum;
        if (e.getX() != -1 && e.getY() != -1)
            newEnum = new EnumClass(e.getX(), e.getY(), e.getClassData(), emptyList1, emptyList2);
        else
            newEnum = new EnumClass(pcg.getX() + 15, pcg.getY() + 15, e.getClassData(), emptyList1, emptyList2);
        newEnum.setPackage(pcg);
        pcg.addClass(newEnum);
        this.addClassSelectEventListener(newEnum);
        classes.add(newEnum);

        this.repaint();
    }

    @Override
    public void onAddNewPackage(PackageSelectEvent e) {

        Package pcg = null;
        for (Package p : packages) {
            if (p.getData() == e.getSource()) {
                pcg = p;
                break;
            }
        }

        Package newPackage = new Package(e.getPackageData(), this.packageService.createNewPackageColor());

        newPackage.setParent(pcg);
        this.addPackageSelectEventListener(newPackage);
        if (e.getX() != -1 && e.getY() != -1) {
            newPackage.setMetrics(e.getX(), e.getY(), 50, 30);
        } else if (pcg != null) {
            newPackage.setMetrics(pcg.getX() + 15, pcg.getY() + 15, 50, 30);
        }
        if (pcg != null) {
            pcg.addSubPackage(newPackage);
        }

        packages.add(newPackage);

        this.repaint();
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }

    public void setDraggedClass(Class draggedClass) {
        this.draggedClass = draggedClass;
    }

    public void setClickedClass(Class clickedClass) {
        this.clickedClass = clickedClass;
    }

    public void setDraggedPackage(Package draggedPackage) {
        this.draggedPackage = draggedPackage;
    }

    public void setDraggedPackage_isEntireVisible(boolean draggedPackage_isEntireVisible) {
        this.draggedPackage_isEntireVisible = draggedPackage_isEntireVisible;
    }

    public void setResizedPackage(Package resizedPackage) {
        this.resizedPackage = resizedPackage;
    }

    public void setActual_origX(int actual_origX) {
        this.actual_origX = actual_origX;
    }

    public void setActual_origY(int actual_origY) {
        this.actual_origY = actual_origY;
    }

    public void setTmpX(int tmpX) {
        this.tmpX = tmpX;
    }

    public void setTmpY(int tmpY) {
        this.tmpY = tmpY;
    }

    public void setIsAsociationEnabled(boolean isAsociationEnabled) {
        this.isAsociationEnabled = isAsociationEnabled;
    }

    public void setPainted(boolean painted) {
        this.painted = painted;
    }

    public void setClassSelectEventlisteners(List<ClassSelectListener> classSelectEventlisteners) {
        this.classSelectEventlisteners = classSelectEventlisteners;
    }

    public void setPackageSelectListeners(List<PackageSelectListener> packageSelectListeners) {
        this.packageSelectListeners = packageSelectListeners;
    }

    public static void setColoredDiagram(boolean coloredDiagram) {
        Diagram.coloredDiagram = coloredDiagram;
    }

    public void setPackageService(PackageService packageService) {
        this.packageService = packageService;
    }
    
    public void setDiagramAfterRefactor(ArrayList<Class> classes, ArrayList<Line> lines, ArrayList<Package> packages, Dimension diagramSize, PackageService service){
        
        this.classes = classes;
        this.lines = lines;
        this.packages = packages;

        this.draggedClass = null;
        this.clickedClass = null;
        this.draggedPackage = null;
        this.resizedPackage = null;
        this.packageService = service;
        this.setPreferredSize(diagramSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        

        for (Class c : classes) {
            this.addClassSelectEventListener(c);
        }

        for (Package p : packages) {
            this.addPackageSelectEventListener(p);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;


import org.fei.ClassDiagramEditor.Layout.LayoutService;
import org.fei.ClassDiagramEditor.Layout.Package.PackageHierarchicalLayoutService;
import org.fei.ClassDiagramEditor.WindowComponents.Diagram;
import org.fei.ClassDiagramEditor.WindowComponents.MessageJPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.fei.ClassDiagramEditor.drawing.Lines.AsotiationArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.InheritanceArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.InnerClassArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.InterfaceArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;


import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.fei.ClassDiagramEditor.ClassDiagramView;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.ProjectScannerFactory;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;
import org.fei.ClassDiagramEditor.Data.MethodData;
import org.fei.ClassDiagramEditor.Data.VariableData;
import org.fei.ClassDiagramEditor.DirecotoryWatch.DirectoryWatch;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Element.Class.AbstractClassDrawing;
import org.fei.ClassDiagramEditor.Element.Class.ClassDrawing;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.Element.Class.EnumClass;
import org.fei.ClassDiagramEditor.Element.Class.InterfaceClass;
import org.fei.ClassDiagramEditor.Element.Package.Package;
import org.fei.ClassDiagramEditor.Element.Package.PackageService;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;

/**
 *
 * @author mairo744
 * 
 * Spustame parsovanie Java zdrojovych suborov
 */

public class JavaReaderAction{
    
    
    
    private volatile ArrayList<String> javaFiles;
    private ArrayList<String> javaFilesRefactor = new ArrayList<String>();
    
    private String projectPath;
    private boolean packageError;
   /*
    * Kolekcia tried ktore znazornuju triedy, rozhrania atd.
    */
    private ArrayList<Class> classes = new ArrayList<Class>();
   /*
    * Kolekcia tried ktore znazornuju vztahy medzi triedami.
    */
    private ArrayList<Line> lines = new ArrayList<Line>();
   /*
    * Kolekcia tried ktore znazornuju baliky.
    */
    private ArrayList<Package> packages;
    
    private ClassOrInterfaceVisitor visitor;
    private ClassOrInterfaceVisitor visitor2;

    private Thread RP;
    private DirectoryWatch watcher;
    private Diagram diagram;
    private Diagram newDiagram;
    private BufferedImage bi;
    private Graphics graphics;
    private PackageService packageService;
    private LayoutService layoutService;
    private Dimension diagramDimenstion;
    private ClassDiagramView view;
    private volatile static boolean repaint;
    private boolean firstTime = true;
    private boolean cancel;
    
    private ArrayList<Class> newClasses = new ArrayList<Class>();
    private ArrayList<Line> newLines = new ArrayList<Line>();
    private ArrayList<Package> newPackages = new ArrayList<Package>();
    
    public JavaReaderAction(ArrayList<String> javaPath,String projectPath) throws FileNotFoundException, IOException, ParseException, InterruptedException, InvocationTargetException {
            
        cancel = true;
        this.projectPath = projectPath;
        this.javaFiles = javaPath;
        String projectName = ProjectScannerFactory.getProjectScanner().getProjectName();
        CompilationUnit cu = null;


        // postupne sa prejde vsetkymi triedami
        for (String s : this.javaFiles){               

            /* parsuje konkretnu triedu */
            parseAndSaveMainData(s,cu);
            
            /* Parsuje vnorene triedy */
            for(int i=0; i< visitor.getClassData().size(); i++)
                parseAndSaveInnerData(visitor.getClassData().get(i).getInnerClassesInterfacesAndEnum());
               
        }
        
        // vytvorenie ciar pre dedicnost       
        createLine(projectName);
            
        // Vytvorenie graphics
        bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        graphics = bi.createGraphics();

        // Vytvorenie balíkov
        packageService = new PackageService(classes);
        packages = packageService.createPackages();           

        // Layoutovanie
        layoutService = new LayoutService(new PackageHierarchicalLayoutService(graphics, 50, 50, 50, 50), 10, 30, new Point(10, 35));
        diagramDimenstion = layoutService.applyLayout(packages);      
        
        // Diagram tried a balíkov
        diagram = new Diagram(classes, lines, packages, diagramDimenstion, packageService,firstTime);

        /* Spusti sa sledovanie priecinku kvoli zmene */
        watcher = new DirectoryWatch(ProjectScannerFactory.getProjectScanner().getProjectSrc().getPath().toString(), this);
        RP = new Thread(watcher);
        RP.start();

        diagram.paint(graphics);        
        graphics.dispose();
        
        final String name = projectName;
        while(!watcher.isCancel()){
            
            SwingUtilities.invokeAndWait(new Runnable() {
                
                @Override
                public void run() {
                    
                    if(firstTime){ 

                        view = new ClassDiagramView(diagram, name,watcher,firstTime);
                        firstTime = false;
                    }
                    
                    else{
                        
                        
                        try {
                            Diagram testDiagram = new Diagram(newClasses, newLines, newPackages, diagramDimenstion, packageService, firstTime);
                            
                            int widthDiagram = diagram.getWidth();
                            
                            for (Class d : newClasses) {
                                d.calculateSize(graphics, false);
                                
                                if(widthDiagram < (d.getCornerX()+ d.getWidth()))
                                    widthDiagram = d.getCornerX() + d.getWidth() +10;
                            }
     

                            for (Package packageObj : newPackages) {
                                packageObj.draw(graphics, newPackages, newClasses, false);
                                if(widthDiagram < (packageObj.getX() + packageObj.getWidth()))
                                    widthDiagram = packageObj.getX() + packageObj.getWidth() +10;
                            }

                            classes.clear();
                            lines.clear();
                            packages.clear();

                            classes.addAll(newClasses);
                            packages.addAll(newPackages);
                            lines.addAll(newLines);

                            diagram.setDiagramAfterRefactor(classes, lines, packages, diagramDimenstion, packageService);
                            diagram.paint(graphics);        
                            graphics.dispose();

                            diagramDimenstion.width = widthDiagram;
                            diagram.setDiagramSize(diagramDimenstion);

                            view.getEditor().ClassUnselect();
                            view.getEditor().packageUnselect();
                          
                            view.repaint();
                        
                        } catch (NullPointerException ex) {

                        }

                    }
                }

            });
            synchronized (RP) {
                JavaReaderAction.setRepaint(false);
                RP.notify();
            }

            synchronized (this) {
                while(!JavaReaderAction.getRepaint()){
                    wait();
                }
            }
        }
        
        synchronized (RP) {
                RP.interrupt();
        }
    }
    
    public void sourceCodeModify(JavaReaderAction jra) throws FileNotFoundException, ParseException, IOException, InterruptedException{
        
        packageError = false;
        newClasses.clear();
        newLines.clear();
        newPackages.clear();
        diagramDimenstion = null;
        packageService = null;

        javaFilesRefactor.clear();
        this.findJavaFiles(projectPath); 
        visitor2 = null;
        ArrayList<Class> oldClasses = new ArrayList<Class>();
        ArrayList<Package> oldPackages = new ArrayList<Package>();
        CompilationUnit cu = null;
        oldClasses.addAll(classes);
        oldPackages.addAll(packages);

        
        // postupne sa prejde vsetkymi triedami
        for (String s : this.javaFilesRefactor){  

            parseAndSaveMainDataRefactor(s,cu);

            for(int i=0; i< visitor2.getClassData().size(); i++){
                parseAndSaveInnerDataRefactor(visitor2.getClassData().get(i).getInnerClassesInterfacesAndEnum());
            }
               
        }
        
        //vytvorenie ciar pre dedicnost       
        createLineRefactor(ProjectScannerFactory.getProjectScanner().getProjectName()); 
                
        // Vytvorenie balíkov
        packageService = new PackageService(newClasses);
        newPackages = packageService.createPackages();
               
        // Layoutovanie
        layoutService = new LayoutService(new PackageHierarchicalLayoutService(graphics, 50, 50, 50, 50), 10, 30, new Point(10, 35));
        diagramDimenstion = layoutService.applyLayout(newPackages);
           
        findOldPositionAlgoritmus(oldClasses);
        setOldBackgroundColorAndPositionPackage(oldPackages);
        this.javaFilesRefactor.clear();

        if(!DirectoryWatch.isStartParse() && !packageError){
   
            synchronized (jra) {
                JavaReaderAction.setRepaint(true);
                jra.notify();
            }

            synchronized (this.RP) {
                while(JavaReaderAction.getRepaint()){
                    this.RP.wait();
                }
            }
        }
            
    }
    
    public void findJavaFiles(String projectPath) {
       
        File dir = new File(projectPath);
       
        File[] fList = dir.listFiles();
        
        for (File file : fList) {
            
            if (file.isDirectory()) {
                findJavaFiles(file.getAbsolutePath().toString());
            }
            else if(file.getName().endsWith(".java")) {
                     this.javaFilesRefactor.add(file.getAbsolutePath());
             }
        }

    }
    
    public void findOldPositionAlgoritmus(ArrayList<Class> oldClasses){
        
        for(Class oldClass: oldClasses){
            oldClass.setAssigned(false);
        }
        
        for(Class newClass: newClasses){
            newClass.setAssigned(false);
        }
        
        for(Class oldClass: oldClasses){
            
            for(Class newClass: newClasses ){
             
                if((oldClass.getClassData().getFullName().equals(newClass.getClassData().getFullName())) 
                        && (!newClass.isAssigned()) && (!oldClass.isAssigned())){                  
                    newClass.setPosition(oldClass.getCornerX(), oldClass.getCornerY());
                    oldClass.setAssigned(true);
                    newClass.setAssigned(true);
                }
                
            }  
        }
        
        int pocet = 0,size = 1;
        while(pocet != size){
            
            String oldClassName = "";
            String newClassName = "";
            ArrayList<Class> tempOldClasses = new ArrayList<Class>();
            ArrayList<Class> tempNewClasses = new ArrayList<Class>();

            for(Class oldClass: oldClasses){

                if(!oldClass.isAssigned()){
                    tempOldClasses.add(oldClass);
                }
            }

            for(Class newClass: newClasses){

                if(!newClass.isAssigned()){
                    tempNewClasses.add(newClass);
                }
            }
            pocet = tempNewClasses.size();
            
            for(Class oldClass: tempOldClasses){

                for(Class newClass: tempNewClasses ){

                    if(newClass.getClassData().getFullNameWithoutClassName().equals(oldClass.getClassData().getFullNameWithoutClassName())){

                        if(!newClass.isAssigned() && !oldClass.isAssigned()){
                            oldClassName = oldClass.getClassData().getFullName();
                            newClassName = newClass.getClassData().getFullName();
                            oldClass.setAssigned(true);
                            newClass.setAssigned(true);
                            newClass.setPosition(oldClass.getCornerX(), oldClass.getCornerY());
                            break;
                        }
                    }  
                    else if((!oldClassName.equals("")) && (!newClassName.equals(""))){
                        
                        String temp = oldClass.getClassData().getFullName().replaceAll(oldClassName, newClassName);

                        if(temp.equals(newClass.getClassData().getFullName())){
                            
                            if(!newClass.isAssigned() && !oldClass.isAssigned()){

                                oldClass.setAssigned(true);
                                newClass.setAssigned(true);
                                newClass.setPosition(oldClass.getCornerX(), oldClass.getCornerY());
                                break;
                            }
                        }
                    }


                }
            }
            
            size = 0;
           for(Class newClass: newClasses){

                if(!newClass.isAssigned()){
                    size ++;
                }
            }
        }
    }
    
    public void setOldBackgroundColorAndPositionPackage(ArrayList<Package> oldPackages){
                
        /* Nastavenie false pre znovupriradenie */
        for(Package oldP: oldPackages){
            oldP.setAssigned(false);
        }
        
        for(Class newClass: newClasses ){
            
            Package p = newClass.getPackage();
            for(Package pack: oldPackages){
                if(p.getName().equals(pack.getName())){
                    newClass.setBackgroundColor(pack.getClassesBackgroundColor());
                    p.setAssigned(true);
                    pack.setAssigned(true);
                }
            }               
        }
        for(Package newPack: newPackages){
            for(Package oldPack: packages){
                if(newPack.getName().equals(oldPack.getName())){

                    newPack.setClassesBackgroundColor(oldPack.getClassesBackgroundColor());
                    newPack.setPositionOnlyPackage(oldPack.getX(), oldPack.getY());
                    newPack.setHeight(oldPack.getHeight());
                    newPack.setWidth(oldPack.getWidth());
                    newPack.setAssigned(true);
                    oldPack.setAssigned(true);
                }
            }      
        }   
        
        /* Ak zmenime nazov package */
        ArrayList<Package> tempOldPackages = new ArrayList<Package>();
        ArrayList<Package> tempNewPackages = new ArrayList<Package>();
        
        for(Package newPackage: newPackages){

            if(!newPackage.isAssigned()){
                tempNewPackages.add(newPackage);
            }
        }
        
        /* Ak je nejaky nepriradeny package */
        if(!tempNewPackages.isEmpty()){
            
            for(Package oldPackage: oldPackages){

                if(!oldPackage.isAssigned()){
                    tempOldPackages.add(oldPackage);
                }
            }
            
            if(tempNewPackages.size() == 1 && tempOldPackages.size() == 1 ){
                
                /* Pridanie pozicie pre a pozadia pre package */
                tempNewPackages.get(0).setClassesBackgroundColor(tempOldPackages.get(0).getClassesBackgroundColor());
                tempNewPackages.get(0).setPositionOnlyPackage(tempOldPackages.get(0).getX(), tempOldPackages.get(0).getY());
                tempNewPackages.get(0).setHeight(tempOldPackages.get(0).getHeight());
                tempNewPackages.get(0).setWidth(tempOldPackages.get(0).getWidth());
                tempNewPackages.get(0).setAssigned(true);
                tempOldPackages.get(0).setAssigned(true);
                
                /* Zachovanie pozicii pre triedy dan=eho package */
                String oldPackageName = tempOldPackages.get(0).getName();
                String newPackageName = tempNewPackages.get(0).getName();
                
                for(Class newClass: tempNewPackages.get(0).getClasses()){
                    
                    for(Class oldClass: tempOldPackages.get(0).getClasses()){
                        
                    
                        String temp = oldClass.getClassData().getFullName().replaceFirst(oldPackageName, newPackageName);
                        
                        if(temp.equals(newClass.getClassData().getFullName())){
                            
                            if(!newClass.isAssigned() && !oldClass.isAssigned()){

                                oldClass.setAssigned(true);
                                newClass.setAssigned(true);
                                newClass.setPosition(oldClass.getCornerX(), oldClass.getCornerY());
                                newClass.setBackgroundColor(tempOldPackages.get(0).getClassesBackgroundColor());
                                break;
                            }
                        }
                    }
                }
            }
        }

        
    }
    
    public void createLine(String projectName){
        
        for (Class child : classes) {
            for (Class parent : classes) {
                if (!parent.equals(child)) {
                   
                    // dedicnost
                    if(child.getClassData().getClassFile().getSuperClass() != null){
                        if (child.getClassData().getClassFile().getSuperClass().equals(parent.getClassData().getClassFile().getNameClass())) {
                            parent.setIsSuperclass(true);
                            lines.add(new InheritanceArrow(child.getTopPoint(), parent.getBottomPoint(), child, parent));
                            parent.addChild(child);
                            child.setParent(parent);
                        }
                    }
                    
                    // implementacia rozhrani
                    for (String interfaceClass : child.getClassData().getClassFile().getInterfaces()) {
                        if (interfaceClass.equals(parent.getClassData().getClassFile().getNameClass())) {
                            parent.setIsSuperclass(true);
                            lines.add(new InterfaceArrow(child.getTopPoint(), parent.getBottomPoint(), child, parent));
                            parent.addChild(child);
                            if (child.getParent() == null) child.setParent(parent);   
                        }
                    }
                    // vnorene triedy
                    for (ClassParser innerClass : child.getClassData().getClassFile().getInnerClassesInterfacesAndEnum()) {
                        if (innerClass.getFullName().equals(parent.getClassData().getClassFile().getFullName())) {
                            child.setHasInnerClasses(true);
                            child.addInnerClass(parent);
                            parent.setIsInncerClassChild(true);
                            lines.add(new InnerClassArrow(parent.getLeftPoint(), child.getInnerClassesPoint(), child, parent));
                        }
                    }

                    // asociacie
                    for (VariableLook vl : child.getVariables()) {
                        //Message.showMessage(v.getDescriptor() + "\n" + v.getName() + "\n" + v.getTypeSignature() + "\n" + v.toString() );
                        VariableData v = vl.getVar();
                        String type = v.getType();
                        //if (type.contains("L") && type.contains(";")) {
                            //type = type.substring(type.indexOf("L") + 1, type.indexOf(";"));
                        //}
                        //type = type.replace("/", ".");
                        //Message.showMessage(type);



                        if (type.equals(parent.getClassData().getClassFile().getNameClass()) && !v.getName().startsWith("this$0")) {

                            parent.addAsotiationChild(child);
                            child.addAsotiationClass(parent);
                            v.setAsotioationType(parent.getClassData());
                            boolean isFirst = true;
                            for (Line l : lines) {
                                if (l instanceof AsotiationArrow) {
                                    if (l.getStart().equals(child.getLeftPoint()) && l.getEnd().equals(parent.getRightPoint())) {
                                        isFirst = false;
                                    }
                                }
                            }
                            // koli vykreslovaniu v triedach
                            vl.setIsAsotiation(true);
                            lines.add(new AsotiationArrow(child.getClassData(), child.getLeftPoint(), parent.getRightPoint(), v, parent.getNumberOfAsotiation(), isFirst, child, parent));
                        }

                    }

                }
            }
        }
    }
        
    public void createLineRefactor(String projectName){

        for (Class child : newClasses) {
            for (Class parent : newClasses) {
                if (!parent.equals(child)) {

                    // dedicnost
                    if(child.getClassData().getClassFile().getSuperClass() != null){
                        if (child.getClassData().getClassFile().getSuperClass().equals(parent.getClassData().getClassFile().getNameClass())) {
                            parent.setIsSuperclass(true);
                            newLines.add(new InheritanceArrow(child.getTopPoint(), parent.getBottomPoint(), child, parent));
                            parent.addChild(child);
                            child.setParent(parent);
                        }
                    }
                    // implementacia rozhrani
                    for (String interfaceClass : child.getClassData().getClassFile().getInterfaces()) {
                        if (interfaceClass.equals(parent.getClassData().getClassFile().getNameClass())) {
                            parent.setIsSuperclass(true);
                            newLines.add(new InterfaceArrow(child.getTopPoint(), parent.getBottomPoint(), child, parent));
                            parent.addChild(child);
                            if (child.getParent() == null) child.setParent(parent);   
                        }
                    }
                    // vnorene triedy
                    for (ClassParser innerClass : child.getClassData().getClassFile().getInnerClassesInterfacesAndEnum()) {
                        if (innerClass.getFullName().equals(parent.getClassData().getClassFile().getFullName())) {
                            child.setHasInnerClasses(true);
                            child.addInnerClass(parent);
                            parent.setIsInncerClassChild(true);
                            newLines.add(new InnerClassArrow(parent.getLeftPoint(), child.getInnerClassesPoint(), child, parent));
                        }
                    }

                    // asociacie
                    for (VariableLook vl : child.getVariables()) {
                        //Message.showMessage(v.getDescriptor() + "\n" + v.getName() + "\n" + v.getTypeSignature() + "\n" + v.toString() );
                        VariableData v = vl.getVar();
                        String type = v.getType();
                        //if (type.contains("L") && type.contains(";")) {
                            //type = type.substring(type.indexOf("L") + 1, type.indexOf(";"));
                        //}
                        //type = type.replace("/", ".");
                        //Message.showMessage(type);



                        if (type.equals(parent.getClassData().getClassFile().getNameClass()) && !v.getName().startsWith("this$0")) {

                            parent.addAsotiationChild(child);
                            child.addAsotiationClass(parent);
                            v.setAsotioationType(parent.getClassData());
                            boolean isFirst = true;
                            for (Line l : newLines) {
                                if (l instanceof AsotiationArrow) {
                                    if (l.getStart().equals(child.getLeftPoint()) && l.getEnd().equals(parent.getRightPoint())) {
                                        isFirst = false;
                                    }
                                }
                            }
                            // koli vykreslovaniu v triedach
                            vl.setIsAsotiation(true);
                            newLines.add(new AsotiationArrow(child.getClassData(), child.getLeftPoint(), parent.getRightPoint(), v, parent.getNumberOfAsotiation(), isFirst, child, parent));
                        }

                    }

                }
            }
        }
        
        if (newClasses.isEmpty()) {
            String[] msg = {"Your project does not contains any java files."};
            new ClassDiagramView(new MessageJPanel(msg), projectName,watcher,false);
            return;
        }
    }
    
    public void parseAndSaveMainData(String s, CompilationUnit cu) throws FileNotFoundException, ParseException{
        

        // creates an input stream for the file to be parsed

                FileInputStream in = new FileInputStream(s);
                // parse the file
                cu = JavaParser.parse(in);
  
        

            
            visitor = new ClassOrInterfaceVisitor();
            visitor.setSourceName(new File(s));
            visitor.setPack(new PackageParser(cu.getPackage()));

            visitor.visit(cu, null); // spusti parsovanie
            
            /* 
                V jednom java subore moze byt viacej class interface
                alebo enum nezavislych na sebe.
                Tento cyklus prejde vsetky.
            */
            for(int i=0; i< visitor.getClassData().size(); i++){
                // vytiahnu sa metody
                Collection<MethodAndConstructorParser> methods = visitor.getClassData().get(i).getMethods();
                // vytiahnu sa premenne
                Collection<VariableParser> variables = visitor.getClassData().get(i).getVariables();
                // triedy ktore reprezentuju "vzhlad" metod, tj spravne vykreslovanie v ramci UML standartu
                ArrayList<MethodLook> mDatas = new ArrayList<MethodLook>();

                /*  CLASS EDITACIA */
                ClassData data = new ClassData(visitor.getClassData().get(i));

                // metody triedy
                for (MethodAndConstructorParser m : methods) {

                        MethodData tmp = new MethodData(m);
                        mDatas.add(new MethodLook(tmp, data));

                }

                // pridanie metod do datovej struktury bez konstruktorov, za ucelom editacie
                ArrayList<MethodData> methodsEdit = new ArrayList<MethodData>();
                for (MethodLook m : mDatas) {
                    MethodData classFileMethod = m.getMethod();

                    if (classFileMethod.isConstructor())
                        continue;   // konstruktory nechceme

                    methodsEdit.add(classFileMethod);
                }
                data.setMethods(methodsEdit);

                 // praca s premennymi, konstanty enum sa pridaju zvlast
                ArrayList<VariableLook> vDatas = new ArrayList<VariableLook>();
                ArrayList<VariableLook> enumConstants = new ArrayList<VariableLook>();
                ArrayList<VariableData> varwraplist = new ArrayList<VariableData>();
                
                if (!visitor.getClassData().get(i).isEnumeration()) {
                    for (VariableParser v : variables) {

                        VariableData tmp = new VariableData(v);
                        varwraplist.add(tmp);
                        vDatas.add(new VariableLook(tmp));         
                    }
                }
                
                if (visitor.getClassData().get(i).isEnumeration()) {
                    for(String v : visitor.getClassData().get(i).getEnumTypeName()){

                        VariableData tmp = new VariableData(v,visitor.getClassData().get(i).getNameClass());
                        varwraplist.add(tmp);
                        enumConstants.add(new VariableLook(tmp));


                    }

                    for(VariableParser v : visitor.getClassData().get(i).getVariables()){

                        VariableData tmp = new VariableData(v);
                        varwraplist.add(tmp);
                        enumConstants.add(new VariableLook(tmp));


                    }
                }
                // pridanie do ClassData pre editovanie                
                data.setVariables(varwraplist);

                Class d;

                // vytvorenie spravnych instancii podtried triedy Class
                if (visitor.getClassData().get(i).isInterfaceClass()) {
                    d = new InterfaceClass(0, 0, data, mDatas, vDatas);
                } else if (visitor.getClassData().get(i).isAbstractClass()) {
                    d = new AbstractClassDrawing(0, 0, data, mDatas, vDatas);
                } else if (visitor.getClassData().get(i).isEnumeration()) {
                    d = new EnumClass(0, 0, data, mDatas, enumConstants);
                } else {
                    d = new ClassDrawing(0, 0, data, mDatas, vDatas);
                }

                classes.add(d); 
            }
    }
    
    public void parseAndSaveMainDataRefactor(String s, CompilationUnit cu) throws FileNotFoundException, ParseException{
        

        // creates an input stream for the file to be parsed

            FileInputStream in = new FileInputStream(s);
            // parse the file
            cu = JavaParser.parse(in);

            visitor2 = new ClassOrInterfaceVisitor();
            visitor2.setSourceName(new File(s));
            visitor2.setPack(new PackageParser(cu.getPackage()));
            
            if(!packageError){
                String packageName = visitor2.getPack().getName().replace(".", File.separator);
                boolean packageExist = false;
                String pathName = "";

                for(WatchKey k : watcher.getKeys().keySet()){

                    if(packageName != "")
                        pathName = projectPath + File.separator + packageName;
                    else
                        pathName = projectPath;

                    if(watcher.getKeys().get(k).toString().equals(pathName)){
                       packageExist = true;
                       break;
                    }
                }

                if(!packageExist){
                    if(!DirectoryWatch.isStartParse()){
                        packageError = true;
                        DirectoryWatch.setStartParse(true);
                    }
                }
            }

            visitor2.visit(cu, null); // spusti parsovanie
            
            /* 
                V jednom java subore moze byt viacej class interface
                alebo enum nezavislych na sebe.
                Tento cyklus prejde vsetky.
            */
            for(int i=0; i< visitor2.getClassData().size(); i++){
                // vytiahnu sa metody
                Collection<MethodAndConstructorParser> methods = visitor2.getClassData().get(i).getMethods();
                // vytiahnu sa premenne
                Collection<VariableParser> variables = visitor2.getClassData().get(i).getVariables();
                // triedy ktore reprezentuju "vzhlad" metod, tj spravne vykreslovanie v ramci UML standartu
                ArrayList<MethodLook> mDatas = new ArrayList<MethodLook>();

                /*  CLASS EDITACIA */
                ClassData data = new ClassData(visitor2.getClassData().get(i));

                // metody triedy
                for (MethodAndConstructorParser m : methods) {

                        MethodData tmp = new MethodData(m);
                        mDatas.add(new MethodLook(tmp, data));

                }

                // pridanie metod do datovej struktury bez konstruktorov, za ucelom editacie
                ArrayList<MethodData> methodsEdit = new ArrayList<MethodData>();
                for (MethodLook m : mDatas) {
                    MethodData classFileMethod = m.getMethod();

                    if (classFileMethod.isConstructor())
                        continue;   // konstruktory nechceme

                    methodsEdit.add(classFileMethod);
                }
                data.setMethods(methodsEdit);

                 // praca s premennymi, konstanty enum sa pridaju zvlast
                ArrayList<VariableLook> vDatas = new ArrayList<VariableLook>();
                ArrayList<VariableLook> enumConstants = new ArrayList<VariableLook>();
                ArrayList<VariableData> varwraplist = new ArrayList<VariableData>();
                
                if (!visitor2.getClassData().get(i).isEnumeration()) {
                    for (VariableParser v : variables) {

                        VariableData tmp = new VariableData(v);
                        varwraplist.add(tmp);
                        vDatas.add(new VariableLook(tmp));         
                    }
                }
                
                if (visitor2.getClassData().get(i).isEnumeration()) {
                    for(String v : visitor2.getClassData().get(i).getEnumTypeName()){

                        VariableData tmp = new VariableData(v,visitor2.getClassData().get(i).getNameClass());
                        varwraplist.add(tmp);
                        enumConstants.add(new VariableLook(tmp));


                    }

                    for(VariableParser v : visitor2.getClassData().get(i).getVariables()){

                        VariableData tmp = new VariableData(v);
                        varwraplist.add(tmp);
                        enumConstants.add(new VariableLook(tmp));


                    }
                }
                // pridanie do ClassData pre editovanie                
                data.setVariables(varwraplist);

                Class d;

                // vytvorenie spravnych instancii podtried triedy Class
                if (visitor2.getClassData().get(i).isInterfaceClass()) {
                    d = new InterfaceClass(0, 0, data, mDatas, vDatas);
                } else if (visitor2.getClassData().get(i).isAbstractClass()) {
                    d = new AbstractClassDrawing(0, 0, data, mDatas, vDatas);
                } else if (visitor2.getClassData().get(i).isEnumeration()) {
                    d = new EnumClass(0, 0, data, mDatas, enumConstants);
                } else {
                    d = new ClassDrawing(0, 0, data, mDatas, vDatas);
                }

                newClasses.add(d); 
            }
    }
    
    public void parseAndSaveInnerData(ArrayList<ClassParser> all){
        
        
        for(ClassParser innerClassOrInterface : all){
            
            // vytiahnu sa metody
        Collection<MethodAndConstructorParser> methods = innerClassOrInterface.getMethods();
        // vytiahnu sa premenne
        Collection<VariableParser> variables = innerClassOrInterface.getVariables();
        // triedy ktore reprezentuju "vzhlad" metod, tj spravne vykreslovanie v ramci UML standartu
        ArrayList<MethodLook> mDatas = new ArrayList<MethodLook>();

        /*  CLASS EDITACIA */
        ClassData data = new ClassData(innerClassOrInterface);
            
            // metody triedy
            for (MethodAndConstructorParser m : methods) {
                
                    MethodData tmp = new MethodData(m);
                    mDatas.add(new MethodLook(tmp, data));

                //if (m.isEnumeration()) 
                    //d = new EnumClass(0, 0, data, mDatas, enumConstants);
            }
            
            // pridanie metod do datovej struktury bez konstruktorov, za ucelom editacie
            ArrayList<MethodData> methodsEdit = new ArrayList<MethodData>();
            for (MethodLook m : mDatas) {
                MethodData classFileMethod = m.getMethod();

                if (classFileMethod.isConstructor())
                    continue;   // konstruktory nechceme

                methodsEdit.add(classFileMethod);
            }
            data.setMethods(methodsEdit);
            
             // praca s premennymi, konstanty enum sa pridaju zvlast
            ArrayList<VariableLook> vDatas = new ArrayList<VariableLook>();
            ArrayList<VariableLook> enumConstants = new ArrayList<VariableLook>();
            ArrayList<VariableData> varwraplist = new ArrayList<VariableData>();
            
            if (!innerClassOrInterface.isEnumeration()) {
                for (VariableParser v : variables) {

                    VariableData tmp = new VariableData(v);
                    varwraplist.add(tmp);
                    vDatas.add(new VariableLook(tmp));         
                }
            }
            
            if (innerClassOrInterface.isEnumeration()) {
                for(String v : innerClassOrInterface.getEnumTypeName()){
                    
                    VariableData tmp = new VariableData(v,innerClassOrInterface.getNameClass());
                    varwraplist.add(tmp);
                    enumConstants.add(new VariableLook(tmp));
                       
                    
                }
                
                for(VariableParser v : innerClassOrInterface.getVariables()){
                    
                    VariableData tmp = new VariableData(v);
                    varwraplist.add(tmp);
                    enumConstants.add(new VariableLook(tmp));
                       
                    
                }
            }
            
            // pridanie do ClassData pre editovanie                
            data.setVariables(varwraplist);
            
            Class d;
            
            // vytvorenie spravnych instancii podtried triedy Class
            if (innerClassOrInterface.isInterfaceClass()) {
                d = new InterfaceClass(0, 0, data, mDatas, vDatas);
            } else if (innerClassOrInterface.isAbstractClass()) {
                d = new AbstractClassDrawing(0, 0, data, mDatas, vDatas);
            } else if (innerClassOrInterface.isEnumeration()) {
                d = new EnumClass(0, 0, data, mDatas, enumConstants);
            } else {
                d = new ClassDrawing(0, 0, data, mDatas, vDatas);
            }

            classes.add(d);
            
            if(innerClassOrInterface.getInnerClassesInterfacesAndEnum().size()>0)
                parseAndSaveInnerData(innerClassOrInterface.getInnerClassesInterfacesAndEnum()); 
            
        }
        
    }
    
     public void parseAndSaveInnerDataRefactor(ArrayList<ClassParser> all){
        
        
        for(ClassParser innerClassOrInterface : all){
            
            // vytiahnu sa metody
            Collection<MethodAndConstructorParser> methods = innerClassOrInterface.getMethods();
            // vytiahnu sa premenne
            Collection<VariableParser> variables = innerClassOrInterface.getVariables();
            // triedy ktore reprezentuju "vzhlad" metod, tj spravne vykreslovanie v ramci UML standartu
            ArrayList<MethodLook> mDatas = new ArrayList<MethodLook>();

            /*  CLASS EDITACIA */

            ClassData data = new ClassData(innerClassOrInterface);
            
            // metody triedy
            for (MethodAndConstructorParser m : methods) {
                
                    MethodData tmp = new MethodData(m);
                    mDatas.add(new MethodLook(tmp, data));

                //if (m.isEnumeration()) 
                    //d = new EnumClass(0, 0, data, mDatas, enumConstants);
            }
            
            // pridanie metod do datovej struktury bez konstruktorov, za ucelom editacie
            ArrayList<MethodData> methodsEdit = new ArrayList<MethodData>();
            for (MethodLook m : mDatas) {
                MethodData classFileMethod = m.getMethod();

                if (classFileMethod.isConstructor())
                    continue;   // konstruktory nechceme

                methodsEdit.add(classFileMethod);
            }
            data.setMethods(methodsEdit);
            
            
             // praca s premennymi, konstanty enum sa pridaju zvlast
            ArrayList<VariableLook> vDatas = new ArrayList<VariableLook>();
            ArrayList<VariableLook> enumConstants = new ArrayList<VariableLook>();
            ArrayList<VariableData> varwraplist = new ArrayList<VariableData>();
            
            if (!innerClassOrInterface.isEnumeration()) {
                for (VariableParser v : variables) {

                    VariableData tmp = new VariableData(v);
                    varwraplist.add(tmp);
                    vDatas.add(new VariableLook(tmp));         
                }
            }
            
            if (innerClassOrInterface.isEnumeration()) {
                for(String v : innerClassOrInterface.getEnumTypeName()){
                    
                    VariableData tmp = new VariableData(v,innerClassOrInterface.getNameClass());
                    varwraplist.add(tmp);
                    enumConstants.add(new VariableLook(tmp));
                       
                    
                }
                
                for(VariableParser v : innerClassOrInterface.getVariables()){
                    
                    VariableData tmp = new VariableData(v);
                    varwraplist.add(tmp);
                    enumConstants.add(new VariableLook(tmp));
                       
                    
                }
            }
            
            // pridanie do ClassData pre editovanie                
            data.setVariables(varwraplist);
            
            Class d;
            
            // vytvorenie spravnych instancii podtried triedy Class
            if (innerClassOrInterface.isInterfaceClass()) {
                d = new InterfaceClass(0, 0, data, mDatas, vDatas);
            } else if (innerClassOrInterface.isAbstractClass()) {
                d = new AbstractClassDrawing(0, 0, data, mDatas, vDatas);
            } else if (innerClassOrInterface.isEnumeration()) {
                d = new EnumClass(0, 0, data, mDatas, enumConstants);
            } else {
                d = new ClassDrawing(0, 0, data, mDatas, vDatas);
            }

            newClasses.add(d);
            
            if(innerClassOrInterface.getInnerClassesInterfacesAndEnum().size()>0)
                parseAndSaveInnerDataRefactor(innerClassOrInterface.getInnerClassesInterfacesAndEnum()); 
            
        }
        
    }
    
    private static class ClassOrInterfaceVisitor extends VoidVisitorAdapter {

        private PackageParser pack;
        private String sourceName;
        private FileObject fileobject;
        public ArrayList<ClassParser> newClass = new ArrayList<ClassParser>();
 
        @Override
        public void visit(ClassOrInterfaceDeclaration classOrInterface, Object arg) {
            
          newClass.add(new ClassParser(classOrInterface,pack,sourceName, fileobject,null));

        }
        
        public void visit(EnumDeclaration enumator, Object arg) {
             
          newClass.add(new ClassParser(enumator,pack,sourceName, fileobject,null));
          
        }

        
        public void setSourceName(File file) {
            this.sourceName = file.getName();
            fileobject = (FileObject) FileUtil.toFileObject(file);
        }

        public void setPack(PackageParser pack) {
            this.pack = pack;
        }

        public PackageParser getPack() {
            return pack;
        }

        public String getSourceName() {
            return sourceName;
        }

        public FileObject getFileobject() {
            return fileobject;
        }


        public ArrayList<ClassParser> getClassData() {
            return newClass;
        }

    }

    
    public synchronized ArrayList<String> getJavaFiles() {
        return javaFiles;
    }

    public synchronized void setJavaFiles(ArrayList<String> javaFiles) {
        this.javaFiles = javaFiles;
    }
    
    public synchronized void addJavaFile(String path) {
        this.javaFiles.add(path);
    }
    
    public synchronized void removeJavaFile(String path) {
        this.javaFiles.remove(path);
    }
    public synchronized boolean findJavaFile(String path) {
        return this.javaFiles.contains(path);
    }
    
    public synchronized void findAndRemoveJavaFile(String old, String newString){
        this.javaFiles.remove(old);
        this.javaFiles.add(newString);
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public ArrayList<Package> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }

    public ClassOrInterfaceVisitor getVisitor() {
        return visitor;
    }

    public void setVisitor(ClassOrInterfaceVisitor visitor) {
        this.visitor = visitor;
    }

    public ClassDiagramView getView() {
        return view;
    }
    
    public synchronized static void setRepaint(boolean value){
        repaint = value;
    }
    
    public synchronized static boolean getRepaint(){
        return repaint;
    }
    
}

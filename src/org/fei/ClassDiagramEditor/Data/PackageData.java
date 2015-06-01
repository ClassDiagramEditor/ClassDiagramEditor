/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Data;

import java.util.ArrayList;
import org.fei.ClassDiagramEditor.ProjectScannerFactory;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;

/**
 *
 * @author Tomas
 */
public class PackageData {
    
    private String name;
    private boolean defaultPackage = false;
    private ArrayList<FileObject> fileObjects;
    
    private PackageData parent;
    private ArrayList<ClassData> classes;
    private ArrayList<PackageData> subPackages;
    
    private String xmiid;
    
    public PackageData(String name) {
        
        this.name = name;
        this.parent = null;
        this.classes = new ArrayList<ClassData>();
        this.subPackages = new ArrayList<PackageData>();
        
        if (this.name.isEmpty())
            defaultPackage = true;
        
        fileObjects = ProjectScannerFactory.getProjectScanner().getFileObjectForPackage(name);
        xmiid = XmiIdFactory.getId();
    }
    
    public PackageData(String name, ArrayList<FileObject> fileObjects, PackageData parent) {
    
        if(parent != null)
            this.name = parent.name+"."+name;
        else
            this.name = name;
        this.parent = parent;
        this.fileObjects = fileObjects;
        
        this.classes = new ArrayList<ClassData>();
        this.subPackages = new ArrayList<PackageData>();
        xmiid = XmiIdFactory.getId();
    }

    public String getName() {
        if (defaultPackage)
            return "<default package>";
        return name;
    }

    public boolean isDefaultPackage() {
        return defaultPackage;
    }
    
    public ArrayList<FileObject> getFileObjects() {
        return this.fileObjects;
    }
    
    public void setName(String newName) {
        this.name = newName;
        
        fileObjects = ProjectScannerFactory.getProjectScanner().getFileObjectForPackage(newName);
    }

    /**
     * @return the parent
     */
    public PackageData getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(PackageData parent) {
        this.parent = parent;
    }

    /**
     * @return the classes
     */
    public ArrayList<ClassData> getClasses() {
        return classes;
    }

    /**
     * @return the subPackages
     */
    public ArrayList<PackageData> getSubPackages() {
        return subPackages;
    }
    
    public void addClass(ClassData c) {
        this.classes.add(c);
    }
    
    public void addSubPackage(PackageData p) {
        this.subPackages.add(p);
    }
    
    public boolean isTopLevelPackage() {
        return this.parent == null;
    }
    
    /*
     *<UML:Package xmi.id = '-64--88-1-118--446e940f:144b0a84533:-8000:0000000000000962'
          name = 'MyPackage' isSpecification = 'false' isRoot = 'false' isLeaf = 'false'
          isAbstract = 'false'>
     */
    
    public Element xmiCreatePackageElement(Document document) {
        
        Element owned = document.createElement("UML:Namespace.ownedElement");
        Element packageElement = document.createElement("UML:Package");
    
        packageElement.setAttribute("xmi.id", this.xmiid);
        packageElement.setAttribute("name", this.getName());
        packageElement.setAttribute("isSpecification", "false");
        packageElement.setAttribute("isRoot", "false");
        packageElement.setAttribute("isLeaf", "false");
        packageElement.setAttribute("isAbstract", "false");

        packageElement.appendChild(owned);
        
        for (ClassData c : this.classes) {
                owned.appendChild(c.xmiCreateClassElement(document));
        }
        
        // ak ma podbaliky
        if (!subPackages.isEmpty()) { 
            for (PackageData p : this.subPackages) {
                owned.appendChild(p.xmiCreatePackageElement(document));
            }
        }
        
        return packageElement;
    }

}

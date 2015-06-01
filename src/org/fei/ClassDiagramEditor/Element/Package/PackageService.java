/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Package;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.Element.Class.Class;

/**
 *
 * @author Mr.Phoenixxx
 */
public class PackageService {
    private ArrayList<Class> classes;
    private ArrayList<Color> packagesColors;

    public PackageService(ArrayList<Class> classes) {
        this.classes = classes;
        this.packagesColors = new ArrayList<Color>();
    }
    
    public ArrayList<Package> createPackages() {
        ArrayList<Package> packages = new ArrayList<Package>();
        
        for (Class classObj : this.classes) {        // Rozdelenie tried do balíkov
            /* ULOZI DO WRAPPERU */
            Package packageObj = findPackage(classObj.getClassData().getPackageName(), packages);
            packageObj.addClass(classObj);      
            classObj.setPackage(packageObj);
        }
               
        for (Package parent : packages) {               // Hierarchické rozdelenie balíkov
            for (Package child : packages) {
                if (parent == child) continue;                
                if (child.getName().matches(parent.getName() + ".\\w+")) {  // Ak je "parent" rodičom pre "child" tak...
                    parent.addSubPackage(child);
                    child.setParent(parent);
                }                  
            }           
        }    
        return packages;
    }
    
    private Package findPackage(String name, ArrayList<Package> packages) {
        for (Package packageObj : packages) {
            if (name.isEmpty() && packageObj.getData().isDefaultPackage())
                return packageObj;
            if(packageObj.getName().equals(name))
                return packageObj;
        }
        PackageData data = new PackageData(name);
        /* ULOZI V KONSTRUKTORE */
        Package newPackage = new Package(data, this.createNewPackageColor());
        packages.add(newPackage);
        return newPackage;
    }
    
    public Color createNewPackageColor() {
        
        Random rand = new Random();
        Color color;
        do {        
            color = new Color(rand.nextInt(95)+150, rand.nextInt(95)+150, rand.nextInt(95)+150); // [150, 245] 
        } while(this.isColorExist(color, this.packagesColors));    
        return color;
        
    }
    
    private boolean isColorExist(Color color, ArrayList<Color> colors) {
        for (Color c : colors) {
            if (color.equals(c)) return true;
        }
        return false;
    }
}

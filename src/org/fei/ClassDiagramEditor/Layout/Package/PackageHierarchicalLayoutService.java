/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Layout.Package;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.Element.Package.Package;

/**
 *
 * @author Mr.Phoenixxx
 */
public class PackageHierarchicalLayoutService extends PackageLayoutService {
    private int classDistanceX;
    private int classDistanceY;
    private int packageDistanceX;
    private int packageDistanceY;  
    
    public PackageHierarchicalLayoutService(Graphics graphics, int classDistanceX, int classDistanceY, int packageDistanceX, int packageDistanceY) {
        super(graphics);
        this.setDistances(classDistanceX, classDistanceY, packageDistanceX, packageDistanceY);
    }

    @Override
    public Package applyLayout(Package packageObj) {
        this.calculateClassesSize(packageObj.getClasses());
        ArrayList<ArrayList<Class>> levelsList = this.createLevelLists(this.calculateLevels(packageObj.getClasses())); 
        this.calculatePositions(levelsList, packageObj);        
        return packageObj; 
    }
    
    public final void setDistances(int classDistanceX, int classDistanceY, int packageDistanceX, int packageDistanceY) {
        this.classDistanceX = classDistanceX;
        this.classDistanceY = classDistanceY;
        this.packageDistanceX = packageDistanceX;
        this.packageDistanceY = packageDistanceY;
    }
    
    /* ======================================================================= */
    private void calculatePositions(ArrayList<ArrayList<Class>> levelsList, Package packageObj) {
        int x;
        int y; 
        
        // Počítanie pozícií pre triedy daného balíka
        int x_max = classDistanceX;
        int y_max = classDistanceY;
        for (ArrayList<Class> classes : levelsList) {
            x = classDistanceX;
            y = y_max;
            for (Class classObj : classes) {
                classObj.setPosition(x, y);
                x = classObj.getCornerX() + classObj.getWidth() + classDistanceX;
                
                int i = classObj.getCornerY() + classObj.getHeight() + classDistanceY;
                if (i > y_max) y_max = i;                 
                if (x > x_max) x_max = x;
            }
        }    
               
        // Počítanie pozícií pre vnorené balíky
        x = packageDistanceX;
        y = y_max;
        for (Package subPackage : packageObj.getSubPackages()) {
            subPackage.setPosition(x, y);
            x = subPackage.getX() + subPackage.getWidth() + packageDistanceX;
            
            int i = subPackage.getY() + subPackage.getHeight() + packageDistanceY;
            if (i > y_max) y_max = i;  
            if (x > x_max) x_max = x;
        }       
        packageObj.setMetrics(0, 0, x_max, y_max);
    }
    
    
    /* ======================================================================= */
    private void calculateClassesSize(ArrayList<Class> classes) {
        for (Class classObj : classes) {
            classObj.calculateSize(this.graphics, true);
        }
    }
    
    
    
    /* ======================================================================= */
    private HashMap<Class, Integer> createLevelHashMap(ArrayList<Class> classes) {
        HashMap<Class, Integer> levelHashMap = new HashMap<Class, Integer>(classes.size());      
        for (Class classObj : classes) {
            levelHashMap.put(classObj, 0);
        }     
        return levelHashMap;
    }   
    
    private HashMap<Class, Integer> calculateLevels(ArrayList<Class> classes) {
        HashMap<Class, Integer> levelHashMap = this.createLevelHashMap(classes);
        for (Class classObj : classes) {
            this.recursiveDecrease(classObj, classes, levelHashMap);
        }     
        return levelHashMap;
    }
    
    private int getLevesCount(HashMap<Class, Integer> levelHashMap) {
        int i = 0;  
        for (Class key : levelHashMap.keySet()) {
            int levelNumber = Math.abs(levelHashMap.get(key));
            if (levelNumber > i) i = levelNumber;  
        }
        return i+1;
    }
    
    private ArrayList<ArrayList<Class>> createLevelLists(HashMap<Class, Integer> levelHashMap) {
        ArrayList<ArrayList<Class>> levelLists_temp = new ArrayList<ArrayList<Class>>();
        ArrayList<ArrayList<Class>> levelLists_noChildrens = new ArrayList<ArrayList<Class>>(); 
        ArrayList<ArrayList<Class>> levelLists = new ArrayList<ArrayList<Class>>();
        for (int i=0; i<this.getLevesCount(levelHashMap); i++) {
            levelLists_temp.add(new ArrayList<Class>());
            levelLists_noChildrens.add(new ArrayList<Class>());
            levelLists.add(new ArrayList<Class>());
        }
            
        for (Class classObj : levelHashMap.keySet()) {
            ArrayList<Class> level = levelLists_temp.get(Math.abs(levelHashMap.get(classObj)));
            level.add(classObj);
        }
        
        for (int i = 0; i<levelLists_temp.size()-1; i++) {
            for (int j = 0; j<levelLists_temp.get(i).size(); j++) {
                Class classObj = levelLists_temp.get(i).get(j);
                if (classObj.getChildrens().isEmpty()) {
                    levelLists_noChildrens.get(i).add(classObj);
                } else {
                    levelLists.get(i).add(classObj);
                    levelLists.get(i+1).addAll(classObj.getChildrens(classObj.getPackage())); 
                }       
            }
        }
        
        for (int i = 0; i<levelLists_noChildrens.size()-1; i++) {
            for (int j = 0; j<levelLists_noChildrens.get(i).size(); j++) {
                Class classObj = levelLists_temp.get(i).get(j);
                levelLists.get(i).add(classObj);       
            }
        }
        
        levelLists.get(levelLists.size()-1).addAll(levelLists_temp.get(levelLists_temp.size()-1));    
        return levelLists_temp;
    }
    
    private void recursiveDecrease(Class classObj, ArrayList<Class> classes, HashMap<Class, Integer> levelHashMap) {
        if (classObj.getChildrens().isEmpty()) return;
        
        for (Class child : classObj.getChildrens()) {  
            if (!classes.contains(child)) continue;     // Ak dieťa nie je z tohoto balíku, tak sa preskočí
            if ((levelHashMap.get(child)) >= levelHashMap.get(classObj)) levelHashMap.put(child, levelHashMap.get(child)-1);          
            this.recursiveDecrease(child, classes, levelHashMap);
        }
    }
    /* ======================================================================= */    
}

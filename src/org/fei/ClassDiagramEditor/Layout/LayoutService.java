/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Layout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Layout.Package.PackageLayoutService;
import org.fei.ClassDiagramEditor.Element.Package.Package;

/**
 *
 * @author Mr.Phoenixxx
 */
public class LayoutService {
    private PackageLayoutService packageLayoutService;
    private Point startPoint;
    private int packageDistanceX;
    private int packageDistanceY;  

    public LayoutService(PackageLayoutService packageLayoutService, int packageDistanceX, int packageDistanceY, Point startPoint) {
        this.packageLayoutService = packageLayoutService;
        this.setDistances(packageDistanceX, packageDistanceY);
        this.setStartPoint(startPoint);
    }
    
    public final void setDistances(int packageDistanceX, int packageDistanceY) {
        this.packageDistanceX = packageDistanceX;
        this.packageDistanceY = packageDistanceY;
    }
    
    public final void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }
    
    public Dimension applyLayout(ArrayList<Package> packages) {
        ArrayList<Package> processed = new ArrayList<Package>(packages.size());      
        for (Package packageObj : packages) {
            processed = this.applyRecursively(packageObj, processed);
        }  
         
        // TODO: Sprav rozonavanie dedicnosti a podla toho ich daj pod seba
        int x = startPoint.x;
        int y = startPoint.y;
        int y_max = y;
        for (Package packageObj : packages) {
            if (packageObj.getParent() == null) {
                packageObj.setPosition(x, y);
                x = packageObj.getX() + packageObj.getWidth() + packageDistanceX;
                int i = packageObj.getY() + packageObj.getHeight() + packageDistanceY;
                if (i > y_max) y_max = i;
            }
        }       
        
        return new Dimension(x, y_max);
    }
    
    private ArrayList<Package> applyRecursively(Package packageObj, ArrayList<Package> processed) {
        if (processed.contains(packageObj)) return processed;
        
        for (Package p : packageObj.getSubPackages()) {
            this.applyRecursively(p, processed);
        }
        this.packageLayoutService.applyLayout(packageObj);
        processed.add(packageObj);        
        return processed;
    }
    
}

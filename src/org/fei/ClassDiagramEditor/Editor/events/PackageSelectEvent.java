/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events;

import java.util.EventObject;
import org.fei.ClassDiagramEditor.Data.PackageData;

/**
 *
 * @author Tomas
 */
public class PackageSelectEvent extends EventObject {
    
    private PackageData data;
    private int x, y;
    
    public PackageSelectEvent(Object source, PackageData data) {
        super(source);
        this.data = data;
        x = y = -1;
    }
    
    public PackageSelectEvent(Object source, PackageData data, int x, int y) {
        super(source);
        this.data = data;
        this.x = x;
        this.y = y;
    }
    
    public PackageData getPackageData() {
        return this.data;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
}

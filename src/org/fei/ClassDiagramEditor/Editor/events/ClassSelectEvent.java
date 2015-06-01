 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events;

import java.util.EventObject;
import org.fei.ClassDiagramEditor.Data.ClassData;

/**
 *
 * @author Tomas
 */
public class ClassSelectEvent extends EventObject {
    
    private ClassData classData;
    private int x, y;
    
    public ClassSelectEvent(Object source, ClassData classData) {
        super(source);
        this.classData = classData;
        x = y = -1;
    }
    
    public ClassSelectEvent(Object source, ClassData classData, int x, int y) {
        super(source);
        this.classData = classData;
        this.x = x;
        this.y = y;
    }
    
    public ClassData getClassData() {
        return classData;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }
}

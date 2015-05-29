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
public class ClassRenameEvent extends EventObject {
    
    private ClassData classData;
    
    public ClassRenameEvent(Object source, ClassData classData) {
        super(source);
        this.classData = classData;
    }
    
    public ClassData getClassData() {
        return classData;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events;

import java.util.EventObject;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;

/**
 *
 * @author Tomas
 */
public class ClassMethodEvent extends EventObject {
    
    private ClassData classData;
    private MethodData methodData;
    
    public ClassMethodEvent(Object source, ClassData classData, MethodData methodData) {
        super(source);
        this.classData = classData;
        this.methodData = methodData;
    }

    public ClassData getClassData() {
        return classData;
    }

    public MethodData getMethodData() {
        return methodData;
    }
}

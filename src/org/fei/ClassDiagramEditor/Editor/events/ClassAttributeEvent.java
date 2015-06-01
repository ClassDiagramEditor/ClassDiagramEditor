/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events;

import java.util.EventObject;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.VariableData;

/**
 *
 * @author Tomas
 */
public class ClassAttributeEvent extends EventObject {
    
    private ClassData classData;
    private VariableData variableData;
    
    public ClassAttributeEvent(Object source, ClassData classData, VariableData methodData) {
        super(source);
        this.classData = classData;
        this.variableData = methodData;
    }

    public ClassData getClassData() {
        return classData;
    }

    public VariableData getVariableData() {
        return variableData;
    }
    
}

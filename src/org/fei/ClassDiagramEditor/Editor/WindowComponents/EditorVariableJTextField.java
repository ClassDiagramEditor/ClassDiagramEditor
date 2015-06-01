/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.fei.ClassDiagramEditor.Editor.Parsers.AttributeParser;
import org.fei.ClassDiagramEditor.Editor.Parsers.AttributeParsingException;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.VariableData;

/**
 *
 * @author Tomas
 */
public class EditorVariableJTextField extends EditorJTextField {
    
    private ClassData classData = null;
    private VariableData variableData = null;
    
    public EditorVariableJTextField() {
        super();
         
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        //super.keyPressed(e);
        if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {            
            try {
                AttributeParser parser = new AttributeParser(this.getText(), classData.isEnumClass());
                String name = parser.getName();
                String type;
                if (classData.isEnum()) {
                    type = classData.getSimpleName();
                }
                else {
                    type = parser.getType();
                }
                Set<Modifier> modifiers = parser.getModifiers();
                
                if (classData.isEnum()) {
                    Refactoring.renameEnumConstant(variableData, classData, name);
                }
                else {
                   Refactoring.changeDeclaration(variableData, classData, name, type, modifiers);
                }
                
            } catch (AttributeParsingException ex) {
                this.setForeground(CUSTOM_RED);
            }
        }    
    }
    
    /**
     * @param classData the classData to set
     */
    public void setClassData(ClassData classData) {
        this.classData = classData;
    }

    /**
     * @param variableData the variableData to set
     */
    public void setVariableData(VariableData variableData) {
        this.variableData = variableData;
    }
}

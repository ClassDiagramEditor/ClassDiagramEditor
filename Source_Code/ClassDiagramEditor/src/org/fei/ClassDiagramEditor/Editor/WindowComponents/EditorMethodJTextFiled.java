/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.fei.ClassDiagramEditor.Editor.Parsers.MethodParser;
import org.fei.ClassDiagramEditor.Editor.Parsers.MethodParser.ParameterInfo;
import org.fei.ClassDiagramEditor.Editor.Parsers.MethodParsingException;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;
import org.fei.ClassDiagramEditor.WindowComponents.Message;

/**
 *
 * @author Tomas
 */
public class EditorMethodJTextFiled extends EditorJTextField {
    
    private MethodData methodData = null;
    private ClassData classData = null;
    
    public EditorMethodJTextFiled() {
        super();
        
        
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        
        if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {            
            try {
                MethodParser mparser = new MethodParser(this.getText());
                
                Set<Modifier> mod = mparser.getModifiers();
                String name = mparser.getName();
                ArrayList<ParameterInfo> pinfo = mparser.getParameters();
                String ret = mparser.getReturnType();
                
                Refactoring.changeDeclaration(methodData, classData, mod, name, pinfo, ret);
                
            } catch (MethodParsingException ex) {
                this.setForeground(CUSTOM_RED);
            }
        }    
    }

    /**
     * @param methodData the methodData to set
     */
    public void setMethodData(MethodData methodData) {
        this.methodData = methodData;
    }

    /**
     * @param classData the classData to set
     */
    public void setClassData(ClassData classData) {
        this.classData = classData;
    }
    
}

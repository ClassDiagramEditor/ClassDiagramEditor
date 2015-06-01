/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.event.KeyEvent;
import org.fei.ClassDiagramEditor.Editor.Parsers.ClassNameParser;
import org.fei.ClassDiagramEditor.Editor.Parsers.ClassNameParsingException;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Data.ClassData;


/**
 *
 * @author Tomas
 */
public class EditorClassNameJTextField extends EditorJTextField {
    
    private ClassData data;
    
    public EditorClassNameJTextField() {
        
        super();
        
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        
        if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {            
            
            try {
                ClassNameParser.CheckIfNameIsValid(this.getText());
                
                if (data != null)
                    Refactoring.rename(data, this.getText());
            
            } catch (ClassNameParsingException ex) {
                this.setForeground(CUSTOM_RED);
                this.repaint();
                return;
            }
        }    
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e); 
        
        try {
            ClassNameParser.CheckIfNameIsValid(this.getText());
        } catch (ClassNameParsingException ex) {
            this.setForeground(CUSTOM_RED);
        }
        
        this.repaint();
    }
    
    
    
    public void setData(ClassData data) {
        this.data = data;
    }
}

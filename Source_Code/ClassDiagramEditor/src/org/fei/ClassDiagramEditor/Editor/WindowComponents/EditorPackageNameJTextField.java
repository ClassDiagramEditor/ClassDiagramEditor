/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import org.fei.ClassDiagramEditor.Editor.Parsers.PackageNameParser;
import org.fei.ClassDiagramEditor.Editor.Parsers.PackageNameParsingException;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Data.PackageData;

/**
 *
 * @author Tomas
 */
public class EditorPackageNameJTextField extends EditorJTextField {
    
    private PackageData data = null;
    
    public EditorPackageNameJTextField() { 
        super();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e); 
        
        // todo enter
        if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
        
            try {
                HashMap<Integer, String> newNames = PackageNameParser.ParseName(originalText, this.getText());
                
                if (!newNames.isEmpty()) {
                    if (data != null)
                        Refactoring.rename(data, newNames);
                }
            } catch (PackageNameParsingException ex) {
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
            PackageNameParser.CheckIfNameIsValid(originalText, this.getText());
        } catch (PackageNameParsingException ex) {
            this.setForeground(CUSTOM_RED);
        }
        
        this.repaint();
    }

    /**
     * @param data the data to set
     */
    public void setData(PackageData data) {
        this.data = data;
    }
    
    
}

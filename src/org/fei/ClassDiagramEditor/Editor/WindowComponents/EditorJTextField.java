/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTextField;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tomas
 */
public class EditorJTextField extends JTextField implements KeyListener {
    

    protected String originalText;
    final protected Color CUSTOM_GREEN;
    final protected Color CUSTOM_RED;

    public EditorJTextField() {
        super();
        this.addKeyListener(this);
        CUSTOM_GREEN = new Color(53, 171, 69);
        CUSTOM_RED = new Color(220, 1, 57);

    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {   
        
        
        if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
            this.setText(originalText);
            this.setForeground(Color.black);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        if (!this.originalText.equals(this.getText())) {
            this.setForeground(CUSTOM_GREEN);
        }
        else {
            this.setForeground(Color.black);
        }
        
    }
    


    @Override
    public void setText(String t) {
        super.setText(t); 
        this.originalText = t;
        this.setForeground(Color.black);
        this.repaint();
    }
    
    
    
}

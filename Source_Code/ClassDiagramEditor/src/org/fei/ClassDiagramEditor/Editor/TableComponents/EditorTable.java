/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Color;

/**
 *
 * @author Tomas
 */
public abstract class EditorTable extends JTable implements FocusListener, KeyListener {
    
    public EditorTable() {
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addFocusListener(this);
        this.addKeyListener(this);
        this.setRowHeight(30);
    }

    @Override
    public void focusGained(FocusEvent e) {
        
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.clearSelection();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getExtendedKeyCode() == KeyEvent.VK_DELETE) {
        
            int selectedRow = this.getSelectedRow();
            
            if (selectedRow != -1) {
                onDeleteKeyPressed(selectedRow);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    abstract void onDeleteKeyPressed(int selectedRow);
}

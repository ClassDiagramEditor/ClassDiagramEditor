/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Tomas
 */
public class DeletePopup extends JPopupMenu {
    
    JMenuItem deleteBtn = new JMenuItem("Delete");
    
    public DeletePopup() {
        
        this.add(deleteBtn);
    }
}

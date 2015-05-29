/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents.Popups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Tomas
 */
public class EmptySpacePopup extends JPopupMenu{
    
    JMenuItem addSubpackageBtn = new JMenuItem("New package");
    

    public EmptySpacePopup(final int x, final int y) {

        addSubpackageBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new package:", "Add new package");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (!params.answer.isEmpty()) {
                        Refactoring.createPackage(null, params.answer, x, y);
                    }
                }
            }
        });
        
        add(addSubpackageBtn);
    }
}

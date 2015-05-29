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
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Tomas
 */
public class ClassPopup extends JPopupMenu {
    
    JMenuItem deleteBtn = new JMenuItem("Delete");
    JMenuItem addInnerClassBtn = new JMenuItem("New inner class");
    
    public ClassPopup(final ClassData data) {
        
        addInnerClassBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                                Message.InputParams params = Message.showInputLine("Enter name for new inner class:", "Add new inner class");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    
                    if (data != null && !params.answer.isEmpty()) {
                        Refactoring.createInnerClass(data, params.answer);
                    }
                }
            }
        });
        
        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (data == null) {
                    return;
                }

                Refactoring.safeDelete(data);
            }
        });
    
        this.add(deleteBtn);
        this.add(addInnerClassBtn);
    }
}

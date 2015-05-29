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
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Tomas
 */
public class PackagePopup extends JPopupMenu {
    
    JMenuItem deleteBtn = new JMenuItem("Delete");
    JMenuItem addSubpackageBtn = new JMenuItem("New subpackage");
    JMenuItem addClassBtn = new JMenuItem("New class");
    JMenuItem addAbstractClassBtn = new JMenuItem("New abstract class");
    JMenuItem addInterfaceBtn = new JMenuItem("New interface");
    JMenuItem addEnumBtn = new JMenuItem("New enum");
    
    public PackagePopup(final PackageData pcg, final int x, final int y) {
        
        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
        
                if (pcg == null)
                    return;
                
                Refactoring.safeDelete(pcg);
            }
        });
        
        addClassBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new class:", "Add new class");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (pcg != null && !params.answer.isEmpty()) {
                        Refactoring.createClass(pcg, params.answer, false, x, y);
                    }
                }
            }
        });
        
        addAbstractClassBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new class:", "Add new class");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (pcg != null && !params.answer.isEmpty()) {
                        Refactoring.createClass(pcg, params.answer, true, x, y);
                    }
                }
            }
        });
        
        addInterfaceBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new interface:", "Add new interface");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (pcg != null && !params.answer.isEmpty()) {
                        Refactoring.createInterface(pcg, params.answer, x, y);
                    }
                }
            }
        });
        
        addEnumBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new enum:", "Add new enum");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (pcg != null && !params.answer.isEmpty()) {
                        Refactoring.createEnum(pcg, params.answer, x, y);
                    }
                }
            }
        });
        
        addSubpackageBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new package:", "Add new package");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (pcg != null && !params.answer.isEmpty()) {
                        Refactoring.createPackage(pcg, params.answer, x, y);
                    }
                }
            }
        });
    
        add(deleteBtn);
        add(addSubpackageBtn);
        add(addClassBtn);
        add(addAbstractClassBtn);
        add(addInterfaceBtn);
        add(addEnumBtn);
        
        if (pcg.isDefaultPackage()) {
            deleteBtn.setVisible(false);
            addClassBtn.setVisible(false);
            addAbstractClassBtn.setVisible(false);
            addInterfaceBtn.setVisible(false);
            addEnumBtn.setVisible(false);        
        }
    }
}

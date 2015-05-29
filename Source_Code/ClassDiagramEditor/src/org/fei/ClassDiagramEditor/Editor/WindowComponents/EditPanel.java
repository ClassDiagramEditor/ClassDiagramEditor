/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSelectListener;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSelectListener;

/**
 *
 * @author Tomas
 */
public class EditPanel extends JPanel implements ClassSelectListener, PackageSelectListener {
    
    private ClassEditPanel classEditPanel = new ClassEditPanel();
    private PackageEditPanel packageEditPanel = new PackageEditPanel();
    
    
    public EditPanel() {
                
        this.setLayout(new BorderLayout());
    }


    public ClassEditPanel getClassEditPanel() {
        return classEditPanel;
    }    
    
    public PackageEditPanel getPackageEditPanel() {
        return packageEditPanel;
    }

    @Override
    public void ClassSelect(ClassSelectEvent e) {
        this.add(classEditPanel, BorderLayout.CENTER);
        this.repaint();
    }

    @Override
    public void ClassUnselect() {
        this.remove(classEditPanel);
        this.repaint();
    }

    @Override
    public void packageSelect(PackageSelectEvent e) {
        this.add(packageEditPanel, BorderLayout.CENTER);
        this.repaint();
    }

    @Override
    public void packageUnselect() {
        this.remove(packageEditPanel);
        this.repaint();
    }
}

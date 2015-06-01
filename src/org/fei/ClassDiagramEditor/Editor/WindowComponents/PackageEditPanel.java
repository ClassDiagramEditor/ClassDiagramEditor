/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.Editor.TableComponents.PackageEditTable;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.PackageEditTableModel;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSelectListener;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Tomas
 */
public class PackageEditPanel extends JPanel implements PackageSelectListener, PackageRenameListener {

    private JLabel packageLabel = new JLabel("Package");
    
    private PackageEditTable packageEditTable = new PackageEditTable();
    
    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JPanel panel31 = new JPanel();
    private JPanel panel311 = new JPanel();
    private JPanel panel41 = new JPanel();
    private JPanel panel411 = new JPanel();
    private JPanel panel51 = new JPanel();
    private JPanel panel511 = new JPanel();
    private JPanel panel61 = new JPanel();
    private JPanel panel611 = new JPanel();
    
    private JButton deleteBtn = new JButton("Delete");
    private JLabel addnewLabel = new JLabel("Add new:");
    private JButton addSubpackageBtn = new JButton("Subpackage");
    private JButton addClassBtn = new JButton("Class");
    private JCheckBox isAbstractChck = new JCheckBox("abstract");
    private JButton addInterfaceBtn = new JButton("Interface");
    private JButton addEnumBtn = new JButton("Enum");
    
    private PackageData data = null;
    
    public PackageEditPanel() {
        
        this.setLayout(new BorderLayout());
        
        packageEditTable.setFillsViewportHeight(true);
        packageEditTable.setMinimumSize(new Dimension(150, 50));
        
        panel1.setLayout(new BorderLayout());
        panel2.setLayout(new BorderLayout());
        panel31.setLayout(new BorderLayout());
        panel41.setLayout(new BorderLayout());
        panel51.setLayout(new BorderLayout());
        panel61.setLayout(new BorderLayout());
        panel311.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel411.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel511.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel611.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        panel2.add(packageEditTable, BorderLayout.NORTH);
        
        panel1.add(packageLabel, BorderLayout.NORTH);
        panel1.add(panel2, BorderLayout.CENTER);
        
        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
        
                if (data == null)
                    return;
                
                Refactoring.safeDelete(data);
            }
        });
        
        addClassBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new class:", "Add new class");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (data != null && !params.answer.isEmpty()) {
                        Refactoring.createClass(data, params.answer, isAbstractChck.isSelected());
                    }
                }
            }
        });
        
        addInterfaceBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new interface:", "Add new interface");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (data != null && !params.answer.isEmpty()) {
                        Refactoring.createInterface(data, params.answer);
                    }
                }
            }
        });
        
        addEnumBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new enum:", "Add new enum");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (data != null && !params.answer.isEmpty()) {
                        Refactoring.createEnum(data, params.answer);
                    }
                }
            }
        });
        
        addSubpackageBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Message.InputParams params = Message.showInputLine("Enter name for new package:", "Add new package");
                
                if (params.option == NotifyDescriptor.OK_OPTION) {
                    if (data != null && !params.answer.isEmpty()) {
                        Refactoring.createPackage(data, params.answer);
                    }
                }
            }
        });
        
        panel311.add(deleteBtn);
        panel31.add(panel311, BorderLayout.NORTH);
        panel31.add(panel41, BorderLayout.CENTER);
        
        panel411.add(addnewLabel);
        panel41.add(panel411, BorderLayout.NORTH);
        panel41.add(panel61, BorderLayout.CENTER);
        
        panel611.add(addClassBtn);
        panel611.add(isAbstractChck);
        panel61.add(panel611, BorderLayout.NORTH);
        panel61.add(panel51, BorderLayout.CENTER);
        
        panel511.add(addSubpackageBtn);
        panel511.add(addInterfaceBtn);
        panel511.add(addEnumBtn);
        panel51.add(panel511, BorderLayout.NORTH);


        this.add(panel31, BorderLayout.NORTH);
        this.add(panel1, BorderLayout.CENTER);
        
        this.setVisible(false);
        
        
        RefactoringEvents.addPackageRenameListener(this);
    }
    
    
    

    @Override
    public void packageSelect(PackageSelectEvent e) {
        
        packageEditTable.setModel(new PackageEditTableModel(e.getPackageData()));
        
        data = e.getPackageData();
        
        if (data.isDefaultPackage()) {
            deleteBtn.setEnabled(false);
            this.addClassBtn.setEnabled(false);
            this.addEnumBtn.setEnabled(false);
            this.addInterfaceBtn.setEnabled(false);
            this.isAbstractChck.setEnabled(false);
        }
        else {
            deleteBtn.setEnabled(true);
            this.addClassBtn.setEnabled(true);
            this.addEnumBtn.setEnabled(true);
            this.addInterfaceBtn.setEnabled(true);
            this.isAbstractChck.setEnabled(true);
        }
        
        this.setVisible(true);      
        this.repaint();
    }

    @Override
    public void packageUnselect() {
        
        data = null;
        
        this.setVisible(false);      
        this.repaint();
    }

    @Override
    public void onPackageRename(PackageSelectEvent e) {
        
        packageEditTable.setModel(new PackageEditTableModel(e.getPackageData()));
        
        data = e.getPackageData();
        
        this.setVisible(true);      
        this.repaint();
    }
    
}

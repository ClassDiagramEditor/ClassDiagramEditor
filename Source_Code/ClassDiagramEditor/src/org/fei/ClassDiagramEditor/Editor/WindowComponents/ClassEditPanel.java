/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.WindowComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.Editor.TableComponents.ClassEditTable;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.ClassNameTableModel;
import org.fei.ClassDiagramEditor.Editor.TableComponents.MethodEditTable;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.MethodsTableModel;
import org.fei.ClassDiagramEditor.Editor.TableComponents.VariableEditTable;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.VariablesTableModel;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Editor.events.ClassAttributeEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassMethodEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassRenameEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddMethodListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAttributeSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassChangeAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodChangeDeclarationListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSelectListener;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Tomas
 */
public class ClassEditPanel extends JPanel implements ClassSelectListener, ClassRenameListener,
        ClassMethodSafeDeleteListener, ClassAttributeSafeDeleteListener, ClassMethodChangeDeclarationListener, 
        ActionListener, ClassAddAttributeListener, ClassAddMethodListener, ClassChangeAttributeListener {

    private ClassEditTable classEditTable = new ClassEditTable();
    private MethodEditTable methodEditTable = new MethodEditTable();
    private VariableEditTable variablesEditTable = new VariableEditTable();
    private JLabel methodsLabel = new JLabel("Methods");
    private JLabel attributesLabel = new JLabel("Attributes");
    // class panels
    private JPanel panel1 = new JPanel();
    private JPanel panel2 = new JPanel();
    private JPanel panel3 = new JPanel();
    private JPanel panel4 = new JPanel();
    private JPanel panel5 = new JPanel();
    private JPanel panel31 = new JPanel();
    private JPanel panel51 = new JPanel();
    private JPanel panel311 = new JPanel();
    private JPanel panel511 = new JPanel();
    private JPanel panel6 = new JPanel();
    private JPanel panel61 = new JPanel();
    private JPanel panel611 = new JPanel();
    private JPanel panel62 = new JPanel();
    private JPanel panel621 = new JPanel();
    private JPanel panel63 = new JPanel();
    private JPanel panel631 = new JPanel();
    private JButton deleteBtn = new JButton("Delete");
    private JLabel addnewLabel = new JLabel("Add new:");
    private JButton addInnerClass = new JButton("Inner class");
    
    private ClassData data = null;
    
    private JButton addAttribute = new JButton("+");
    private JButton addMethod = new JButton("+");

    public ClassEditPanel() {

        this.setLayout(new BorderLayout());
        panel1.setLayout(new BorderLayout());
        panel2.setLayout(new BorderLayout());
        panel3.setLayout(new BorderLayout());
        panel4.setLayout(new BorderLayout());
        panel5.setLayout(new BorderLayout());
        panel31.setLayout(new BorderLayout());
        panel51.setLayout(new BorderLayout());
        panel311.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel511.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel611.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel621.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel631.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel61.setLayout(new BorderLayout());
        panel62.setLayout(new BorderLayout());
        panel63.setLayout(new BorderLayout());

        
        classEditTable.setFillsViewportHeight(true);
        classEditTable.setMinimumSize(new Dimension(150, 150));

        methodEditTable.setFillsViewportHeight(true);
        methodEditTable.setMinimumSize(new Dimension(150, 150));

        variablesEditTable.setFillsViewportHeight(true);
        variablesEditTable.setMinimumSize(new Dimension(150, 150));
        
        
        
        panel51.add(variablesEditTable, BorderLayout.NORTH);
        panel511.add(addAttribute);
        panel51.add(panel511, BorderLayout.CENTER);
        panel5.add(panel51, BorderLayout.NORTH);
        panel5.add(panel2, BorderLayout.CENTER);
        

       
        panel4.add(attributesLabel, BorderLayout.NORTH);
        panel4.add(panel5, BorderLayout.CENTER);

        panel31.add(methodEditTable, BorderLayout.NORTH);
        panel311.add(addMethod);
        panel31.add(panel311, BorderLayout.CENTER);
        panel3.add(panel31, BorderLayout.NORTH);
  

        panel2.add(methodsLabel, BorderLayout.NORTH);
        panel2.add(panel3, BorderLayout.CENTER);

        panel1.add(classEditTable, BorderLayout.NORTH);
        panel1.add(panel4, BorderLayout.CENTER);

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (data == null) {
                    return;
                }

                Refactoring.safeDelete(data);
            }
        });
        
        addInnerClass.addActionListener(new ActionListener() {

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
        
        addMethod.addActionListener(this);
        addAttribute.addActionListener(this);

        panel611.add(deleteBtn);
        panel61.add(panel611, BorderLayout.NORTH);
        
        panel631.add(addnewLabel);
        panel63.add(panel631, BorderLayout.NORTH);
        
        panel621.add(addInnerClass);
        panel62.add(panel621, BorderLayout.NORTH);
        
        panel61.add(panel63, BorderLayout.CENTER);
        panel63.add(panel62, BorderLayout.CENTER);
                
        this.add(panel61, BorderLayout.NORTH);
        this.add(panel1, BorderLayout.CENTER);

        this.setVisible(false);

        RefactoringEvents.addClassRenameEventListener(this);
        RefactoringEvents.addClassMethodSafeDeleteListener(this);
        RefactoringEvents.addClassAttributeSafeDeleteListener(this);
        RefactoringEvents.addClassMethodChangeDeclarationListener(this);
        RefactoringEvents.addClassAddAttributeListener(this);
        RefactoringEvents.addClassAddMethodListener(this);
        RefactoringEvents.addClassChangeAttributeListener(this);
    }

    @Override               
    public void ClassSelect(ClassSelectEvent e) {

        classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
        methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
        variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));

        this.data = e.getClassData();
        
        if (data.isEnum())
            this.addAttribute.setEnabled(false);
        else
            this.addAttribute.setEnabled(true);

        this.setVisible(true);
        this.repaint();
    }

    @Override
    public void ClassUnselect() {

        this.data = null;
        methodEditTable.clearNewRows();
        variablesEditTable.clearNewRows();

        this.setVisible(false);
        this.repaint();
    }

    @Override
    public void ClassRenamed(ClassRenameEvent e) {

        classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
        methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
        variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));

        this.data = e.getClassData();

        this.setVisible(true);
        this.repaint();
    }

    @Override
    public void onMethodSafeDelete(ClassMethodEvent e) {

        if (this.data == e.getClassData()) {
            this.data.getMethods().remove(e.getMethodData());

            classEditTable.setModel(new ClassNameTableModel(data));
            methodEditTable.setModel(new MethodsTableModel(data));
            variablesEditTable.setModel(new VariablesTableModel(data));

            this.setVisible(true);
            this.repaint();
        }
    }

    @Override
    public void onAttributeSafeDelete(ClassAttributeEvent e) {

        if (this.data == e.getClassData()) {
            this.data.getVariables().remove(e.getVariableData());

            classEditTable.setModel(new ClassNameTableModel(data));
            methodEditTable.setModel(new MethodsTableModel(data));
            variablesEditTable.setModel(new VariablesTableModel(data));

            this.setVisible(true);
            this.repaint();
        }
    }

    @Override
    public void onMethodChangeDeclaration(ClassMethodEvent e) {

        if (this.data == e.getClassData()) {


            classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
            methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
            variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));

            this.data = e.getClassData();

            this.setVisible(true);
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == addMethod) {
            methodEditTable.addRow();
            methodEditTable.updateUI();
        }
        if (e.getSource() == addAttribute) {
            variablesEditTable.addRow();
            variablesEditTable.updateUI();
        }
        
        this.repaint();
    }

    @Override
    public void onAddAttribute(ClassAttributeEvent e) {
        
        if (this.data == e.getClassData()) {

            classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
            methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
            variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));
            methodEditTable.clearNewRows();
            variablesEditTable.clearNewRows();
            
            this.data = e.getClassData();

            this.setVisible(true);
            this.repaint();
        }
    }

    @Override
    public void onAddMethod(ClassMethodEvent e) {
        
        if (this.data == e.getClassData()) {
            
            classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
            methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
            variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));
            methodEditTable.clearNewRows();
            variablesEditTable.clearNewRows();
            
            this.data = e.getClassData();
            
            this.setVisible(true);
            this.repaint();
        }
    }

    @Override
    public void onChangeAttribute(ClassAttributeEvent e) {
        
        if (this.data == e.getClassData()) {


            classEditTable.setModel(new ClassNameTableModel(e.getClassData()));
            methodEditTable.setModel(new MethodsTableModel(e.getClassData()));
            variablesEditTable.setModel(new VariablesTableModel(e.getClassData()));

            this.data = e.getClassData();
            this.setVisible(true);
            this.repaint();
        }
    }
}

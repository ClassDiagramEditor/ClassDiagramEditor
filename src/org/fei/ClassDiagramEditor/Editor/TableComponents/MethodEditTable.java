/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.MethodsTableModel;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorAddMethodJTextField;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorMethodJTextFiled;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;

/**
 *
 * @author Tomas
 */
public class MethodEditTable extends EditorTable {
    
    private EditorMethodJTextFiled methodEdit = new EditorMethodJTextFiled();
    private EditorAddMethodJTextField methodAdd = new EditorAddMethodJTextField();
    private int newRows = 0;
    
    public MethodEditTable() {
        super();
        methodAdd.setBackground(gridColor);
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        
        if (row - ((MethodsTableModel)getModel()).getDataSize() < 0) {    // ak su nove riadky skoci do else
            MethodData methodData = ((MethodsTableModel)getModel()).getSelectedData(row);
            ClassData classData = ((MethodsTableModel)getModel()).getClassData();
            methodEdit.setMethodData(methodData);
            methodEdit.setClassData(classData);
            return new DefaultCellEditor(methodEdit);
        }
        else {
            ClassData classData = ((MethodsTableModel)getModel()).getClassData();
            methodAdd.setClassData(classData);
            return new DefaultCellEditor(methodAdd);
        }
    } 
    

    @Override
    void onDeleteKeyPressed(int selectedRow) {
        
        if (selectedRow - ((MethodsTableModel)getModel()).getDataSize() < 0) { // ak su nove riadky skoci do else
            MethodData methodData = ((MethodsTableModel)getModel()).getSelectedData(selectedRow);
            ClassData classData = ((MethodsTableModel)getModel()).getClassData();

            Refactoring.safeDelete(methodData, classData);
        }
        else {
            newRows--;
            ((MethodsTableModel)getModel()).setEmptyRows(newRows);
            this.updateUI();
        }
    }
    
    public void addRow() {
        newRows++;
        ((MethodsTableModel)getModel()).setEmptyRows(newRows);
    }
    
    public void clearNewRows() {
        newRows = 0;
    }
}

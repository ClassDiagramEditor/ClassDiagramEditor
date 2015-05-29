/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents;


import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import org.fei.ClassDiagramEditor.Editor.Refactoring.Refactoring;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.VariablesTableModel;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorAddVariableJTextField;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorVariableJTextField;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.VariableData;


/**
 *
 * @author Tomas
 */
public class VariableEditTable extends EditorTable {
    
    private EditorVariableJTextField editVariable = new EditorVariableJTextField();
    private EditorAddVariableJTextField addVariable = new EditorAddVariableJTextField();
    private int newRows = 0;
    
    public VariableEditTable() {
        
        super(); 
   }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        
        if (row - ((VariablesTableModel)getModel()).getDataSize() < 0) {    // ak su nove riadky skoci do else
            VariableData variableData = ((VariablesTableModel)getModel()).getSelectedData(row);
            ClassData classData = ((VariablesTableModel)getModel()).getClassData();
            editVariable.setVariableData(variableData);
            editVariable.setClassData(classData);
            return new DefaultCellEditor(editVariable);
        }
        else {
            
            ClassData classData = ((VariablesTableModel)getModel()).getClassData();
            addVariable.setClassData(classData);
            return new DefaultCellEditor(addVariable);
                   
        }
    }
    
    

    @Override
    void onDeleteKeyPressed(int selectedRow) {
        
        if (selectedRow - ((VariablesTableModel)getModel()).getDataSize() < 0) {
            VariableData variableData = ((VariablesTableModel)getModel()).getSelectedData(selectedRow);
            ClassData classData = ((VariablesTableModel)getModel()).getClassData();
        
            Refactoring.safeDelete(variableData, classData);
        }
        else {
            newRows--;
            ((VariablesTableModel)getModel()).setEmptyRows(newRows);
            this.updateUI();
        }
    }
    
    public void addRow() {
        newRows++;
        ((VariablesTableModel)getModel()).setEmptyRows(newRows);
    }
    
    public void clearNewRows() {
        newRows = 0;
    }
    
}

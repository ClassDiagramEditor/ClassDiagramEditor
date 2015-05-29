/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.VariableData;
import org.fei.ClassDiagramEditor.ViewData.VariableTextLook;

/**
 *
 * @author Tomas
 */
public class VariablesTableModel extends AbstractTableModel {
    
    private ArrayList<VariableData> data;
    private ClassData classData;
    private int emptyRows = 0;
    
    public VariablesTableModel(ClassData data) {
        this.data = data.getVariables();
        classData = data;
        
        
    }

    @Override
    public int getRowCount() {
        return data.size() + emptyRows;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex - data.size() < 0) { // ak su aj prazdne riadky padne do else
            VariableTextLook variableTextLook = new VariableTextLook(data.get(rowIndex), classData.isEnum());
            return variableTextLook.toString();
        }
        else {
            return "";
        }
    }
    
    @Override
    public String getColumnName(int col) {
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }
    
    public VariableData getSelectedData(int row) {
        
        if (row < 0)
            return null;
        
        if (row >= data.size())
            return null;
        
        return data.get(row);
    }
    
    public ClassData getClassData() {
        return this.classData;
    }
    
    /**
     * @return the emptyRows
     */
    public int getEmptyRows() {
        return emptyRows;
    }

    /**
     * @param emptyRows the emptyRows to set
     */
    public void setEmptyRows(int emptyRows) {
        this.emptyRows = emptyRows;
    }

    public int getDataSize() {
        return data.size();
    }
}

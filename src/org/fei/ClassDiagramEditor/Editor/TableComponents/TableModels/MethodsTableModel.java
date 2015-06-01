/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;
import org.fei.ClassDiagramEditor.ViewData.MethodTextLook;

/**
 *
 * @author Tomas
 */
public class MethodsTableModel extends AbstractTableModel {
    
    private ArrayList<MethodData> data;
    private ClassData classData;
    private int emptyRows = 0;
    
    public MethodsTableModel(ClassData data) {
        this.data = data.getMethods();
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
            MethodTextLook methodTextLook = new MethodTextLook(data.get(rowIndex), classData);
            return methodTextLook.toString();           
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
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public MethodData getSelectedData(int row) {
        
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

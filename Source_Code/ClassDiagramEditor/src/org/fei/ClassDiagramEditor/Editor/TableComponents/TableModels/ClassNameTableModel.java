/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels;

import javax.swing.table.AbstractTableModel;
import org.fei.ClassDiagramEditor.Data.ClassData;

/**
 *
 * @author Tomas
 */
public class ClassNameTableModel extends AbstractTableModel {
    
    ClassData classData;

    private Object[][] data = {
        {"Package", ""},
        {"Class", ""},
        {"Visibility", ""},
        {"Modificator", ""}
    };
    
    public ClassNameTableModel(ClassData clsData) {

        data[0][1] = clsData.getPackageName();
        data[1][1] = clsData.getSimpleName();
        data[2][1] = clsData.accessToFullString();
        data[3][1] = clsData.modificatorToString();
               
        classData = clsData;
    }
    
    @Override
    public int getRowCount() {
        
        return data.length;
    }

    @Override
    public int getColumnCount() {
        
        return data[0].length;
    }

    @Override
    public String getColumnName(int col) {
        return null;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
/*    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
*/
    @Override
    public boolean isCellEditable(int row, int col) {
        if (row == 1 && col == 1) {
            return true;
        }
        return false;
    }
 
    public ClassData getClassData() {
        return this.classData;
    }
}

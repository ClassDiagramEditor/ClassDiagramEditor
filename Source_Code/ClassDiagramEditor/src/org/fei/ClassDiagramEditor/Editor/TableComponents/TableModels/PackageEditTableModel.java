/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels;

import javax.swing.table.AbstractTableModel;
import org.fei.ClassDiagramEditor.Data.PackageData;

/**
 *
 * @author Tomas
 */
public class PackageEditTableModel extends AbstractTableModel {
    
    private Object[] data = {""};
    private PackageData packageData;

    
    public PackageEditTableModel(PackageData packageData) {

        data[0] = packageData.getName();

        this.packageData = packageData;
    }
    
    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int col) {
        return null;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row];
    }
    
/*    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
*/
    @Override
    public boolean isCellEditable(int row, int col) {
        if (getPackageData().isDefaultPackage())
            return false;
        else
            return true;
    }

    /**
     * @return the packageData
     */
    public PackageData getPackageData() {
        return packageData;
    }
    
   
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.PackageEditTableModel;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorPackageNameJTextField;

/**
 *
 * @author Tomas
 */
public class PackageEditTable extends EditorTable {
    
    EditorPackageNameJTextField editor;

    public PackageEditTable() {
        super();
        
        editor = new EditorPackageNameJTextField();
    }

    @Override
    void onDeleteKeyPressed(int selectedRow) {
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return new DefaultCellEditor(editor);
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel); 
        
        if (dataModel instanceof PackageEditTableModel) {
            editor.setData(((PackageEditTableModel)this.getModel()).getPackageData());
        }
    }
    
    
}

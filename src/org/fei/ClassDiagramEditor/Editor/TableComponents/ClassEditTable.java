/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.TableComponents;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.fei.ClassDiagramEditor.Editor.TableComponents.TableModels.ClassNameTableModel;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditorClassNameJTextField;

/**
 *
 * @author Tomas
 */
public class ClassEditTable extends EditorTable {
    
    EditorClassNameJTextField className = new EditorClassNameJTextField();
    
    public ClassEditTable() {
        super();

    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        
        if (row == 1 && column == 1) {
            return new DefaultCellEditor(className);
        }
        return super.getCellEditor(row, column); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        
        if (dataModel instanceof ClassNameTableModel)
            className.setData(((ClassNameTableModel)this.getModel()).getClassData());
    }
    
    

    @Override
    void onDeleteKeyPressed(int selectedRow) {
    }
    
}

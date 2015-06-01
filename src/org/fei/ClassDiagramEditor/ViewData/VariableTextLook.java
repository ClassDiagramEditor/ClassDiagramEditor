/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.ViewData;

import org.fei.ClassDiagramEditor.Data.VariableData;


/**
 *
 * @author Tomas
 */
public class VariableTextLook extends VariableViewData {
    
    private boolean isEnumConstant;
    
    public VariableTextLook(VariableData variable, boolean isEnum) {
        super(variable);
        this.isEnumConstant = isEnum;
    }
    
    @Override
    public String toString() {
            
        if (isEnumConstant && var.getType() == "enum") {
                return var.getName(); 
        }
        
        String str = new String();
        
        if (var.isStaticMember())
            str += "s";

        // pridanie medzery ak je treba
        if (str.length() > 0)
            str += " ";
        
        str += super.toString();
        
        return str;
    }
}

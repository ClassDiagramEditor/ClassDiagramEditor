/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.ViewData;

import java.lang.reflect.Modifier;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;

/**
 *
 * @author Tomas
 */
public class MethodTextLook extends MethodViewData {
    
    public MethodTextLook(MethodData method, ClassData classData) {
        super(method, classData);
    }
    
    @Override
    public String toString() {
        
        String str = new String();
        
        if (met.isStaticMember())
            str += "s";
        if (met.isAbstract() && !data.isInterface())
            str += "a";
        
        // pridanie medzery ak je treba
        if (str.length() > 0)
            str += " ";
        
        str += super.toString();
        
        return str;
    }
}

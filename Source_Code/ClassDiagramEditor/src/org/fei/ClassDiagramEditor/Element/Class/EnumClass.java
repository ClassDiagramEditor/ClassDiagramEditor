/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;

/**
 * Konecna trieda pre graficku reprezentaciu enum.
 * 
 * @author Tomas
 */
public class EnumClass extends LabeledClass {

    public EnumClass(int cornerX, int cornerY, ClassData classData, ArrayList<MethodLook> methodLook, ArrayList<VariableLook> variableLook) {
        super(cornerX, cornerY, classData, methodLook, variableLook, "«enumeration»", true, false);
    }
}
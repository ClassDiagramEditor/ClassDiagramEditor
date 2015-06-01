/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

import java.awt.Font;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.ViewData.MethodLook;
import org.fei.ClassDiagramEditor.ViewData.VariableLook;

/**
 * Konecna trieda pre graficku reprezentaciu normalnej triedy.
 * 
 * @author Tomas
 */
public class ClassDrawing extends PlaneClass {
    
    public ClassDrawing(int cornerX, int cornerY, ClassData classData, ArrayList<MethodLook> methodLook, ArrayList<VariableLook> variableLook) {
        super(cornerX, cornerY, classData, methodLook, variableLook);

    }
    
    @Override
    protected Font setClassNameFont() {
        return new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
    }
}

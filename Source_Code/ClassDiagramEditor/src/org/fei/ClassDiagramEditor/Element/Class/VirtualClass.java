/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Element.Class;

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * 
 * 
 * @author Mr.Phoenixxx
 */
public class VirtualClass extends PlaneClass {
    
    public VirtualClass(int cornerX, int cornerY) {
        super(cornerX, cornerY, null, null, null);
    }
    
    @Override
    protected Font setClassNameFont() {
        return null;
    }   

    @Override
    public void draw(Graphics g, ArrayList<Class> classes) {}
    
    @Override
    protected void parseGeneric() {}
    
}

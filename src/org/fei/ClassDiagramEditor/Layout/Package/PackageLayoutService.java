/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Layout.Package;

import java.awt.Graphics;
import org.fei.ClassDiagramEditor.Element.Package.Package;

/**
 *
 * @author Mr.Phoenixxx
 */
abstract public class PackageLayoutService {
    protected Graphics graphics;

    public PackageLayoutService(Graphics graphics) {
        this.graphics = graphics;
    }
    
    abstract public Package applyLayout(Package packageObj);    
}

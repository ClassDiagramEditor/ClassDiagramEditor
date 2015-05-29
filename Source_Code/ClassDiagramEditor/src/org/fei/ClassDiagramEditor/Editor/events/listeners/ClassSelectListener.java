/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events.listeners;

import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;

/**
 *
 * @author Tomas
 */
public interface ClassSelectListener {
    public void ClassSelect(ClassSelectEvent e);
    public void ClassUnselect();
}

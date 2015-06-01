/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events.listeners;

import org.fei.ClassDiagramEditor.Editor.events.ClassRenameEvent;

/**
 * reakcia na uspesnu udalost premenovania triedy
 * @author Tomas
 */
public interface ClassRenameListener {
    public void ClassRenamed(ClassRenameEvent e);
}

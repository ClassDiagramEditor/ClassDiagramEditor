/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.events.listeners;

import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;

/**
 *
 * @author Tomas
 */
public interface PackageSelectListener {
    public void packageSelect(PackageSelectEvent e);
    public void packageUnselect();
}

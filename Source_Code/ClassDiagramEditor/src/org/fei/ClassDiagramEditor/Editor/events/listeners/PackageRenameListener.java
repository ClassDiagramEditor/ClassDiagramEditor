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
public interface PackageRenameListener {
    
    void onPackageRename(PackageSelectEvent e);
}

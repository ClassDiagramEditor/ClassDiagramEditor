/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor;

/**
 *
 * @author mairo744
 */
public class ProjectScannerFactory {
    
    private static ProjectScanner ps = null;
    
    public static void createProjectScanner(ProjectScanner ps) {
        ProjectScannerFactory.ps = ps;
    }
    
    public static ProjectScanner getProjectScanner() {
        return ProjectScannerFactory.ps;
    }
}

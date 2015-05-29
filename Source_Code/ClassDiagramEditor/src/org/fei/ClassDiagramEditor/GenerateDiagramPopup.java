/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor;

import org.fei.ClassDiagramEditor.GenerateDiagramMainProject;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;

import java.net.URL;
import javax.swing.SwingUtilities;
import org.fei.ClassDiagramEditor.ProjectScanner;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mairo744
 */

@ActionID(id = "org.myorg.helloyou.DemoAction", category = "SomeFolder")
@ActionRegistration(displayName = "Generate Class diagram")
@ActionReference(path = "Projects/Actions")
public class GenerateDiagramPopup extends AbstractAction implements ContextAwareAction{
    public @Override void actionPerformed(ActionEvent e) {assert false;}
    public @Override Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }
    
    // vypinanie tlacitka
    private static boolean enabling = true;
    
    private static final class ContextAction extends AbstractAction {
        
  
         private final Project p;
        private ProjectScanner projectScanner;
        // background thread pool
        private static final RequestProcessor RP = new RequestProcessor(GenerateDiagramMainProject.class);
  
        public ContextAction(Lookup context) {
            p = context.lookup(Project.class);
            String name = ProjectUtils.getInformation(p).getDisplayName();
           
            
            ProjectScannerFactory.createProjectScanner(new ProjectScanner(p));
            projectScanner = ProjectScannerFactory.getProjectScanner();
            
            if (projectScanner.isProjectValid() && GenerateDiagramPopup.enabling) {
                setEnabled(true);                
            }
            else {           
                setEnabled(false);
            }
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            // TODO menu item label with optional mnemonics
            putValue(NAME, "Generate Class diagram");
        }

        public @Override void actionPerformed(ActionEvent e) {        
            // zalozit nove vlakno
            // vypnut tlacitko
            // na konci pracovneho vlakna zapnut akciu

            GenerateDiagramPopup.setEnabling(false);
            
            Thread t = new Thread(new GetAllProjectsAndRunParser(this, p));
            t.start();
        }
    } 
     
    public static void setEnabling(boolean enabling) {
            GenerateDiagramPopup.enabling = enabling;
    }
       
}

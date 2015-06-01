/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.fei.ClassDiagramEditor.WindowComponents.MessageJPanel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Trieda prida tlacitko Generate class diagram do panulu nastrojov (taka siva baseballova lopticka)
 * 
 * Tlacitko sa pokusi vygenerovat diagramy z projektu oznaceneho ako hlavny.
 * Ak projekt nie je podporovana Java SE aplikacia, vypise o tom informacnu spravu a diagram nevygeneruje.
 * @author Tomas
 */
@ActionID(category = "Build",
id = "org.fei.ClassDiagram.GenerateDiagramMainProject")
@ActionRegistration(iconBase = "org/fei/ClassDiagramEditor/ball.png",
displayName = "#CTL_GenerateDiagramAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/ClassDiagramPlugin", position = 0)
})
@NbBundle.Messages("CTL_GenerateDiagramAction=Generate Class Diagram")

/**
 *
 * @author mairo744
 */
public final class GenerateDiagramMainProject extends AbstractAction implements ActionListener{
    
    // background thread pool
    private static final RequestProcessor RP = new RequestProcessor(GenerateDiagramMainProject.class);

    
    public void actionPerformed(ActionEvent e) {
        
        // inicializacia referencie na otvorene projekty v IDE
        OpenProjects open = OpenProjects.getDefault();
        // ziskanie referencie na hlavny projekt
        Project mainProject = open.getMainProject();
        
        if (mainProject == null) {
            String[] messages = {"Main Project not found"};            
            new ClassDiagramView(new MessageJPanel(messages), "Error message",null,false);
        }
        else {
            ProjectScannerFactory.createProjectScanner(new ProjectScanner(mainProject));

            ProjectScanner ps = ProjectScannerFactory.getProjectScanner();

            if (ps.isProjectValid()) {
                setEnabled(false);           
                //RP.post(new getAllProjectsAndRunParser(this, mainProject), 0, Thread.NORM_PRIORITY);
                Thread t = new Thread(new GetAllProjectsAndRunParser(this, mainProject));
                t.start();
            }
            else {
                String[] messages = {"Your Main Project " + ps.getProjectName() + " is not supported project type.",
                                     "In this plugin version are supported only Java SE projects."};

                new ClassDiagramView(new MessageJPanel(messages), ps.getProjectName(),null,false);
            }
        }
    }
}

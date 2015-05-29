/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor;


import japa.parser.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.JavaParser.JavaReaderAction;
import org.fei.ClassDiagramEditor.ProjectPropertiesScanner;
import org.fei.ClassDiagramEditor.ProjectScanner;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.fei.ClassDiagramEditor.XMI.XmiTypePlaceHolder;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author mairo744
 */
public class GetAllProjectsAndRunParser implements Runnable{
    
    private AbstractAction actionClass;
    private Project p;
    private ProjectScanner ps;
    private String projectPath;
    private ArrayList<String> javaFiles = new ArrayList<String>();

    public GetAllProjectsAndRunParser(AbstractAction actionClass, Project p) {
        this.actionClass = actionClass;
        this.p = p;
        this.ps = ProjectScannerFactory.getProjectScanner();
        File f = new File(ps.getProjectSrc().getPath());
        this.projectPath = f.getAbsolutePath();
    }

    @Override
    public void run() {
        
        if(ClassDiagramView.lastOpened != null){
            
            GenerateDiagramPopup.setEnabling(true);
            RefactoringEvents.clearData();
            XmiTypePlaceHolder.clearData();
        }
       
       /* prehlada project a najde java subory  */        
        this.findJavaFiles(this.projectPath);
        

        actionClass.setEnabled(true);
        GenerateDiagramPopup.setEnabling(true);
        
        try {
            
            JavaReaderAction javaScanner = new JavaReaderAction(this.javaFiles,this.projectPath);
        } catch (IOException ex) {
            Message.showMessage((ex.getMessage()));
        } catch (ParseException ex) {
            Message.showMessage("Mas chybne zdrojove subory");         
        } catch (InterruptedException ex) {
            Message.showMessage("InterruptedException " + ex.getMessage());  
        } catch (InvocationTargetException ex) {
            Message.showMessage("InvocationTargetException " + ex.getMessage());
        }
        
              
    }
     

    public void findJavaFiles(String projectPath) {
       
        File dir = new File(projectPath);
       
        File[] fList = dir.listFiles();
        
        for (File file : fList) {
            
            if (file.isDirectory()) {
                findJavaFiles(file.getAbsolutePath());
            }
            else if(file.getName().endsWith(".java")) {
                     this.javaFiles.add(file.getAbsolutePath());
             }
        }

    }
}

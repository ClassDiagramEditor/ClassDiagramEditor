/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Refactoring;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import org.fei.ClassDiagramEditor.Editor.Parsers.MethodParser;
import org.fei.ClassDiagramEditor.Editor.Parsers.PackageNameParser;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;
import org.fei.ClassDiagramEditor.Data.MethodParameter;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.Data.VariableData;
import org.fei.ClassDiagramEditor.DirecotoryWatch.DirectoryWatch;
import org.fei.ClassDiagramEditor.JavaParser.ClassParser;
import org.fei.ClassDiagramEditor.ProjectScannerFactory;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.api.SafeDeleteRefactoring;
import org.netbeans.modules.refactoring.java.api.ChangeParametersRefactoring;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas
 */
public class Refactoring {

    // background thread pool
    private static final RequestProcessor RP = new RequestProcessor(Refactoring.class);

    private static class AsyncSafeDeletePackageRefactoring implements Runnable {

        PackageData data;

        public AsyncSafeDeletePackageRefactoring(PackageData data) {
            this.data = data;
        }

        @Override
        public void run() {

            DirectoryWatch.setAccessToRefactor(false);
            
            if (data == null || data.getFileObjects().isEmpty()) {
                Message.showMessage("AsyncSafeDeletePackageRefactoring null error");
                return;
            }

            RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

            SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(data.getFileObjects().get(data.getFileObjects().size() - 1)));

            Problem pre = refactoring.preCheck();

            if (pre != null && pre.isFatal()) {
                Message.showMessage("AsyncSafeDeletePackageRefactoring error");
                return;
            }

            Problem p = refactoring.prepare(safeDeleteSession);

            if (p != null && p.isFatal()) {
                Message.showMessage("AsyncSafeDeletePackageRefactoring error");
                return;
            }

            safeDeleteSession.doRefactoring(true);
            RefactoringEvents.firePackageSafeDeleteEvent(data);
            DirectoryWatch.setAccessToRefactor(true);
            
        }
    }

    private static class AsyncSafeDeleteClassRefactoring implements Runnable {

        ClassData data;

        public AsyncSafeDeleteClassRefactoring(ClassData data) {
            this.data = data;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (data == null || data.getSourceFileObject() == null) {
                Message.showMessage("AsyncSafeDeleteClassRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(data.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncSafeDeleteClassRefactoring error: JavaSource not found");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncSafeDeleteClassRefactoring exception:" + ex.getMessage());
                        }
                        //CompilationUnitTree cu = compilationController.getCompilationUnit();

                        Elements e = compilationController.getElements();
                        
                        TypeElement typeElement = e.getTypeElement(data.getFullName(true));                      

                        TreePathHandle treePathHandle = TreePathHandle.create(typeElement, compilationController);

                        RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

                        SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(treePathHandle));

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteClassRefactoring error");
                            return;
                        }

                        Problem p = refactoring.prepare(safeDeleteSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteClassRefactoring error");
                            return;
                        }

                        safeDeleteSession.doRefactoring(true);
                        
                        RefactoringEvents.fireClassSafeDeleteEvent(data);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncSafeDeleteClassRefactoring exception:" + ex.getMessage());
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncSafeDeleteFileRefactoring implements Runnable {

        FileObject fileObject;

        public AsyncSafeDeleteFileRefactoring(FileObject fileObject) {
            this.fileObject = fileObject;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (fileObject == null) {
                Message.showMessage("AsyncSafeDeleteFileRefactoring null error");
                return;
            }
            
            RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

            SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(fileObject));

            Problem pre = refactoring.preCheck();

            if (pre != null && pre.isFatal()) {
                Message.showMessage("AsyncSafeDeleteFileRefactoring error");
                return;
            }

            Problem p = refactoring.prepare(safeDeleteSession);

            if (p != null && p.isFatal()) {
                Message.showMessage("AsyncSafeDeleteFileRefactoring error");
                return;
            }

            safeDeleteSession.doRefactoring(true);
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncSafeDeleteMethodRefactoring implements Runnable {

        ClassData classData;
        MethodData methodData;

        public AsyncSafeDeleteMethodRefactoring(ClassData classData, MethodData methodData) {
            this.classData = classData;
            this.methodData = methodData;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
            if (methodData == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncSafeDeleteMethodRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncSafeDeleteMethodRefactoring error: JavaSource not found");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncSafeDeleteMethodRefactoring exception:" + ex.getMessage());
                        }

                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));

                        List<ExecutableElement> methods = ElementFilter.methodsIn(e.getAllMembers(typeElement));

                        ExecutableElement mymethod = null;

                        for (ExecutableElement exl : methods) {
                            Name name = exl.getSimpleName();
                            String s = name.toString();

                            if (s.equals(methodData.getName())) {

                                if (exl.getParameters().size() != methodData.getParameters().size()) {
                                    continue;
                                }

                                // metoda nema parametre
                                if (exl.getParameters().isEmpty()) {
                                    mymethod = exl;
                                    break;
                                } else {
                                    boolean sameParameters = true;
                                    for (int i = 0; i < exl.getParameters().size(); i++) {

                                        VariableElement fileparam = exl.getParameters().get(i);
                                        MethodParameter dataparam = methodData.getParameters().get(i);

                                        String simplename = fileparam.asType().toString();

                                        if (!simplename.startsWith(dataparam.getDescriptor())) {
                                            sameParameters = false;
                                        }
                                    }

                                    if (sameParameters) {
                                        mymethod = exl;
                                    }
                                }
                            }
                        }

                        if (mymethod == null) {
                            Message.showMessage("AsyncSafeDeleteMethodRefactoring error: Method not found");
                            return;
                        }

                        TreePathHandle treePathHandle = TreePathHandle.create(mymethod, compilationController);

                        RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

                        SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(treePathHandle));

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteMethodRefactoring error");
                            return;
                        }

                        Problem p = refactoring.prepare(safeDeleteSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteMethodRefactoring error");
                            return;
                        }

                        safeDeleteSession.doRefactoring(true);
                        RefactoringEvents.fireClassMethodSafeDeleteEvent(classData, methodData);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncSafeDeleteMethodRefactoring exception:" + ex.getMessage());
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
        
    }

    private static class AsyncSafeDeleteAttributeRefactoring implements Runnable {

        ClassData classData;
        VariableData variableData;

        public AsyncSafeDeleteAttributeRefactoring(ClassData classData, VariableData variableData) {
            this.classData = classData;
            this.variableData = variableData;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
            if (variableData == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncSafeDeleteAttributeRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncSafeDeleteAttributeRefactoring error: JavaSource not found");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncSafeDeleteAttributeRefactoring exception:" + ex.getMessage());
                        }

                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));

                        List<VariableElement> fields = ElementFilter.fieldsIn(e.getAllMembers(typeElement));
                        
                        VariableElement myfield = null;

                        for (VariableElement exl : fields) {
                            Name name = exl.getSimpleName();
                            String s = name.toString();

                            if (s.equals(variableData.getName()) && classData.getFullName(true).equals(exl.getEnclosingElement().toString())) {
                                myfield = exl;
                                break;
                            }
                        }

                        if (myfield == null) {
                            Message.showMessage("AsyncSafeDeleteAttributeRefactoring error: Attribute not found");
                            return;
                        }

                        TreePathHandle treePathHandle = TreePathHandle.create(myfield, compilationController);

                        RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

                        SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(treePathHandle));

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteAttributeRefactoring error");
                            return;
                        }

                        Problem p = refactoring.prepare(safeDeleteSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncSafeDeleteAttributeRefactoring error");
                            return;
                        }

                        safeDeleteSession.doRefactoring(true);
                        RefactoringEvents.fireClassAttributeSafeDeleteEvent(classData, variableData);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncSafeDeleteAttributeRefactoring exception:" + ex.getMessage());
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
private static class AsyncRenameAttributeRefactoring implements Runnable {

        ClassData classData;
        VariableData variableData;
        String newName;

        public AsyncRenameAttributeRefactoring(ClassData classData, VariableData variableData, String newName) {
            this.classData = classData;
            this.variableData = variableData;
            this.newName = newName;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
            if (variableData == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncRenameAttributeRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncRenameAttributeRefactoring error: JavaSource not found");
                return;
            }
            
            if (newName.isEmpty()) {
                Message.showMessage("AsyncRenameAttributeRefactoring error: New name is empty");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncRenameAttributeRefactoring exception:" + ex.getMessage());
                        }

                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));

                        List<VariableElement> fields = ElementFilter.fieldsIn(e.getAllMembers(typeElement));
                        
                        VariableElement myfield = null;

                        for (VariableElement exl : fields) {
                            Name name = exl.getSimpleName();
                            String s = name.toString();

                            if (s.equals(variableData.getName()) && classData.getFullName(true).equals(exl.getEnclosingElement().toString())) {
                                myfield = exl;
                                break;
                            }
                        }
                        
                        if (myfield == null) {
                            Message.showMessage("AsyncRenameAttributeRefactoring error: Attribute not found");
                            return;
                        }

                        TreePathHandle treePathHandle = TreePathHandle.create(myfield, compilationController);

                        RefactoringSession renameSession = RefactoringSession.create("Rename");

                        RenameRefactoring refactoring = new RenameRefactoring(Lookups.fixed(treePathHandle));

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncRenameAttributeRefactoring error");
                            return;
                        }

                        refactoring.setNewName(newName);
                        
                        Problem p = refactoring.prepare(renameSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncRenameAttributeRefactoring error");
                            return;
                        }

                        renameSession.doRefactoring(true);
                        
                         // update
                         variableData.setName(newName);

                        RefactoringEvents.fireClassChangeAttributeEvent(classData, variableData);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncRenameAttributeRefactoring exception:" + ex.getMessage());
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncRenamePackageRefactoring implements Runnable {

        PackageData data;
        HashMap<Integer, String> newNames;

        public AsyncRenamePackageRefactoring(PackageData data, HashMap<Integer, String> newNames) {
            this.data = data;
            this.newNames = newNames;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (data == null || data.getFileObjects().isEmpty()) {
                Message.showMessage("AsyncRenamePackageRefactoring null error");
                return;
            }

            ArrayList<RefactoringSession> refactoringSessions = new ArrayList<RefactoringSession>();

            for (Map.Entry<Integer, String> set : newNames.entrySet()) {

                int pos = set.getKey();
                String newName = set.getValue();

                RefactoringSession refactoringSession = RefactoringSession.create("Rename refactoring");

                RenameRefactoring refactoring = new RenameRefactoring(Lookups.fixed(data.getFileObjects().get(pos)));

                Problem pre = refactoring.preCheck();

                if (pre != null && pre.isFatal()) {
                    Message.showMessage("AsyncRenamePackageRefactoring error");
                    return;
                }

                refactoring.setNewName(newName);

                Problem p = refactoring.prepare(refactoringSession);

                if (p != null && p.isFatal()) {
                    Message.showMessage("AsyncRenamePackageRefactoring error");
                    return;
                }

                refactoringSessions.add(refactoringSession);
            }

            for (RefactoringSession refactoring : refactoringSessions) {
                refactoring.doRefactoring(true);
            }

            // nastavenie noveho mena
            String[] orig = data.getName().split("\\.");

            for (Map.Entry<Integer, String> set : newNames.entrySet()) {

                int pos = set.getKey();
                String newName = set.getValue();

                orig[pos] = newName;
            }

            String newName = new String();

            for (int i = 0; i < orig.length; i++) {

                newName += orig[i];
                if (i != orig.length - 1) {
                    newName += ".";
                }
            }

            data.setName(newName);
            RefactoringEvents.firePackageRenameEvent(data);
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncRenameClassRefactoring implements Runnable {

        ClassData data;
        String newName;

        public AsyncRenameClassRefactoring(ClassData data, String newName) {

            this.data = data;
            this.newName = newName;

        }

        @Override
        public void run() {
                        
            DirectoryWatch.setAccessToRefactor(false);
            
            if (data == null || data.getSourceFileObject() == null) {
                Message.showMessage("AsyncRenameClassRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(data.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncRenameClassRefactoring error: JavaSource not found");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncRenameClassRefactoring exception:" + ex.getMessage());
                        }
                        
                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(data.getFullName(true));

                        TreePathHandle treePathHandle = TreePathHandle.create(typeElement, compilationController);
                        
                        RefactoringSession renameSession = RefactoringSession.create("Rename");

                        RenameRefactoring refactoring;

                        if (data.isPublicSourceClass()) {
                            refactoring = new RenameRefactoring(Lookups.fixed(data.getSourceFileObject()));
                        } else {
                            refactoring = new RenameRefactoring(Lookups.fixed(treePathHandle));
                        }

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncRenameClassRefactoring error");
                            return;
                        }
                        
                        
                        refactoring.setNewName(newName);

                        Problem p = refactoring.prepare(renameSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncRenameClassRefactoring error");
                            return;
                        }
                        
                        renameSession.doRefactoring(true);
                        
                        data.setClassName(newName);
                        
                        
                        RefactoringEvents.fireClassRenamedEvent(data);
                        
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncRenameClassRefactoring exception:" + ex.getMessage());
            }

            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncChangeDeclarationMethodRefactoring implements Runnable {

        private MethodData oldMethod;
        private ClassData classData;
        private Set<Modifier> newModifiers;
        private String newName;
        private ArrayList<MethodParser.ParameterInfo> newParameters;
        private String newReturn;

        public AsyncChangeDeclarationMethodRefactoring(MethodData oldMethod, ClassData classData, Set<Modifier> newModifiers, String newName, ArrayList<MethodParser.ParameterInfo> newParameters, String newReturn) {
            this.oldMethod = oldMethod;
            this.classData = classData;
            this.newModifiers = newModifiers;
            this.newModifiers.remove(Modifier.DEFAULT);
            this.newName = newName;
            this.newParameters = newParameters;
            this.newReturn = newReturn;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (oldMethod == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncChangeDeclarationMethodRefactoring null error");
                return;
            }
            
            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncChangeDeclarationMethodRefactoring error: JavaSource not found");
                return;
            }

            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncChangeDeclarationMethodRefactoring exception:" + ex.getMessage());
                        }

                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));

                        List<ExecutableElement> methods = ElementFilter.methodsIn(e.getAllMembers(typeElement));

                        ExecutableElement mymethod = null;

                        for (ExecutableElement exl : methods) {
                            Name name = exl.getSimpleName();
                            String s = name.toString();
                            
                            if (s.equals(oldMethod.getName())) {

                                
                                if (exl.getParameters().size() != oldMethod.getParameters().size()) {
                                    continue;
                                }

                                // metoda nema parametre
                                if (exl.getParameters().isEmpty()) {
                                    mymethod = exl;
                                    break;
                                } else {
                                    boolean sameParameters = true;
                                    for (int i = 0; i < exl.getParameters().size(); i++) {

                                        VariableElement fileparam = exl.getParameters().get(i);
                                        MethodParameter dataparam = oldMethod.getParameters().get(i);

                                        String simplename = fileparam.asType().toString();

                                        String[] tokens = simplename.split("\\.");
                                        simplename = tokens[tokens.length-1];                                       
                                        simplename += " " + fileparam.getSimpleName(); 

                                        if (!simplename.startsWith(dataparam.getDescriptor())) {
                                            
                                            sameParameters = false;
                                        }
                                    }

                                    if (sameParameters) {
                                        mymethod = exl;
                                    }
                                }
                            }
                        }

                        if (mymethod == null) {
                            Message.showMessage("AsyncChangeDeclarationMethodRefactoring error: Method not found");
                            return;
                        }

                        TreePathHandle treePathHandle = TreePathHandle.create(mymethod, compilationController);

                        RefactoringSession changeDeclarationSession = RefactoringSession.create("Change declaration");

                        ChangeParametersRefactoring refactoring = new ChangeParametersRefactoring(treePathHandle);

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncChangeDeclarationMethodRefactoring error");
                            return;
                        }

                        ChangeParametersRefactoring.ParameterInfo[] parameterInfos = new ChangeParametersRefactoring.ParameterInfo[newParameters.size()];

                        for (int i = 0; i < newParameters.size(); i++) {
                            MethodParser.ParameterInfo pinfo = newParameters.get(i);

                            // pre nove parametre musi byt index -1
                            int index = -1;
                            if (oldMethod.getParameters().size() > i) {
                                index = pinfo.index;
                            }

                            parameterInfos[i] = new ChangeParametersRefactoring.ParameterInfo(index, pinfo.name, pinfo.type, "0");
                        }

                        refactoring.setParameterInfo(parameterInfos);
                        
                        
                        refactoring.setModifiers(newModifiers);                      
                        refactoring.setMethodName(newName);
                        refactoring.setReturnType(newReturn);


                        Problem pre2 = refactoring.checkParameters();

                        if (pre2 != null && pre2.isFatal()) {
                            Message.showMessage("AsyncChangeDeclarationMethodRefactoring error");
                            return;
                        }

                        Problem p = refactoring.prepare(changeDeclarationSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncChangeDeclarationMethodRefactoring error");
                            return;
                        }

                        changeDeclarationSession.doRefactoring(true);

                        // update dat
                        ArrayList<MethodParameter> params = new ArrayList<MethodParameter>();
                        for (MethodParser.ParameterInfo tmp : newParameters) {
                            params.add(new MethodParameter(tmp.name, tmp.type));
                        }
                        oldMethod.setParameters(params);
                        oldMethod.setByModifiers(newModifiers);
                        oldMethod.setName(newName);
                        oldMethod.setReturnType(newReturn);

                        RefactoringEvents.fireClassMethodChangeDeclarationEvent(classData, oldMethod);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncChangeDeclarationMethodRefactoring exception:" + ex.getMessage());
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncAddClassAttributeRefactoring implements Runnable {

        ClassData classData;
        String newName;
        String newType;
        Set<Modifier> newMods; 

        public AsyncAddClassAttributeRefactoring(ClassData classData, String newName, String newType, Set<Modifier> newMods) {
            this.classData = classData;
            this.newName = newName;
            this.newType = newType;
            this.newMods = newMods;
            this.newMods.remove(Modifier.DEFAULT);
            
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (newName == null || newType == null || newMods == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncAddClassAttributeRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncAddClassAttributeRefactoring error: JavaSource not found");
                return;
            }

            try {

                ModificationResult result = javaSource.runModificationTask(new Task<WorkingCopy>() {
                    @Override
                    public void run(WorkingCopy workingCopy) throws IOException {

                        workingCopy.toPhase(JavaSource.Phase.RESOLVED);

                        TreeMaker make = workingCopy.getTreeMaker();

                        Elements e = workingCopy.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));
                        
                        
                        ClassTree origclass = workingCopy.getTrees().getTree(typeElement);
   
                        ModifiersTree variableModifiers = make.Modifiers(newMods, Collections.<AnnotationTree>emptyList());
                        VariableTree newVariable = make.Variable(variableModifiers, newName, make.Type(newType), null);
                            
                        ClassTree modifclass = make.insertClassMember(origclass, 0, newVariable);

                        workingCopy.rewrite(origclass, modifclass);
                        
                    }
                });

                result.commit();

                VariableData newVariable = new VariableData(newName, newType, newMods);
                classData.addVariable(newVariable);

                RefactoringEvents.fireClassAddAttributeEvent(classData, newVariable);

            } catch (IOException e) {
                Message.showMessage("AsyncAddClassAttributeRefactoring error: " + e.getMessage());
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncAddClassMethodRefactoring implements Runnable {

        private ClassData classData;
        private Set<Modifier> newMods;
        private String newName;
        private ArrayList<MethodParser.ParameterInfo> newParameters;
        private String newReturn;

        public AsyncAddClassMethodRefactoring(ClassData classData, Set<Modifier> newModifiers, String newName, ArrayList<MethodParser.ParameterInfo> newParameters, String newReturn) {
            this.classData = classData;
            this.newMods = newModifiers;
            this.newName = newName;
            this.newParameters = newParameters;
            this.newReturn = newReturn;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (newName == null || newReturn == null || newMods == null || newParameters == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncAddClassMethodRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncAddClassMethodRefactoring error: JavaSource not found");
                return;
            }

            try {

                ModificationResult result = javaSource.runModificationTask(new Task<WorkingCopy>() {
                    @Override
                    public void run(WorkingCopy workingCopy) throws IOException {

                        workingCopy.toPhase(JavaSource.Phase.RESOLVED);

                        TreeMaker make = workingCopy.getTreeMaker();

                        Elements e = workingCopy.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));
                        ClassTree origclass = workingCopy.getTrees().getTree(typeElement);

                        ModifiersTree methodModifiers = make.Modifiers(newMods, Collections.<AnnotationTree>emptyList());
                        ArrayList<VariableTree> parameters = new ArrayList<VariableTree>();
                        for (MethodParser.ParameterInfo pi : newParameters) {
                            VariableTree param = make.Variable(make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList()), pi.name, make.Type(pi.type), null);
                            parameters.add(param);
                        }
                        MethodTree newMethod;
                        if (newMods.contains(Modifier.ABSTRACT) || classData.isInterface()) {
                            newMethod = make.Method(methodModifiers, newName, make.Type(newReturn), Collections.<TypeParameterTree>emptyList(), parameters, Collections.<ExpressionTree>emptyList(), (BlockTree) null, null);
                        } else {
                            newMethod = make.Method(methodModifiers, newName, make.Type(newReturn), Collections.<TypeParameterTree>emptyList(), parameters, Collections.<ExpressionTree>emptyList(), "{ }", null);
                        }
                        ClassTree modifclass = make.addClassMember(origclass, newMethod);

                        workingCopy.rewrite(origclass, modifclass);
                    }
                });

                result.commit();

                MethodData newMethod = new MethodData(newName, newReturn, newMods, newParameters);
                classData.addMethod(newMethod);

                RefactoringEvents.fireClassAddMethodEvent(classData, newMethod);

            } catch (IOException e) {
                Message.showMessage("AsyncAddClassMethodRefactoring error: " + e.getMessage());
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }

    private static class AsyncChangeAttributeRefactoring implements Runnable {

        private VariableData variableData;
        private ClassData classData;
        private String newName;
        private String newType;
        private Set<Modifier> newMods;
        

        public AsyncChangeAttributeRefactoring(VariableData variableData, ClassData classData, String newName, String newType, Set<Modifier> newMods) {
            this.variableData = variableData;
            this.classData = classData;
            this.newName = newName;
            this.newType = newType;
            this.newMods = newMods;
            this.newMods.remove(Modifier.DEFAULT);
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (variableData == null || newName == null || newType == null || newMods == null || classData == null || classData.getSourceFileObject() == null) {
                Message.showMessage("AsyncChangeAttributeRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(classData.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncChangeAttributeRefactoring error: JavaSource not found");
                return;
            }
            
            // ak sa meni meno treba najprv premenovat
            if (!newName.equals(variableData.getName())) {
                
                try {

                    javaSource.runUserActionTask(new Task<CompilationController>() {
                        @Override
                        public void run(CompilationController compilationController) {

                            try {
                                compilationController.toPhase(JavaSource.Phase.RESOLVED);
                            } catch (IOException ex) {
                                Message.showMessage("AsyncChangeAttributeRefactoring exception:" + ex.getMessage());
                            }

                            Elements e = compilationController.getElements();
                            TypeElement typeElement = e.getTypeElement(classData.getFullName(true));
                            List<VariableElement> fields = ElementFilter.fieldsIn(e.getAllMembers(typeElement));

                            VariableElement myfield = null;

                            for (VariableElement exl : fields) {
                                Name name = exl.getSimpleName();
                                String s = name.toString();
                              
                                if (s.equals(variableData.getName()) && classData.getFullName(true).equals(exl.getEnclosingElement().toString())) {
                                    myfield = exl;
                                    break;
                                }
                            }

                            if (myfield == null) {
                                Message.showMessage("AsyncChangeAttributeRefactoring error: Attribute not found");
                                return;
                            }

                            TreePathHandle treePathHandle = TreePathHandle.create(myfield, compilationController);

                            RefactoringSession renameSession = RefactoringSession.create("Rename");

                            RenameRefactoring refactoring = new RenameRefactoring(Lookups.fixed(treePathHandle));

                            Problem pre = refactoring.preCheck();

                            if (pre != null && pre.isFatal()) {
                                Message.showMessage("AsyncChangeAttributeRefactoring error");
                                return;
                            }
                            
                            refactoring.setNewName(newName);
                            

                            Problem p = refactoring.prepare(renameSession);

                            if (p != null && p.isFatal()) {
                                Message.showMessage("AsyncChangeAttributeRefactoring error");
                                return;
                            }

                            renameSession.doRefactoring(true);
                        }
                    }, true);
                } catch (IOException ex) {
                    Message.showMessage("AsyncChangeAttributeRefactoring exception:" + ex.getMessage());
                }
            }
            
            // vymaze sa s novym menom
            try {

                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController compilationController) {

                        try {
                            compilationController.toPhase(JavaSource.Phase.RESOLVED);
                        } catch (IOException ex) {
                            Message.showMessage("AsyncChangeAttributeRefactoring exception:" + ex.getMessage());
                        }

                        Elements e = compilationController.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));

                        List<VariableElement> fields = ElementFilter.fieldsIn(e.getAllMembers(typeElement));

                        VariableElement myfield = null;

                        for (VariableElement exl : fields) {
                            Name name = exl.getSimpleName();
                            String s = name.toString();

                            if (s.equals(newName) && classData.getFullName(true).equals(exl.getEnclosingElement().toString())) {
                                myfield = exl;
                                break;
                            }
                        }

                        if (myfield == null) {
                            Message.showMessage("AsyncChangeAttributeRefactoring error: Attribute not found");
                            return;
                        }

                        TreePathHandle treePathHandle = TreePathHandle.create(myfield, compilationController);

                        RefactoringSession safeDeleteSession = RefactoringSession.create("Safe delete");

                        SafeDeleteRefactoring refactoring = new SafeDeleteRefactoring(Lookups.fixed(treePathHandle));

                        Problem pre = refactoring.preCheck();

                        if (pre != null && pre.isFatal()) {
                            Message.showMessage("AsyncChangeAttributeRefactoring error");
                            return;
                        }

                        Problem p = refactoring.prepare(safeDeleteSession);

                        if (p != null && p.isFatal()) {
                            Message.showMessage("AsyncChangeAttributeRefactoring error");
                            return;
                        }

                        safeDeleteSession.doRefactoring(true);
                    }
                }, true);
            } catch (IOException ex) {
                Message.showMessage("AsyncChangeAttributeRefactoring exception:" + ex.getMessage());
            }
            
            // nakoniec sa prida s novym datovym typom, menom, modifikatormi
            try {

                ModificationResult result = javaSource.runModificationTask(new Task<WorkingCopy>() {
                    @Override
                    public void run(WorkingCopy workingCopy) throws IOException {

                        workingCopy.toPhase(JavaSource.Phase.RESOLVED);

                        TreeMaker make = workingCopy.getTreeMaker();

                        Elements e = workingCopy.getElements();
                        TypeElement typeElement = e.getTypeElement(classData.getFullName(true));
                        ClassTree origclass = workingCopy.getTrees().getTree(typeElement);
                        
                        ModifiersTree variableModifiers = null;
                        

                        variableModifiers = make.Modifiers(newMods, Collections.<AnnotationTree> emptyList());
                     
                        VariableTree newVariable = make.Variable(variableModifiers,newName, make.Type(newType), null);
                        ClassTree modifclass = make.insertClassMember(origclass, 0, newVariable);

                        workingCopy.rewrite(origclass, modifclass);
                    }
                });

                result.commit();

                // update
                variableData.setName(newName);
                variableData.setReturnType(newType);
                                    
                if(!newMods.toString().contains("default"))
                    variableData.setByModifiers(newMods);

                RefactoringEvents.fireClassChangeAttributeEvent(classData, variableData);

            } catch (IOException e) {
                Message.showMessage("AsyncChangeAttributeRefactoring error: " + e.getMessage());
                return;
            }
            /*
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncCreateNewInnerClassRefactoring implements Runnable {

        ClassData data;
        String newName;

        public AsyncCreateNewInnerClassRefactoring(ClassData data, String newName) {
            this.data = data;
            this.newName = newName;
        }
        
        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);

            if (newName == null || data == null || data.getSourceFileObject() == null) {
                Message.showMessage("AsyncCreateNewInnerClassRefactoring null error");
                return;
            }

            JavaSource javaSource = JavaSource.forFileObject(data.getSourceFileObject());

            if (javaSource == null) {
                Message.showMessage("AsyncCreateNewInnerClassRefactoring error: JavaSource not found");
                return;
            }

            try {

                ModificationResult result = javaSource.runModificationTask(new Task<WorkingCopy>() {
                    @Override
                    public void run(WorkingCopy workingCopy) throws IOException {

                        workingCopy.toPhase(JavaSource.Phase.RESOLVED);

                        TreeMaker make = workingCopy.getTreeMaker();

                        Elements e = workingCopy.getElements();
                        TypeElement typeElement = e.getTypeElement(data.getFullName(true));
                        ClassTree origclass = workingCopy.getTrees().getTree(typeElement);

                        ModifiersTree modifiers = make.Modifiers(Collections.<Modifier>emptySet());
                        ClassTree innerClass = make.Class(modifiers, newName, Collections.<TypeParameterTree>emptyList(), null, Collections.<Tree>emptyList(), Collections.<Tree>emptyList());
                        
                        ClassTree modifclass = make.addClassMember(origclass, innerClass);

                        workingCopy.rewrite(origclass, modifclass);
                    }
                });

                result.commit();

                ClassData newInnerClass = new ClassData(newName, data);

                RefactoringEvents.fireAddNewInnerClassEvent(data, newInnerClass);

            } catch (IOException e) {
                Message.showMessage("AsyncCreateNewInnerClassRefactoring error: " + e.getMessage());
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncCreateClassRefactoring implements Runnable {

        PackageData pcg;
        String newName;
        boolean isAbstract;
        int x, y;

        public AsyncCreateClassRefactoring(PackageData pcg, String newName, boolean isAbstract) {
            this.pcg = pcg;
            this.newName = newName;
            this.isAbstract = isAbstract;
            x = y = -1;
        }
        
        public AsyncCreateClassRefactoring(PackageData pcg, String newName, boolean isAbstract, int x, int y) {
            this.pcg = pcg;
            this.newName = newName;
            this.isAbstract = isAbstract;
            this.x = x;
            this.y = y;
        }
        
        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
        
            if (pcg == null || pcg.getFileObjects().size() <= 0) {
                Message.showMessage("AsyncCreateClassRefactoring null error");
                return;
            }
            
            FileObject packageFileObject = pcg.getFileObjects().get(pcg.getFileObjects().size() - 1);
            
            try {
                
                FileObject newFileObject = packageFileObject.createData(newName + ".java");
                
                OutputStreamWriter osw = new OutputStreamWriter(newFileObject.getOutputStream());
                BufferedWriter writer = new BufferedWriter(osw);
                
                FileCreator.createClass(writer, pcg.getName(), newName, isAbstract);
                
                writer.close();
                
                ClassData newClass = new ClassData(newFileObject, newName, pcg, isAbstract, false, false);
                
                if (x != -1 && y != -1)
                    RefactoringEvents.fireAddNewClassEvent(pcg, newClass, x, y);
                else
                    RefactoringEvents.fireAddNewClassEvent(pcg, newClass);
                
            } catch (IOException ex) {
                Message.showMessage("AsyncCreateClassRefactoring IOException");
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncCreateInterfaceRefactoring implements Runnable {

        PackageData pcg;
        String newName;
        int x, y;

        public AsyncCreateInterfaceRefactoring(PackageData pcg, String newName) {
            this.pcg = pcg;
            this.newName = newName;
            x = y = -1;
        }
        
        public AsyncCreateInterfaceRefactoring(PackageData pcg, String newName, int x, int y) {
            this.pcg = pcg;
            this.newName = newName;
            this.x = x;
            this.y = y;
        }
        
        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
        
            if (pcg == null || pcg.getFileObjects().size() <= 0) {
                Message.showMessage("AsyncCreateInterfaceRefactoring null error");
                return;
            }
            
            FileObject packageFileObject = pcg.getFileObjects().get(pcg.getFileObjects().size() - 1);
            
            try {
                
                FileObject newFileObject = packageFileObject.createData(newName + ".java");
                
                OutputStreamWriter osw = new OutputStreamWriter(newFileObject.getOutputStream());
                BufferedWriter writer = new BufferedWriter(osw);
                
                FileCreator.createInterface(writer, pcg.getName(), newName);
                
                writer.close();
                
                ClassData newInterface = new ClassData(newFileObject, newName, pcg, false, false, true);
                
                if (x != -1 && y != -1)
                    RefactoringEvents.fireAddNewInterfaceEvent(pcg, newInterface, x, y);
                else
                    RefactoringEvents.fireAddNewInterfaceEvent(pcg, newInterface);
                
            } catch (IOException ex) {
                Message.showMessage("AsyncCreateInterfaceRefactoring IOException");
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncCreateEnumRefactoring implements Runnable {

        PackageData pcg;
        String newName;
        int x;
        int y;
        
        public AsyncCreateEnumRefactoring(PackageData pcg, String newName) {
            this.pcg = pcg;
            this.newName = newName;
            x = y = -1;
        }
        
        public AsyncCreateEnumRefactoring(PackageData pcg, String newName, int x, int y) {
            this.pcg = pcg;
            this.newName = newName;
            this.x = x;
            this.y = y;
        }
        
        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
        
            if (pcg == null || pcg.getFileObjects().size() <= 0) {
                Message.showMessage("AsyncCreateEnumRefactoring null error");
                return;
            }
            
            FileObject packageFileObject = pcg.getFileObjects().get(pcg.getFileObjects().size() - 1);
            
            try {
                
                
                FileObject newFileObject = packageFileObject.createData(newName + ".java");
                
                OutputStreamWriter osw = new OutputStreamWriter(newFileObject.getOutputStream());
                BufferedWriter writer = new BufferedWriter(osw);
                
                FileCreator.createEnum(writer, pcg.getName(), newName);
                
                writer.close();
                
                ClassData newClass = new ClassData(newFileObject, newName, pcg, false, true, false);
                
                if (x != -1 && y != -1)
                    RefactoringEvents.fireAddNewEnumEvent(pcg, newClass, x, y);
                else
                    RefactoringEvents.fireAddNewEnumEvent(pcg, newClass);
                
            } catch (IOException ex) {
                Message.showMessage("AsyncCreateEnumRefactoring IOException");
                return;
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
    }
    
    private static class AsyncCreateNewPackageRefactoring implements Runnable{
    
        PackageData parent;
        String newName;
        int x, y;

        public AsyncCreateNewPackageRefactoring(PackageData parent, String newName) {
            this.parent = parent;
            this.newName = newName;
            x = y = -1;
        }
        
        public AsyncCreateNewPackageRefactoring(PackageData parent, String newName, int x, int y) {
            this.parent = parent;
            this.newName = newName;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            DirectoryWatch.setAccessToRefactor(false);
            
            if (newName.isEmpty())
                return;
            
                FileObject parentDir;
            if (parent == null)
                parentDir = ProjectScannerFactory.getProjectScanner().getProjectSrc();
            else if (parent.isDefaultPackage())
                parentDir = ProjectScannerFactory.getProjectScanner().getProjectSrc();
            else
                parentDir = parent.getFileObjects().get(parent.getFileObjects().size() - 1);
            
            try {
                
                HashMap<Integer, String> newDirs = PackageNameParser.ParseName(newName);
                
                ArrayList<FileObject> fileObjects = new ArrayList<FileObject>();
            
                // vytvorenie novych balikov a pridanie do pola
                for (int i = 0; i < newDirs.size(); i++) 
                    fileObjects.add(parentDir = parentDir.createFolder(newDirs.get(i)));
                
                PackageData newPackage = new PackageData(newName, fileObjects, parent);
                
                if (x != -1 && y != -1)
                    RefactoringEvents.fireAddNewPackageEvent(parent, newPackage, x, y);
                else
                    RefactoringEvents.fireAddNewPackageEvent(parent, newPackage);
            
            } catch (IOException e) {
                Message.showMessage("AsyncCreateNewPackageRefactoring IOException");
            }
            DirectoryWatch.setAccessToRefactor(true);
        }
        
        
    }
    
    
    // asynchronne vymazanie balika
    public static void safeDelete(PackageData data) {

        AsyncSafeDeletePackageRefactoring async = new AsyncSafeDeletePackageRefactoring(data);
        RP.post(async);
    }

    // asynchronne vymazanie triedy
    public static void safeDelete(ClassData data) {

        AsyncSafeDeleteClassRefactoring async = new AsyncSafeDeleteClassRefactoring(data);
        RP.post(async);
    }
    
    // asynchronne vymazanie suboru
    public static void safeDelete(FileObject fileObject) {
    
        AsyncSafeDeleteFileRefactoring async = new AsyncSafeDeleteFileRefactoring(fileObject);
        RP.post(async);
    }

    // asynchronne vymazanie metody
    public static void safeDelete(MethodData methodData, ClassData classData) {

        AsyncSafeDeleteMethodRefactoring async = new AsyncSafeDeleteMethodRefactoring(classData, methodData);
        RP.post(async);
    }

    // asynchronne vymazanie atributu
    public static void safeDelete(VariableData variableData, ClassData classData) {

        AsyncSafeDeleteAttributeRefactoring async = new AsyncSafeDeleteAttributeRefactoring(classData, variableData);
        RP.post(async);
    }

    // asynchronne premenovanie balika
    public static void rename(PackageData packageData, HashMap<Integer, String> newNames) {

        AsyncRenamePackageRefactoring async = new AsyncRenamePackageRefactoring(packageData, newNames);
        RP.post(async);
    }

    // asynchronne premenovanie triedy
    public static void rename(ClassData classData, String newName) {

        AsyncRenameClassRefactoring async = new AsyncRenameClassRefactoring(classData, newName);
        RP.post(async);
    }

    // asynchronna zmena deklaracie metody
    public static void changeDeclaration(MethodData oldMethod, ClassData classData, Set<Modifier> newModifiers, String newName, ArrayList<MethodParser.ParameterInfo> newParameters, String newReturn) {

        AsyncChangeDeclarationMethodRefactoring async = new AsyncChangeDeclarationMethodRefactoring(oldMethod, classData, newModifiers, newName, newParameters, newReturn);
        RP.post(async);
    }
    
    // asynchronna zmena deklaracie atributu
    public static void changeDeclaration(VariableData variable, ClassData classData, String newName, String newType, Set<Modifier> newModifiers) {
    
        
        AsyncChangeAttributeRefactoring async = new AsyncChangeAttributeRefactoring(variable, classData, newName, newType, newModifiers);
        RP.post(async);
    }

    // asynchronne pridanie noveho atributu
    public static void addAttribute(ClassData classData, String newName, String newType, Set<Modifier> newModifiers) {

        AsyncAddClassAttributeRefactoring async = new AsyncAddClassAttributeRefactoring(classData, newName, newType, newModifiers);
        RP.post(async);
    }

    // asynchronne pridanie novej metody
    public static void addMethod(ClassData classData, Set<Modifier> newModifiers, String newName, ArrayList<MethodParser.ParameterInfo> newParameters, String newReturn) {

        AsyncAddClassMethodRefactoring async = new AsyncAddClassMethodRefactoring(classData, newModifiers, newName, newParameters, newReturn);
        RP.post(async);
    }
    
    // asynchronne pridanie novej vnorenej triedy
    public static void createInnerClass(ClassData classData, String newName) {
    
        AsyncCreateNewInnerClassRefactoring async = new AsyncCreateNewInnerClassRefactoring(classData, newName);
        RP.post(async);
    }
    
    // asynchronne vytvorenie novej triedy
    public static void createClass(PackageData pcg, String newName, boolean isAbstract) {
    
        AsyncCreateClassRefactoring async = new AsyncCreateClassRefactoring(pcg, newName, isAbstract);
        RP.post(async);
    }
    
    public static void createClass(PackageData pcg, String newName, boolean isAbstract, int x, int y) {
    
        AsyncCreateClassRefactoring async = new AsyncCreateClassRefactoring(pcg, newName, isAbstract, x, y);
        RP.post(async);
    }
    
    // asynchronne vytvorenie noveho rozhrania
    public static void createInterface(PackageData pcg, String newName) {
    
        AsyncCreateInterfaceRefactoring async = new AsyncCreateInterfaceRefactoring(pcg, newName);
        RP.post(async);
    }
    
    public static void createInterface(PackageData pcg, String newName, int x, int y) {
    
        AsyncCreateInterfaceRefactoring async = new AsyncCreateInterfaceRefactoring(pcg, newName, x, y);
        RP.post(async);
    }
    
    public static void createEnum(PackageData pcg, String newName) {
    
        AsyncCreateEnumRefactoring async = new AsyncCreateEnumRefactoring(pcg, newName);
        RP.post(async);
    }
    
    public static void createEnum(PackageData pcg, String newName, int x, int y) {
    
        AsyncCreateEnumRefactoring async = new AsyncCreateEnumRefactoring(pcg, newName, x, y);
        RP.post(async);
    }
    
    public static void createPackage(PackageData parent, String newPackage) {
    
        AsyncCreateNewPackageRefactoring asnyc = new AsyncCreateNewPackageRefactoring(parent, newPackage);
        RP.post(asnyc);
    }
    
    public static void createPackage(PackageData parent, String newPackage, int x, int y) {
    
        AsyncCreateNewPackageRefactoring async = new AsyncCreateNewPackageRefactoring(parent, newPackage, x, y);
        RP.post(async);
    }
    
    public static void renameEnumConstant(VariableData variable, ClassData classData, String newName) {

        
        if(variable.getType() == "enum"){
            AsyncRenameAttributeRefactoring async = new AsyncRenameAttributeRefactoring(classData, variable, newName);
            RP.post(async);
        }

    }
}

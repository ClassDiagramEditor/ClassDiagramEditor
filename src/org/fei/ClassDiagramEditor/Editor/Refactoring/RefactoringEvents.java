/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Refactoring;

import java.util.ArrayList;
import java.util.List;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.Data.VariableData;
import org.fei.ClassDiagramEditor.Editor.events.ClassAttributeEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassMethodEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassRenameEvent;
import org.fei.ClassDiagramEditor.Editor.events.ClassSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewClassListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewEnumListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewInnerClassListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewInterfaceListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.AddNewPackageListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAddMethodListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassAttributeSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassChangeAttributeListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodChangeDeclarationListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassMethodSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.ClassSafeDeleteListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageRenameListener;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageSafeDeleteListener;

/**
 *
 * @author Tomas
 */
public class RefactoringEvents {
    
    private static List<PackageSafeDeleteListener> packageSafeDeleteListeners = new ArrayList<PackageSafeDeleteListener>();
    private static List<ClassSafeDeleteListener> classSafeDeleteListeners = new ArrayList<ClassSafeDeleteListener>();
    private static List<ClassMethodSafeDeleteListener> classMethodSafeDeleteListeners = new ArrayList<ClassMethodSafeDeleteListener>();
    private static List<ClassAttributeSafeDeleteListener> classAttributeSafeDeleteListeners = new ArrayList<ClassAttributeSafeDeleteListener>();
    private static List<PackageRenameListener> packageRenameListeners = new ArrayList<PackageRenameListener>();
    private static List<ClassRenameListener> classRenamedEventlisteners = new ArrayList<ClassRenameListener>();
    private static List<ClassMethodChangeDeclarationListener> classMethodChangeDeclarationListeners = new ArrayList<ClassMethodChangeDeclarationListener>();
    private static List<ClassAddAttributeListener> classAddAttributeListeners = new ArrayList<ClassAddAttributeListener>();
    private static List<ClassAddMethodListener> classAddMethodListeners = new ArrayList<ClassAddMethodListener>();
    private static List<ClassChangeAttributeListener> classChangeAttributeListeners = new ArrayList<ClassChangeAttributeListener>();
    private static List<AddNewInnerClassListener> addNewInnerClassListeners = new ArrayList<AddNewInnerClassListener>();
    public static List<AddNewClassListener> addNewClassListeners = new ArrayList<AddNewClassListener>();
    private static List<AddNewInterfaceListener> addNewInterfaceListeners = new ArrayList<AddNewInterfaceListener>();
    private static List<AddNewEnumListener> addNewEnumListeners = new ArrayList<AddNewEnumListener>();
    private static List<AddNewPackageListener> addNewPackageListeners = new ArrayList<AddNewPackageListener>();
    
    public static void clearData() {
        packageSafeDeleteListeners.clear();
        classSafeDeleteListeners.clear();
        classMethodSafeDeleteListeners.clear();
        classAttributeSafeDeleteListeners.clear();
        packageRenameListeners.clear();
        classRenamedEventlisteners.clear();
        classMethodChangeDeclarationListeners.clear();
        classAddAttributeListeners.clear();
        classAddMethodListeners.clear();
        classChangeAttributeListeners.clear();
        addNewInnerClassListeners.clear();
        addNewClassListeners.clear();
        addNewInterfaceListeners.clear();
        addNewEnumListeners.clear();
        addNewPackageListeners.clear();
    }
    
    protected static synchronized void fireClassRenamedEvent(ClassData classData) {

        ClassRenameEvent e = new ClassRenameEvent(new Object(), classData);
        for (ClassRenameListener listener : RefactoringEvents.classRenamedEventlisteners) {
            listener.ClassRenamed(e);
        }
    }

    public static synchronized void addClassRenameEventListener(ClassRenameListener listener) {
        RefactoringEvents.classRenamedEventlisteners.add(listener);
    }

    public static synchronized void removeClassSelectEventListener(ClassRenameListener listener) {
        RefactoringEvents.classRenamedEventlisteners.remove(listener);
    }

    protected static synchronized void firePackageSafeDeleteEvent(PackageData data) {

        PackageSelectEvent e = new PackageSelectEvent(new Object(), data);
        for (PackageSafeDeleteListener listener : RefactoringEvents.packageSafeDeleteListeners) {
            listener.onSafePackageDelete(e);
        }
    }

    public static synchronized void addPackageSafeDeleteListener(PackageSafeDeleteListener listener) {
        RefactoringEvents.packageSafeDeleteListeners.add(listener);
    }

    public static synchronized void removePackageSafeDeleteListener(PackageSafeDeleteListener listener) {
        RefactoringEvents.packageSafeDeleteListeners.remove(listener);
    }

    protected static synchronized void fireClassSafeDeleteEvent(ClassData data) {

        ClassSelectEvent e = new ClassSelectEvent(new Object(), data);
        for (ClassSafeDeleteListener listener : RefactoringEvents.classSafeDeleteListeners) {
            listener.onSafeClassDelete(e);
        }
    }

    public static synchronized void addClassSafeDeleteListener(ClassSafeDeleteListener listener) {
        RefactoringEvents.classSafeDeleteListeners.add(listener);
    }

    public static synchronized void removeClassSafeDeleteListener(ClassSafeDeleteListener listener) {
        RefactoringEvents.classSafeDeleteListeners.remove(listener);
    }
    
    protected static synchronized void fireClassMethodSafeDeleteEvent(ClassData classData, MethodData methodData) {
    
        ClassMethodEvent e = new ClassMethodEvent(new Object(), classData, methodData);
        
        for (ClassMethodSafeDeleteListener listener : RefactoringEvents.classMethodSafeDeleteListeners) {
            listener.onMethodSafeDelete(e);
        }
    }
    
    public static synchronized void addClassMethodSafeDeleteListener(ClassMethodSafeDeleteListener listener) {
        RefactoringEvents.classMethodSafeDeleteListeners.add(listener);
    }
    
    public static synchronized void removeClassMethodSafeDeleteListener(ClassMethodSafeDeleteListener listener) {
        RefactoringEvents.classMethodSafeDeleteListeners.remove(listener);
    }
    
    protected static synchronized void fireClassAttributeSafeDeleteEvent(ClassData classData, VariableData variableData) {
    
        ClassAttributeEvent e = new ClassAttributeEvent(new Object(), classData, variableData);
        
        for (ClassAttributeSafeDeleteListener listener : RefactoringEvents.classAttributeSafeDeleteListeners) {
            listener.onAttributeSafeDelete(e);
        }
    }
    
    public static synchronized void addClassAttributeSafeDeleteListener(ClassAttributeSafeDeleteListener listener) {
        RefactoringEvents.classAttributeSafeDeleteListeners.add(listener);
    }
    
    public static synchronized void removeClassAttributeSafeDeleteListener(ClassAttributeSafeDeleteListener listener) {
        RefactoringEvents.classAttributeSafeDeleteListeners.remove(listener);
    }
    
    protected static synchronized void firePackageRenameEvent(PackageData data) {
        
        PackageSelectEvent e = new PackageSelectEvent(new Object(), data);
        
        for (PackageRenameListener listener : RefactoringEvents.packageRenameListeners) {
            listener.onPackageRename(e);
        }
    }
    
    public static synchronized void addPackageRenameListener(PackageRenameListener listener) {
        RefactoringEvents.packageRenameListeners.add(listener);
    }
    
    public static synchronized void removePackageRenameListener(PackageRenameListener listener) {
        RefactoringEvents.packageRenameListeners.remove(listener);
    }
    
    protected static synchronized void fireClassMethodChangeDeclarationEvent(ClassData data, MethodData methodData) {
    
        ClassMethodEvent e = new ClassMethodEvent(new Object(), data, methodData);
        
        for (ClassMethodChangeDeclarationListener listener : RefactoringEvents.classMethodChangeDeclarationListeners) {
            listener.onMethodChangeDeclaration(e);
        }
    }
    
    public static synchronized void addClassMethodChangeDeclarationListener(ClassMethodChangeDeclarationListener listener) {
        RefactoringEvents.classMethodChangeDeclarationListeners.add(listener);
    }
    
    public static synchronized void removeClassMethodChangeDeclarationListener(ClassMethodChangeDeclarationListener listener) {
        RefactoringEvents.classMethodChangeDeclarationListeners.remove(listener);
    }
    
    protected static synchronized void fireClassAddAttributeEvent(ClassData data, VariableData variableData) {
    
        ClassAttributeEvent e = new ClassAttributeEvent(new Object(), data, variableData);
        
        for (ClassAddAttributeListener listener : RefactoringEvents.classAddAttributeListeners) {
            listener.onAddAttribute(e);
        }
    }
    
    public static synchronized void addClassAddAttributeListener(ClassAddAttributeListener listener) {
        RefactoringEvents.classAddAttributeListeners.add(listener);
    }
    
    public static synchronized void removeClassAddAttributeListener(ClassAddAttributeListener listener) {
        RefactoringEvents.classAddAttributeListeners.remove(listener);
    }
    
    protected static synchronized void fireClassAddMethodEvent(ClassData data, MethodData methodData) {
    
        ClassMethodEvent e = new ClassMethodEvent(new Object(), data, methodData);
        
        for (ClassAddMethodListener listener : RefactoringEvents.classAddMethodListeners) {
            listener.onAddMethod(e);
        }
    }
    
    public static synchronized void addClassAddMethodListener(ClassAddMethodListener listener) {
        RefactoringEvents.classAddMethodListeners.add(listener);
    }
    
    public static synchronized void removeClassAddMethodListener(ClassAddMethodListener listener) {
        RefactoringEvents.classAddMethodListeners.remove(listener);
    }
    
    protected static synchronized void fireClassChangeAttributeEvent(ClassData data, VariableData variable) {
    
        ClassAttributeEvent e = new ClassAttributeEvent(new Object(), data, variable);
        
        for (ClassChangeAttributeListener listener : RefactoringEvents.classChangeAttributeListeners) {
            listener.onChangeAttribute(e);
        }
    }
    
    public static synchronized void addClassChangeAttributeListener(ClassChangeAttributeListener listener) {
        RefactoringEvents.classChangeAttributeListeners.add(listener);
    }
    
    public static synchronized void removeClassChangeAttributeListener(ClassChangeAttributeListener listener) {
        RefactoringEvents.classChangeAttributeListeners.remove(listener);
    }
    
    protected static synchronized void fireAddNewInnerClassEvent(ClassData src, ClassData inner) {
    
        ClassSelectEvent e = new ClassSelectEvent(src, inner);
        
        for (AddNewInnerClassListener listener : RefactoringEvents.addNewInnerClassListeners) {
            listener.onAddNewInnerClass(e);
        }
    }
    
    public static synchronized void addAddNewInnerClassListener(AddNewInnerClassListener listener) {
        RefactoringEvents.addNewInnerClassListeners.add(listener);
    }
    
    public static synchronized void removeAddNewInnerClassListener(AddNewInnerClassListener listener) {
        RefactoringEvents.addNewInnerClassListeners.remove(listener);
    }
    
    protected static synchronized void fireAddNewClassEvent(PackageData source, ClassData newClass) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass);
        
        for (AddNewClassListener listener : RefactoringEvents.addNewClassListeners) {
            listener.onAddNewClass(e);
        }
    }
    
    protected static synchronized void fireAddNewClassEvent(PackageData source, ClassData newClass, int x, int y) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass, x, y);
        
        for (AddNewClassListener listener : RefactoringEvents.addNewClassListeners) {
            listener.onAddNewClass(e);
        }
    }
    
    public static synchronized void addAddNewClassListener(AddNewClassListener listener) {
        RefactoringEvents.addNewClassListeners.add(listener);
    }
    
    public static synchronized void removeAddNewClassListener(AddNewClassListener listener) {
        RefactoringEvents.addNewClassListeners.remove(listener);
    }
    
    protected static synchronized void fireAddNewInterfaceEvent(PackageData source, ClassData newClass) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass);
        
        for (AddNewInterfaceListener listener : RefactoringEvents.addNewInterfaceListeners) {
            listener.onAddNewInterface(e);
        }
    }
    
    protected static synchronized void fireAddNewInterfaceEvent(PackageData source, ClassData newClass, int x, int y) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass, x, y);
        
        for (AddNewInterfaceListener listener : RefactoringEvents.addNewInterfaceListeners) {
            listener.onAddNewInterface(e);
        }
    }
    
    public static synchronized void addAddNewInterfaceListener(AddNewInterfaceListener listener) {
        RefactoringEvents.addNewInterfaceListeners.add(listener);
    }
    
    public static synchronized void removeAddNewInterfaceListener(AddNewInterfaceListener listener) {
        RefactoringEvents.addNewInterfaceListeners.remove(listener);
    }
    
    protected static synchronized void fireAddNewEnumEvent(PackageData source, ClassData newClass) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass);
        
        for (AddNewEnumListener listener : RefactoringEvents.addNewEnumListeners) {
            listener.onAddNewEnum(e);
        }
    }
    
    protected static synchronized void fireAddNewEnumEvent(PackageData source, ClassData newClass, int x, int y) {
    
        ClassSelectEvent e = new ClassSelectEvent(source, newClass, x, y);
        
        for (AddNewEnumListener listener : RefactoringEvents.addNewEnumListeners) {
            listener.onAddNewEnum(e);
        }
    }
    
    public static synchronized void addAddNewEnumListener(AddNewEnumListener listener) {
        RefactoringEvents.addNewEnumListeners.add(listener);
    }
    
    public static synchronized void removeAddNewEnumListener(AddNewEnumListener listener) {
        RefactoringEvents.addNewEnumListeners.remove(listener);
    }
    
    protected static synchronized void fireAddNewPackageEvent(PackageData source, PackageData newPackage) {
    
        PackageSelectEvent e = new PackageSelectEvent(source, newPackage);
        
        for (AddNewPackageListener listener : RefactoringEvents.addNewPackageListeners) {
            listener.onAddNewPackage(e);
        }
    }
    
    protected static synchronized void fireAddNewPackageEvent(PackageData source, PackageData newPackage, int x, int y) {
    
        
        PackageSelectEvent e = new PackageSelectEvent((source == null) ? new Object() : source, newPackage, x, y);
        
        for (AddNewPackageListener listener : RefactoringEvents.addNewPackageListeners) {
            listener.onAddNewPackage(e);
        }
    }
    
    public static synchronized void addAddNewPackageListener(AddNewPackageListener listener) {
        RefactoringEvents.addNewPackageListeners.add(listener);
    }
    
    public static synchronized void removeAddNewPackageListener(AddNewPackageListener listener) {
        RefactoringEvents.addNewPackageListeners.remove(listener);
    }
}

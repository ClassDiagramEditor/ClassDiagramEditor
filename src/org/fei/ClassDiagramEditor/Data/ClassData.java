/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Data;

import japa.parser.ast.body.ModifierSet;
import java.util.ArrayList;
import org.fei.ClassDiagramEditor.Editor.events.PackageSelectEvent;
import org.fei.ClassDiagramEditor.Editor.events.listeners.PackageRenameListener;
import org.fei.ClassDiagramEditor.ProjectScannerFactory;
import org.fei.ClassDiagramEditor.JavaParser.ClassParser;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;

/**
 *
 * @author Tomas
 */
public class ClassData implements PackageRenameListener {

    private ClassParser classFile;
    private FileObject sourceFileObject;  // fileobject of java source file
    private ArrayList<MethodData> methods;  // metody (bez konstruktorov)
    private ArrayList<VariableData> variables;  // premenne triedy
    private PackageData packageData = null;
    private String packageName;
    private String className;
    private String typeSignature;
    private String sourceFileName;
    private boolean publicClass;
    private boolean interfaceClass;
    private boolean abstractClass;
    private String xmiid;
    private int access;
    private boolean enumClass;
    private String fullName;
    private boolean publicSourceClass; // treba pre refaktorizaciu ci ide o origos triedu

    public ClassData(ClassParser classFile) {

        this.classFile = classFile;
        enumClass = classFile.isEnumeration();
        packageName = classFile.getPackageName();
        className = classFile.getNameClass();
        fullName = classFile.getFullName();
        typeSignature = classFile.getGeneric();
        sourceFileName = classFile.getSourceFileName();
        
        publicSourceClass = classFile.isPublicSourceClass();

        interfaceClass = classFile.isInterfaceClass();
        abstractClass = classFile.isAbstractClass();
        publicClass = classFile.isPublicClass();

        sourceFileObject = classFile.getSourceFileObject();

        access = classFile.getAccess();
        
        xmiid = XmiIdFactory.getId();
    }
    
    // konstruktor pre inner class
    public ClassData(String name, ClassData parent){
    
        classFile = new ClassParser(name,parent.getClassFile()); 
        enumClass = false;
        
        access = parent.getAccess();
        
        packageName = parent.getPackageName();
        className = name;
        fullName = parent.getFullNameWitoutPackage() + "." + name ;
        classFile.setFullName(fullName);
        typeSignature = "";
        sourceFileName = parent.getSourceFileName();
        packageData = parent.getPackageData();
        
        access = ModifierSet.PUBLIC;
        
        interfaceClass = false;
        abstractClass = false;
        publicClass = false;
        
        publicSourceClass = false;

        publicClass = true;
        
        sourceFileObject = parent.getSourceFileObject();
        
        methods = new ArrayList<MethodData>();
        variables = new ArrayList<VariableData>();
        
        xmiid = XmiIdFactory.getId();
    }
    
    public ClassData(String name) {
    
        enumClass = false;

        
        packageName = "confi";
        className = name;
        fullName = "confi" + "." + name ;
        typeSignature = "";

        
        access = ModifierSet.PUBLIC;
        
        interfaceClass = false;
        abstractClass = false;
        publicClass = false;
        
        publicSourceClass = false;

        publicClass = true;

        
        methods = new ArrayList<MethodData>();
        variables = new ArrayList<VariableData>();
        
        xmiid = XmiIdFactory.getId();
    }
    
    // konstruktor pre novu triedu, rozhranie, enum, alebo abstraktnu triedu
    public ClassData(FileObject fo, String name, PackageData packageData, boolean isAbstract, boolean isEnum, boolean isInterface) {
    
        classFile = new ClassParser(fo,name,packageData.getName(),isAbstract,isEnum,isInterface);
        
        enumClass = isEnum;
        
        this.packageName = packageData.getName();
        className = name;
        fullName = name;
        
            
        typeSignature = "";
        sourceFileName = fo.getName();
        
        this.packageData = packageData;
        
        interfaceClass = isInterface;
        abstractClass = isAbstract;
        publicClass = true;
        
        publicSourceClass = true;
        
        sourceFileObject = fo;
        
        methods = new ArrayList<MethodData>();
        variables = new ArrayList<VariableData>();
        
        xmiid = XmiIdFactory.getId();
    }
    
   
    public Element xmiCreateClassElement(Document document) {

        if (this.isEnum()) {
            return xmiCreateEnumElement(document);
        }
        
        if (this.isInterface()) {
            return xmiCreateInterfaceElement(document);
        }

        Element feature = document.createElement("UML:Classifier.feature");
        Element classElement = document.createElement("UML:Class");

        classElement.setAttribute("xmi.id", this.getXmiid());
        classElement.setAttribute("name", this.getSimpleName());
        classElement.setAttribute("visibility", this.accessToFullString());
        classElement.setAttribute("isSpecification", "false");
        classElement.setAttribute("isAbstract", this.isAbstractClass() ? "true" : "false");
        classElement.setAttribute("isActive", "false");

        for (VariableData var : this.variables) {
            feature.appendChild(var.xmiCreateAttributeElement(document));
        }

        for (MethodData met : this.methods) {
            if (!met.isConstructor()) {
                feature.appendChild(met.xmiCreateOperationElement(document));
            }
        }

        classElement.appendChild(feature);

        return classElement;
    }

    public Element xmiCreateEnumElement(Document document) {

        Element feature = document.createElement("UML:Classifier.feature");
        Element literal = document.createElement("UML:Enumeration.literal");
        Element enumeration = document.createElement("UML:Enumeration");

        enumeration.setAttribute("xmi.id", this.xmiid);
        enumeration.setAttribute("name", this.getSimpleName());
        enumeration.setAttribute("isSpecification", "false");
        enumeration.setAttribute("isAbstract", "false");

        enumeration.appendChild(literal);

        for (VariableData v : this.variables) {
            literal.appendChild(v.xmiCreateEnumerationLiteralElement(document));
        }
        
        for (MethodData met : this.methods) {
            if (!met.isConstructor()) {
                feature.appendChild(met.xmiCreateOperationElement(document));
            }
        }
        
        enumeration.appendChild(feature);
        
        return enumeration;
    }

    public Element xmiCreateInterfaceElement(Document document) {
        
        Element feature = document.createElement("UML:Classifier.feature");
        Element interfaceElement = document.createElement("UML:Interface");

        interfaceElement.setAttribute("xmi.id", this.getXmiid());
        interfaceElement.setAttribute("name", this.getSimpleName());
        interfaceElement.setAttribute("visibility", this.accessToFullString());
        interfaceElement.setAttribute("isSpecification", "false");
        interfaceElement.setAttribute("isAbstract", "false");

        for (VariableData var : this.variables) {
            feature.appendChild(var.xmiCreateAttributeElement(document));
        }

        for (MethodData met : this.methods) {
            if (!met.isConstructor()) {
                feature.appendChild(met.xmiCreateOperationElement(document));
            }
        }

        interfaceElement.appendChild(feature);

        return interfaceElement;
    }

    public boolean isEnumClass() {
        return enumClass;
    }

    public boolean isPublicSourceClass() {
        return publicSourceClass;
    }

    
    public boolean isEnum() {
        return this.enumClass;
    }

    public boolean isInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(boolean interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    
    

    public ClassParser getClassFile() {
        return this.classFile;
    }

    public FileObject getSourceFileObject() {
        return this.sourceFileObject;
    }

    public void setSourceFileObject(FileObject fo) {
        this.sourceFileObject = fo;
    }
    
    public String getFullNameWitoutPackage(){
        return this.classFile.getFullName();
    }
    
    public String getFullNameWithoutClassName(){
        
        String[] temp = this.classFile.getFullName().split("\\.");
        String nameWithoutClassName = getPackageName() + ".";

        for(int i=0;i<temp.length-1;i++){
            nameWithoutClassName += temp[i];
            if(i != temp.length-2)
                nameWithoutClassName += ".";
        }
        return nameWithoutClassName;
    }
    
    public String getFullNameWithoutClassNameAndPackageName(){
        
        String[] temp = this.classFile.getFullName().split("\\.");
        String nameWithoutClassName = "";

        for(int i=0;i<temp.length-1;i++){
            nameWithoutClassName += temp[i];
            if(i != temp.length-2)
                nameWithoutClassName += ".";
        }
        return nameWithoutClassName;
    }
    
    public String getFullName() {
        return getPackageName() + "." + this.classFile.getFullName();
    }
    
    public String getFullName(boolean withoutDefaultPackage) {
        if (this.packageData.isDefaultPackage() && withoutDefaultPackage) {
            return className;
        }
        else {
            return getFullName();
        }
    }

    public String getSimpleName(){
        return className;
    }
    
    public void setFullNameToInnerClasses(ArrayList<ClassParser> triedy,String className){
        
        for(ClassParser trieda: triedy){

            String temp;
            
            
            if(this.getFullNameWithoutClassNameAndPackageName()!= "")
                 temp = trieda.getFullName().replaceFirst(this.getFullNameWitoutPackage(), this.getFullNameWithoutClassNameAndPackageName()+"."+ className);
            else
                temp = trieda.getFullName().replaceFirst(this.getFullNameWitoutPackage(), className);

            trieda.setFullName(temp);
            
            if(!trieda.getInnerClassesInterfacesAndEnum().isEmpty())
                this.setFullNameToInnerClasses(trieda.getInnerClassesInterfacesAndEnum(),className);
        }
        
    }

    public void setClassName(String className) {
        
        if(!classFile.getInnerClassesInterfacesAndEnum().isEmpty())
            this.setFullNameToInnerClasses(classFile.getInnerClassesInterfacesAndEnum(),className);
        
        this.className = className;

        if (this.isPublicSourceClass()) {
            sourceFileName = className + ".java";
            sourceFileObject = ProjectScannerFactory.getProjectScanner().getSourceFileObject(getSourceFileName(), packageData.getName());
        }
        
        if(this.isEnumClass()){
            
            for(VariableData var : variables){
                
                if(var.getType() == "enum"){
                    var.setEnumName(className);
                }
            }
        }

        
        String fullNameArray[] = getFullNameWitoutPackage().split("\\.");
        fullName = "";
        
        for(int i =0 ; i< fullNameArray.length-1 ; i++ )
            fullName += fullNameArray[i] + ".";
        fullName += className;
        
        
        this.classFile.setFullName(fullName);
        
    }
    
    public String modificatorToString() {
    
        if (isAbstractClass() && !isInterface())
            return "abstract";
        if (isInterface())
            return "interface";
        else
            return "class";
    }

    public ArrayList<MethodData> getMethods() {
        return this.methods;
    }

    public void setMethods(ArrayList<MethodData> methods) {
        this.methods = methods;
    }

    public ArrayList<VariableData> getVariables() {
        return this.variables;
    }

    public void setVariables(ArrayList<VariableData> variables) {
        this.variables = variables;
    }

    /**
     * @return the typeSignature
     */
    public String getTypeSignature() {
        return typeSignature;
    }

    public String accessToFullString() {

        if (classFile.isPublicClass()) {
            return "public";
        }
        if (classFile.isPrivateClass()) {
            return "private";
        }
        if (classFile.isProtectedClass()) {
            return "protected";
        }
        if (classFile.isPackageClass()) {
            return "package";
        }
        return "";
    }

    public boolean isPublicClass() {
        return this.publicClass;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        if (packageData != null) {
            return packageData.getName();
        } else {
            return packageName;
        }
    }

    public PackageData getPackageData() {
        return packageData;
    }

    public void setPackageData(PackageData packageData) {
        this.packageData = packageData;
        this.packageName = packageData.getName();
    }

    public String getClassName() {
        return className;
    }

    public int getAccess() {
        return access;
    }

    
    
    
    @Override
    public void onPackageRename(PackageSelectEvent e) {

        if (packageData == e.getPackageData()) {
            sourceFileObject = ProjectScannerFactory.getProjectScanner().getSourceFileObject(getSourceFileName(), e.getPackageData().getName());
        }
    }

    /**
     * @return the xmiid
     */
    public String getXmiid() {
        return xmiid;
    }

    public boolean isInterface() {
        return this.interfaceClass;
    }
    
    public void addVariable(VariableData var) {
        this.variables.add(var);
    }
    
    public void addMethod(MethodData method) {
        this.methods.add(method);
    }

    /**
     * @return the abstractClass
     */
    public boolean isAbstractClass() {
        return abstractClass;
    }

    /**
     * @return the sourceFileName
     */
    public String getSourceFileName() {
        return sourceFileName;
    }

    
}

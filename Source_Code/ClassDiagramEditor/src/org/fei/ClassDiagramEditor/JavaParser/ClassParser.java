/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.type.ClassOrInterfaceType;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mairo744
 */
public class ClassParser {

    private FileObject sourceFileObject;  // fileobject of java source file
    
    private ArrayList<MethodAndConstructorParser> methodAndConstruct = new ArrayList<MethodAndConstructorParser>();  // metody a konstruktory
    private ArrayList<VariableParser> variables = new ArrayList<VariableParser>();  // premenne triedy
    private ArrayList<ClassParser> innerClassesInterfacesAndEnum = new ArrayList<ClassParser>(); // vnorene tried
    
    private PackageParser packageData;
    private String packageName;
    private String nameClass; // Class simple name
    private String sourceFileName;
    
    private boolean publicSourceClass; //kvoli refaktorizacii musime vediet ci je vnorena alebo zakladna
    
    private String superClass;
    private ArrayList<String> interfaces = new ArrayList<String>();
    
    private int access;
    
    private boolean publicClass = false;
    private boolean privateClass = false;
    private boolean protectedClass = false;
    private boolean packageClass = false;
    
    private boolean interfaceClass = false;
    private boolean abstractClass = false;
    private boolean staticClass = false;
    private boolean finalClass = false;
    private boolean nativeClass = false;
    private boolean synchronizedClass = false;
    private boolean enumeration = false;
    
    private String generic = "";
    
    private String fullName;
    
    private String xmiid;
    
    private ArrayList<String> enumTypeName = new ArrayList<String>();
    
    
    /* Parsuje triedy */
    public ClassParser(ClassOrInterfaceDeclaration trieda, PackageParser pack, String sourceName, FileObject fileobject,String parent) {
               
        
        // typeSignature chyba
        // access chyba ale pomocou neho zistuje len modificator
        sourceFileName = sourceName;
        interfaceClass = trieda.isInterface();
        sourceFileObject = fileobject;
        
        
        /* Ulozenie baliku */
        packageData = pack;
        packageName = pack.getName();
        
        /* Nazov triedy */
        nameClass = trieda.getName();
        
        if((sourceName.equals(nameClass + ".java")) && parent == null)
            publicSourceClass = true;
        else
            publicSourceClass = false;
        
        if(parent!=null){
            this.fullName = parent + "." + nameClass;  
        }
        else{
            this.fullName = nameClass;
        }
        
        /* Generics */
        if(trieda.getTypeParameters()!=null){
         
            this.generic  = " <";
            for (int i=0;i<trieda.getTypeParameters().size();i++) {
                this.generic += trieda.getTypeParameters().get(i);
                if(i+1 < trieda.getTypeParameters().size())
                    this.generic += ", ";
            }
            this.generic  += ">";
        }
        
        /* Extends */
        if(trieda.getExtends() != null){

            List<ClassOrInterfaceType> allExt = trieda.getExtends();

            for (ClassOrInterfaceType ext : allExt) {
                superClass = ext.getName();
            }
        }
        

        /* Implements */
        if(trieda.getImplements() != null){

            List<ClassOrInterfaceType> allImp = trieda.getImplements() ;

            for(ClassOrInterfaceType imp : allImp) {
                interfaces.add(imp.getName());
            }
        }
        
        /* Modificator */
        access = trieda.getModifiers();
        if (ModifierSet.isPrivate(trieda.getModifiers())){
            privateClass = true;
        }
       else if (ModifierSet.isProtected(trieda.getModifiers())){
            protectedClass = true;
       }
       else if (ModifierSet.isPublic(trieda.getModifiers())){
            publicClass = true;
       }
       else {
           packageClass = true;
       }

       if (ModifierSet.isStatic(trieda.getModifiers())){
            staticClass = true;
       }
       if (ModifierSet.isAbstract(trieda.getModifiers())){
           abstractClass = true;
       }
       if (ModifierSet.isFinal(trieda.getModifiers())){
            finalClass = true;
       }
       if (ModifierSet.isNative(trieda.getModifiers())){
            nativeClass = true;
       }
       if (ModifierSet.isSynchronized(trieda.getModifiers())){
           synchronizedClass = true;
        }
        

    
        

        /* Prejdeme celu triedu */
        for(int i=0;i<trieda.getMembers().size();i++) {
          
             /* InnerClass */
            if(trieda.getMembers().get(i) instanceof ClassOrInterfaceDeclaration)
            {
                ClassOrInterfaceDeclaration classOrInterface = (ClassOrInterfaceDeclaration) trieda.getMembers().get(i);
                innerClassesInterfacesAndEnum.add( new ClassParser(classOrInterface,packageData,sourceFileName,fileobject,fullName));

            } 
            /* Metody */
            else if (trieda.getMembers().get(i) instanceof MethodDeclaration)
                methodAndConstruct.add(new MethodAndConstructorParser((MethodDeclaration) trieda.getMembers().get(i), interfaceClass));
            
            
            /* Konstruktor */
            else if (trieda.getMembers().get(i) instanceof ConstructorDeclaration)
                methodAndConstruct.add(new MethodAndConstructorParser((ConstructorDeclaration) trieda.getMembers().get(i))); 
            
            /* Enum */
            else if (trieda.getMembers().get(i) instanceof EnumDeclaration)
                innerClassesInterfacesAndEnum.add(new ClassParser((EnumDeclaration) trieda.getMembers().get(i),packageData,sourceFileName,fileobject,fullName)); 
                
        
            /* Premenne sa musia parsovat cez field */
            else if (trieda.getMembers().get(i) instanceof FieldDeclaration){
                
                FieldDeclaration field = (FieldDeclaration) trieda.getMembers().get(i);
                for(int position =0; position < field.getVariables().size();position++){
                    variables.add(new VariableParser((FieldDeclaration) trieda.getMembers().get(i),this.interfaceClass,position));
                }
            }

        }
    } 
    
    /* Pre enum */
    public ClassParser(EnumDeclaration enumVariable, PackageParser pack, String sourceName, FileObject fileobject,String parent) {
        
        enumeration = true;
        interfaceClass = false;
        
        sourceFileName = sourceName;
        sourceFileObject = fileobject;
        /* Ulozenie baliku */
        packageData = pack;
        packageName = pack.getName();

        
        
        if(enumVariable.getMembers()!=null){
            for(int i=0;i<enumVariable.getMembers().size();i++) {

                 /* Method */
                if(enumVariable.getMembers().get(i) instanceof MethodDeclaration)
                    methodAndConstruct.add(new MethodAndConstructorParser((MethodDeclaration) enumVariable.getMembers().get(i),false));
                else if(enumVariable.getMembers().get(i) instanceof ConstructorDeclaration)
                    methodAndConstruct.add(new MethodAndConstructorParser((ConstructorDeclaration) enumVariable.getMembers().get(i)));
                else if (enumVariable.getMembers().get(i) instanceof FieldDeclaration){
                    FieldDeclaration field = (FieldDeclaration) enumVariable.getMembers().get(i);
                    for(int position =0; position < field.getVariables().size();position++){
                        variables.add(new VariableParser((FieldDeclaration) enumVariable.getMembers().get(i),false,position));
                    }
                }
            }
        }
        
        /* Nazov */
        nameClass = enumVariable.getName();
        
        if(sourceName.equals(nameClass + ".java"))
            publicSourceClass = true;
        else
            publicSourceClass = false;
        
        if(parent!=null){
            this.fullName = parent + "." + nameClass;
        }
        else{
            this.fullName = nameClass;  
        }
        
        
        /* Implements */
        if(enumVariable.getImplements() != null){

            List<ClassOrInterfaceType> allImp = enumVariable.getImplements() ;

            for(ClassOrInterfaceType imp : allImp) {
                interfaces.add(imp.getName());
            }
        }
        
         /* Modificator enum */
        access = enumVariable.getModifiers();
        if (ModifierSet.isPrivate(enumVariable.getModifiers()))
             privateClass = true;
        else if(ModifierSet.isProtected(enumVariable.getModifiers()))
             protectedClass = true;           
        else if(ModifierSet.isPublic(enumVariable.getModifiers()))
             publicClass = true;
        else
            packageClass = true;

        if (ModifierSet.isStatic(enumVariable.getModifiers()))
             staticClass = true;

        if (ModifierSet.isAbstract(enumVariable.getModifiers()))
            abstractClass = true;

        if (ModifierSet.isFinal(enumVariable.getModifiers()))
             finalClass = true;

        if (ModifierSet.isNative(enumVariable.getModifiers()))
             nativeClass = true;

        if (ModifierSet.isSynchronized(enumVariable.getModifiers()))
             synchronizedClass = true;
        
        /* Mozne typy enum */
        if(enumVariable.getEntries() != null){
            List<EnumConstantDeclaration> allenum = enumVariable.getEntries();
            
            for (EnumConstantDeclaration enumName : allenum){
                enumTypeName.add(enumName.getName()); 
                
            }

        }
    }
    
    // konstruktor pre novu triedu, rozhranie, enum, alebo abstraktnu triedu
    public ClassParser(FileObject fo, String name, String packageName, boolean isAbstract, boolean isEnum, boolean isInterface) {
        
        this.nameClass = name;
        this.enumeration = isEnum;
        this.interfaceClass = isInterface;
        this.sourceFileObject = fo;
        this.packageName = packageName;
        this.abstractClass = isAbstract;
        this.publicClass = true;
        this.privateClass = false;
        this.protectedClass = false;
        this.packageClass = false;
        this.fullName = nameClass;
        this.publicSourceClass = true;

    }
    
    // konstruktor pre inner class
    public ClassParser(String name, ClassParser parent) {
        
        this.nameClass = name;
        this.enumeration = false;
        this.interfaceClass = false;
        this.sourceFileObject = parent.getSourceFileObject();
        this.packageName = parent.getPackageName();
        this.abstractClass = false;
        this.publicClass = true;
        this.privateClass = false;
        this.protectedClass = false;
        this.packageClass = false;
        this.fullName = name;
        this.publicSourceClass = false;
        
        parent.getInnerClassesInterfacesAndEnum().add(this);
    }
    
    
    public boolean isPublicSourceClass() {
        return publicSourceClass;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGeneric() {
        return generic;
    }
    

    public ArrayList<MethodAndConstructorParser> getMethodAndConstruct() {
        return methodAndConstruct;
    }

    public ArrayList<ClassParser> getInnerClassesInterfacesAndEnum() {
        return innerClassesInterfacesAndEnum;
    }

    public boolean isEnumeration() {
        return enumeration;
    }

    public ArrayList<String> getEnumTypeName() {
        return enumTypeName;
    }
    
    

    public ArrayList<String> getInterfaces() {
        return interfaces;
    }

    public int getAccess() {
        return access;
    }
    
    public FileObject getSourceFileObject() {
        return sourceFileObject;
    }

    public ArrayList<MethodAndConstructorParser> getMethods() {
        return methodAndConstruct;
    }

    public ArrayList<MethodAndConstructorParser> getMethodEnumConstruct() {
        return methodAndConstruct;
    }

    public String getSuperClass() {
        return superClass;
    }
    
    public ArrayList<VariableParser> getVariables() {
        return variables;
    }
    
    public boolean isEmptyInnerClasses(){
        
        if(innerClassesInterfacesAndEnum == null || innerClassesInterfacesAndEnum.isEmpty())
            return false;
        else
            return true;
    }

    public PackageParser getPackageData() {
        return packageData;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getNameClass() {
        return nameClass;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public boolean isPublicClass() {
        return publicClass;
    }

    public boolean isPrivateClass() {
        return privateClass;
    }

    public boolean isProtectedClass() {
        return protectedClass;
    }

    public boolean isPackageClass() {
        return packageClass;
    }

    public boolean isInterfaceClass() {
        return interfaceClass;
    }

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public boolean isStaticClass() {
        return staticClass;
    }

    public boolean isFinalClass() {
        return finalClass;
    }

    public boolean isNativeClass() {
        return nativeClass;
    }

    public boolean isSynchronizedClass() {
        return synchronizedClass;
    }

    public String getXmiid() {
        return xmiid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    
}

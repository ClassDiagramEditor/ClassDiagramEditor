/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.type.ClassOrInterfaceType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mairo744
 */
public class MethodAndConstructorParser {
    
    private ArrayList<MethodAndConstructorParameterParser> parameters = new ArrayList<MethodAndConstructorParameterParser>();

    private boolean constructor;
    private boolean enumeration;
    
    private boolean publicMEC = false;
    private boolean privateMEC = false;
    private boolean protectedMEC = false;
    private boolean packageMEC = false;
    
    private boolean interfaceMEC = false;
    private boolean abstractMEC = false;
    private boolean staticMEC = false;
    private boolean finalMEC = false;
    private boolean nativeMEC = false;
    private boolean synchronizedMEC = false;
    
    private String MECReturnType;
    private String MECName;
    
    private int access;
    
    private ArrayList<String> enumTypeName = new ArrayList<String>();
    private ArrayList<String> interfaces = new ArrayList<String>();
    
    /* Parsuje metody */
    public MethodAndConstructorParser( MethodDeclaration method, boolean isInterface) {
        
        constructor = false;
        
         /* Nazov metody */
        MECName = method.getName();

        /* Return type metody */
        MECReturnType = method.getType().toString();

        /* Modificator metody */
        if(!isInterface){
            if (ModifierSet.isPrivate(method.getModifiers()))
                 privateMEC = true;
            else if (ModifierSet.isProtected(method.getModifiers()))
                 protectedMEC = true; 
            else if (ModifierSet.isPublic(method.getModifiers()))
                 publicMEC = true;
            else
                packageMEC = true;

            access = method.getModifiers();
            if (ModifierSet.isStatic(method.getModifiers()))
                 staticMEC = true;
            if (ModifierSet.isAbstract(method.getModifiers()))
                abstractMEC = true;
            if (ModifierSet.isFinal(method.getModifiers()))
                 finalMEC = true;
            if (ModifierSet.isNative(method.getModifiers()))
                 nativeMEC = true;    
            if (ModifierSet.isSynchronized(method.getModifiers()))
                 synchronizedMEC = true;
        }else{
            publicMEC = true;
            staticMEC = true;
            access = ModifierSet.PUBLIC + ModifierSet.STATIC;
        }


         /* Parametre metody s typom */
        if(method.getParameters() != null){
            List<Parameter> allparam = method.getParameters();

            for (Parameter param : allparam) {

                parameters.add(new MethodAndConstructorParameterParser(param));
            }
        }

    }
    
    /* Parsuj constuctorData */
    public MethodAndConstructorParser(ConstructorDeclaration construct) {
       
        constructor = true;
        
        /* Meno */
        MECName = construct.getName();

        /* Modificators */
        access = construct.getModifiers();
        if (ModifierSet.isPrivate(construct.getModifiers()))
               privateMEC = true;
          else if (ModifierSet.isProtected(construct.getModifiers()))
               protectedMEC = true; 
          else if (ModifierSet.isPublic(construct.getModifiers()))
               publicMEC = true;
          else
               packageMEC = true;

        /* Parametre metody s typom */
        if(construct.getParameters() != null){
              List<Parameter> allparam = construct.getParameters();

              for (Parameter param : allparam) {

                  parameters.add(new MethodAndConstructorParameterParser(param));    
              }
         }

      }


    public int getAccess() {
        return access;
    }

    public ArrayList<MethodAndConstructorParameterParser> getParameters() {
        return parameters;
    }

    public boolean isConstructor() {
        return constructor;
    }

    public boolean isEnumeration() {
        return enumeration;
    }

    public boolean isPublicMEC() {
        return publicMEC;
    }

    public boolean isPrivateMEC() {
        return privateMEC;
    }

    public boolean isProtectedMEC() {
        return protectedMEC;
    }

    public boolean isPackageMEC() {
        return packageMEC;
    }

    public boolean isInterfaceMEC() {
        return interfaceMEC;
    }

    public boolean isAbstractMEC() {
        return abstractMEC;
    }

    public boolean isStaticMEC() {
        return staticMEC;
    }

    public boolean isFinalMEC() {
        return finalMEC;
    }

    public boolean isNativeMEC() {
        return nativeMEC;
    }

    public boolean isSynchronizedMEC() {
        return synchronizedMEC;
    }

    public String getMECReturnType() {
        return MECReturnType;
    }

    public String getMECName() {
        return MECName;
    }

    public ArrayList<String> getEnumTypeName() {
        return enumTypeName;
    }
    
    
    
}

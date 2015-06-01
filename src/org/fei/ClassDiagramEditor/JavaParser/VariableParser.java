/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.VariableDeclarator;

/**
 *
 * @author mairo744
 */
public class VariableParser {

    private String variableName;
    private String variableType;
     
    private boolean publicVariable = false;
    private boolean privateVariable = false;
    private boolean protectedVariable = false;
    private boolean packageVariable = false;
    
    private boolean interfaceVariable = false;
    private boolean abstractVariable = false;
    private boolean staticVariable = false;
    private boolean finalVariable = false;
    private boolean nativeVariable = false;
    private boolean synchronizedVariable = false;
    
    
    private int access;
     
    /* Parsuje Variable */
    public VariableParser(FieldDeclaration field, boolean isInterface,int i) {

        if(field.getVariables().get(i) instanceof VariableDeclarator){
             VariableDeclarator variable = (VariableDeclarator) field.getVariables().get(i); 

             /* Nazov premennej */
             variableName = variable.getId().getName();
             variableType = field.getType().toString();

             /* Modificator premennej */
             if(!isInterface){
                 access = field.getModifiers();
                 if (ModifierSet.isPrivate(field.getModifiers()))
                      privateVariable = true;
                 else if (ModifierSet.isProtected(field.getModifiers()))
                      protectedVariable = true;
                 else if (ModifierSet.isPublic(field.getModifiers()))
                      publicVariable = true;
                 else
                     packageVariable = true;

                 if (ModifierSet.isStatic(field.getModifiers()))
                     staticVariable = true;
                 if (ModifierSet.isAbstract(field.getModifiers()))
                     abstractVariable = true;   
                 if (ModifierSet.isFinal(field.getModifiers()))
                     finalVariable = true;
                 if (ModifierSet.isNative(field.getModifiers()))
                     nativeVariable = true;
                 if (ModifierSet.isSynchronized(field.getModifiers()))
                     synchronizedVariable = true;
             }else{
                 publicVariable = true;
                 staticVariable = true;
                 access = ModifierSet.PUBLIC + ModifierSet.STATIC;
             }
        } 
    }

    public int getAccess() {
        return access;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableType() {
        return variableType;
    }

    public boolean isPublicVariable() {
        return publicVariable;
    }

    public boolean isPrivateVariable() {
        return privateVariable;
    }

    public boolean isProtectedVariable() {
        return protectedVariable;
    }

    public boolean isPackageVariable() {
        return packageVariable;
    }

    public boolean isInterfaceVariable() {
        return interfaceVariable;
    }

    public boolean isAbstractVariable() {
        return abstractVariable;
    }

    public boolean isStaticVariable() {
        return staticVariable;
    }

    public boolean isFinalVariable() {
        return finalVariable;
    }

    public boolean isNativeVariable() {
        return nativeVariable;
    }

    public boolean isSynchronizedVariable() {
        return synchronizedVariable;
    }
    
}

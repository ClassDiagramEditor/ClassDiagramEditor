/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import java.util.List;

/**
 *
 * @author mairo744
 */
public class MethodAndConstructorParameterParser {
    
    private Parameter param;
    private String descriptor;
    private String name;
    private String type;
     /* Parser na parametre metod */
     public MethodAndConstructorParameterParser( Parameter param) {
          
         this.param = param;
         descriptor = param.getType() + " " + param.getId().getName();
         name       = param.getId().getName();
         type       = param.getType().toString();

    }

    public Parameter getParam() {
        return param;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
     
     
    
}

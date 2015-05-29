/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.JavaParser;

import japa.parser.ast.PackageDeclaration;


/**
 *
 * @author mairo744
 */
public class PackageParser {
    
    private String name;
    private String fullName;
    
    public PackageParser( PackageDeclaration pack) {
    
        
        if(pack != null){
            this.name = pack.getName().toString();
            //this.name = pack.getName().getName();
        }
        else{
            this.name = "";
            //this.fullName = "";
        }
    }

    public String getName() {
        return name;
    }
    
}

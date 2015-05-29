/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Parsers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
/**
 *
 * @author Tomas
 */
public class MethodParser {
    
    // parsing results
    private Set<Modifier> modifiers;    // modifikatory
    private ArrayList<ParameterInfo> parameters;   // parametre metody
    private String name;    // nazov metody
    private String returnType;  // method return type
    
    
    private String declString;  // method declaration
    
    public class ParameterInfo {
        public int index;
        public String type;
        public String name;
    }
    
    public MethodParser(String declaration) throws MethodParsingException {
        
        modifiers = new HashSet<Modifier>();
        parameters = new ArrayList<ParameterInfo>();
        declString = declaration;
        String declnowhitespaces = declaration.replaceAll("\\s+","");
        
        parseReturnType(declnowhitespaces);

        
        int leftBracketPos = declnowhitespaces.indexOf("(");
        int rightBracketPos = declnowhitespaces.indexOf("):");
        if (leftBracketPos >= rightBracketPos)
            throw new MethodParsingException("Illegal method statement");
        
        String parametersString = declnowhitespaces.substring(leftBracketPos + 1, rightBracketPos);
        parseParameters(parametersString);
        
        int startparam = declString.indexOf("(");
        String methodsubstr = declString.substring(0, startparam);
        
        parseNameAndModifiers(methodsubstr);
    }
    
    // kontrola pred parsovanim na neziaduce znaky
    private void checkBeforeParse(String str) throws MethodParsingException {
    
        // kontrola na zatvorky ( )
        if (ParserTools.countMatches(str, '(') > 1)
            throw new MethodParsingException("Illegal method statement");
        if (ParserTools.countMatches(str, ')') > 1)
            throw new MethodParsingException("Illegal method statement");

    }
    
    private void parseReturnType(String str) {
        
        int delpos = str.indexOf("):");
        returnType = str.substring(delpos+2);
        
        if (returnType.length() == 0)
            returnType = "void";
    }
    
    private void parseParameters(String str) throws MethodParsingException {
    
        String[] splitParams = str.split(",");
        
        if (splitParams.length == 1 && splitParams[0].isEmpty())
            return;
        
        for (int i = 0; i < splitParams.length; i++) {
            String[] splitNameAndType = splitParams[i].split(":");
            
            if (splitNameAndType.length != 2)
                throw new MethodParsingException("Illegal method parameters expression");
            
            ParameterInfo pinfo = new ParameterInfo();
            pinfo.index = i;
            pinfo.name = splitNameAndType[0];
            pinfo.type = splitNameAndType[1];
            
            parameters.add(pinfo);
        }
    }
    
    private void parseNameAndModifiers(String str) throws MethodParsingException {
        
        String accesslist = "-+#~";
        
        // zistime ci obsahuje pristupove prava
        for (int i = 0; i < 4; i++) {
            
            String access = String.valueOf(accesslist.charAt(i));
        
            // ak obsahuje pristupove prava pred nazvom metody rozparsujeme to bez medzier
            // bude v tvare 'SA-nazov'
            if (str.contains(access)) {
            
                String tmp = str.replaceAll("\\s+","");
                String tmp2[] = tmp.split("\\" + access);  // podla pristupu rozdelime na dve casti
                
                if (tmp2.length == 2) {
                   
                    // prva cast su modifikatory
                    if (tmp2[0].contains("s"))
                        modifiers.add(Modifier.STATIC);
                    if (tmp2[0].contains("a"))
                        modifiers.add(Modifier.ABSTRACT);
                    
                    // druha cast meno
                    name = tmp2[1];
                }
                else if (tmp2.length == 1) {
                    // ak neobsahuje modifikatory
                    name = tmp2[0];
                }
                else
                    throw new MethodParsingException("Illegal method name or modifiers");
                
                switch (access.charAt(0)) {
                    case '-':
                        modifiers.add(Modifier.PRIVATE);
                        break;
                    case '+':
                        modifiers.add(Modifier.PUBLIC);
                        break;
                    case '#':
                        modifiers.add(Modifier.PROTECTED);
                        break; 
                    case '~':
                        //modifiers.add(Modifier.DEFAULT);
                        break;    
                }
                
                // blba zaplata, niekedy sa dostal prazdny znak pred nazov metody co robilo problemy
                name = name.replaceAll("\\s+","");
                return;
            }
        }
        
        // neobsahuje pristupove prava je v tvare 'S nazov' alebo 'nazov'
        if (str.matches("\\s+")) {
            
            String[] tmp = str.split("\\s+");
            
            if (tmp.length != 2)
                throw new MethodParsingException("Illegal method name or modifiers");
            
            // modifikatory
            if (tmp[0].contains("s"))
                modifiers.add(Modifier.STATIC);
            if (tmp[0].contains("a"))
                modifiers.add(Modifier.ABSTRACT);
           
            
            // nazov metody
            name = tmp[1];
        }
        else
            name = str;
        
        // blba zaplata, niekedy sa dostal prazdny znak pred nazov metody co robilo problemy
        name = name.replaceAll("\\s+","");
    }
    
    public Set<Modifier> getModifiers() {
        return this.modifiers;
    }
    
    public String getReturnType() {
        return this.returnType;
    }
    
    public ArrayList<ParameterInfo> getParameters() {
        return this.parameters;
    }
    
    public String getName() {
        return this.name;
    }
}

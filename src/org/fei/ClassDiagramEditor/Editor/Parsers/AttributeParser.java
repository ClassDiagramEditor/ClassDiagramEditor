/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Parsers;

import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 *
 * @author Tomas
 */
public class AttributeParser {
    
    private Set<Modifier> modifiers;
    private String name;
    private String type;    // return type

    public AttributeParser(String declaration, boolean isEnum) throws AttributeParsingException {
        
        
        modifiers = new HashSet<Modifier>();
        
        if (isEnum) {
            parseEnumConstants(declaration);
            return;
        }
        
        if (ParserTools.countMatches(declaration, ':') != 1)
            throw new AttributeParsingException("Illegal statement");
        
        String[] splits = declaration.split("\\:");
        
        parseNameAndModifiers(splits[0]);
        type = splits[1].replaceAll("\\s+","");
    }
    
    private void parseEnumConstants(String declaration) throws AttributeParsingException {
        
        
        if (ParserTools.containsWhiteSpace(declaration))
            throw new AttributeParsingException("Enum constants can not contains white spaces");
        
        if (ParserTools.countMatches(declaration, ':') != 0)
             throw new AttributeParsingException("Enum constants parse error");
        
        name = declaration;
        modifiers.add(Modifier.PUBLIC);
        modifiers.add(Modifier.STATIC);
    }
    
    private void parseNameAndModifiers(String str) throws AttributeParsingException {
        
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
                    
                    // druha cast meno
                    name = tmp2[1];
                }
                else if (tmp2.length == 1) {
                    // ak neobsahuje modifikatory
                    name = tmp2[0];
                }
                else
                    throw new AttributeParsingException("Illegal attribute name or modifiers");
                
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
                throw new AttributeParsingException("Illegal attribute name or modifiers");
            
            // modifikatory
            if (tmp[0].contains("s"))
                modifiers.add(Modifier.STATIC);
            
            // nazov metody
            name = tmp[1];
        }
        else
            name = str;
        
        // blba zaplata, niekedy sa dostal prazdny znak pred nazov metody co robilo problemy
        name = name.replaceAll("\\s+","");
    }
    

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

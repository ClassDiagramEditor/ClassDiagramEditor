/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Parsers;

/**
 *
 * @author Tomas
 */
public class ClassNameParser {
    
    public static void CheckIfNameIsValid(String name) throws ClassNameParsingException {
        
        if (ParserTools.containsWhiteSpace(name))
            throw new ClassNameParsingException("Class name contains whitespace");
        
        // A non-word character
        if (name.matches(".*\\W+.*"))
            throw new ClassNameParsingException("Class name contains non word character");
    }
}

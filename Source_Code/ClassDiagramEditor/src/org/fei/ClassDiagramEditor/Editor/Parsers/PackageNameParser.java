/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Parsers;

import java.util.HashMap;

/**
 *
 * @author Tomas
 */
public class PackageNameParser {

    public static void CheckIfNameIsValid(String origs, String news) throws PackageNameParsingException {

        int separators = ParserTools.countMatches(origs, '.');

        if (separators != ParserTools.countMatches(news, '.')) {
            throw new PackageNameParsingException("Invalid number of package separator (.)");
        }

        String[] names = news.split("\\.");

        if (names.length != separators + 1) {
            throw new PackageNameParsingException("Invalid new package name");
        }

        for (String s : names) {
            // prazdny nazov nemoze byt
            if (s.isEmpty()) {
                throw new PackageNameParsingException("Invalid new package name");
            }
            // ani prazdne miesta v nazve nesmie obsahovat
            if (ParserTools.containsWhiteSpace(s)) {
                throw new PackageNameParsingException("Invalid new package name");
            }
        }
    }

    public static HashMap<Integer, String> ParseName(String origName, String newName) throws PackageNameParsingException {
        
        CheckIfNameIsValid(origName, newName);
        
        HashMap<Integer, String> elementsMap = new HashMap<Integer, String>();
        
        String[] origStrings = origName.split("\\.");
        String[] newStrings = newName.split("\\.");
        
        if (origStrings.length != newStrings.length)
            throw new PackageNameParsingException("Invalid new package name");
        
        for (int i = 0; i < newStrings.length; i++) {
            
            // zistime ktora cast sa lisi
            if (!origStrings[i].equals(newStrings[i])) {
                elementsMap.put(i, newStrings[i]);
            }
        }
        
//        if (elementsMap.isEmpty())
//           throw new PackageNameParsingException("No changes detected");
        
        return elementsMap;
    }
    
    public static HashMap<Integer, String> ParseName(String newName) {
        
        HashMap<Integer, String> elementsMap = new HashMap<Integer, String>();
        
        String[] newStrings = newName.split("\\.");
        
        for (int i = 0; i < newStrings.length; i++)
            elementsMap.put(i, newStrings[i]);
        
        return elementsMap;
    }
}

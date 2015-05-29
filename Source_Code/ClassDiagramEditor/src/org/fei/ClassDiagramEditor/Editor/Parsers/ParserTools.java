/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Parsers;

/**
 *
 * @author Tomas
 */
public class ParserTools {
    
    public static int countMatches(String str, char matcher) {
        return str.length() - str.replace(String.valueOf(matcher), "").length();
    }

    public static boolean containsWhiteSpace(String testCode) {
        if (testCode != null) {
            for (int i = 0; i < testCode.length(); i++) {
                if (Character.isWhitespace(testCode.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}

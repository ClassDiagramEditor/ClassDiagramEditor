/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Editor.Refactoring;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Tomas
 */
public class FileCreator {
    
    public static void createClass(BufferedWriter fileOutput, String pcg, String newName, boolean isAbstract) throws IOException {
    
        fileOutput.write("package " + pcg + ";" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        if (isAbstract)
            fileOutput.write("abstract ");
        fileOutput.write("public class " + newName + " {" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        fileOutput.write("}" + System.lineSeparator());
    }
    
    public static void createInterface(BufferedWriter fileOutput, String pcg, String newName) throws IOException {
    
        fileOutput.write("package " + pcg + ";" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        fileOutput.write("public interface " + newName + " {" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        fileOutput.write("}" + System.lineSeparator());
    }
    
    public static void createEnum(BufferedWriter fileOutput, String pcg, String newName) throws IOException {
    
        fileOutput.write("package " + pcg + ";" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        fileOutput.write("public enum " + newName + " {" + System.lineSeparator());
        fileOutput.write(System.lineSeparator());
        fileOutput.write("}" + System.lineSeparator());
    }
}

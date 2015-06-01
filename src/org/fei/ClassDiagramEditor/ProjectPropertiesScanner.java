package org.fei.ClassDiagramEditor;

//import org.fei.ClassDiagramEditor.WindowComponents.Message;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.fei.ClassDiagramEditor.WindowComponents.Message;

/**
 * Trieda skuma subor project.properties.
 * Ma za ulohu najst adresar v ktorom sa nachadzaju class subory impl. build/classes
 * 
 * !!!! FUNGUJE LEN PRE Java SE projekty !!!!
 * 
 * @author mairo744
 */
public class ProjectPropertiesScanner {
    
    /**
     * Premenna do ktorej sa ulozi cesta k adresaru v ktorm su class subory.
     */
    String classesDir;
    
    ProjectPropertiesScanner(String filePath) {
        try {
            FileInputStream fstream = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            classesDir = new String();
            String buildDir = new String();
            
            // nacitava po riadkoch cely subor
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("build.classes.dir"))
                    classesDir = strLine.split("=")[1];
                if (strLine.startsWith("build.dir"))
                    buildDir = strLine.split("=")[1];
                
            }
            
            // nakoniec posklada cestu
            classesDir = classesDir.replace("${build.dir}", buildDir);
                   
            in.close();
        }
        catch (Exception e) {
            Message.showMessage(e.getMessage());
        }
    }

    public String getClassesDir() {
        return classesDir;
    }
}

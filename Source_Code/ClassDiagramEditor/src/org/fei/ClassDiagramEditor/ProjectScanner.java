package org.fei.ClassDiagramEditor;



import java.util.List;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.support.ProjectOperations;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.netbeans.api.project.ProjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.net.URL;

/**
 * Trieda ma za ulohu zistit informacie o projekte.
 * Testovat projekt ci je spustitelna java aplikacia a najst class subory projektu.
 * 
 * @author      Tomas
 */
public class ProjectScanner {

    private FileObject projectXml;
    private FileObject projectProperties;
    private String projectName;
    private String projectType;
    private FileObject buildImplXml;
    private Project project;

    public ProjectScanner(Project p) {

        // vrati metadata o projekte, medzi datami je aj nbproject adresar
        List<FileObject> fo = ProjectOperations.getMetadataFiles(p);
        for (FileObject f : fo) {
            if (f.isFolder()) {
                this.findFiles(f, "project", "xml");
                this.findFiles(f, "project", "properties");
                this.findFiles(f, "build-impl", "xml");
            }
        }
        
        project = p;

        this.parseTypeFromProjectXml();

        projectName = ProjectUtils.getInformation(p).getDisplayName();
    }

    /**
     * Rekurzivne prehladava adresare hlavneho projektu podla nazvu suboru a koncovky.
     *
     * @param object typu FileObjekt moze byt bud subor alebo adresar. 
     * @param fileName typu String predstavuje nazov hladaneho suboru.
     * @param extension typu String predstavuje koncovku hladaneho suboru.
     */
    private void findFiles(FileObject object, String fileName, String extension) {

        FileObject[] objects = object.getChildren();
        for (int i = 0; i < objects.length; i++) {
            findFiles(objects[i], fileName, extension);

            if (fileName.equals(objects[i].getName()) && objects[i].hasExt(extension)) {
                if (fileName.equals("project") && extension.equals("xml")) {
                    this.projectXml = objects[i];
                }
                if (fileName.equals("project") && extension.equals("properties")) {
                    this.projectProperties = objects[i];
                }
                if (fileName.equals("build-impl") && extension.equals("xml")) {
                    this.buildImplXml = objects[i];
                }
            }
        }

        if (objects.length == 0) {
            return;
        }
    }
    /**
     * Rekurzivne prehladava adresare hlavneho projektu iba podla koncovky.
     *
     * @param object typu FileObjekt moze byt bud subor alebo adresar. 
     * @param extension typu String predstavuje koncovku hladaneho suboru.
     * @param resultsPath do ArrayListu uklada cestu k najdenym suborom.
     */
    public void findFiles(FileObject object, String extension, ArrayList<String> resultsPath) {

        FileObject[] objects = object.getChildren();
        for (int i = 0; i < objects.length; i++) {
            findFiles(objects[i], extension, resultsPath);

            if (objects[i].hasExt(extension)) {
                resultsPath.add(objects[i].getPath());
            }
        }

        if (objects.length == 0) {
            return;
        }
    }

    /*
     * Podobne ako findFiles ale vracia list URL adries suborov ktore najde
     * 
     * @param object typu FileObjekt moze byt bud subor alebo adresar. 
     * @param extension typu String predstavuje koncovku hladaneho suboru.
     * @param resultsURL do ArrayListu uklada URL najdenych suborov.
     */
    public void findFilesURL(FileObject object, String extension, ArrayList<URL> resultsURL) {

        FileObject[] objects = object.getChildren();
        for (int i = 0; i < objects.length; i++) {
            findFilesURL(objects[i], extension, resultsURL);

            if (objects[i].hasExt(extension)) {
                resultsURL.add(objects[i].toURL());               
            }
        }

        if (objects.length == 0) {
            return;
        }
    }
    
    // najde na zaklade nazvu zdrojoveho suboru triedy a nazvu balika triedy jej FileObject
    public FileObject getSourceFileObject(String classSourceFileName, String classPackage) {
        
        classPackage = classPackage.replaceAll("\\.", "/");
        
        String classSuffix = classPackage.isEmpty() ? classSourceFileName : classPackage + "/" + classSourceFileName;
        
        ArrayList<FileObject> firstResult = new ArrayList<FileObject>();  // tu moze byt viac suborov s rovnakym nazvom
        
        String[] splitStrings = classSourceFileName.split("\\.");
        
        getFileObjectRecursive(project.getProjectDirectory(), splitStrings[0], splitStrings[1], firstResult);
        
        for (FileObject fo : firstResult) {
            if (fo.getPath().equals(project.getProjectDirectory().getPath() + "/src/" + classSuffix))
                return fo;
        }
        
        return null;
    }
    

    private void getFileObjectRecursive(FileObject fo, String fileName, String extension, ArrayList<FileObject> result) {
        
        FileObject[] objects = fo.getChildren();
        
        for (int i = 0; i < objects.length; i++) {
            
            if ((objects[i].getName()).equals(fileName) && objects[i].hasExt(extension))
                result.add(objects[i]);
            else if (objects[i].isFolder())
                getFileObjectRecursive(objects[i], fileName, extension, result);
        }
        
        if (objects.length == 0) {
            return;
        }
    }

    /*
     * Nacitava typ projektu zo suboru project.xml 
     * Typ projektu sa nachadza v elemente <type></type>
     */
    private void parseTypeFromProjectXml() {

        try {
            File file = new File(projectXml.getPath());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList typElement = doc.getElementsByTagName("type");
            projectType = typElement.item(0).getFirstChild().getNodeValue();

        } catch (Exception ex) {
            
            /* Docasne zakomentovanie chyby */
            //Message.showMessage(ex.getMessage());
        }
    }
    
    public ArrayList<FileObject> getFileObjectForPackage(String packageName) {
        
        FileObject fo = null;
        FileObject src = null;
        FileObject[] tmp = null;
        
        ArrayList<FileObject> result = new ArrayList<FileObject>();
        
        String[] pcgStrs = packageName.split("\\.");
        
        for (FileObject fotmp : project.getProjectDirectory().getChildren()) {
            if (fotmp.isFolder()) {
                if (fotmp.getName().endsWith("src")) {
                    src = fotmp;
                    break;
                }
            }
        }
        
        fo = src;
        for (String foldername : pcgStrs) {
        
            for (FileObject fotmp : fo.getChildren()) {
                if (fotmp.isFolder()) {
                    if (fotmp.getName().endsWith(foldername)) {
                        fo = fotmp;
                        result.add(fotmp);
                    }
                }
            }
        }
        
        return result;
    }

    /*
     * Testuje ci je projekt podporovaneho typu
     * 
     * Ak je typ projektu podporovana java aplikacia, vrati true inak false
     * Zatial sa podporuje len java SE apliakacia.
     */
    public boolean isProjectValid() {
        
        if (projectType.equals("org.netbeans.modules.java.j2seproject")) {  // java SE project
            return true;
        }
        else if (projectType.equals("org.netbeans.modules.apisupport.project")) { // netbeans module project
            return true;
        }
   
        return false;
    }

    public FileObject getBuildImplXml() {
        return buildImplXml;
    }

    public String getProjectName() {
        return projectName;
    }

    public FileObject getProjectProperties() {
        return projectProperties;
    }
    
    public FileObject getProjectSrc() {
        FileObject src = null;
        for (FileObject fo : project.getProjectDirectory().getChildren()) {
            if (fo.isFolder()) {
                if (fo.getName().endsWith("src")) {
                    src = fo;
                    break;
                }
            }
        }
        return src;
    }
}

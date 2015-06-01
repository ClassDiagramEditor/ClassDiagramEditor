/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.DirecotoryWatch;


import japa.parser.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static org.fei.ClassDiagramEditor.DirecotoryWatch.DirectoryWatch.cast;
import org.fei.ClassDiagramEditor.JavaParser.JavaReaderAction;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.util.Exceptions;
import java.util.ArrayList;

/**
 *
 * @author mairo744
 */
public class RefactorDiagram implements Runnable {

    private final Map<WatchKey,Path> keys;
    private JavaReaderAction javaDatas;
    private WatchKey key;
    private DirectoryWatch dw;
    private ArrayList<String> arrayPaths = new ArrayList<String>();

    
    public RefactorDiagram(Map keys, JavaReaderAction sources,DirectoryWatch dw) {
        
        this.keys = keys;
        this.javaDatas = sources;
        this.dw = dw;
    }

    
    
    @Override
    public void run() {
        
        WatchKey kluc = null;
        while (!dw.isCancel()) {

            synchronized (this) {
                try {
                    //setStartParse(true);
                    while(!DirectoryWatch.isStartParse()){
                        this.wait();
                    }
                    DirectoryWatch.setStartParse(false);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            boolean isDataModelUpdate = dw.isAccessToRefactor();
            
            if(isDataModelUpdate){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            
            if(!isDataModelUpdate || !DirectoryWatch.isStartParse()){
                
                ArrayList<FileSystemEvent> events = new ArrayList<FileSystemEvent>();
                Path dir;
                
                synchronized (this) {
                    kluc = dw.getKey();
                    dir = dw.getKeys().get(kluc);
                    for (WatchEvent<?> event : kluc.pollEvents()) {
                        WatchEvent<Path> ev = cast(event);
                        events.add(new FileSystemEvent(ev.context(), event.kind()));
                    }
                }

                int pocet = 0;
                for (FileSystemEvent event : events) {
                    pocet = 1;
                    
                    WatchEvent.Kind kind = event.getNameOfEvent();
                    Path name = event.getPathOfFile();
                    Path child = dir.resolve(name);

                    if(!name.toString().startsWith(".")){
                        if(isDataModelUpdate){
                            
                            // TBD - provide example of how OVERFLOW event is handled
                            if (kind == OVERFLOW) {
                                continue;
                            }           

                            if(name.toString().endsWith(".java") || name.toString().endsWith(".java~")){

                                try {
                                    if(kind == ENTRY_MODIFY){
                                        this.javaDatas.sourceCodeModify(this.javaDatas);

                                    } 
                                    else if(kind == ENTRY_CREATE){
                                        this.javaDatas.sourceCodeModify(this.javaDatas);

                                    }
                                    else if(kind == ENTRY_DELETE){
                                        this.javaDatas.sourceCodeModify(this.javaDatas);
                                    }



                                } catch (FileNotFoundException ex) {
                                    Message.showMessage("FileNotFoundException: " + ex.getMessage());
                                } catch (ParseException ex) {
                                    Message.showMessage("Mas chybne zdrojove subory");
                                } catch (IOException ex) {
                                    Message.showMessage("IOExceprion: " + ex.getMessage());
                                }catch (InterruptedException ex) {
                                    //Message.showMessage("InterruptedException: " + ex.getMessage());
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                            else{

                                // if directory is created, and watching recursively, then
                                // register it and its sub-directories
                                if (kind == ENTRY_CREATE) {
                                    try {
                                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                            dw.registerAll(child);
                                            this.javaDatas.sourceCodeModify(this.javaDatas);
                                        }
                                    } catch (IOException x) {
                                        Message.showMessage("WatchService: " + x.getMessage());
                                    } catch (ParseException ex) {
                                        Message.showMessage("Mas chybne zdrojove subory");
                                    }catch (InterruptedException ex) {
                                        Message.showMessage("InterruptedException: " + ex.getMessage());
                                    }
                                }else if(kind == ENTRY_DELETE){
                                    if(!dw.isStartParse()){
                                        try {
                                            this.javaDatas.sourceCodeModify(this.javaDatas);
                                    } catch (IOException x) {
                                            Message.showMessage("WatchService: " + x.getMessage());
                                        } catch (ParseException ex) {
                                            Message.showMessage("Mas chybne zdrojove subory");
                                        }catch (InterruptedException ex) {
                                            Message.showMessage("InterruptedException: " + ex.getMessage());
                                        }
                                    }
                                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                        dw.getKeys().remove(kluc);
                                    }

                                }
                                else if(kind == ENTRY_MODIFY){

                                        try {
                                                this.javaDatas.sourceCodeModify(this.javaDatas);
                                        } catch (IOException x) {
                                            Message.showMessage("WatchService: " + x.getMessage());
                                        } catch (ParseException ex) {
                                            Message.showMessage("Mas chybne zdrojove subory");
                                        }catch (InterruptedException ex) {
                                            Message.showMessage("InterruptedException: " + ex.getMessage());
                                        }
                                    } 
                            }

                        }
                        else{
                            if(name.toString().endsWith(".java")){

                                if(kind == ENTRY_CREATE){

                                    this.javaDatas.addJavaFile(child.toString());

                                }
                                else if(kind == ENTRY_DELETE){

                                    if(this.javaDatas.findJavaFile(child.toString())){
                                        this.javaDatas.removeJavaFile(child.toString());
                                    }  
                                }
                            }

                            if (kind == ENTRY_CREATE) {

                                if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                    try {
                                        dw.registerAll(child);
                                    } catch (IOException ex) {
                                       Message.showMessage("IOExceprion: " + ex.getMessage());
                                    }
                                }

                            }else if(kind == ENTRY_DELETE){
                                if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                   dw.getKeys().remove(kluc);
                                }
                            }
                        }
                    }
                }
                
                if(pocet == 0 && !dw.isStartParse() ){
                    try {
                            this.javaDatas.sourceCodeModify(this.javaDatas);
                    } catch (IOException x) {
                        Message.showMessage("WatchService: " + x.getMessage());
                    } catch (ParseException ex) {
                        Message.showMessage("Mas chybne zdrojove subory");
                    }catch (InterruptedException ex) {
                        Message.showMessage("InterruptedException: " + ex.getMessage());
                    }
                }

                
                // reset key and remove from set if directory no longer accessible
                //if(!dw.isStartParse()){
                    arrayPaths.clear();
                    
                    synchronized(this){
                        boolean valid = kluc.reset();
                        if (!valid) {
                            dw.getKeys().remove(kluc);

                            // all directories are inaccessible
                            if (dw.getKeys().isEmpty()) {
                                //break;
                                dw.setCancel(true);
                            }
                        }
                    }
                    
                    try {
                        
                        checkFolders();
                        
                    } catch (IOException ex) {
                        Message.showMessage("IOException" + ex.getMessage());
                    }
                //}
                    
                Iterator<WatchKey> i = dw.getKeys().keySet().iterator();
                while (i.hasNext()) {
                   WatchKey k = i.next(); // must be called before you can call i.remove()
                   
                   if(!k.reset()){
                        i.remove();
                    }
                }
            }
        
        }
    }
    
    private void checkFolders() throws IOException{
          
        numbersAllPackages(dw.getMainPath(),1);
        for(String path: arrayPaths){
            boolean isRegisterPath = false;
            for(WatchKey k : dw.getKeys().keySet()){

                if(path.equals(dw.getKeys().get(k).toString())){
                    isRegisterPath = true;
                }
            }
            if(!isRegisterPath){
                dw.registerAll(Paths.get(path));
            }

        }

        
    }
    
    private int numbersAllPackages(String projectPath,int pocet){
        
        File dir = new File(projectPath);

        File[] fList = dir.listFiles();
        
        if(fList.length == 0)
            pocet++;
        
        for (File file : fList) {
            
            if (file.isDirectory()) {
                arrayPaths.add(file.getAbsolutePath());
                numbersAllPackages(file.getAbsolutePath().toString(),pocet++);
            }
            
        }
        return pocet;
    }
    
}

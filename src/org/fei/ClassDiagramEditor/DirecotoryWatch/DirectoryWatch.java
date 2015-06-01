/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.DirecotoryWatch;

import japa.parser.ParseException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.JavaParser.JavaReaderAction;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.openide.util.Exceptions;

/**
 *
 * @author mairo744
 * 
 * Trieda, ktora sleduje zmeny suborov v priecinkoch
 * 
 */
public class DirectoryWatch implements Runnable{
    
    private String mainPath;
    private final WatchService watcher;
    private volatile Map<WatchKey,Path> keys;
    private volatile  boolean  accessToKeys = false;
    private boolean trace = false;
    private JavaReaderAction javaDatas;
    private volatile boolean cancel = false;
    private volatile static boolean accessToRefactor = true;
    private ArrayList<String> javaFiles = new ArrayList<String>();
    private volatile WatchKey key = null;
    private static boolean startParse = false;
    
    public DirectoryWatch(String path,JavaReaderAction sources) throws IOException {

        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.mainPath = path;
        this.javaDatas = sources;
        
        registerAll(Paths.get(path));
        this.trace = true;      
    }
   
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                 
            } else {
                if (!dir.equals(prev)) {
                    
                }
            }
        }

        keys.put(key, dir);
    }
    

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    public void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    @Override
    public void run() {
        
        //Thread change = new Thread();
        RefactorDiagram rd = new RefactorDiagram(keys, javaDatas, this);
        Thread change = new Thread(rd);
        change.start();
        WatchKey k = null;
                    
        while (!isCancel()) {

            try {
                k = watcher.take();
            } catch (InterruptedException ex) {
                change.interrupt();
                continue;
            }
            synchronized (rd) {
                setKey(k);
                setStartParse(true);
                rd.notify();
            }
            
            
        }
        try {
            watcher.close();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public synchronized boolean isCancel() {
        return cancel;
    }

    public synchronized void setCancel(boolean cancel) {
        this.cancel = cancel;
    } 

    public synchronized static boolean isAccessToRefactor() {
        return accessToRefactor;
    }

    public synchronized static void setAccessToRefactor(boolean accessToRefactor) {
        DirectoryWatch.accessToRefactor = accessToRefactor;
    }

    public synchronized JavaReaderAction getJavaDatas() {
        return javaDatas;
    }



    public synchronized static boolean isStartParse() {
        return DirectoryWatch.startParse;
    }

    public synchronized static void setStartParse(boolean startParse) {
        DirectoryWatch.startParse = startParse;
    }

    public synchronized Map<WatchKey, Path> getKeys() {
        return keys;
    }
    
    public synchronized void setKey(WatchKey key) {
        
        while(accessToKeys){
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        setAccessToKeys(true);      
        this.key = key;
        setAccessToKeys(false);
        this.notify();
        //this.key = key;
    }

    public synchronized WatchKey getKey() {
        
        while(accessToKeys){
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        setAccessToKeys(true);      
        WatchKey temp = this.key;
        setAccessToKeys(false);
        this.notify();
        
        return temp;
        //return this.key;
    }

    
    
    public synchronized boolean isAccessToKeys() {
        return accessToKeys;
    }

    public synchronized void setAccessToKeys(boolean accessToKeys) {
        this.accessToKeys = accessToKeys;
    }
    
    public synchronized String getMainPath() {
        return mainPath;
    }
    
}

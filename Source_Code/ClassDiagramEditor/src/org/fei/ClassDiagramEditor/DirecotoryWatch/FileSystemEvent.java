/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.DirecotoryWatch;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

/**
 *
 * @author mairo744
 */
public class FileSystemEvent{
    
    private Path pathOfFile;
    private WatchEvent.Kind nameOfEvent;

    public FileSystemEvent(Path pathOfFile, WatchEvent.Kind nameOfEvent) {
        this.pathOfFile = pathOfFile;
        this.nameOfEvent = nameOfEvent;
    }

    public Path getPathOfFile() {
        return pathOfFile;
    }

    public WatchEvent.Kind getNameOfEvent() {
        return nameOfEvent;
    }
    
    
}

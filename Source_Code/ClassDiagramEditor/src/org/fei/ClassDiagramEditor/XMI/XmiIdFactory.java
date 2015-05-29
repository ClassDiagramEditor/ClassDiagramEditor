/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.XMI;

import java.util.UUID;

/**
 *
 * @author Tomas
 */
public class XmiIdFactory {
    
    private static class IdProvider {
        
        private String baseUUID;
        private int counter;
        
        public IdProvider() {
            baseUUID = UUID.randomUUID().toString();
            counter = 0;
        }
        
        public String generateId() {
            return baseUUID + (counter++);
        }
    }
    
    private static IdProvider idProvider = null;
    
    public static String getId() {
    
        if (XmiIdFactory.idProvider == null)
            XmiIdFactory.idProvider = new IdProvider();
        
        return XmiIdFactory.idProvider.generateId();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.XMI;

import java.util.HashMap;
import org.fei.ClassDiagramEditor.WindowComponents.Message;

/**
 *
 * @author Tomas
 */
public class XmiTypePlaceHolder {
    
    // <nazov, uuid>
    private static HashMap<String, String> types = new HashMap<String, String>();
    
    public static String getTypeId(String typeName) {
    
        if (getTypes().containsKey(typeName)) {
            return getTypes().get(typeName);
        }
        else {
            String newUuid = XmiIdFactory.getId();
            getTypes().put(typeName, newUuid);
            return newUuid;
        }
    }

    /**
     * @return the types
     */
    public static HashMap<String, String> getTypes() {
        return types;
    }

    public static void clearData() {
        types.clear();
    }
    
    
}

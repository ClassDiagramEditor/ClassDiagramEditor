/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.ViewData;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodData;

/**
 * Trieda upravy vzhlad vykreslovanej metody v triede.
 * 
 * @author Tomas
 */
public class MethodLook extends MethodViewData {

    private int stringWidth;
    private int stringHeight;
    
    public MethodLook(MethodData method, ClassData classData) {
        super(method, classData);
    }
    
    public void draw(Graphics2D g, int x, int y, boolean isInterface, int fontSize) {
        Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        
        if (met.isAbstract() && isInterface != true)
            f = new Font(Font.SANS_SERIF, Font.ITALIC, fontSize);
        
        if (met.isStaticMember()) {
            Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
            fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize).deriveFont(fontAttributes);
            
            if(met.isAbstract())
                f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize).deriveFont(fontAttributes);
        }
                
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        stringWidth = fm.stringWidth(this.toString());
        stringHeight = fm.getAscent();
        g.drawString(this.toString(), x, y);
    }
    
    public int getStringWidth() {
        return stringWidth;
    }

    public int getStringHeight() {
        return stringHeight;
    }
}

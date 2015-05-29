/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.ViewData;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.fei.ClassDiagramEditor.Data.VariableData;


/**
 * Trieda upravy vzhlad vykreslovaneho datoveho clenu v triede.
 * 
 * @author Tomas
 */
public class VariableLook extends VariableViewData {
    
    private int stringWidth;
    private int stringHeight;
    
    public VariableLook(VariableData variable) {
        super(variable);
    }
    
    public void draw(Graphics2D g, int x, int y, boolean isEnum, int fontSize) {
        Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        
        if (isEnum) {
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            stringWidth = fm.stringWidth(this.toString());
            if(stringWidth < fm.stringWidth(var.getName() + ": " + var.getEnumName()))
               stringWidth = fm.stringWidth(var.getName() + ": " + var.getEnumName()); 
            
            stringHeight = fm.getAscent();
            
            if(var.getType() == "enum")
                g.drawString(var.getName() + ": " + var.getEnumName(), x, y);
            else
                g.drawString(this.toString(), x, y);
                
            return;
        }
                
        if (var.isStaticMember()) {
            Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
            fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize).deriveFont(fontAttributes);
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

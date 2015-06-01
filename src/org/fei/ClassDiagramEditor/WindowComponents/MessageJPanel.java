/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.WindowComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Platno ktore vypise iba informacnu spravu
 * 
 * @author Tomas
 */
public class MessageJPanel extends JPanel {

    private String[] message;
    
    /*
     * @param message pole retazcov ktore predstavuje danu spravu. Jeden retazec sa rovna jednemu riadku
     */
    public MessageJPanel(String[] message) {
        this.message = message;
        this.setBackground(Color.white);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        // The rendering hints are used to make the drawing smooth.
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);       
        g2d.setRenderingHints(rh);
        g2d.setColor(Color.red);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        for (int i = 0; i < message.length; i++)
            g2d.drawString(message[i], 25, 40+(25*i));
        g.dispose();
    }
    
    
}

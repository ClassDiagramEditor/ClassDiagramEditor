/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.StitchingStructure;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import org.fei.ClassDiagramEditor.Router.SubLine;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.fei.ClassDiagramEditor.drawing.Point;

/**
 *
 * @author Mr.Phoenixxx
 */
public class StitchingStructure {
    private ArrayList<Class> classes;
    private ArrayList<SolidTile> solidTiles;
    private ArrayList<SpaceTile> spaceTiles;
    private ArrayList<StitchingLine> stitchingLines;  
    private int structureWidth;
    private int structureHeight;
    
    public StitchingStructure(ArrayList<Class> classes, Dimension structureDimension) {
        this.classes = classes;
        this.structureWidth = structureDimension.width;
        this.structureHeight = structureDimension.height;
        this.solidTiles = this.createSolidTiles(classes);
        this.stitchingLines = this.createStitchingLines(this.solidTiles);
        this.spaceTiles = this.createSpaceTiles(this.stitchingLines);
    }
    
    
    
    /* =============================================================================================== */
    /* ===================================== VYTVORENIE DLAŽDÍC ====================================== */ 
    private ArrayList<SolidTile> createSolidTiles(ArrayList<Class> classes) {
        ArrayList<SolidTile> newSolidTiles = new ArrayList<SolidTile>();
        
        // Vytvorí solidTiles podla tried
        for (Class c : classes) {
            newSolidTiles.add(c.getSolidTile());
        }   
        return newSolidTiles;
    }  
    
    /* ----------------------------------------------------------------------------------------------- */    
    private ArrayList<StitchingLine> createStitchingLines(ArrayList<SolidTile> solidTiles) {
        ArrayList<StitchingLine> newStitchingLines = new ArrayList<StitchingLine>();
        newStitchingLines.add(new StitchingLine(new Point(0, 0), new Point(structureWidth, 0)));
        newStitchingLines.add(new StitchingLine(new Point(0, structureHeight), new Point(structureWidth, structureHeight)));
        
        for (SolidTile solidTile : solidTiles) {
            // Získa koncové body čiar
            Point topLeft = getNearestSolidPoint(solidTile.getTopLeft(), new Point(0, solidTile.getTopLeft().getY()), solidTile, true);
            Point topRight = getNearestSolidPoint(solidTile.getTopRight(), new Point(structureWidth, solidTile.getTopRight().getY()), solidTile, true);
            Point bottomLeft = getNearestSolidPoint(solidTile.getBottomLeft(), new Point(0, solidTile.getBottomLeft().getY()), solidTile, true);
            Point bottomRight = getNearestSolidPoint(solidTile.getBottomRight(), new Point(structureWidth, solidTile.getBottomRight().getY()), solidTile, true);
            
            Point topLeft1 = getNearestSolidPoint(solidTile.getTopLeft(), new Point(0, solidTile.getTopLeft().getY()), solidTile, false);
            Point topRight1 = getNearestSolidPoint(solidTile.getTopRight(), new Point(structureWidth, solidTile.getTopRight().getY()), solidTile, false);
            Point bottomLeft1 = getNearestSolidPoint(solidTile.getBottomLeft(), new Point(0, solidTile.getBottomLeft().getY()), solidTile, false);
            Point bottomRight1 = getNearestSolidPoint(solidTile.getBottomRight(), new Point(structureWidth, solidTile.getBottomRight().getY()), solidTile, false);
            
            // Vytvorí čiary 
            newStitchingLines.add(new StitchingLine(topLeft1, topRight1));
            newStitchingLines.add(new StitchingLine(topLeft, topRight));
            newStitchingLines.add(new StitchingLine(topLeft, solidTile.getTopLeft()));
            newStitchingLines.add(new StitchingLine(solidTile.getTopRight(), topRight));
            
            newStitchingLines.add(new StitchingLine(bottomLeft1, bottomRight1));
            newStitchingLines.add(new StitchingLine(bottomLeft, bottomRight));
            newStitchingLines.add(new StitchingLine(bottomLeft, solidTile.getBottomLeft()));
            newStitchingLines.add(new StitchingLine(solidTile.getBottomRight(), bottomRight));
        }
        
        ArrayList<StitchingLine> newStitchingLines_noDuplicates = new ArrayList<StitchingLine>();
        for (StitchingLine line : newStitchingLines) {
            if (!newStitchingLines_noDuplicates.contains(line)) newStitchingLines_noDuplicates.add(line);
        }    
        
        Collections.sort(newStitchingLines_noDuplicates);
        return newStitchingLines_noDuplicates;
    }
    
    private Point getNearestSolidPoint(Point startOfLine, Point endOfLine, SolidTile analyzedTile, boolean withBorder) {
        SubLine tempLine = new SubLine(startOfLine, endOfLine, null, null);
        ArrayList<SolidTile> intersectSolidTiles = getIntersectSolidTiles(tempLine, withBorder);
        intersectSolidTiles.remove(analyzedTile);   // Odstráni aktuálne analyzovanú dlaždicu zo zoznamu
        
        for (SolidTile solidTile : intersectSolidTiles) {
            Point newEndOfLine = new Point(solidTile.getTopLeft().getX(), endOfLine.getY());
            if (Math.abs(startOfLine.getX()-newEndOfLine.getX()) < Math.abs(startOfLine.getX()-endOfLine.getX())) endOfLine = newEndOfLine;
            
            newEndOfLine = new Point(solidTile.getBottomRight().getX(), endOfLine.getY());
            if (Math.abs(startOfLine.getX()-newEndOfLine.getX()) < Math.abs(startOfLine.getX()-endOfLine.getX())) endOfLine = newEndOfLine;   
        }         
        return endOfLine;
    }
    
    /* ----------------------------------------------------------------------------------------------- */     
    private ArrayList<SpaceTile> createSpaceTiles(ArrayList<StitchingLine> stitchingLines) {
        stitchingLines = new ArrayList<StitchingLine>(stitchingLines);  // Vytvorí kópiu zoznamu
        ArrayList<SpaceTile> newSpaceTiles = new ArrayList<SpaceTile>();      
        boolean contains;
        
        for (int actualLine_Index = 0; actualLine_Index < stitchingLines.size(); actualLine_Index++) {
            StitchingLine actualLine = stitchingLines.get(actualLine_Index);
            
            for (int secondLine_Index = actualLine_Index+1; secondLine_Index < stitchingLines.size(); secondLine_Index++) {
                StitchingLine secondLine = stitchingLines.get(secondLine_Index);
                if ((actualLine.getStart().getX() == secondLine.getStart().getX())&&(actualLine.getEnd().getX() == secondLine.getEnd().getX())) {
                    SpaceTile s = new SpaceTile(actualLine.getStart(), secondLine.getEnd()); 
                    contains = false;
                    for(SolidTile solid : solidTiles) {
                        if (s.contains(solid.getInsideTile())) contains=true;
                    }  
                    if(!contains)newSpaceTiles.add(s);
                    break;
                }
            }
        }
        
        ArrayList<SpaceTile> newSpaceTiles_noDuplicates = new ArrayList<SpaceTile>();
        for (SpaceTile spaceTile : newSpaceTiles) {
            if (!newSpaceTiles_noDuplicates.contains(spaceTile)) newSpaceTiles_noDuplicates.add(spaceTile);
        }   
        
        return newSpaceTiles_noDuplicates;       
    }
    /* ===================================== VYTVORENIE DLAŽDÍC ====================================== */     
    /* =============================================================================================== */    
    
    
    
    
    /* =============================================================================================== */       
    public ArrayList<SolidTile> getIntersectSolidTiles(SubLine line, boolean withBorder) {
        ArrayList<SolidTile> intersectTiles = new ArrayList<SolidTile>();
        for(SolidTile solidTile:solidTiles) {
            if (withBorder) {
                if(solidTile.intersects(line)) intersectTiles.add(solidTile);
            }else {
                if(solidTile.getInsideTile().intersects(line)) intersectTiles.add(solidTile);
            }
        }
        return intersectTiles;
    }
 
    /* =============================================================================================== */   
    public void clearSpaceTilesDistances() {
        for (SpaceTile spaceTile : spaceTiles) {
            spaceTile.clearDistances();
        }
    }
        
    public ArrayList<StitchingLine> getStitchingLines() {
        return stitchingLines;
    }
    
    public ArrayList<SpaceTile> getSpaceTiles() {
        return spaceTiles;
    }    
    
    public ArrayList<SolidTile> getSolidTiles() {
        return solidTiles;
    }      
  
    public boolean containsIn(ArrayList<? extends Tile> tiles, Point point) {
        for (Tile tile : tiles) {
            if(tile.contains(point)) return true;
        }
        return false;
    }
    
    public boolean intersectIn(ArrayList<? extends Tile> tiles, Line line) {
        for (Tile tile : tiles) {
            if(tile.intersects(line)) return true;
        }
        return false;
    } 
}

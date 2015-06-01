/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import org.fei.ClassDiagramEditor.StitchingStructure.SolidTile;
import org.fei.ClassDiagramEditor.StitchingStructure.SpaceTile;
import org.fei.ClassDiagramEditor.StitchingStructure.StitchingStructure;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.fei.ClassDiagramEditor.drawing.Point;

/**
 *
 * @author Mr.Phoenixxx
 */
public class RouterService {    
    private StitchingStructure stitchingStruct;
    private ArrayList<Route> routes;

    public RouterService(StitchingStructure stitchingStruct) {
        this.stitchingStruct = stitchingStruct;
        
    }
    
    // Logika routovania    
    public Route createRoute(Line line) {
        ArrayList<SpaceTile> bestTiles = findShortestPath(line);
        Route route = new Route(line.getChild(), line.getParent());
        
        Point startPoint = line.getEnd();
        Point endPoint = line.getStart();
        
        route.addWayoint(startPoint);
        
        if(bestTiles == null) {   // Ak nenašlo cestu (Tiles)
            route.addWayoint(endPoint);
            route.makeOrthogonal();
            return route;            
        }
        
        bestTiles.remove(0);
        
        for(SpaceTile tile : bestTiles) {
            Point actualPoint = route.getLastWaypoint();
            Point newPoint = tile.findNewNearestTilePoint(actualPoint);
            route.addWayoint(newPoint);      
        }
        route.addWayoint(endPoint);
        
        route.makeOrthogonal();
        return route;
    }
         
    public ArrayList<SpaceTile> findShortestPath(Line line) {
        Point startPoint = line.getStart();
        Point endPoint = line.getEnd();     
        SpaceTile startTile = findSpaceTile(startPoint);
        SpaceTile endTile = findSpaceTile(endPoint);    
        
        if(startTile == null) return null;
        if(startPoint == null) return null;
        int fuse=0;
        
        //----------------
        try {
            stitchingStruct.clearSpaceTilesDistances();   
            startTile.setNewDistances(0, getManhattanDistance(startPoint, endPoint), startPoint);

            TreeSet<SpaceTile> omega = new TreeSet<SpaceTile>();
            omega.add(startTile);

            while(fuse<500) {
                if(omega.isEmpty()) break;
                SpaceTile actualTile = omega.first();
                omega.remove(actualTile);
                if(actualTile == endTile) break;    // Našlo koniec

                ArrayList<SpaceTile> neighboringTiles = (ArrayList<SpaceTile>) actualTile.findNeighboringTiles(stitchingStruct.getSpaceTiles());
                for (SpaceTile neighbor : neighboringTiles) {       

                    Point nearestPoint = neighbor.getNearestTilePoint();
                    if(nearestPoint == null) nearestPoint = neighbor.findNewNearestTilePoint(actualTile.getNearestTilePoint());

                    int sourceDistance = actualTile.getSourceDistance()+getManhattanDistance(actualTile.getNearestTilePoint(), nearestPoint);
                    int distance = sourceDistance + getManhattanDistance(nearestPoint, endPoint);

                    if(neighbor.isNewDistanceLower(distance)){
                        neighbor.setNewDistances(sourceDistance, distance, nearestPoint);
                        omega.add(neighbor);
                    }
                }      
                fuse++;
            }

            ArrayList<SpaceTile> ulozenisusedia = new ArrayList<SpaceTile>(); 
            SpaceTile actualTile = endTile;
            ArrayList<SpaceTile> tiles = new ArrayList<SpaceTile>();
            tiles.add(actualTile);
            ulozenisusedia.add(actualTile);
            fuse=0;
            while(fuse<500) {
                if(actualTile == startTile) break;
                ArrayList<SpaceTile> neighboringTiles = (ArrayList<SpaceTile>) actualTile.findNeighboringTiles(stitchingStruct.getSpaceTiles());
                Collections.sort(neighboringTiles);
                for (SpaceTile neighbor : neighboringTiles) {
                    if(ulozenisusedia.contains(neighbor)) continue;                  
                    ulozenisusedia.addAll(neighboringTiles);
                    actualTile = neighbor;  
                    tiles.add(actualTile);
                    break;
                }
                fuse++;
            }   
            return tiles;  
            
        } catch(Exception e) {
            return null;
        }      
    }
      
    private SpaceTile findSpaceTile(Point startPoint) {
        ArrayList<SpaceTile> spaceTiles = stitchingStruct.getSpaceTiles();
        for (SpaceTile spaceTile : spaceTiles) {
            if(spaceTile.contains(startPoint)) return spaceTile;
        }
        return null;
    }
      
    private int getManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.getX()-p2.getX()) + Math.abs(p1.getY()-p2.getY());
    }    
}

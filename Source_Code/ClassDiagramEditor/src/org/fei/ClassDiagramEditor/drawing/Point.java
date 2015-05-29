/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.drawing;

/**
 * Reprezentacia body v kartezianskej sustave.
 * @author Tomas
 */
public class Point {
    private int x;
    private int y;
    // premenna zavedena koli asociaciam, pocita kolko sa ich vztahuje k danej triede
    // na zaklade toho ich mozem vykreslovat pod sebou
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;

    }
    
    public Point(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }   
    
    public Point() {
        //this.x = 0;
        //this.y = 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public boolean isInVerticalLine(Point otherPoint) {
        return (this.x == otherPoint.getX());
    }
    
    @Override
    public boolean equals(Object object) {
        Point p = (Point)object;
        if((p.getX() == x)&&(p.getY() == y)) {
            return true;
        }else {
            return false;
        }
    }
}

// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.sierpinski;

import java.awt.Point;
import java.awt.Polygon;

/**
 * This class contains the three points that define a triangle.
 * @author Barry Becker
 */
public class Triangle {

    Point A, B, C;

    /**
     * Constructor.
     */
    public Triangle(Point A, Point B, Point C) {
        this.A = A;
        this.B = B;
        this.C = C;
    }

    /**
     * @return a triangular polygon
     */
    public Polygon getPoly(){
        Polygon triangle = new Polygon();
        triangle.addPoint(A.x, A.y);
        triangle.addPoint(B.x, B.y);
        triangle.addPoint(C.x, C.y);
        return triangle;
    }
}
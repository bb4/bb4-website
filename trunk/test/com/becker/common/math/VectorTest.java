// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.math;

import junit.framework.TestCase;


/**
 * @author Barry Becker
 */
public class VectorTest extends TestCase {

    /** instance under test */
    private Vector vector;

    /** must be at least one dim */
    public void testCreateLength0Vector() {

        try {
            new Vector(0);
            fail();
        } catch (AssertionError e) {
            // success
        }
    }

    public void testCreateLength1Vector() {

        vector = new Vector(1);
        vector.set(0, 1.23);

        assertEquals("Unexpected value.", 1.23, vector.get(0));
    }

    public void testCreateLength2Vector() {

        vector = new Vector(2);
        vector.set(0, 1.23);
        vector.set(1, 2.34);

        assertEquals("Unexpected value at 0.", 1.23, vector.get(0));
        assertEquals("Unexpected value at 1.", 2.34, vector.get(1));
    }

    public void testCreateLength2VectorWithData() {
        vector = new Vector(new double[] {1.23, 2.34});

        assertEquals("Unexpected value at 0.", 1.23, vector.get(0));
        assertEquals("Unexpected value at 1.", 2.34, vector.get(1));
    }


    public void testDistanceTo() {

        vector = new Vector(new double[] {1.0, 2.0});
        Vector vector2 = new Vector(new double[] {3.0, 4.0});

        assertEquals("Unexpected distance to itself.",
                0.0, vector.distanceTo(vector));
        assertEquals("Unexpected distance to vector2.",
                2.8284271247461903, vector.distanceTo(vector2));
    }

    public void testMagnitude() {

        vector = new Vector(new double[] {3.0, 4.0});
        assertEquals("Unexpected magnitude.",
                5.0, vector.magnitude());
    }

    public void testDotProduct() {

        vector = new Vector(new double[] {3.0, 4.0});
        Vector vector2 = new Vector(new double[] {5.0, 12.0});
        assertEquals("Unexpected dot product.",
                63.0, vector.dot(vector2));
    }

    public void testNormalizedDotProduct() {

        vector = new Vector(new double[] {3.0, 4.0});
        Vector vector2 = new Vector(new double[] {5.0, 12.0});
        assertEquals("Unexpected normalizedDot product.",
                0.9692307692307692, vector.normalizedDot(vector2));
    }

    public void testNormalizedDotProductWhenParallel() {

        vector = new Vector(new double[] {3.0, 4.0});
        Vector vector2 = new Vector(new double[] {3.0, 4.0});
        assertEquals("Unexpected normalizedDot product.",
                1.0, vector.normalizedDot(vector2));
    }

    public void testNormalizedDotProductWhenSmall() {

        vector = new Vector(new double[] {0.0000003, 0.0000004});
        Vector vector2 = new Vector(new double[] {0.0000005, 0.0000012});
        assertEquals("Unexpected normalizedDot product.",
                0.9692307692307692, vector.normalizedDot(vector2));
    }
}

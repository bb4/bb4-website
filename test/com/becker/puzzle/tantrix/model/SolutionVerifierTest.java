// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.common.math.MathUtil;
import junit.framework.TestCase;

import static com.becker.puzzle.tantrix.model.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class SolutionVerifierTest extends TestCase {

    /** instance under test */
    SolutionVerifier verifier;


    public void test3TilesIsNotSolved() {
        verifier = new SolutionVerifier(place3UnsolvedTiles());
        assertFalse("Unexpectedly solved", verifier.isSolved());
    }

    public void test3TilesIsSolved() {
        verifier = new SolutionVerifier(place3SolvedTiles());
        assertTrue("Unexpectedly not solved", verifier.isSolved());
    }


    public void test4TilesIsNotSolved() {
        verifier = new SolutionVerifier(place4UnsolvedTiles());
        assertFalse("Unexpectedly solved", verifier.isSolved());
    }

    public void test4TilesIsSolved() {
        verifier = new SolutionVerifier(place4SolvedTiles());
        assertTrue("Unexpectedly not solved", verifier.isSolved());
    }

}
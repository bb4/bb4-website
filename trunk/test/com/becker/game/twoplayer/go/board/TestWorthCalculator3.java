/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.options.GoWeights;
import junit.framework.Assert;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public class TestWorthCalculator3 extends WorthCalculatorBase {

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    public void testSamePositionFromDifferentPathsEqual() {

        compareWorths("worth3x3_A", "worth3x3_B", -163);
    }

}

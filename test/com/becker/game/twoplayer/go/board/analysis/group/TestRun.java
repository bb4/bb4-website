package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import junit.framework.Assert;

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 *
 * @author Barry Becker
 */
public class TestRun extends GoTestCase {

    private Run run;

    public void testRunZeroLength() {

        run = new Run(5, 6, 10, false);
        verifyPotential(0.05f);
    }

    public void testRunOneLength() {

        run = new Run(5, 7, 10, false);
        verifyPotential(0.15f);
    }

    public void testRunOneLengthBounded() {

        run = new Run(5, 7, 10, true);
        verifyPotential(0.35f);
    }

    public void testRunFourLength() {

        run = new Run(5, 9, 10, false);
        verifyPotential(0.25f);
    }

    public void testRunFourLengthBounded() {

        run = new Run(5, 9, 10, true);
        verifyPotential(0.3f);
    }


    public void testRunOneOnEndInternal() {

        run = new Run(10, 11, 10, false);
        verifyPotential(0.25f);
    }

    public void testRunOneAtStartInternal() {

        run = new Run(1, 2, 10, false);
        verifyPotential(0.25f);
    }

    private void verifyPotential(float expectedPotential)  {

        assertEquals("Unexpected run potential for " + run , expectedPotential, run.getPotential());
    }

}
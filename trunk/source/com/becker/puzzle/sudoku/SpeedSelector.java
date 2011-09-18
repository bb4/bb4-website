package com.becker.puzzle.sudoku;

import java.awt.*;

/**
 * A combo box that allows the user to select the speed at which the puzzle is generated or solved.
 *
 * @author Barry becker
 */
public final class SpeedSelector extends Choice {

    private String[] speedChoices_ = {
        "As fast as possible",
        "Medium speed",
        "Slow speed",
        "Extremely slow"
    };

    /**
     * Constructor.
     */
    public SpeedSelector() {
        for (final String item : speedChoices_) {
            add(item);
        }
        select(0);
    }

    /**
     * @return  the delay for selected speeed.
     */
    public int getSelectedDelay() {
        switch (this.getSelectedIndex())  {
            case 0 : return 0;
            case 1 : return 10;
            case 2 : return 50;
            case 3 : return 400;
            default: assert false : " undexpected index";
        }
        return 0;
    }
}
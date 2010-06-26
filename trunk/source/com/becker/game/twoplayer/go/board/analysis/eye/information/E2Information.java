package com.becker.game.twoplayer.go.board.analysis.eye.information;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.SINGLE_EYE;

/**
 * Two space eye - **
 *
 * @author Barry Becker
 */
public class E2Information extends AbstractEyeSubtypeInformation
{
    public E2Information() {
        initialize(false, 2, 1, SINGLE_EYE);
    }

    public String getTypeName() {
       return "E11";
    }
}
package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.SINGLE_EYE;

/**
 * Two space eye - **
 *
 * @author Barry Becker
 */
public class E2Subtype extends AbstractEyeSubtype
{
    public E2Subtype() {
        initialize(false, 2, 1, SINGLE_EYE);
    }

    public String getTypeName() {
       return "E11";
    }
}
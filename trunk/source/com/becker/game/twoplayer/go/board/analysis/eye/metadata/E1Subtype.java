package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Single space eye  - *
 *
 * @author Barry Becker
 */
public class E1Subtype extends AbstractEyeSubtype
{
    public E1Subtype() {
        initialize(false, 1, 1, SINGLE_EYE);
    }

    public String getTypeName() {
       return "E1";
    }

}
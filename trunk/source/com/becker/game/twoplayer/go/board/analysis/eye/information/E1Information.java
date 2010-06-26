package com.becker.game.twoplayer.go.board.analysis.eye.information;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Single space eye  - *
 *
 * @author Barry Becker
 */
public class E1Information extends AbstractEyeSubtypeInformation
{
    public E1Information() {
        initialize(false, 1, 1, SINGLE_EYE);
    }

    public String getTypeName() {
       return "E1";
    }

}
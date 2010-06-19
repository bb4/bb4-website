package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.metadata.EyeInformation;

/**
 * Determine the status of an eye on the board.
 * Must be applied only after determining the type of the eye.
 * Put into subpackage and make package protecteed.
 * Primary entry point for this package.
 *
 * @author Barry Becker
 */
public class EyeAnalyzer {

    /** The kind of eye that this is. */
    private final EyeInformation info_;

    /** In addition to the type, an eye can have a status like nakade, unsettled, or aliveInAtari. */
    private final EyeStatus status_;


    /**
     * Constructor
     * Initializes the tyep and status in the correct order.
     */
    public EyeAnalyzer(GoEye eye, GoBoard board) {

        EyeTypeAnalyzer eyeTypeAnalyzer = new EyeTypeAnalyzer(eye, board);
        info_ = eyeTypeAnalyzer.determineEyeInformation();

        EyeStatusAnalyzer eyeStatusAnalyzer = new EyeStatusAnalyzer(eye, board);
        status_ = eyeStatusAnalyzer.determineEyeStatus();
    }

    public EyeInformation getEyeInformation() {
        return info_;
    }

    public EyeStatus getEyeStatus() {
        return status_;
    }
}
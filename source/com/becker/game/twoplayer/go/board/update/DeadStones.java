package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoOptions;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.GoWeights;
import com.becker.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.PositionalScoreAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.StringShapeAnalyzer;
import com.becker.game.twoplayer.go.persistence.GoGameExporter;
import com.becker.game.twoplayer.go.persistence.GoGameImporter;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.becker.game.twoplayer.go.GoControllerConstants.*;

/**
 * Keeps track of the number of dead stones of each color that are on the board.
 * At the very end of the game we visibly mark dead stones dead.
 *
 * @author Barry Becker
 */
public final class DeadStones
{
    private int numDeadBlackStonesOnBoard_ = 0;
    private int numDeadWhiteStonesOnBoard_ = 0;


    /**
     * Constructor.
     */
    public DeadStones()
    {
    }

    public void clear() {
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
    }

    /**
     *
     * @param player1 black player if true
     * @return the number of dead stones on the board for the specified player
     */
    public int getNumberOnBoard(boolean player1) {
        return player1 ?  numDeadBlackStonesOnBoard_ : numDeadWhiteStonesOnBoard_;
    }

    /**
     * Add to the dead stone count for the specified player
     * @param player1 player to add a dead stone for.
     */
    public void increment(boolean player1) {
        if (player1)
            numDeadBlackStonesOnBoard_++;
        else
            numDeadWhiteStonesOnBoard_++;
    }

}
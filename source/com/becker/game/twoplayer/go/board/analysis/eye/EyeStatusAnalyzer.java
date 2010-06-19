package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Determine the status of an eye on the board.
 * Must be applied only after determining the type of the eye.
 *
 * @@ we could remove this class and move its code into AbstractEyeSubtype   (handleSubtypeWithLifeProperty)
 *
 * @author Barry Becker
 */
public class EyeStatusAnalyzer {

    private GoEye eye_;
    private GoBoard board_;

    /**
     * Constructor
     */
    public EyeStatusAnalyzer(GoEye eye, GoBoard board) {
        eye_ = eye;
        board_ = board;
    }

    /**
     * @return eye status
     */
    public EyeStatus determineEyeStatus()
    {
        if (eye_.size() < 3) {
            return EyeStatus.NAKADE;
        }
        if (eye_.getInformation().hasLifeProperty())  {
            return getAliveStatus();
        }
        return determineNonLifePropertyStatus();
    }

    /**
     * When the eye type has the alive property, we can only be alive or alive in atari.
     * @return either alive or alive in atari (rare)
     */
    private EyeStatus getAliveStatus() {
        List<GoBoardPosition> filledSpaces = findFilledSpaces();
        if (eye_.size() - filledSpaces.size() == 1 && eye_.getGroup().getLiberties(board_).size() == 1) {
            return EyeStatus.ALIVE_IN_ATARI;
        }
        return EyeStatus.ALIVE;
    }

    private EyeStatus determineNonLifePropertyStatus() {

        EyeNeighborMap nbrMap = new EyeNeighborMap(eye_);
        return eye_.getInformation().determineStatus(eye_, nbrMap);
    }

    /**
     * I suppose, in very rare cases, there could be a same side stone among the enemy filled spaces in the eye.
     * @return the eye spaces that have enemy stones in them.
     */
    private List<GoBoardPosition> findFilledSpaces() {
        List<GoBoardPosition> filledSpaces = new ArrayList<GoBoardPosition>(6);
        for (GoBoardPosition space : eye_.getMembers()) {
            if (space.isOccupied()) {
                assert eye_.isOwnedByPlayer1() != space.getPiece().isOwnedByPlayer1();
                filledSpaces.add(space);
            }
        }
        return filledSpaces;
    }
}
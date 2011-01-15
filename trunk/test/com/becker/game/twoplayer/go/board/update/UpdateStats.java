package com.becker.game.twoplayer.go.board.update;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoStone;


/**
 * Things to verify after moving or removing stones from the board
 * @author Barry Becker
 */
public class UpdateStats {


    public int expCaptures;
    public int expStringLiberties;
    public int expNumStonesInString;
    public int expNumStringsInGroup;
    public int expNumEyesInGroup;
    public int expGroupsOnBoard;
    public boolean expValid;

    public UpdateStats(int expCaptures,
                     int expStringLiberties, int expNumStonesInString,
                     int expNumStringsInGroup, int expNumEyesInGroup,
                     int expGroupsOnBoard, boolean expValid) {

        this.expCaptures = expCaptures;
        this.expStringLiberties = expStringLiberties;
        this.expNumStonesInString = expNumStonesInString;
        this.expNumStringsInGroup = expNumStringsInGroup;
        this.expNumEyesInGroup = expNumEyesInGroup;
        this.expGroupsOnBoard = expGroupsOnBoard;
        this.expValid = expValid;

    }

}
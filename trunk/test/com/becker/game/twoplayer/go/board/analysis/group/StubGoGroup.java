package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.IGoGroup;
import com.becker.game.twoplayer.go.board.IGoString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Barry Becker
 */
public class StubGoGroup implements IGoGroup {

    private float absHealth;
    private boolean isOwnedByPlayer1;
    private int numStones;

    public StubGoGroup(float absHealth,  boolean isOwnedByPlayer1, int numStones) {
        this.absHealth= absHealth;
        this.isOwnedByPlayer1 = isOwnedByPlayer1;
        this.numStones = numStones;

    }

    public float getAbsoluteHealth() {
        return absHealth;
    }

    public boolean isOwnedByPlayer1() {
        return isOwnedByPlayer1;
    }

    public int getNumStones() {
        return numStones;
    }
}
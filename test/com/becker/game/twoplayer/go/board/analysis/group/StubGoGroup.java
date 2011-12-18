/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.common.geometry.Box;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.group.GroupChangeListener;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.elements.string.GoStringSet;
import com.becker.game.twoplayer.go.board.elements.string.IGoString;

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

    public void addChangeListener(GroupChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    public void removeChangeListener(GroupChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    public void addMember(IGoString string) {
        throw new UnsupportedOperationException();
    }

    public GoStringSet getMembers() {
        throw new UnsupportedOperationException();
    }

    public boolean isEnemy(GoBoardPosition pos) {
        throw new UnsupportedOperationException();
    }

    public void setVisited(boolean visited) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public GoBoardPositionSet getLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public int getNumLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public int getNumStones() {
        return numStones;
    }

    public GoEyeSet getEyes(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public float getRelativeHealth(GoBoard board, boolean useCachedValue) {
        throw new UnsupportedOperationException();
    }

    public boolean containsStone(GoBoardPosition stone) {
        throw new UnsupportedOperationException();
    }

    public void remove(IGoString string) {
        throw new UnsupportedOperationException();
    }

    public GoBoardPositionSet getStones() {
        throw new UnsupportedOperationException();
    }

    public float calculateAbsoluteHealth(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public float calculateRelativeHealth(GoBoard board) {
       throw new UnsupportedOperationException();
    }

    public void updateTerritory(float health) {
        throw new UnsupportedOperationException();
    }

    public Box findBoundingBox() {
        throw new UnsupportedOperationException();
    }

    public boolean isStoneMuchWeaker(GoStone stone) {
        throw new UnsupportedOperationException();
    }

    public String toHtml() {
        throw new UnsupportedOperationException();
    }
}
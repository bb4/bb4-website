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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        throw new NotImplementedException();
    }

    public void removeChangeListener(GroupChangeListener listener) {
        throw new NotImplementedException();
    }

    public void addMember(IGoString string) {
        throw new NotImplementedException();
    }

    public GoStringSet getMembers() {
        throw new NotImplementedException();
    }

    public boolean isEnemy(GoBoardPosition pos) {
        throw new NotImplementedException();
    }

    public void setVisited(boolean visited) {
        throw new NotImplementedException();
    }

    public int size() {
        throw new NotImplementedException();
    }

    public GoBoardPositionSet getLiberties(GoBoard board) {
        throw new NotImplementedException();
    }

    public int getNumLiberties(GoBoard board) {
        throw new NotImplementedException();
    }

    public int getNumStones() {
        return numStones;
    }

    public GoEyeSet getEyes(GoBoard board) {
        throw new NotImplementedException();
    }

    public float getRelativeHealth(GoBoard board, boolean useCachedValue) {
        throw new NotImplementedException();
    }

    public boolean containsStone(GoBoardPosition stone) {
        throw new NotImplementedException();
    }

    public void remove(IGoString string) {
        throw new NotImplementedException();
    }

    public GoBoardPositionSet getStones() {
        throw new NotImplementedException();
    }

    public float calculateAbsoluteHealth(GoBoard board) {
        throw new NotImplementedException();
    }

    public float calculateRelativeHealth(GoBoard board) {
       throw new NotImplementedException();
    }

    public void updateTerritory(float health) {
        throw new NotImplementedException();
    }

    public Box findBoundingBox() {
        throw new NotImplementedException();
    }

    public boolean isStoneMuchWeaker(GoStone stone) {
        throw new NotImplementedException();
    }

    public String toHtml() {
        throw new NotImplementedException();
    }
}
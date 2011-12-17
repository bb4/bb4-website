/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.GoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.List;

/**
 * @author Barry Becker
 */
public class StubGoEye implements IGoEye {

    private GoBoardPositionSet members;
    boolean visited = false;

    public StubGoEye(List<GoBoardPosition> list) {

        this.members = new GoBoardPositionSet();
        this.members.addAll(list);
    }

    public StubGoEye(GoBoardPositionSet members) {
        this.members = members;
    }

    public GoBoardPositionSet getMembers() {
        return members;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }


    public boolean isEnemy(GoBoardPosition pos) {
        throw new UnsupportedOperationException();
    }

    public boolean isOwnedByPlayer1() {
        return false;
    }

    public void setVisited(boolean visited) {
        visited = false;
    }

    public EyeInformation getInformation() {
        throw new UnsupportedOperationException();
    }

    public EyeStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    public String getEyeTypeName() {
        throw new UnsupportedOperationException();
    }

    public int getNumCornerPoints() {
        throw new UnsupportedOperationException();
    }

    public int getNumEdgePoints() {
        throw new UnsupportedOperationException();
    }

    public GoGroup getGroup() {
        return null;
    }

    public boolean isUnconditionallyAlive() {
        throw new UnsupportedOperationException();
    }

    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
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
}
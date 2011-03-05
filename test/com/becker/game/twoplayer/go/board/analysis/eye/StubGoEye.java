package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.GoGroup;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.List;

/**
 * @author Barry Becker
 */
class StubGoEye implements IGoEye {

    private GoBoardPositionSet members;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isEnemy(GoBoardPosition pos) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isOwnedByPlayer1() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EyeInformation getInformation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EyeStatus getStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getEyeTypeName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumCornerPoints() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumEdgePoints() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GoGroup getGroup() {
        return null;
    }

    public boolean contains(GoBoardPosition pos) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isUnconditionallyAlive() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void remove(GoBoardPosition stone, GoBoard board) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateTerritory(float health) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setGroup(IGoGroup grou) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setVisited(boolean visted) {
        for (GoBoardPosition p : members) {
            p.setVisited(visted);
        }
    }

    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GoBoardPositionSet getLiberties(GoBoard board) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumLiberties(GoBoard board) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
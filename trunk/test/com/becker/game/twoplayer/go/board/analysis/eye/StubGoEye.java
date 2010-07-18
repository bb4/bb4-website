package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.GoGroup;
import com.becker.game.twoplayer.go.board.elements.IGoString;

import java.util.List;

/**
 * @author Barry Becker
 */
class StubGoEye implements IGoString {

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

    public GoGroup getGroup() {
        return null;
    }

    public void unvisit() {
        for (GoBoardPosition p : members) {
            p.setVisited(false);
        }
    }
}
package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.IGoString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Barry Becker
 */
class StubGoEye implements IGoString {

    private  Set<GoBoardPosition> members;

    public StubGoEye(List<GoBoardPosition> list) {

        this.members = new HashSet<GoBoardPosition>();
        this.members.addAll(list);
    }

    public StubGoEye(Set<GoBoardPosition> members) {
        this.members = members;
    }


    public Set<GoBoardPosition> getMembers() {
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
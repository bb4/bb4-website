/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeList;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.becker.game.twoplayer.go.board.elements.string.GoStringSet;
import com.becker.game.twoplayer.go.board.elements.string.IGoString;
import com.sun.org.apache.xpath.internal.operations.Variable;

import java.util.*;

/**
 * Always returns a fixed value depending on boolean passed to constructor.
 *
 * @author Barry Becker
 */
public final class StubLifeAnalyzer extends LifeAnalyzer {

    boolean isUnconditionallyAlive;

    /**
     * Constructor.
     * @param isUnconditionallyAlive always returns this value.
     */
    public StubLifeAnalyzer(boolean isUnconditionallyAlive) {
        this.isUnconditionallyAlive = isUnconditionallyAlive;
    }

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *
     * @return true if unconditionally alive
     */
    @Override
    public boolean isUnconditionallyAlive() {
        return isUnconditionallyAlive;
    }

}
// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group.eye;

import com.becker.common.geometry.Box;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.becker.game.twoplayer.go.board.elements.eye.EyeSerializer;
import com.becker.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.becker.game.twoplayer.go.board.elements.eye.IGoEye;
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
class StubGoEye implements IGoEye {

    private boolean ownedByPlayer1;
    private EyeStatus status;
    private EyeInformation eyeInfo;
    private int numCornerPoints;
    private int numEdgePoints;
    private boolean isUncondAlive;
    private int numMembers;

    public StubGoEye(boolean ownedByPlayer1, EyeStatus status, EyeInformation eyeInfo,
                     int numCornerPoints, int numEdgePoints, 
                     boolean isUncondAlive, int numMembers) {
        
        this.ownedByPlayer1 = ownedByPlayer1;               
        this.status = status;
        this.eyeInfo = eyeInfo;
        this.numCornerPoints = numCornerPoints;
        this.numEdgePoints = numEdgePoints;
        this.isUncondAlive = isUncondAlive;
        this.numMembers = numMembers;       
    }

    public EyeStatus getStatus() {
        return status;
    }

    public EyeInformation getInformation() {
        return eyeInfo;
    }

    public String getEyeTypeName() {
        return eyeInfo.getTypeName();
    }

    public int getNumCornerPoints() {
        return numCornerPoints;
    }

    public int getNumEdgePoints() {
        return numEdgePoints;
    }

    public IGoGroup getGroup() {
        return null;
    }

    public GoBoardPositionSet getMembers() {
        return null;
    }

    public boolean isEnemy(GoBoardPosition pos) {
        throw new IllegalStateException("not supported");
    }

    public boolean isOwnedByPlayer1() {
        return ownedByPlayer1;
    }

    public void setVisited(boolean visited) {}

    public int size() {
        return numMembers;
    }

    public GoBoardPositionSet getLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public int getNumLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean isUnconditionallyAlive() {
        return isUncondAlive;
    }

    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param eye eye to test for rough equality with.
     * @return true if all the fields match the specified eye
     */
    public boolean isMatch(IGoEye eye) {

        boolean infoEqual = getInformation().equals(eye.getInformation());
        boolean pointsEqual = getNumCornerPoints() == eye.getNumCornerPoints()
                        && getNumEdgePoints() == eye.getNumEdgePoints();
        boolean match =
                   isOwnedByPlayer1() == eye.isOwnedByPlayer1()
                && isUnconditionallyAlive() == eye.isUnconditionallyAlive()
                && getStatus() == eye.getStatus()
                && infoEqual
                && pointsEqual
                && size() == eye.size();
        return match;
    }
    
    public String toString() {
        return new EyeSerializer(this).serialize();
    }

}
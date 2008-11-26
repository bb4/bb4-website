package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.common.*;

import java.util.*;

/**
 * static methods for determing properties of groups
 * Includes Benson's algoritm for unconditional life.
 *
 * @author Barry Becker Date: Aug 28, 2005
 */
public final class LifeAnalyzer {

    private GoGroup group_;
    private GoBoard board_;
    
    public LifeAnalyzer(GoGroup group, GoBoard board) {
        group_ = group;
        board_ = board;
    }

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @return true if unconditionally alive
     */
    public boolean isUnconditionallyAlive() {

        // mark all the strings in the group as not UA
        Set<GoString> candidateStrings = new HashSet<GoString>();
        for (GoString str : group_.getMembers()) {
            str.setUnconditionallyAlive(true);
            candidateStrings.add(str);
        }
       
        findNeighborStringSets();

        // now create the neighbor eye sets for each qualified string
        for (GoEye eye : group_.getEyes(board_)) {          
            if (eye.getNeighbors() != null) {
                for (GoString str : eye.getNeighbors()) {                  
                    if (str.getNeighbors() == null) {
                        str.setNbrs(new HashSet<GoString>());
                    }
                    // only add the eye if every unoccupied position in the eye is adjacent to the str
                    if  (eye.allUnocupiedAdjacentToString(str, board_)) {
                        str.getNeighbors().add(eye);
                    }
                }
            }
        }

        boolean done;
        do {
            done = true;
            for (GoEye eye : group_.getEyes(board_)) {
                eye.setUnconditionallyAlive(true);
                if (eye.getNeighbors() != null) {
                    for (GoString nbrStr : eye.getNeighbors()) {                     
                        if (!nbrStr.isUnconditionallyAlive()) {
                            eye.setUnconditionallyAlive(false);
                        }
                    }
                }
            }
            Iterator<GoString> it = candidateStrings.iterator();
            while (it.hasNext()) {
                GoString str = it.next();
                // find the number of ua eyes adjacent
                int numUAEyesAdjacent = 0;
                if (str.getNeighbors() != null) {
                    for (GoString eye : str.getNeighbors()) {                      
                        if (eye.isUnconditionallyAlive()) {
                            numUAEyesAdjacent++;
                        }
                    }
                }
                if (numUAEyesAdjacent < 2) {
                    str.setUnconditionallyAlive(false);
                    it.remove();
                    done = false; // something changed
                }
            }

        }  while ( !(done || candidateStrings.isEmpty()));

        // clear str nbrs
        for (GoString str : group_.getMembers()) {  
            str.setNbrs(null);
        }

       return  !candidateStrings.isEmpty();
    }


    /**
     * first find the neighbor string sets for each true eye in the group.
     */
    private void findNeighborStringSets() {
        
        for (GoEye eye : group_.getEyes(board_)) {         
            if (eye.getNeighbors() == null) {
                eye.setNbrs(new HashSet<GoString>());
            }
            for (GoBoardPosition pos : eye.getMembers()) {
                if (pos.isUnoccupied()) {
                    Set<GoBoardPosition> nbrs = board_.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
                    for (GoBoardPosition nbr : nbrs) {
                    
                        if (nbr.getString().getGroup() != group_) {
                            // this eye is not UA.
                            eye.setNbrs(null);
                            break;
                        }
                        else {
                            if (eye.getNeighbors() != null ) {
                                eye.getNeighbors().add(nbr.getString());
                                //candidateUAStrings.add(nbr.getString());
                            }
                        }
                    }
                }
            }
            GameContext.log(2, "num string nbrs of eyes = "
                    + ((eye.getNeighbors() == null)? 0 : eye.getNeighbors().size()));
        }
    }
}

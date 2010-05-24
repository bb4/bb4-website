package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.*;

import java.util.*;

/**
 * Determine if group is unconditionally alive using
 * Benson's algorithm for unconditional life.
 * see http://senseis.xmp.net/?BensonSAlgorithm
 *
 * @author Barry Becker
 */
public final class LifeAnalyzer {

    private GoGroup group_;
    private GoBoard board_;

    /** Keep track of living strings neighboring eyes. */
    private Map<GoEye, List<GoString>> eyeStringNbrMap;
    
    /** Keep track of vital eyes neighboring living string. */
    private Map<GoString, List<GoEye>> stringEyeNbrMap;


    /**
     * Constructor.
     * @param group the group to analyze for unconditional life.
     * @param board board on which the group exists.
     */
    public LifeAnalyzer(GoGroup group, GoBoard board) {
        group_ = group;
        board_ = board;
    }

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *
     * @return true if unconditionally alive
     */
    public boolean isUnconditionallyAlive() {
        initMaps();

        Set<GoEye> eyes = group_.getEyes(board_);
        findNeighborStringSetsForEyes(eyes);
        createVitalEyeSets(eyes);

        return determineUnconditionalLife();
    }

    private void initMaps() {
        eyeStringNbrMap = new HashMap<GoEye, List<GoString>>();
        stringEyeNbrMap = new HashMap<GoString, List<GoEye>>();
    }

    /**
     * first find the neighbor string sets for each true eye in the group.
     */
    private void findNeighborStringSetsForEyes(Set<GoEye> eyes) {

        for (GoEye eye : eyes) {
            List<GoString> stringNbrs = findNeighborStringsForEye(eye);
            eyeStringNbrMap.put(eye, stringNbrs);
        }
    }

    /**
     * Find the neighbor string sets for a specific eye in the group.
     * @param eye eye to find neighboring strings of.
     * @return living neighbor strings. May be empty, but never null.
     */
    private List<GoString> findNeighborStringsForEye(GoEye eye) {
        List<GoString> nbrStrings = new LinkedList<GoString>();
        for (GoBoardPosition pos : eye.getMembers()) {
            if (pos.isUnoccupied()) {
                findNeighborStringsForEyeSpace(eye, pos, nbrStrings);
            }
        }
        return nbrStrings;
    }

    /**
     * Find the neighbor string sets for a specific empty point within an eye.
     * @param eye
     * @param pos empty position within eye.
     * @param nbrStrings the list to add neighboring still living strings to.
     */
    private void findNeighborStringsForEyeSpace(GoEye eye, GoBoardPosition pos, List<GoString> nbrStrings) {
        Set<GoBoardPosition> nbrs =
                board_.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
        for (GoBoardPosition nbr : nbrs) {

            if (nbr.getString().getGroup() != group_) {
                // this eye is not unconditionally alive (UA).
                nbrStrings.clear();
                return;
            }
            else {
                if (!nbrStrings.contains(nbr.getString())) {
                    // assume its alive at first.
                    nbr.getString().setUnconditionallyAlive(true);
                    nbrStrings.add(nbr.getString());
                }
            }
        }
    }

    /**
     * Create the neighbor eye sets for each qualified string.
     */
    private void createVitalEyeSets(Set<GoEye> eyes) {
        for (GoEye eye : eyes) {
            updateVitalEyesForStringNeighbors(eye);
        }
        GameContext.log(3, "num strings with vital eye nbrs = " + stringEyeNbrMap.size());
    }

    /**
     *
     * @param eye
     */
    private void updateVitalEyesForStringNeighbors(GoEye eye) {

        EyeAnalyzer analyzer = new EyeAnalyzer(eye);
        for (GoString str : eyeStringNbrMap.get(eye)) {
            // only add the eye if every unoccupied position in the eye is adjacent to the string
            List<GoEye> vitalEyes;
            if (stringEyeNbrMap.containsKey(str)) {
                vitalEyes = stringEyeNbrMap.get(str);
            }
            else {
                vitalEyes = new LinkedList<GoEye>();
                stringEyeNbrMap.put(str, vitalEyes);
            }

            if (analyzer.allUnocupiedAdjacentToString(str, board_)) {
                eye.setUnconditionallyAlive(true);
                vitalEyes.add(eye);
            }
        }
    }

    /**
     * @return true if any of the candidateStrings are unconditionally alive (i.e. pass alive).
     */
    private boolean determineUnconditionalLife() {

        Set<GoString> livingStrings = findPassAliveStrings();
        return !livingStrings.isEmpty();
    }

    /**
     * @return the set of strings in the group that are unconditionally alive.
     */
    private Set<GoString> findPassAliveStrings() {

        Set<GoString> candidateStrings = initializeCandidateStrings();
        boolean done;
        int ct = 0;
        do {
            GameContext.log(2, "\nIteration  = " + (ct++) + " num cand strings="+ candidateStrings.size());
            initializeEyeLife();
            Iterator<GoString> it = candidateStrings.iterator();

            done = true;
            while (it.hasNext()) {

                GoString str = it.next();
                int numLivingAdjacentEyes = findNumLivingAdjacentEyes(str);
                if (numLivingAdjacentEyes < 2) {
                    str.setUnconditionallyAlive(false);
                    it.remove();
                    done = false; // something changed
                }
            }

        }  while ( !(done || candidateStrings.isEmpty()));
        GameContext.log(2, "Done Iterating. num cand strings="+ candidateStrings.size());
        return candidateStrings;
    }

    /**
     * For each eye in the group determine if it is unconditionally alive by verifying that
     * all its neighbors are unconditional life candidates still.
     */
    private void initializeEyeLife() {
        for (GoEye eye : group_.getEyes(board_)) {
            eye.setUnconditionallyAlive(true);
            for (GoString nbrStr : eyeStringNbrMap.get(eye)) {
                if (!(nbrStr.isUnconditionallyAlive())) {
                    eye.setUnconditionallyAlive(false);
                }
            }
        }
    }

    /**
     * @return the number of unconditionally alive adjacent eyes.
     */
    private int findNumLivingAdjacentEyes(GoString str) {
        int numLivingAdjacentEyes = 0;

        List<GoEye> vitalEyeNbrs = stringEyeNbrMap.get(str);
        if (vitalEyeNbrs != null)  {
            for (GoString eye : vitalEyeNbrs) {
                if (eye.isUnconditionallyAlive()) {
                    numLivingAdjacentEyes++;
                }
            }
        }
        return numLivingAdjacentEyes;
    }

    /**
     * mark all the strings in the group as not UA.
     * @return set of candidate strings initially marked not UA
     */
    private Set<GoString> initializeCandidateStrings() {
        Set<GoString> candidateStrings = new HashSet<GoString>();
        for (GoString str : group_.getMembers()) {
            candidateStrings.add(str);
        }
        return candidateStrings;
    }
}
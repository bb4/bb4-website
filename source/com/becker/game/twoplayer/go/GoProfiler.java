package com.becker.game.twoplayer.go;

import com.becker.common.Profiler;
import com.becker.game.common.GameContext;

/**
 * User: Barry Becker
 * Date: Apr 16, 2005
 * Time: 6:20:14 AM
 */
public final class GoProfiler extends Profiler {

    public static final String GENERATE_MOVES = "generating moves";
    private static final String UNDO_MOVE = "undoing move";
    private static final String UPDATE_STRINGS_AFTER_REMOVE = "updating strings after remove";
    private static final String UPDATE_GROUPS_AFTER_REMOVE = "updating groups after remove";
    private static final String MAKE_MOVE = "making move";
    private static final String UPDATE_STRINGS_AFTER_MOVE = "updating strings after move";
    private static final String UPDATE_GROUPS_AFTER_MOVE = "updating groups after move";
    //private static final String GET_NBR_GROUPS = "getting nbr groups";
    public static final String UPDATE_TERRITORY = "updating territory";
    public static final String ABSOLUTE_TERRITORY = "absolute territory";
    public static final String RELATIVE_TERRITORY = "relative territory";
    public static final String UPDATE_EMPTY = "updating empty regions";
    //private static final String CHECK_FOR_CUTS = "checking for cuts";
    public static final String GET_GROUP_NBRS = "getting group nbrs";
    public static final String FIND_GROUPS = "finding groups";
    public static final String FIND_STRINGS = "finding strings";
    public static final String FIND_CAPTURES = "finding capturess";
    public static final String UPDATE_EYES = "update eyes";
    public static final String GET_ENEMY_GROUPS_NBRS = "get enemy group nbrs";

    public GoProfiler() {
        add(GENERATE_MOVES);
        add(UNDO_MOVE);
          add(UPDATE_STRINGS_AFTER_REMOVE, UNDO_MOVE);
          add(UPDATE_GROUPS_AFTER_REMOVE, UNDO_MOVE);
        add(MAKE_MOVE);
          add(FIND_CAPTURES, MAKE_MOVE);
          add(UPDATE_STRINGS_AFTER_MOVE, MAKE_MOVE);
          add(UPDATE_GROUPS_AFTER_MOVE, MAKE_MOVE);
            //add(GET_NBR_GROUPS, UPDATE_GROUPS_AFTER_MOVE);
            //add(CHECK_FOR_CUTS, UPDATE_GROUPS_AFTER_MOVE);
            add(UPDATE_TERRITORY, UPDATE_GROUPS_AFTER_MOVE);
              add(ABSOLUTE_TERRITORY, UPDATE_TERRITORY);
                add(UPDATE_EYES, ABSOLUTE_TERRITORY);
              add(RELATIVE_TERRITORY, UPDATE_TERRITORY);
                add(GET_ENEMY_GROUPS_NBRS, RELATIVE_TERRITORY);
              add(UPDATE_EMPTY, UPDATE_TERRITORY);
        add(GET_GROUP_NBRS);
        add(FIND_GROUPS);
        add(FIND_STRINGS);
    }

    public void initialize() {
        resetAll();
        setEnabled(GameContext.isProfiling());
        setLogger(GameContext.getLogger());
    }

    public void startUndoMove() {
        this.start(UNDO_MOVE);
    }

    public void stopUndoMove() {
        this.stop(UNDO_MOVE);
    }

    public void startMakeMove() {
        this.start(MAKE_MOVE);
    }

    public void stopMakeMove() {
        this.stop(MAKE_MOVE);
    }

    public void startUpdateStringsAfterRemove() {
        this.start(UPDATE_STRINGS_AFTER_REMOVE);
    }

    public void stopUpdateStringsAfterRemove() {
        this.stop(UPDATE_STRINGS_AFTER_REMOVE);
    }

    public void startUpdateGroupsAfterRemove() {
        this.start(UPDATE_GROUPS_AFTER_REMOVE);
    }

    public void stopUpdateGroupsAfterRemove() {
        this.stop(UPDATE_GROUPS_AFTER_REMOVE);
    }


    public void startUpdateStringsAfterMove() {
        this.start(UPDATE_STRINGS_AFTER_MOVE);
    }

    public void stopUpdateStringsAfterMove() {
        this.stop(UPDATE_STRINGS_AFTER_MOVE);
    }

    public void startUpdateGroupsAfterMove() {
        this.start(UPDATE_GROUPS_AFTER_MOVE);
    }

    public void stopUpdateGroupsAfterMove() {
        this.stop(UPDATE_GROUPS_AFTER_MOVE);
    }

}

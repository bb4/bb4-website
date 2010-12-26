package com.becker.game.twoplayer.go;

import com.becker.game.common.GameProfiler;


/**
 * Keep track of how much time is spent in each time critical part of the 
 * comupter move processing.   Singleton.
 * 
 * @author Barry Becker
 */
public final class GoProfiler extends GameProfiler {

    /** singleton instance */
    private static GoProfiler instance;

    private static final String UPDATE_STRINGS_AFTER_REMOVE = "updating strings after remove";
    private static final String UPDATE_GROUPS_AFTER_REMOVE = "updating groups after remove";
    private static final String UPDATE_STRINGS_AFTER_MOVE = "updating strings after move";
    private static final String UPDATE_GROUPS_AFTER_MOVE = "updating groups after move";
    private static final String RECREATE_GROUPS_AFTER_MOVE = "recreating groups after move";
    //private static final String GET_NBR_GROUPS = "getting nbr groups";
    public static final String UPDATE_TERRITORY = "updating territory";
    public static final String ABSOLUTE_TERRITORY = "absolute territory";
    public static final String RELATIVE_TERRITORY = "relative territory";
    public static final String UPDATE_EMPTY = "updating empty regions";
    //private static final String CHECK_FOR_CUTS = "checking for cuts";
    public static final String GET_GROUP_NBRS = "getting group nbrs";
    private static final String FIND_GROUPS = "finding groups";
    public static final String FIND_STRINGS = "finding strings";
    public static final String FIND_CAPTURES = "finding capturess";
    public static final String UPDATE_EYES = "update eyes";
    public static final String GET_ENEMY_GROUPS_NBRS = "get enemy group nbrs";
    public static final String COPY_BOARD = "copy go board";

    public static GoProfiler getInstance() {
        if (instance == null) {
            instance = new GoProfiler();
        }
        return instance;
    }

    private GoProfiler() {
        add(GENERATE_MOVES);
          add(CALC_WORTH, GENERATE_MOVES);
        add(UNDO_MOVE);
          add(UPDATE_STRINGS_AFTER_REMOVE, UNDO_MOVE);
          add(UPDATE_GROUPS_AFTER_REMOVE, UNDO_MOVE);
        add(MAKE_MOVE);
          add(FIND_CAPTURES, MAKE_MOVE);
          add(UPDATE_STRINGS_AFTER_MOVE, MAKE_MOVE);
          add(UPDATE_GROUPS_AFTER_MOVE, MAKE_MOVE);
            add(RECREATE_GROUPS_AFTER_MOVE);
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
        add(COPY_BOARD);
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

    public void startRecreateGroupsAfterMove() {
        this.start(RECREATE_GROUPS_AFTER_MOVE);
    }

    public void stopRecreateGroupsAfterMove() {
        this.stop(RECREATE_GROUPS_AFTER_MOVE);
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

    public void startCopyBoard() {
        this.start(COPY_BOARD);
    }

    public void stopCopyBoard() {
        this.stop(COPY_BOARD);
    }


}

package com.becker.game.twoplayer.go;


/**
 * Go constraint parameters that can be optimized.
 * Some of these could be moved to GoOptions.
 *
 * @author Barry Becker    Date: Aug 21, 2005
 */
public final class GoControllerConstants {

    private GoControllerConstants() {}

    /** debug level can be overridden in ui.  */
    public static final int DEFAULT_DEBUG_LEVEL = 0;

    /** if true use an additional heuristic to get more accurate scoring of group health in a second pass. */
    public static final boolean USE_RELATIVE_GROUP_SCORING = true;

    /** default num row and columns for a default square go board.   */
    static final int DEFAULT_NUM_ROWS = 13;

    /** if difference greater than this, then consider a win. */
    static final int WIN_THRESHOLD = 1000;

}

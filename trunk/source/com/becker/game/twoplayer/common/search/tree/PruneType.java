package com.becker.game.twoplayer.common.search.tree;

/**
 * Either alpha or beta pruning.
 *
 * @author Barry Becker
 */
public enum PruneType {

    /** A pruning of the tree occurred because the alpha value was exceeded. */
    ALPHA,

    /** A pruning of the tree occurred because the beta value was exceeded. */
    BETA
}

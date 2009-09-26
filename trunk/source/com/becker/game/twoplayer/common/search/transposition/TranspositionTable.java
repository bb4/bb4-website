package com.becker.game.twoplayer.common.search.transposition;

/**
 * A kind of LRU cache for game moves so that we do not need to
 * recompute scores. Since lookups can use more accurate scores, alpha-beta
 * pruning can be much more effective - thereby dramatically reducing the
 * search space.
 *
 * @author Barry Becker
 */
public class TranspositionTable {

}

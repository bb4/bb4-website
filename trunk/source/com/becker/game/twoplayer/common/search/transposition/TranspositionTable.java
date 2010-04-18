package com.becker.game.twoplayer.common.search.transposition;

import com.becker.common.LRUCache;

/**
 * A kind of LRU cache for game moves so that we do not need to
 * recompute scores. Since lookups can use more accurate scores, alpha-beta
 * pruning can be much more effective - thereby dramatically reducing the
 * search space.
 * This technique applies the memoization pattern to search.
 * See http://en.wikipedia.org/wiki/Transposition_table
 * http://pages.cs.wisc.edu/~mjr/Pente/
 * shttp://people.csail.mit.edu/plaat/mtdf.html
 *
 * @author Barry Becker
 */
public class TranspositionTable extends LRUCache<Long, Entry> {

    /** Size of the table. If bigger, will take longer before we have to recycle positions. */
    private static final int MAX_ENTRIES = 100000;

    public TranspositionTable() {
        super(MAX_ENTRIES);
    }
}

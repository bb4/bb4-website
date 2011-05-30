package com.becker.game.twoplayer.common.cache;

import com.becker.game.twoplayer.common.search.transposition.HashKey;

/**
 * Holds the score and the board state
 *
 * @author Barry Becker
 */
public class ScoreEntry {

    private int score;
    private String boardDesc;
    private HashKey key;

    public ScoreEntry(int score, String boardDesc) {
        this.score = score;
        this.boardDesc = boardDesc;
    }

    /** only use this for debugging. normally we do not store the key */
    public ScoreEntry(int score, String boardDesc, HashKey key) {
        this.score = score;
        this.boardDesc = boardDesc;
        this.key = key;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return "Cached score="+ score +" for\n" + boardDesc + "\nhashKey used: " + key;
    }
}

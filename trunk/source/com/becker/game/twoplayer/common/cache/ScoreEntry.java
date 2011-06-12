package com.becker.game.twoplayer.common.cache;

import com.becker.game.twoplayer.common.search.transposition.HashKey;
import com.becker.game.twoplayer.go.board.WorthInfo;

/**
 * Holds the score and the board state
 *
 * @author Barry Becker
 */
public class ScoreEntry {

    private HashKey key;
    private int score;
    private String boardDesc;
    private WorthInfo info;

    public ScoreEntry(int score, String boardDesc) {
        this.score = score;
        this.boardDesc = boardDesc;
    }

    /** only use this for debugging. normally we do not store the key */
    public ScoreEntry(HashKey key,int score, String boardDesc, WorthInfo info) {
        this.key = key;
        this.score = score;
        this.boardDesc = boardDesc;
        this.info = info;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return "Cached scoreEntry (for key="+ key +")\n = "+ score +" for\n" + boardDesc  + "\n info="+ info;
    }
}

package com.becker.game.twoplayer.common.cache;

/**
 * Holds the score and the board state
 *
 * @author Barry Becker
 */
public class ScoreEntry {

    private double score;
    private String boardDesc;

    public ScoreEntry(double score, String boardDesc) {
        this.score = score;
        this.boardDesc = boardDesc;
    }

    public double getScore() {
        return score;
    }

    public String toString() {
        return "Cached score="+ score +" for\n" + boardDesc;
    }
}

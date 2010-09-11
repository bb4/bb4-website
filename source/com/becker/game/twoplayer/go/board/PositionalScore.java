package com.becker.game.twoplayer.go.board;

import com.becker.common.util.Util;

/**
 * For debugging purposes we want to keep more detail on what composes the overall score.
 * @author Barry Becker 
 */
public class PositionalScore {

    public double deadStoneScore = 0;
    public double eyeSpaceScore = 0;
    public double badShapeScore = 0;
    public double posScore = 0;
    public double healthScore = 0;

    /** Loosely based on badShapeScore, posScore, and healthScore */
    private double positionScore_ = 0;
    
    private boolean incremented_ = false;

    public double getPositionScore() {
        return positionScore_;
    }
    
    public void incrementBy(PositionalScore score) {
        positionScore_ += score.getPositionScore();
        deadStoneScore += score.deadStoneScore;
        eyeSpaceScore += score.eyeSpaceScore;
        badShapeScore += score.badShapeScore;
        posScore += score.posScore;
        healthScore += score.healthScore;
        incremented_ = true;
    }

    /**
     * don't call this after incrementing.
     * but you must call it if not incremented.
     */
    public void calcPositionScore() {
        assert (!incremented_);
        double s = deadStoneScore + eyeSpaceScore + healthScore + posScore + badShapeScore;
        positionScore_ = Math.max(-1.0, Math.min(1.0, s));
    }

    public String getDescription(double worth, double captureScore, double territoryDelta, double scaleFactor)  {
        StringBuilder buf =
                new StringBuilder("<html>Breakdown for <b>value</b> = "+ Util.formatNumber(worth));
        buf.append("<br>");
        buf.append("captureScore=").append(Util.formatNumber(captureScore)).append("<br>");
        buf.append("territoryDelta=").append(Util.formatNumber(territoryDelta)).append("<br>");
        buf.append("scaleFactor=").append(Util.formatNumber(scaleFactor)).append("<br>");
        buf.append(toString(true));
        buf.append("</html>");
        return buf.toString();
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean htmlForm) {
        StringBuilder buf = new StringBuilder();
        String sep = htmlForm ? "<br>" : "\n";
        buf.append("overall positionScore=").append(format(positionScore_)).append(sep);
        buf.append("  deadStoneScore=").append(format(deadStoneScore)).append(sep);
        buf.append("  eyeSpaceScore=").append(format(eyeSpaceScore)).append(sep);
        buf.append("  badShapeScore=").append(format(badShapeScore)).append(sep);
        buf.append("  posScore=").append(format(posScore)).append(sep);
        buf.append("  healthScore=").append(format(healthScore));
        return buf.toString();
    }

    private String format(double num) {
        // Util.formatNumber(num);
        return Double.toString(num);
    }
}

/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board;

import com.becker.common.format.FormatUtil;

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
     * Don't call this after incrementing, but you must call once before incrementing.
     */
    public void calcPositionScore() {
        assert (!incremented_);
        positionScore_ = deadStoneScore + eyeSpaceScore + healthScore + posScore + badShapeScore;
    }

    public String getDescription(double worth, double captureScore, double territoryDelta, double scaleFactor)  {
        StringBuilder buf =
                new StringBuilder("<html>Breakdown for <b>value</b> = "+ FormatUtil.formatNumber(worth));
        buf.append("<br>");
        buf.append("captureScore=").append(FormatUtil.formatNumber(captureScore)).append("<br>");
        buf.append("territoryDelta=").append(FormatUtil.formatNumber(territoryDelta)).append("<br>");
        buf.append("scaleFactor=").append(FormatUtil.formatNumber(scaleFactor)).append("<br>");
        buf.append(toString(true));
        buf.append("</html>");
        return buf.toString();
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean htmlForm) {
        StringBuilder buf = new StringBuilder();
        String sep = htmlForm ? "<br>" : "\t";
        buf.append("  Overall positionScore=").append(format(positionScore_)).append(sep);
        buf.append("  deadStoneScore=").append(format(deadStoneScore)).append(sep);
        buf.append("  eyeSpaceScore=").append(format(eyeSpaceScore)).append(sep);
        buf.append("  badShapeScore=").append(format(badShapeScore)).append(sep);
        buf.append("  posScore=").append(format(posScore)).append(sep);
        buf.append("  healthScore=").append(format(healthScore));
        buf.append("\n");
        return buf.toString();
    }

    private String format(double num) {
        return FormatUtil.formatNumber(num);
    }
}

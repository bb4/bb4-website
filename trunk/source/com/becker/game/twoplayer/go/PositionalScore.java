package com.becker.game.twoplayer.go;

import com.becker.common.*;
import com.becker.common.util.Util;

/**
 * For debuggin purposes we want to keep more detail on what composes the overall score.
 * @author Barry Becker Date: Dec 30, 2006
 */
public class PositionalScore {

    private double positionScore_ = 0; // loosely based on badShapeScore, posScore, and healthScore
    double deadStoneScore = 0;
    double eyeSpaceScore = 0;
    double badShapeScore = 0;
    double posScore = 0;
    double healthScore = 0;
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
        buf.append("captureScore=" + Util.formatNumber(captureScore) + "<br>");
        buf.append("territoryDelta=" + Util.formatNumber(territoryDelta) + "<br>");
        buf.append("scaleFactor=" + Util.formatNumber(scaleFactor) + "<br>");
        buf.append("positionScore=" + Util.formatNumber(positionScore_) + "<br>");
        buf.append("  deadStoneScore=" + Util.formatNumber(deadStoneScore) + "<br>");
        buf.append("  eyeSpaceScore=" + Util.formatNumber(eyeSpaceScore) + "<br>");
        buf.append("  badShapeScore=" + Util.formatNumber(badShapeScore) + "<br>");
        buf.append("  posScore=" + Util.formatNumber(posScore) + "<br>");
        buf.append("  healthScore=" + Util.formatNumber(healthScore) + "</html>");
        return buf.toString();
    }
}

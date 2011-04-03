package com.becker.simulation.liquid.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.liquid.Logger;
import com.becker.simulation.liquid.compute.VelocityInterpolator;
import com.becker.simulation.liquid.model.Cell;
import com.becker.simulation.liquid.model.Grid;
import com.becker.simulation.liquid.model.LiquidEnvironment;
import com.becker.simulation.liquid.model.Particle;

import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * Liquid Rendering options.
 *
 * @author Barry Becker
 */
public final class RenderingOptions {

    private boolean showVelocities_ = false;
    private boolean showPressures_ = false;
    private boolean showCellStatus_ = false;


    public RenderingOptions() {
    }

    public void setShowVelocities(boolean show) {
        showVelocities_ = show;
    }

    public boolean getShowVelocities() {
        return showVelocities_;
    }

    public void setShowPressures(boolean show) {
        showPressures_ = show;
    }

    public boolean getShowPressures() {
        return showPressures_;
    }

    public void setShowCellStatus(boolean show) {
        showCellStatus_ = show;
    }

    public boolean getShowCellStatus() {
        return showCellStatus_;
    }

}

package com.becker.simulation.reactiondiffusion;

import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Nov 5, 2006
 */
public class RDOptionsDialog extends SimulatorOptionsDialog {

    public RDOptionsDialog( JFrame parent, Simulator simulator ) {
        super(parent, simulator);
    }

    protected JPanel createCustomParamPanel() {
        return new JPanel();        
    }
}

package com.becker.simulation.dice;


import com.becker.ui.*;
import com.becker.simulation.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker Date: 2007
 */
public class DiceOptionsDialog extends SimulatorOptionsDialog {


    /** number of dice to use.   */
    private NumberInput numDiceField_;
    /** number of sides on dice.  */
    private NumberInput numSidesField_;


    // constructor
    public DiceOptionsDialog( Frame parent, Simulator simulator )
    {
        super( parent, simulator );
    }

    public String getTitle()
    {
        return "Dice Simulation Configuration";
    }


    protected JPanel createCustomParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        numDiceField_ =
                new NumberInput("Number of Dice (1 - 200): ", 2,
                                "This sets the number of dice to throw on each step of the simulation.", 1, 200, true);
        numSidesField_ =
                new NumberInput( "Number of Sides on Dice (2 - 100): ", 6,
                                  "This sets the number of sides on each dice that is thrown.", 1, 100, true);

        innerPanel.add( numDiceField_ );
        innerPanel.add( numSidesField_);
        JPanel fill = new JPanel();
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(fill, BorderLayout.CENTER);

        return paramPanel;
    }



    protected void ok()
    {
        super.ok();

        DiceSimulator simulator = (DiceSimulator) getSimulator();
        // set the common rendering and global physics options
        simulator.setNumDice(numDiceField_.getIntValue());
        simulator.setNumSides(numSidesField_.getIntValue());
    }

}

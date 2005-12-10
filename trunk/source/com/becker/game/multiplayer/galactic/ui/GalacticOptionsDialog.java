package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.Box;
import java.awt.event.*;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class GalacticOptionsDialog extends GameOptionsDialog implements ActionListener, ItemListener
{

    // game params
    private NumberInput numPlanets_;
    private NumberInput planetProductionRate_;
    private NumberInput maxYearsToPlay_;
    private JCheckBox neutralsBuild_;


    // constructor
    GalacticOptionsDialog( JFrame parent, GameController controller )
    {
        super( parent, controller);
    }


    /**
     * @return galactic game optiosn tab panel.
     */
    protected JPanel createControllerParamPanel()
    {
        JPanel p = new JPanel();

        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder(
                       BorderFactory.createEtchedBorder(),
                         GameContext.getLabel("GAME_OPTIONS")) );

        GalacticController c = (GalacticController)controller_;

        numPlanets_ =  new NumberInput( GameContext.getLabel("NUMBER_OF_PLANETS"), c.getNumPlanets(),
                                               GameContext.getLabel("NUMBER_OF_PLANETS_TIP"), Galaxy.MIN_NUM_PLANETS, Galaxy.MAX_NUM_PLANETS, true);
        planetProductionRate_ =
                new NumberInput( GameContext.getLabel("PLANETS_PRODUCTION_RATE"), c.getPlanetProductionRate(),
                                      GameContext.getLabel("PLANETS_PRODUCTION_RATE_TIP"), 0, 10, true);
        neutralsBuild_ = new JCheckBox( GameContext.getLabel("SHOULD_NEUTRALS_BUILD"), c.getNeutralsBuild() );
        neutralsBuild_.setToolTipText(GameContext.getLabel("SHOULD_NEUTRALS_BUILD_TIP"));

        maxYearsToPlay_ =  new NumberInput( GameContext.getLabel("MAX_YEARS_TO_PLAY"), c.getMaxYearsToPlay(),
                                            GameContext.getLabel("MAX_YEARS_TO_PLAY_TIP"), 1, 100, true);

        p.add( numPlanets_ );
        p.add( planetProductionRate_ );
        p.add( neutralsBuild_ );
        p.add( maxYearsToPlay_ );

        p.add(Box.createVerticalGlue());

        p.setName(GameContext.getLabel("GAME"));
        return p;
    }


   protected void ok()
    {
        GalacticController c = (GalacticController)controller_;

        c.setNumPlanets(numPlanets_.getIntValue());
        c.setPlanetProductionRate(planetProductionRate_.getIntValue());
        c.setMaxYearsToPlay(maxYearsToPlay_.getIntValue());
        c.setNeutralsBuild(neutralsBuild_.isSelected());

        super.ok();
    }

}
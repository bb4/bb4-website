package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameOptionsDialog;
import com.becker.game.multiplayer.galactic.GalacticController;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
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
    private JTextField numPlanets_;
    private JTextField planetProductionRate_;
    private JTextField maxYearsToPlay_;
    private JCheckBox neutralsBuild_;


    // constructor
    public GalacticOptionsDialog( JFrame parent, GameController controller )
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

        // num Planets
        numPlanets_ = new JTextField( Integer.toString( c.getNumPlanets() ) );
        numPlanets_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel p1 =
                new NumberInputPanel( GameContext.getLabel("NUMBER_OF_PLANETS"), numPlanets_,
                                      GameContext.getLabel("NUMBER_OF_PLANETS_TIP"));
        p.add( p1 );

        // production level
        planetProductionRate_ = new JTextField( Integer.toString( c.getPlanetProductionRate() ) );
        planetProductionRate_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel p2 =
                new NumberInputPanel( GameContext.getLabel("PLANETS_PRODUCTION_RATE"), planetProductionRate_,
                                      GameContext.getLabel("PLANETS_PRODUCTION_RATE_TIP"));
        p.add( p2 );

        // should neutrals build?
        neutralsBuild_ = new JCheckBox( GameContext.getLabel("SHOULD_NEUTRALS_BUILD"), c.getNeutralsBuild() );
        neutralsBuild_.setToolTipText(GameContext.getLabel("SHOULD_NEUTRALS_BUILD_TIP"));
        p.add( neutralsBuild_ );

        // max years to play
        maxYearsToPlay_ = new JTextField(Integer.toString(c.getMaxYearsToPlay()) );
        maxYearsToPlay_.setMaximumSize( new Dimension( 30, ROW_HEIGHT ) );
        JPanel p4 =
                new NumberInputPanel( GameContext.getLabel("MAX_YEARS_TO_PLAY"), maxYearsToPlay_,
                                      GameContext.getLabel("MAX_YEARS_TO_PLAY_TIP"));
        p.add( p4 );

        p.add(Box.createVerticalGlue());

        p.setName(GameContext.getLabel("GAME"));
        return p;
    }


    protected void ok()
    {
        GalacticController c = (GalacticController)controller_;

        Integer numPlanets = new Integer(numPlanets_.getText());
        c.setNumPlanets(numPlanets.intValue());

        Integer planetProductionRate = new Integer(planetProductionRate_.getText());
        c.setPlanetProductionRate(planetProductionRate.intValue());

        Integer maxYearsToPlay = new Integer(maxYearsToPlay_.getText());
        c.setMaxYearsToPlay(maxYearsToPlay.intValue());

        c.setNeutralsBuild(neutralsBuild_.isSelected());

        super.ok();
    }

}
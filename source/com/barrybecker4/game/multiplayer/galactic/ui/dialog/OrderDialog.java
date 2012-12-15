/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Allow the user to specify a single order
 * @@ should show the distance when they have both origin and dest specified.
 *
 * @author Barry Becker
 */
final class OrderDialog extends OptionsDialog
                                                  implements ActionListener, ItemListener
{
    private GradientButton okButton_;

    private JComboBox originCombo_;
    private JComboBox destinationCombo_;

    private JLabel availableShips_;
    private NumberInput numShips_;
    private GalacticPlayer player_;

    private int numYearsRemaining_;

    private Map totalOutgoing_;

    private static final int DEFAULT_FLEET_SIZE = 10;


    /**
     * constructor - create the tree dialog.
     */
    public OrderDialog(GalacticPlayer player, Map totalOutgoing, int numYearsRemaining)
    {

        totalOutgoing_ = totalOutgoing;
        numYearsRemaining_ = numYearsRemaining;
        player_ = player;

        showContent();
    }


    /**
     * ui initialization of the tree control.
     */
    @Override
    protected JComponent createDialogContent()
    {
        JPanel mainPanel = new JPanel();
        setResizable( true );
        mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        // add the form elements
        String labelText = GameContext.getLabel("ORIGIN");
        originCombo_ = new JComboBox();
        originCombo_.addItemListener(this);
        initPlanetSelect(originCombo_, player_);
        JPanel originPanel = createComboInputPanel(labelText, originCombo_);

        labelText = GameContext.getLabel("DESTINATION");
        destinationCombo_ = new JComboBox();
        JPanel destPanel = createComboInputPanel( labelText, destinationCombo_);
        initPlanetSelect(destinationCombo_, null);
        availableShips_ = new JLabel();

        showAvailableShips(getOrigin());

        JPanel routePanel = new JPanel(new BorderLayout());
        routePanel.setMinimumSize(new Dimension(30,60));
        routePanel.add(originPanel, BorderLayout.NORTH);
        routePanel.add(destPanel, BorderLayout.CENTER);
        routePanel.add(availableShips_, BorderLayout.SOUTH);

        numShips_ = new NumberInput(GameContext.getLabel("NUMBER_OF_SHIPS_TO_SEND"), DEFAULT_FLEET_SIZE);

        mainPanel.add(routePanel, BorderLayout.NORTH);
        mainPanel.add(numShips_, BorderLayout.CENTER);
        //mainPanel_.add(new JLabel(" "), BorderLayout.SOUTH);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void initPlanetSelect(JComboBox combo, GalacticPlayer player) {
        // if player is null return a combo with all planets
        List planets =  Galaxy.getPlanets(player);
        String sPlanets[] = new String[planets.size()];
        for (int i=0; i<planets.size(); i++)  {
            Planet planet = (Planet)planets.get(i);
            sPlanets[i] = Character.toString(planet.getName());
        }
        ComboBoxModel comboModel = new DefaultComboBoxModel(sPlanets);
        combo.setModel(comboModel);

    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("MAKE_ORDER");
    }

    /**
     * Shows the number of available ships remaining for the specified planet
     * @param planet
     */
    private void showAvailableShips(Planet planet)
    {
        assert(planet != null);


        int availShips = planet.getNumShips() - getOutgoingShips(planet);
        String[] arg = {(""+planet.getName()), Integer.toString(availShips)};
        String text = MessageFormat.format(GameContext.getLabel("AVAILABLE_SHIPS"), (Object[])arg);
        availableShips_.setText(text);
    }

    private int getOutgoingShips(Planet planet)
    {
         int outgoing = 0;
        if (totalOutgoing_.get(planet)!=null)  {
           outgoing = (Integer) totalOutgoing_.get(planet);
        }
        return outgoing;
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    @Override
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if (source == okButton_) {
            // if there is not enough time to reach the planet, warn the user, and don't close the dlg.
            Order order = getOrder();
            if (order == null)
                return;  // not sure why this can happen, but it did.
            if (order.getTimeNeeded() > numYearsRemaining_) {
                JOptionPane.showMessageDialog(this,
                       "There are not enough years left ("+numYearsRemaining_+") in the game to reach that planet",
                       "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                this.setVisible(false);
            }
        }
        else if ( source == cancelButton) {
            cancel();
        }
        else {
           GameContext.log(0, "actionPerformed source=" + source + ". not cancel and not ok" );
        }
    }

    /**
     * called when the origin combo changes
     * @param e
     */
    public void itemStateChanged( ItemEvent e)
    {
        Object source = e.getSource();
        if (source == originCombo_)  {
            showAvailableShips(getOrigin());
        }
    }

    /**
     * @return retrieve the specified order.
     */
    public Order getOrder()
    {
        // fill it it based on field elements
        Planet origin = getOrigin();
        Planet destination = getDestination();

        int fleetSize = getFleetSize();
        if (fleetSize > (origin.getNumShips() - getOutgoingShips(origin))) {
            JOptionPane.showMessageDialog(this, GameContext.getLabel("CANT_SEND_MORE_THAN_YOU_HAVE"));
            return null;
        }
        return new Order(origin, destination, fleetSize);
    }

    private Planet getOrigin() {
        return Galaxy.getPlanet(originCombo_.getSelectedItem().toString().charAt(0));
    }

    private Planet getDestination() {
        return Galaxy.getPlanet(destinationCombo_.getSelectedItem().toString().charAt(0));
    }

    private int getFleetSize() {
        return numShips_.getIntValue();
    }

    /**
     *
     * @param labelText  the left hand side label.
     * @param combo the dropdown of values
     * @return a combo element with a label on the left and a dropdown on the right.
     */
    private JPanel createComboInputPanel(String labelText, JComboBox combo)
    {
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new BoxLayout( comboPanel, BoxLayout.X_AXIS ));
        comboPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        JLabel label = new JLabel( labelText );
        comboPanel.add( label );

        comboPanel.add( new JPanel());
        comboPanel.add( combo );
        combo.addActionListener(this);
        return comboPanel;
    }

}


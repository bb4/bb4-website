package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.galactic.player.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * Allow the user to specify a single order
 * @@ should show the distance when they have both origin and dest specified.
 *
 * @author Barry Becker
 */
public final class OrderDialog extends OptionsDialog
                               implements ActionListener, ItemListener
{
    private GalacticPlayer player_;

    private GradientButton okButton_;

    private JComboBox originCombo_;
    private JComboBox destinationCombo_;

    private JLabel availableShips_;
    private NumberInput numShips_;

    private int numYearsRemaining_;

    Map totalOutgoing_;

    private static final int DEFAULT_FLEET_SIZE = 10;


    /**
     * constructor - create the tree dialog.
     */
    public OrderDialog(GalacticPlayer player, Map totalOutgoing, int numYearsRemaining)
    {
        player_ = player;
        totalOutgoing_ = totalOutgoing;
        numYearsRemaining_ = numYearsRemaining;

        initUI();
    }


    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        setResizable( true );
        mainPanel_ =  new JPanel();
        mainPanel_.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        // add the form elements

        String labelText = GameContext.getLabel("ORIGIN");
        originCombo_ = createPlanetSelect(player_);
        originCombo_.addItemListener(this);
        JPanel originPanel = createComboInputPanel(labelText, originCombo_);

        labelText = GameContext.getLabel("DESTINATION");
        destinationCombo_ = createPlanetSelect(null);
        JPanel destPanel = createComboInputPanel( labelText, destinationCombo_);

        availableShips_ = new JLabel();

        showAvailableShips(getOrigin());

        JPanel routePanel = new JPanel(new BorderLayout());
        routePanel.setMinimumSize(new Dimension(30,60));
        routePanel.add(originPanel, BorderLayout.NORTH);
        routePanel.add(destPanel, BorderLayout.CENTER);
        routePanel.add(availableShips_, BorderLayout.SOUTH);

        numShips_ = new NumberInput(GameContext.getLabel("NUMBER_OF_SHIPS_TO_SEND"), DEFAULT_FLEET_SIZE);

        mainPanel_.add(routePanel, BorderLayout.NORTH);
        mainPanel_.add(numShips_, BorderLayout.CENTER);
        //mainPanel_.add(new JLabel(" "), BorderLayout.SOUTH);
        mainPanel_.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel_ );
        getContentPane().repaint();
        pack();
    }

    private static JComboBox createPlanetSelect(GalacticPlayer player) {
        // if player is null return a combo with all planets

        List planets =  Galaxy.getPlanets(player);
        String sPlanets[] = new String[planets.size()];
        for (int i=0; i<planets.size(); i++)  {
            Planet planet = (Planet)planets.get(i);
            sPlanets[i] = Character.toString(planet.getName());
        }
        return new JComboBox(sPlanets);
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

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
        String text = MessageFormat.format(GameContext.getLabel("AVAILABLE_SHIPS"), (java.lang.Object[])arg);
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
        else if ( source == cancelButton_ ) {
            cancel();
        }
        else {
           System.out.println( "actionPerformed source="+source+". not cancel and not ok" );
        }
    }

    /**
     * called when the origin combo changes
     * @param e
     */
    public void itemStateChanged( ItemEvent e)
    {
        Object source = e.getSource();
        System.out.println( "itemStateChanged source="+source );
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
        Order order = new Order(origin, destination, fleetSize);
        return order;
    }

    private Planet getOrigin() {
        System.out.println("originCombo_.getSelectedItem()="+originCombo_.getSelectedItem());
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


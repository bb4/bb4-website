package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;



/**
 * Show a summary of the final results.
 * We will show how many planets and how many ships each remaining player has.
 * The winner is the player with the most planets.
 * If there are more than one player with the same number of planets,
 * then the number of ships will be used to break ties.
 *
 * @author Barry Becker
 */
final class TallyDialog extends OptionsDialog

{
    private GalacticController controller_;

    private GradientButton okButton_;

    // list of players that will be admirals in the game.
    private SummaryTable summaryTable_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param controller
     */
    public TallyDialog( JFrame parent, GalacticController controller )
    {
        super( parent );
        controller_ = controller;


        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {
            GameContext.log( 0, "we ran out of memory!" );
            GameContext.log( 0, GUIUtil.getStackTrace( oom ) );
        }
        pack();
    }

    public String getTitle()
    {
        return GameContext.getLabel("TALLY_TITLE");
    }


    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        mainPanel_.setLayout(new BorderLayout());

        GalacticPlayer[] players = (GalacticPlayer[])controller_.getPlayers();
        String winningPlayer = findWinner(players);

        // show a label at the top with who the winner is
        JLabel winnerLabel = new JLabel();
        winnerLabel.setText("<html>"+GameContext.getLabel("GAME_OVER")+"<br>"+GameContext.getLabel("WINNER_IS")+"<br>"+winningPlayer+"</html>");
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        mainPanel_.add(winnerLabel, BorderLayout.NORTH);

        summaryTable_ = new SummaryTable(players);
        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        tablePanel.add(new JScrollPane(summaryTable_.getTable()), BorderLayout.CENTER);

        mainPanel_.add(tablePanel, BorderLayout.CENTER);
        mainPanel_.add(createButtonsPanel(), BorderLayout.SOUTH);


        this.getContentPane().add(mainPanel_);
        //this.setPreferredSize(new Dimension(500,300));
    }

    private String findWinner(GalacticPlayer[] players)
    {
        String winner ="nobody";
        double maxCriteria = -1.0;
        for (int i=0; i<players.length; i++) {
            GalacticPlayer player =players[i];
            List planets = Galaxy.getPlanets(player);
            double criteria = planets.size() + player.getTotalNumShips()/1000000000000.0;

            if (criteria > maxCriteria) {
                maxCriteria = criteria;
                winner = player.getName();
            }
        }
        return winner;
    }

    /**
     *  create the OK Cancel buttons that go at the botton
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        //initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        //buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == okButton_) {
            this.setVisible(false);
        }
    }


}


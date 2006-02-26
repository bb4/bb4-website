package com.becker.puzzle.set;

import com.becker.ui.*;
import com.becker.game.common.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Feb 5, 2006
 */
public class NewSetGameDialog extends OptionsDialog implements ActionListener {


    protected JPanel playerPanel_ = null;
    protected JPanel boardParamPanel_ = null;

    protected final GradientButton startButton_ = new GradientButton();

    private SetGameViewer viewer_;


    // constructor
    public NewSetGameDialog( JFrame parent, SetGameViewer viewer )
    {
        super( parent );

        viewer_ = viewer;
        this.setAlwaysOnTop(true);
        initUI();
    }

    protected void initUI()
    {
        mainPanel_.setLayout( new BorderLayout() );

        playerPanel_ = createPlayerPanel();
        boardParamPanel_ = createBoardParamPanel();

        JPanel mainOptionsPanel = new JPanel();  // "new game" panel
        mainOptionsPanel.setLayout( new BoxLayout( mainOptionsPanel, BoxLayout.Y_AXIS ) );
        JPanel mainLoadGamePanel = new JPanel();  // "load game" panel
        mainLoadGamePanel.setLayout( new BoxLayout( mainLoadGamePanel, BoxLayout.Y_AXIS ) );

        JPanel buttonsPanel = createButtonsPanel();

        mainPanel_.add( playerPanel_, BorderLayout.NORTH );
        mainPanel_.add( boardParamPanel_, BorderLayout.NORTH );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.pack();
    }

    protected JPanel createButtonsPanel()
        {
            JPanel buttonsPanel = new JPanel( new FlowLayout() );

            initBottomButton( startButton_, GameContext.getLabel("START_GAME"), GameContext.getLabel("START_GAME_TIP") );
            initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("NGD_CANCEL_TIP") );

            buttonsPanel.add( startButton_ );
            buttonsPanel.add( cancelButton_ );

            return buttonsPanel;
        }


    public String getTitle()
    {
        return "Set Game Options";
    }

    protected JPanel createBoardParamPanel()
        {
            JPanel outerPanel = new JPanel();
            outerPanel.setLayout(new BorderLayout());
            JPanel p = new JPanel();
            p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
            p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Board Configuration" ) );
            JLabel label = new JLabel( GameContext.getLabel("BOARD_SIZE_COLON") );
            label.setAlignmentX( Component.LEFT_ALIGNMENT );
            p.add(label);

            outerPanel.add(p, BorderLayout.CENTER);
            outerPanel.add(new JPanel(), BorderLayout.EAST);
            return outerPanel;
        }


    public JPanel createPlayerPanel() {
        return new JPanel();
    }

    protected void ok()
    {


        this.setVisible( false );
    }

    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        if ( source == startButton_ ) {
            ok();
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
    }

}

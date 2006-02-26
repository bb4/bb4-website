package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Use this modal dialog to let the user choose from among the different game options.
 *
 * @author Barry Becker
 */
public abstract class NewGameDialog extends OptionsDialog implements ActionListener
{
    /**
     * the options get set directly on the game controller that is passed in.
     */
    protected GameController controller_;

    // contains the two tabs : options for creating a new game, or loading a saved game
    protected final JTabbedPane tabbedPanel_ = new JTabbedPane();

    protected JPanel playerPanel_ = null;
    protected JPanel boardParamPanel_ = null;
    protected JPanel customPanel_ = null;
    protected JPanel loadFilePanel_ = null;

    protected GradientButton openFileButton_;

    protected NumberInput rowSizeField_;
    protected NumberInput colSizeField_;
    protected JTextField openFileField_;

    protected final GradientButton startButton_ = new GradientButton();

    // the options get set directly on the game controller and viewer that are passed in
    protected final Board board_;
    protected final GameBoardViewer viewer_;

    // constructor
    public NewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        super( parent );
        controller_ = viewer.getController();
        board_ = controller_.getBoard();
        assert (board_!=null);
        viewer_ = viewer;
    }

    protected void initUI()
    {
        mainPanel_.setLayout( new BorderLayout() );

        playerPanel_ = createPlayerPanel();
        boardParamPanel_ = createBoardParamPanel();
        customPanel_ = createCustomPanel();
        loadFilePanel_ = createLoadFilePanel();

        JPanel mainOptionsPanel = new JPanel();  // "new game" panel
        mainOptionsPanel.setLayout( new BoxLayout( mainOptionsPanel, BoxLayout.Y_AXIS ) );
        JPanel mainLoadGamePanel = new JPanel();  // "load game" panel
        mainLoadGamePanel.setLayout( new BoxLayout( mainLoadGamePanel, BoxLayout.Y_AXIS ) );

        buildMainOptionsPanel(mainOptionsPanel);

        mainLoadGamePanel.add( loadFilePanel_ );

        JPanel buttonsPanel = createButtonsPanel();

        tabbedPanel_.add( GameContext.getLabel("NEW_GAME"), mainOptionsPanel );
        tabbedPanel_.setToolTipTextAt( 0, GameContext.getLabel("NEW_GAME_TIP") );
        tabbedPanel_.add( GameContext.getLabel("LOAD_GAME"), mainLoadGamePanel );
        tabbedPanel_.setToolTipTextAt( 1, GameContext.getLabel("LOAD_GAME_TIP") );
        mainPanel_.add( tabbedPanel_, BorderLayout.CENTER );
        mainPanel_.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel_ );
        this.pack();
    }

    protected void buildMainOptionsPanel(JPanel mainOptionsPanel)
    {
        if (playerPanel_ != null)
            mainOptionsPanel.add( playerPanel_ );
        if (boardParamPanel_ != null)
            mainOptionsPanel.add( boardParamPanel_ );
        if ( customPanel_ != null )
            mainOptionsPanel.add( customPanel_ );

    }

    protected abstract JPanel createPlayerPanel();


    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( startButton_, GameContext.getLabel("START_GAME"), GameContext.getLabel("START_GAME_TIP") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("NGD_CANCEL_TIP") );

        buttonsPanel.add( startButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    /**
     * Subclasses use this to create their own custom options
     * Default is to have no custom panel.
     */
    protected JPanel createCustomPanel()
    {
        return null;
    }

    /**
     * Subclasses use this to create their own custom board configuration options
     * Default is to have no custom panel.
     */
    protected JPanel createCustomBoardConfigPanel()
    {
        return null;
    }

    public String getTitle()
    {
        return GameContext.getLabel("NEW_GAME_DLG_TITLE");
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
        p.add( label );

        assert (board_!=null);
        rowSizeField_ = new NumberInput(GameContext.getLabel("NUMBER_OF_ROWS"), board_.getNumRows());
        colSizeField_ = new NumberInput( GameContext.getLabel("NUMBER_OF_COLS"), board_.getNumCols());

        rowSizeField_.setAlignmentX( Component.LEFT_ALIGNMENT );
        colSizeField_.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( rowSizeField_ );
        p.add( colSizeField_ );

        // add a custom section if desired (override createCustomBoardConfigPanel in derived class)
        JPanel customConfigPanel = createCustomBoardConfigPanel();
        if ( customConfigPanel != null )
            p.add( customConfigPanel );

        outerPanel.add(p, BorderLayout.CENTER);
        outerPanel.add(new JPanel(), BorderLayout.EAST);
        return outerPanel;
    }

    protected JPanel createLoadFilePanel()
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.X_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                     GameContext.getLabel("SPECIFY_SGF") ) );
        p.setMaximumSize( new Dimension( 400, ROW_HEIGHT + 30 ) );
        //p.setAlignmentX(Component.LEFT_ALIGNMENT);

        openFileField_ = new JTextField( "" );
        openFileField_.setAlignmentX( Component.LEFT_ALIGNMENT );
        openFileButton_ = new GradientButton( "..." );
        openFileButton_.setPreferredSize( new Dimension( 20, ROW_HEIGHT ) );
        openFileButton_.addActionListener( this );
        openFileButton_.setAlignmentX( Component.LEFT_ALIGNMENT );

        JLabel label = new JLabel(GameContext.getLabel("FILE_NAME_COLON") );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        p.add( openFileField_ );
        p.add( openFileButton_ );

        return p;
    }

    protected void ok()
    {
        if (boardParamPanel_ != null) {
            board_.setSize( rowSizeField_.getIntValue(), colSizeField_.getIntValue() );
        }

        //restore the saved file if one was specified
        String fileToOpen = openFileField_.getText();
        if ( fileToOpen != null && fileToOpen.length() > 1 ) {
            controller_.restoreFromFile( fileToOpen );
            canceled_ = true;
        }
        else
            canceled_ = false;
        this.setVisible( false );
    }

    protected void openFile()
    {
       if (GUIUtil.isStandAlone())  {
             JOptionPane.showMessageDialog(this, GameContext.getLabel("CANT_OPEN_WHEN_STANDALONE"));
       } else {
            JFileChooser chooser = GUIUtil.getFileChooser();
            chooser.setCurrentDirectory( new File( GameContext.getHomeDir()) );
            int state = chooser.showOpenDialog( null );
            File file = chooser.getSelectedFile();
            if ( file != null && state == JFileChooser.APPROVE_OPTION )
                openFileField_.setText( file.getAbsolutePath() );
        }
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
        else if ( source == openFileButton_ ) {
            openFile();
        }
    }

}
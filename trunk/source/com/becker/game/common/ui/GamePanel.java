package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.sound.SpeechSynthesizer;
import com.becker.sound.MusicMaker;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.applet.AudioClip;

/**
 * This is an abstract base class for a Game UI.
 * See derived classes for specific game implementations.
 *
 * It contains a dockable toolbar which shows at least 5 buttons:
 *  new game, undo, redo, options, and help.
 * It puts the game board viewer in a scrollable pane on the left.
 * There is an info window on the right that gives statistics about the current game state.
 * There is a brogress bar at the bottom that shows whenever the computer is thinking.
 *
 * This class is the main panel in the applet or application.
 * It contains everything related to acutally playing the board game.
 *
 *  @author Barry Becker
 */
public abstract class GamePanel extends TexturedPanel
                                implements ActionListener, GameChangedListener
{

    // ui elements.
    // There are (at least) 5 buttons in the ToolBar. There could be more depending on the game.
    // toolbar is protected rather than private so derived classes can add buttons to it.
    protected TexturedToolBar toolBar_ = null;

    protected GradientButton newGameButton_;
    protected GradientButton undoButton_;
    protected GradientButton redoButton_;
    protected GradientButton optionsButton_;
    //protected GradientButton resignButton_;
    protected GradientButton helpButton_;
    protected TexturedPanel statusBar_ = null;

    protected final JScrollPane boardViewerScrollPane_ = new JScrollPane();

    // must contain a GameBoardViewer to graphically represent the status of the board.
    protected GameBoardViewer boardViewer_ = null;

    protected NewGameDialog newGameDialog_ = null;
    protected GameOptionsDialog optionsDialog_ = null;
    protected GameInfoPanel infoPanel_ = null;


    // for a resizable applet
    protected ResizableAppletPanel resizablePanel_ = null;

    // font for the undo/redo buttons
    protected static final Font STATUS_FONT = new Font( "SansSerif", Font.PLAIN, 10 );
    protected static final Dimension MAX_BUTTON_SIZE = new Dimension( 100, 24 );
    // A greeting specified using allophones. See SpeechSynthesizer.
    protected static final String[] GREETING = {"w|u|d", "y|ouu", "l|ii|k", "t|ouu", "p|l|ay", "aa", "gg|AY|M"};

    protected static final String CORE_IMAGE_PATH = GameContext.GAME_ROOT+"common/ui/images/";
    protected static final ImageIcon BG_TEXTURE;
    static {
        // this image shows as the transparent background for textured panels.
        GameContext.log(2,  "get ocean image" );
        BG_TEXTURE = GUIUtil.getIcon(CORE_IMAGE_PATH + "ocean_trans_10.png");
    }


    /**
     * Construct the panel.
     */
    public GamePanel()
    {
        super(BG_TEXTURE);
        commonInit();
    }

    /**
     * common initialization in the event that there are multiple constructors.
     */
    protected void commonInit()
    {
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the title for the applet/application window.
     */
    public abstract String getTitle();


    /**
     *  UIComponent initialization.
     */
    protected void initGui()
    {

        String dir = CORE_IMAGE_PATH;
        ImageIcon newGameImage = GUIUtil.getIcon(dir+"newGame.gif");
        ImageIcon helpImage = GUIUtil.getIcon(dir+"help.gif");
        ImageIcon undoImage = GUIUtil.getIcon(dir+"undo_on.gif");
        ImageIcon redoImage = GUIUtil.getIcon(dir+"redo_on.gif");
        ImageIcon undoImageDisabled = GUIUtil.getIcon(dir+"undo_off.gif");
        ImageIcon redoImageDisabled = GUIUtil.getIcon(dir+"redo_off.gif");
        ImageIcon optionsImage = GUIUtil.getIcon(dir+"iconDesktop.gif");

        JPanel mainPanel = new JPanel( new BorderLayout() );

        //this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel statusBarLabel = new JLabel();
        statusBarLabel.setFont(STATUS_FONT);
        statusBarLabel.setOpaque(false);
        statusBarLabel.setText( GameContext.getLabel("STATUS_MSG"));
        statusBar_ = new TexturedPanel(BG_TEXTURE);
        statusBar_.setLayout(new BorderLayout());
        statusBar_.setMaximumSize(new Dimension(1000, 16));
        statusBar_.add(statusBarLabel, BorderLayout.WEST);

        newGameButton_ = createToolBarButton( GameContext.getLabel("NEW_GAME_BTN"),
                                              GameContext.getLabel("NEW_GAME_BTN_TIP"),
                                              newGameImage );
        undoButton_ = createToolBarButton( "", GameContext.getLabel("UNDO_BTN_TIP"), undoImage );
        undoButton_.setDisabledIcon(undoImageDisabled);
        undoButton_.setEnabled(false);    // nothing to undo initially
        redoButton_ = createToolBarButton( "", GameContext.getLabel("REDO_BTN_TIP"), redoImage );
        redoButton_.setDisabledIcon(redoImageDisabled);
        redoButton_.setEnabled(false);    // nothing to redo initially
        optionsButton_ = createToolBarButton( GameContext.getLabel("OPTIONS_BTN"),
                                              GameContext.getLabel("OPTIONS_BTN_TIP"), optionsImage );
        helpButton_ = createToolBarButton( GameContext.getLabel("HELP_BTN"),
                                           GameContext.getLabel("HELP_BTN_TIP"), helpImage );

        toolBar_ = new TexturedToolBar(BG_TEXTURE);
        toolBar_.add( newGameButton_ );
        toolBar_.add( undoButton_ );
        toolBar_.add( redoButton_ );
        addCustomToolBarButtons();
        toolBar_.add( optionsButton_ );
        toolBar_.add( Box.createHorizontalGlue() );
        toolBar_.add( helpButton_ );

        // the main board viewer, It displays the current state of the board.
        // the board viewer creates its own controller
        boardViewer_ = createBoardViewer();


        OutputWindow logWindow = new OutputWindow( GameContext.getLabel("LOG_OUTPUT"), null);
        GameContext.setLogger( new Log( logWindow ) );

        newGameDialog_ = createNewGameDialog( null, boardViewer_ );
        optionsDialog_ = createOptionsDialog( null, boardViewer_.getController() );

        // if the board is too big, allow it to be scrolled.
        boardViewerScrollPane_.setViewportView( boardViewer_ );

        infoPanel_ = createInfoPanel( boardViewer_.getController());
        infoPanel_.setTexture( BG_TEXTURE );

        // this allows the info to update when someone makes a move
        boardViewer_.addGameChangedListener( infoPanel_ );
        // allows the undo button to update initially
        boardViewer_.addGameChangedListener(this);

        // for showing a progress bar for example.
        JPanel bottomDecorationPanel = createBottomDecorationPanel();

        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());
        viewerPanel.add( boardViewerScrollPane_, BorderLayout.CENTER );
        if (bottomDecorationPanel!=null)
            viewerPanel.add( bottomDecorationPanel, BorderLayout.SOUTH);

        mainPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        mainPanel.add( toolBar_, BorderLayout.NORTH );
        mainPanel.add( statusBar_, BorderLayout.SOUTH );
        mainPanel.add( infoPanel_, BorderLayout.EAST );
        mainPanel.add( viewerPanel, BorderLayout.CENTER );

        resizablePanel_ = new ResizableAppletPanel( mainPanel );

        setLayout(new BorderLayout());
        add( resizablePanel_, BorderLayout.CENTER ); //mainPanel_ );
        //this.setSize( new Dimension( 600, 500 ) );


        //start and initialize a new game with the default options
        boardViewer_.startNewGame();

        // misc speech
        if ( GameContext.getUseSound() ) {
            // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
            //SpeechSynthesizer speech = new SpeechSynthesizer();
            //speech.sayPhoneWords( GREETING );

            /* @@ uncomment
            URL url = GUIUtil.getURL("com/becker/sound/play_game_voice.wav");
            AudioClip clip = new sun.applet.AppletAudioClip(url);
            if (clip != null) {
	            clip.play();
	        }
             */
        }
        this.setDoubleBuffered(false);

    }

    protected JPanel createBottomDecorationPanel()
    {
        return null;
    }

    /**
     * create a toolbar button.
     */
    protected final GradientButton createToolBarButton( String text, String tooltip, Icon icon )
    {
        GradientButton button = new GradientButton( text, icon );
        button.addActionListener( this );
        button.setToolTipText( tooltip );
        button.setMaximumSize( MAX_BUTTON_SIZE );
        return button;
    }


    /**
     * @return the game controller. There should only be one of these
     *
    protected abstract GameController createGameController();
     */

    /**
     * @return the ui component used to display the current board state.
     */
    protected abstract GameBoardViewer createBoardViewer();

    /**
     * @return the dialog used for configuring a new game to play.
     */
    protected abstract NewGameDialog createNewGameDialog( JFrame parent, GameBoardViewer viewer );

    /**
     * @return  the dialog used to specify various game options and parameters.
     */
    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller )
    {
        return new GameOptionsDialog( parent, controller );
    }

    /**
     * @return the panel shown on the right hand side that displays statistics about the current game state.
     */
    protected abstract GameInfoPanel createInfoPanel( GameController controller );


    /**
     * Display a help dialog.
     * This dialog should tell about the game and give instructions on how to play.
     */
    protected abstract void showHelpDialog();

    /**
     * show a modal help dialog.
     * @param gameName  name of the game we are showing help for.
     * @param comments  version or other comments.
     * @param overview  Instructions on how to play and other info for the user.
     */
    protected final void showHelpDialog( String gameName, String comments, String overview )
    {
        HelpDialog dlg = new HelpDialog( null, gameName, comments, overview );
        dlg.setLocationRelativeTo( this );
        dlg.setModal( true );
        dlg.setVisible( true );
    }

    /**
     * override to add your own game dependent buttons to the toolbar.
     */
    protected void addCustomToolBarButtons()
    {}

    /**
     * This method allows javascript to resize the applet from the browser.
     */
    public final void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
    }

    /**
     * do any needed cleanup.
     */
    public final void destroy()
    {
        // remove all listeners, dispose components.
        if ( optionsDialog_ != null )
            optionsDialog_.dispose();
        if ( boardViewer_ != null )
            boardViewer_.dispose();
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    public void gameChanged( GameChangedEvent gce )
    {
        undoButton_.setEnabled(boardViewer_.getController().getLastMove() != null);
    }



    /**
     * handle button click actions.
     * If you add your own custom buttons, you should override this, but be sure the first line is
     * <P>
     * super.actionPerformed(e);
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == newGameButton_ ) {
            newGameDialog_.setLocationRelativeTo( this );

            boolean canceled = newGameDialog_.showDialog();
            if ( !canceled ) { // newGame a game with the newly defined options
                boardViewer_.startNewGame();
            }
        }
        else if ( source == undoButton_ ) {
            GameContext.log(1,  "undo clicked" );
            // gray it if there are now no more moves to undo
            undoButton_.setEnabled(boardViewer_.canUndoMove());
            redoButton_.setEnabled(true);
        }
        else if ( source == redoButton_ ) {
            GameContext.log(1,  "redo clicked" );
            // gray it if there are now no more moves to undo
            redoButton_.setEnabled(boardViewer_.canRedoMove());
            undoButton_.setEnabled(true);
        }
        if ( source == optionsButton_ ) {
            optionsDialog_.setLocationRelativeTo( this );
            boolean canceled = optionsDialog_.showDialog();
        }
        else if ( source == helpButton_ )
            showHelpDialog();
    }

}

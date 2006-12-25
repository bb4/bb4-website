package com.becker.game.common.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.online.ui.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

/**
 * This is an abstract base class for a Game UI.
 * See derived classes for specific game implementations.
 *
 * It contains a dockable toolbar which shows at least 5 buttons:
 * new game, undo, redo, options, and help.
 *  @see GameToolBar
 *
 * It puts the game board viewer in a scrollable pane on the left.
 * There is an info window on the right that gives statistics about the current game state.
 * There is a progress bar at the bottom that shows whenever the computer is thinking.
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
    protected GameToolBar toolBar_;

    protected TexturedPanel statusBar_;

    protected final JScrollPane boardViewerScrollPane_ = new JScrollPane();

    // must contain a GameBoardViewer to graphically represent the status of the board.
    protected GameBoardViewer boardViewer_;

    protected NewGameDialog newGameDialog_;
    protected OnlineGameDialog onlineGameDialog_;
    protected GameOptionsDialog optionsDialog_;
    protected GameInfoPanel infoPanel_;

    // for a resizable applet
    protected ResizableAppletPanel resizablePanel_;

    // font for the undo/redo buttons
    protected static final Font STATUS_FONT = new Font( "SansSerif", Font.PLAIN, 10 );

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
    }

    /**
     * common initialization in the event that there are multiple constructors.
     */
    protected void init(JFrame parent)
    {
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        initGui(parent);
    }

    public void openGame() {

        boardViewer_.openGame();
    }

    public void saveGame() {

        boardViewer_.saveGame();
    }


    /**
     * @return the title for the applet/application window.
     */
    public abstract String getTitle();

    protected GameToolBar createToolbar() {
         return new GameToolBar(BG_TEXTURE, this);
    }

    /**
     * Currently most games do not support online play (see poker)
     * @return true if the game supports online play and there is a server available
     */
    protected boolean isOnlinePlayAvailable()
    {
        return false;
    }

    /**
     *  UIComponent initialization.
     */
    protected void initGui(JFrame parent)
    {

        JPanel mainPanel = new JPanel( new BorderLayout() );

        JLabel statusBarLabel = new JLabel();
        statusBarLabel.setFont(STATUS_FONT);
        statusBarLabel.setOpaque(false);
        statusBarLabel.setText( GameContext.getLabel("STATUS_MSG"));
        statusBar_ = new TexturedPanel(BG_TEXTURE);
        statusBar_.setLayout(new BorderLayout());
        statusBar_.setMaximumSize(new Dimension(1000, 16));
        statusBar_.add(statusBarLabel, BorderLayout.WEST);

        toolBar_ = createToolbar();

        // the main board viewer, It displays the current state of the board.
        // the board viewer creates its own controller
        boardViewer_ = createBoardViewer();

        OutputWindow logWindow = new OutputWindow( GameContext.getLabel("LOG_OUTPUT"), null);
        GameContext.setLogger( new Log( logWindow ) );

        newGameDialog_ = createNewGameDialog( parent, boardViewer_ );
        onlineGameDialog_ = createOnlineGameDialog(parent, boardViewer_);
        optionsDialog_ = createOptionsDialog( parent, boardViewer_.getController() );

        // if the board is too big, allow it to be scrolled.
        boardViewerScrollPane_.setViewportView( boardViewer_ );

        infoPanel_ = createInfoPanel(boardViewer_.getController());
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


        //start and initialize a new game with the default options
        boardViewer_.startNewGame();

        // misc speech
        if ( GameContext.getUseSound() ) {
            // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
            /* npe in applet (why?)
            SpeechSynthesizer speech = new SpeechSynthesizer();
            speech.sayPhoneWords( GREETING );
            */

            // use when sound card available
            /* causing security exception in applet?
            URL url = GUIUtil.getURL("com/becker/sound/play_game_voice.wav");
            AudioClip clip = new AppletAudioClip(url);           
            clip.play();
            */

        }
        this.setDoubleBuffered(false);
    }

    protected JPanel createBottomDecorationPanel()
    {
        return null;
    }


    /**
     * @return the ui component used to display the current board state.
     */
    protected abstract GameBoardViewer createBoardViewer();

    /**
     * @return the dialog used for configuring a new game to play.
     */
    protected abstract NewGameDialog createNewGameDialog( JFrame parent, ViewerCallbackInterface viewer );

    /**
     * Only need to return something non-null if the game supports online play.
     * @return the dialog used for configuring online game play.
     */
    protected OnlineGameDialog createOnlineGameDialog( JFrame parent, ViewerCallbackInterface viewer ) {
        return null;
    }

    /**
     * @return  the dialog used to specify various game options and parameters.
     */
    protected abstract GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller );

    /**
     * @return the panel shown on the right hand side that displays statistics about the current game state.
     */
    protected abstract GameInfoPanel createInfoPanel( GameController controller);


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
        //dlg.setLocationRelativeTo( this );
        dlg.setModal( true );
        dlg.setVisible( true );
    }


    /**
     * This method allows javascript to resize the applet from the browser.
     */
    public final void setSize( int width, int height )
    {
        resizablePanel_.setSize( width, height );
    }

    public void saveSnapshot() {

        JFileChooser chooser = GUIUtil.getFileChooser();
        chooser.setCurrentDirectory( new File( GameContext.getHomeDir() ) );
        int state = chooser.showSaveDialog( null );
        File file = chooser.getSelectedFile();
        if ( file != null && state == JFileChooser.APPROVE_OPTION ) {

            BufferedImage img = (BufferedImage)createImage(getWidth(), getHeight());
            this.paint(img.createGraphics());

            ImageUtil.saveAsImage(file.getAbsolutePath(), img, ImageUtil.ImageType.JPG);
        }
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
        toolBar_.getUndoButton().setEnabled(boardViewer_.getBoard().getLastMove() != null);
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
        if ( source == toolBar_.getNewGameButton() ) {
            //newGameDialog_.setLocationRelativeTo( this );

            // if there is an active server and the game supports online play then show the online game dialog
            // instead of the normal new (local) game dialog to allow them to play with others online.
            if (isOnlinePlayAvailable())  {
                onlineGameDialog_.showDialog();
            } else {
                boolean canceled = newGameDialog_.showDialog();
                if ( !canceled ) { // newGame a game with the newly defined options
                    boardViewer_.startNewGame();
                    infoPanel_.reset();
                }
            }
        }
        else if ( source == toolBar_.getUndoButton() ) {
            GameContext.log(1,  "undo clicked" );
            // gray it if there are now no more moves to undo
            toolBar_.getUndoButton().setEnabled(boardViewer_.canUndoMove());
            toolBar_.getRedoButton().setEnabled(true);
        }
        else if ( source == toolBar_.getRedoButton() ) {
            GameContext.log(1,  "redo clicked" );
            // gray it if there are now no more moves to undo
            toolBar_.getRedoButton().setEnabled(boardViewer_.canRedoMove());
            toolBar_.getUndoButton().setEnabled(true);
        }
        if ( source == toolBar_.getOptionsButton() ) {
            //optionsDialog_.setLocationRelativeTo( this );
            optionsDialog_.showDialog();
        }
        else if ( source == toolBar_.getHelpButton() )
            showHelpDialog();
    }

}
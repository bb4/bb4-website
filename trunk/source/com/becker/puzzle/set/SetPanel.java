package com.becker.puzzle.set;

import com.becker.ui.*;
import com.becker.game.common.ui.*;
import com.becker.game.common.*;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Main panel for set game
 *
 * It contains a dockable toolbar and a canvas for showing the cards.
 * new game, add card, solve, and help.
 *  @see SetToolBar
 *
 *  @author Barry Becker
 */
public class SetPanel extends TexturedPanel
                      implements ActionListener
{

    // ui elements.
    protected SetToolBar toolBar_ = null;
    protected TexturedPanel statusBar_ = null;

    // must contain a GameBoardViewer to graphically represent the status of the board.
    private SetGameViewer setGameViewer_ = null;

    protected NewSetGameDialog newGameDialog_ = null;

    // for a resizable applet
    protected ResizableAppletPanel resizablePanel_ = null;

    protected static final Font STATUS_FONT = new Font( "SansSerif", Font.PLAIN, 10 );


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
    public SetPanel()
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
        initGui();
    }


    /**
     * @return the title for the applet/application window.
     */
    public String getTitle() {
        return "Set Game";
    }

    protected SetToolBar createToolbar() {
         return new SetToolBar(BG_TEXTURE, this);
    }

    /**
     *  UIComponent initialization.
     */
    protected void initGui()
    {
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

        toolBar_ = createToolbar();


        // the main board viewer, It displays the current state of the board.
        // the board viewer creates its own controller
        setGameViewer_ = new SetGameViewer();

        OutputWindow logWindow = new OutputWindow( GameContext.getLabel("LOG_OUTPUT"), null);
        GameContext.setLogger( new Log( logWindow ) );

        newGameDialog_ = createNewGameDialog( null, setGameViewer_ );

        // allows the undo button to update initially
        // setGameViewer_.addGameChangedListener(this);

        JPanel viewerPanel = new JPanel();
        viewerPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(setGameViewer_);

        viewerPanel.add( scrollPane, BorderLayout.CENTER );

        mainPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        mainPanel.add( toolBar_, BorderLayout.NORTH );
        mainPanel.add( statusBar_, BorderLayout.SOUTH );
        mainPanel.add( viewerPanel, BorderLayout.CENTER );

        resizablePanel_ = new ResizableAppletPanel( mainPanel );

        setLayout(new BorderLayout());

        add( resizablePanel_, BorderLayout.CENTER ); //mainPanel_ );

        //start and initialize a new game with the default options
        setGameViewer_.startNewGame();

        setDoubleBuffered(false);
    }

    /**
     *
     * @param parent  the frame used for relative posisitioning
     */
    public void setParentFrame(JFrame parent) {
        newGameDialog_.setParentFrame(parent);
        //setGameViewer_.setParentFrame(parent);
    }


    /**
     * @return the dialog used for configuring a new game to play.
     */
    protected NewSetGameDialog createNewGameDialog( JFrame parent, SetGameViewer viewer ) {
        return new NewSetGameDialog(parent, viewer);
    }


    /**
     * Display a help dialog.
     * This dialog should tell about the game and give instructions on how to play.
     */
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = "a Set game simulation by Barry Becker.";
        showHelpDialog( name, comments, "Click on the cards to find sets (of three cards). "+
                                        "Each of the attributes (color, shape, fill, and number) must be all the same or all different for the 3 cards.");
    }

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


    /**
     * do any needed cleanup.
     */
    public final void destroy()
    {
        // remove all listeners, dispose components.
        //if ( setGameViewer_ != null )
        //    setGameViewer_.dispose();
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

            newGameDialog_.setLocationRelativeTo( this );
            boolean canceled = newGameDialog_.showDialog();


            if ( !canceled ) { // newGame a game with the newly defined options
                setGameViewer_.startNewGame();
            }
        }
        else if ( source == toolBar_.getAddButton()) {
            setGameViewer_.addCard();
        }
        else if ( source == toolBar_.getRemoveButton()) {
            setGameViewer_.removeCard();

        }
        else if ( source == toolBar_.getSolveButton()) {
             JOptionPane.showMessageDialog(this, "Solution Requested");
        }
        else if ( source == toolBar_.getHelpButton() )    {
            showHelpDialog();
        }

        toolBar_.getRemoveButton().setEnabled(setGameViewer_.canRemoveCards());
        toolBar_.getAddButton().setEnabled(setGameViewer_.hasCardsToAdd());
        setGameViewer_.repaint();
    }

}

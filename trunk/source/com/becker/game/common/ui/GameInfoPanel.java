package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.border.*;
import java.awt.*;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public abstract class GameInfoPanel extends TexturedPanel implements GameChangedListener
{

    protected GameController controller_ = null;

    protected JFrame parent_;

    protected JLabel moveNumLabel_;
    protected JLabel playerLabel_;

    protected static final Font SECTION_TITLE_FONT = new Font( "SansSerif", Font.BOLD, 11 );
    protected static final Font BOLD_FONT = new Font( "SansSerif", Font.BOLD, 12 );
    protected static final int DEFAULT_MIN_WIDTH = 200;

    /**
     * Constructor
     */
    public GameInfoPanel( GameController controller )
    {
        super(null);
        controller_ = controller;

        this.setBorder( BorderFactory.createLoweredBevelBorder() );
        this.setToolTipText( getTitleText() );
        this.setPreferredSize( new Dimension( getMinWidth(), 1000 ) );
        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        createSubPanels();

        // this pushes everything to the top
        JPanel filler = createPanel();
        filler.setPreferredSize(new Dimension( getMinWidth(), 1000));
        this.add( filler );
    }

    public void setParentFrame(JFrame parent) {
        parent_ = parent;
    }
    /**
     *  create all the sub panels in the desired order.
     *  Subclasses may override to get a different ordering.
     */
    protected void createSubPanels()
    {
        // the custom panel shows game specific info like captures etc.
        JPanel customPanel = createCustomInfoPanel();
        if ( customPanel != null )
            this.add( customPanel );

        this.add( createGeneralInfoPanel() );
    }

    protected int getMinWidth() {
        return DEFAULT_MIN_WIDTH;
    }

    /**
     * @return title to display at the top of the game info window.
     */
    protected String getTitleText()
    {
        return GameContext.getLabel("GAME_INFORMATION");
    }

    /**
     * This panel shows information that is specific to the game type (like captures or territory estimates).
     */
    protected JPanel createCustomInfoPanel()
    {
        return null; // none by default
    }

    /**
     * this is general information that is applicable to every 2 player game.
     */
    protected JPanel createGeneralInfoPanel()
    {
        JPanel generalPanel = createSectionPanel(GameContext.getLabel("GENERAL_INFO"));

        JLabel turnLabel = createLabel(GameContext.getLabel("PLAYER_TO_MOVE_COLON"));
        playerLabel_ = new JLabel();

        playerLabel_.setOpaque(true);
        playerLabel_.setFont(BOLD_FONT);
        setPlayerLabel();

        JLabel moveNumTextLabel = createLabel( getMoveNumLabel());
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel_ = createLabel( " 0" );

        generalPanel.add( createRowEntryPanel( turnLabel, playerLabel_ ) );
        generalPanel.add( createRowEntryPanel( moveNumTextLabel, moveNumLabel_ ) );

        // add this back in when it is implemented
        //generalPanel.add( createRowEntryPanel(showRecommendedMove_) );
        generalPanel.add( Box.createGlue() );

        return generalPanel;
    }

    protected String getMoveNumLabel()
    {
        return GameContext.getLabel("CURRENT_MOVE_NUM_COLON");
    }


    protected Border getPlayerLabelBorder(Color pColor) {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0),
                                                  BorderFactory.createEtchedBorder(pColor, pColor.darker()));
    }

    /**
     * create a row in a panel that has one or 2 components.
     */
    protected final JPanel createRowEntryPanel( JComponent firstComp, JComponent secondComp )
    {
        JPanel rowPanel = createPanel();

        rowPanel.setLayout( new BoxLayout( rowPanel, BoxLayout.X_AXIS ) );
        rowPanel.setMaximumSize( new Dimension( 300, 16 ) );
        if ( firstComp != null ) {
            rowPanel.add( firstComp );
        }
        if ( secondComp != null ) {
            rowPanel.add( secondComp );
        }
        return rowPanel;
    }

    protected final JPanel createRowEntryPanel( JComponent firstComp )
    {
        return createRowEntryPanel( firstComp, null );
    }

    protected final JPanel createPanel()
    {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    /**
     * Create a panel with an etched border for the section.
     * @param title  the title of the panel.
     * @return  the constructed panel
     */
    protected final JPanel createSectionPanel(String title)
    {
        JPanel p = createPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title,
                     TitledBorder.LEFT, TitledBorder.TOP, SECTION_TITLE_FONT) );
        return p;
    }

    protected final JLabel createLabel()
    {
       return createLabel(null);
    }

    protected final JLabel createLabel(String s)
    {
        JLabel l = new JLabel(s);
        l.setOpaque(false);
        return l;
    }

    /**
     * set the appropriate text and color for the player label.
     */
    protected abstract void setPlayerLabel();

    protected static Border createMarginBorder()
    {
        return BorderFactory.createEmptyBorder(3,3,3,3);
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;
        if ( controller_.getBoard().getLastMove() != null ) {
            setPlayerLabel();
            moveNumLabel_.setText( " "+ controller_.getNumMoves() );
        }
    }

}
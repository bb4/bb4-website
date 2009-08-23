package com.becker.game.twoplayer.common.ui;

import com.becker.common.ColorMap;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 *  this class defines how to draw entrees in the game tree ui.
 *
 *  @author Barry Becker
 */
public class GameTreeCellRenderer extends DefaultTreeCellRenderer
{

    private Color p1Color_ = Color.green;
    private ColorMap colormap_ = null;

    private static final Color SELECTED_COLOR = new Color( 200, 50, 0 );

    private final JLabel pieceLabel_ = new JLabel();
    private final JPanel cellPanel_ = new JPanel();

    protected  GameTreeCellRenderer() {
        commonInit();
    }

    // Default Constructor
    public GameTreeCellRenderer(TwoPlayerPieceRenderer pieceRenderer) {
        setColorMap(createColormap(pieceRenderer));
        commonInit();
    }

    /**
     * initialize the colormap used to color the gmae tree rows, nodes, and arcs.
     */
    private static ColorMap createColormap(TwoPlayerPieceRenderer renderer) {
        // we will use this colormap for both the text tree and the graphical
        // tree viewers so they have consistent coloring.
        final double[] values = {-TwoPlayerController.WINNING_VALUE,
                                 -TwoPlayerController.WINNING_VALUE/20.0,
                                 0.0,
                                 TwoPlayerController.WINNING_VALUE/20.0,
                                 TwoPlayerController.WINNING_VALUE};
        final Color[] colors = {renderer.getPlayer2Color().darker(),
                                renderer.getPlayer2Color(),
                                new Color( 160, 160, 160),
                                renderer.getPlayer1Color(),
                                renderer.getPlayer1Color().darker()};
        return new ColorMap( values, colors);
    }

    private void commonInit()  {

        cellPanel_.setBackground( UIManager.getColor( "Tree.textBackground" ) );
        setOpaque( false );
        pieceLabel_.setOpaque( true );
        pieceLabel_.setText( " " );
        pieceLabel_.setBackground( p1Color_ );

        cellPanel_.setLayout( new FlowLayout() );
        cellPanel_.add( pieceLabel_ );
        cellPanel_.add( this );
        cellPanel_.setMinimumSize( new Dimension( 300, 30 ) );
        cellPanel_.setVisible( true );
    }

    public ColorMap getColorMap() {
        return colormap_;
    }

    public void setColorMap(ColorMap cmap) {
        colormap_ = cmap;
    }

    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus1 ) {

        super.getTreeCellRendererComponent( tree, value, sel,
                expanded, leaf, row, hasFocus1 );

        Color bg = getBGColor( value );
        Color fg = getFGColor( value );

        setBackground( bg );
        setForeground( fg );
        setBackgroundNonSelectionColor( bg );
        setBackgroundSelectionColor( Color.orange );
        // for some reason this doesn't work right
        //return cellPanel_;
        return this;
    }

    private static Color getFGColor( Object value ) {

        SearchTreeNode node = (SearchTreeNode) value;
        TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
        if ( m == null )
            return Color.gray;

        if ( m.isSelected() )
            return SELECTED_COLOR;

        return Color.black;
    }

    protected Color getBGColor( Object value )  {

        Color c;
        SearchTreeNode node = (SearchTreeNode) value;
        int numChildren = node.getChildCount();
        setText( getText() + " kids=" + numChildren );
        TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
        if ( m == null ) return Color.gray;    // passing move?

        if (colormap_ != null) {
            c = colormap_.getColorForValue(m.getInheritedValue());
        }
        else {
            int val = (int) (2.0 * Math.sqrt( Math.abs( m.getInheritedValue() ) ));
            if ( m.getInheritedValue() < 0 )
                val = -val;
            int v1 = 255 - Math.min( Math.max( val, 0 ), 255 );
            int v2 = 255 - Math.min( Math.max( -val, 0 ), 255 );
            int v3 = 255;
            c = new Color( v1, v2, v3 );
        }
        return c;
    }

}

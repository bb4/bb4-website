package com.becker.game.twoplayer.common.ui;

import com.becker.common.ColorMap;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.search.SearchTreeNode;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 *  this class defines how to draw entrees in the game tree ui.
 *
 *  @author Barry Becker
 */
final class GameTreeCellRenderer extends DefaultTreeCellRenderer
{

    private Color p1Color_ = Color.green;
    private Color p2Color_ = Color.blue;
    private ColorMap colormap_ = null;

    private static final Color SELECTED_COLOR = new Color( 200, 50, 0 );

    private final JLabel pieceLabel_ = new JLabel();
    private final JPanel cellPanel_ = new JPanel();


    // Default Constructor
    public GameTreeCellRenderer(ColorMap cmap)
    {
        colormap_ = cmap;
        commonInit();
    }

    // Constructor
    public GameTreeCellRenderer( Color player1Color, Color player2Color )
    {
        p1Color_ = player1Color;
        p2Color_ = player2Color;
        commonInit();
    }

    private void commonInit()
    {
        // @@ I want to show the piece color in each row in addition to everything else,
        // but this isn't working right
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

    public final Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus )
    {
        super.getTreeCellRendererComponent( tree, value, sel,
                expanded, leaf, row, hasFocus );

        Color bg = getBGColor( value );
        Color fg = getFGColor( value );

        this.setBackground( bg );
        this.setForeground( fg );
        this.setBackgroundNonSelectionColor( bg );
        this.setBackgroundSelectionColor( Color.orange );
        // for some reason this doesn't work right
        //return cellPanel_;
        return this;
    }

    private static Color getFGColor( Object value )
    {
       SearchTreeNode node = (SearchTreeNode) value;
        TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
        if ( m == null )
            return Color.gray;

        if ( m.selected )
            return SELECTED_COLOR;

        return Color.black;
    }

    private Color getBGColor( Object value )
    {
        Color c = null;
        SearchTreeNode node = (SearchTreeNode) value;
        int numChildren = node.getChildCount();
        this.setText( getText() + " kids=" + numChildren );
        TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
        if ( m == null ) return Color.blue;

        if (colormap_!=null) {
            c = colormap_.getColorForValue(m.inheritedValue);
        }
        else {
            int val = (int) (10.0 * Math.sqrt( Math.abs( m.inheritedValue ) ));
            if ( m.inheritedValue < 0 )
                val = -val;
            int v1 = 255 - Math.min( Math.max( val, 0 ), 255 );
            int v2 = 255 - Math.min( Math.max( -val, 0 ), 255 );
            int v3 = 255;
            c = new Color( v1, v2, v3 );
        }
        return c;
    }

    protected final Color getPieceColor( Object value )
    {
        SearchTreeNode node = (SearchTreeNode) value;
        TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
        if ( m == null )
            return Color.gray;

        return (m.player1) ? p1Color_ : p2Color_;
    }
}

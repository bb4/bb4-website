package com.becker.game.twoplayer.go.ui;

import com.becker.common.ColorMap;
import com.becker.common.Util;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchTreeNode;
import com.becker.game.twoplayer.common.ui.GameTreeCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 *  this class defines how to draw entrees in the text game tree ui.
 *
 *  @author Barry Becker
 */
final class GoTreeCellRenderer extends GameTreeCellRenderer
{

    private static final Color ROW_BG_COLOR = new Color( 220, 210, 240 );

    private static final int STONE_IMG_SIZE = 11;
    private static final int SWATCH_WIDTH = 75;
    private static final int TEXT_MARGIN = 4;
    private static final Font FONT = new Font("Sans Serif", Font.PLAIN, 9);

    private SearchTreeNode node_;


    // Default Constructor
    GoTreeCellRenderer()
    {
        setColorMap(createColormap());
    }


    /**
     * initialize the colormap used to color the gmae tree rows, nodes, and arcs.
     */
    protected ColorMap createColormap()
    {
        // we will use this colormap for both the text tree and the graphical tree viewers so they have consistent coloring.
        final double[] values = {-TwoPlayerController.WINNING_VALUE,
                                  -TwoPlayerController.WINNING_VALUE/2.0,
                                  -TwoPlayerController.WINNING_VALUE/10.0,
                                  -TwoPlayerController.WINNING_VALUE/40.0,
                                  -TwoPlayerController.WINNING_VALUE/100.0,
                                   0.0,
                                   TwoPlayerController.WINNING_VALUE/100.0,
                                   TwoPlayerController.WINNING_VALUE/40.0,
                                   TwoPlayerController.WINNING_VALUE/10.0,
                                   TwoPlayerController.WINNING_VALUE/2.0,
                                   TwoPlayerController.WINNING_VALUE};
        final Color[] colors = { new Color(140, 0, 0),
                                  new Color(255, 10, 10),
                                  new Color(240, 200, 0),
                                  new Color(255, 255, 80),
                                  new Color(200, 200, 100),
                                  new Color(240, 240, 240, 120),
                                  new Color(100, 200, 200),
                                  new Color(70, 255, 200),
                                  new Color(0, 190, 255),
                                  new Color(10, 10, 255),
                                  new Color(0, 0, 140)
                                };
        return new ColorMap( values, colors);
    }

    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus1 )
    {
        super.getTreeCellRendererComponent( tree, value, sel,
                expanded, leaf, row, hasFocus1 );

        node_ = (SearchTreeNode) value;

        Color bg = getBGColor( value );
        //Color fg = getFGColor( value );

        this.setBackgroundNonSelectionColor( bg );
        this.setBackgroundSelectionColor( Color.orange );

        return this;
    }

    protected Color getBGColor( Object value )
    {
        return ROW_BG_COLOR;
    }




    /**
      * Paints the value.  The background is filled based on selected.
      */
    public void paint(Graphics g) {
        Color bColor;

        if(selected) {
            bColor = getBackgroundSelectionColor();
        } else {
            bColor = getBackgroundNonSelectionColor();
            if(bColor == null)
            bColor = getBackground();
        }

        int imageOffset;
        if(bColor != null) {
            //Icon currentI = getIcon();

            imageOffset = 0;
            g.setColor(bColor);
            if(getComponentOrientation().isLeftToRight()) {
                g.fillRect(imageOffset, 0, getWidth() - imageOffset,
                   getHeight());
            } else {
                g.fillRect(0, 0, getWidth() - imageOffset,
                   getHeight());
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        TwoPlayerMove move = (TwoPlayerMove) node_.getUserObject();
        if (move == null) {
            return;
        }

        double inheritedValue = move.inheritedValue;
        double value = move.value;
        if (move.player1)  {
            g2.drawImage(GoStoneRenderer.BLACK_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
        } else {
            g2.drawImage(GoStoneRenderer.WHITE_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
            inheritedValue = -inheritedValue;
            value = -value;
        }


        Color c = getColorMap().getColorForValue(inheritedValue);
        g2.setColor(c);
        g2.fillRect(TEXT_MARGIN + STONE_IMG_SIZE, 1, SWATCH_WIDTH, 9);

        c = getColorMap().getColorForValue(value);
        g2.setColor(c);
        g2.fillRect(2 * TEXT_MARGIN + STONE_IMG_SIZE + SWATCH_WIDTH, 1, SWATCH_WIDTH, 9);

        g2.setColor(this.getForeground());
        g2.setFont(FONT);
        String inhrtdValText = "inhrtd=" + Util.formatNumber(move.inheritedValue);
        String valText = "val=" + Util.formatNumber(move.value) ;
        String text = "";
        if (node_.pruned) {
            text += " *PRUNED";
        }  else {
            text += " kids="+node_.numDescendants;
            text += " a="+Util.formatNumber(node_.alpha)+" b="+ Util.formatNumber(node_.beta);
        }

        g2.drawString(inhrtdValText, TEXT_MARGIN + STONE_IMG_SIZE + 2, 8);
        g2.drawString(valText, 2 * TEXT_MARGIN + STONE_IMG_SIZE + SWATCH_WIDTH + 2, 8);
        g2.drawString(text, 3 * TEXT_MARGIN + STONE_IMG_SIZE + 2 * SWATCH_WIDTH, 8);


        //super.paint(g);
    }

}

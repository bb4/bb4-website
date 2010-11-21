package com.becker.game.twoplayer.go.ui;

import com.becker.common.ColorMap;
import com.becker.common.util.Util;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.tree.SearchTreeNode;
import com.becker.game.twoplayer.common.ui.gametree.GameTreeCellRenderer;

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

    /** the node we are going to render. */
    private SearchTreeNode node_;

    private static final ColorMap COLORMAP = new GoTreeColorMap();

    /**
     *  Default Constructor.
     */
    GoTreeCellRenderer()
    {
        setColorMap(COLORMAP);
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus1 )
    {
        super.getTreeCellRendererComponent( tree, value, sel,
                expanded, leaf, row, hasFocus1 );

        node_ = (SearchTreeNode) value;

        Color bg = getBGColor( value );

        this.setBackgroundNonSelectionColor( bg );
        this.setBackgroundSelectionColor( Color.orange );

        return this;
    }

    @Override
    protected Color getBGColor( Object value )
    {
        return ROW_BG_COLOR;
    }


    /**
      * Paints the value.  The background is filled based on selected.
      */
    @Override
    public void paint(Graphics g) {
        drawCellBackground(g);

        Graphics2D g2 = (Graphics2D) g;
        TwoPlayerMove move = (TwoPlayerMove) node_.getUserObject();
        if (move == null) {
            return;
        }

        double inheritedValue = move.getInheritedValue();
        double value = move.getValue();
        if (move.isPlayer1())  {
            g2.drawImage(GoStoneRenderer.BLACK_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
        } else {
            g2.drawImage(GoStoneRenderer.WHITE_STONE_IMG.getImage(), 1, 0, STONE_IMG_SIZE, STONE_IMG_SIZE, null);
        }

        Color c = getColorMap().getColorForValue(inheritedValue);

        g2.setColor(c);
        g2.fillRect(TEXT_MARGIN + STONE_IMG_SIZE, 1, SWATCH_WIDTH, 9);
        c = getColorMap().getColorForValue(value);
        g2.setColor(c);
        g2.fillRect(2 * TEXT_MARGIN + STONE_IMG_SIZE + SWATCH_WIDTH, 1, SWATCH_WIDTH, 9);

        g2.setColor(this.getForeground());
        g2.setFont(FONT);
        String inhrtdValText = "inhrtd=" + Util.formatNumber(inheritedValue);
        String valText = "val=" + Util.formatNumber(move.getValue()) ;
        StringBuilder bldr = new StringBuilder();
        if (node_.isPruned()) {
            bldr.append(" *PRUNED");
        }  else {
            int numKids = node_.getChildMoves()==null? 0 : node_.getChildMoves().length;
            bldr.append(" kids=").append(numKids);
            bldr.append(" a=").append(node_.toString());
        }

        g2.drawString(inhrtdValText, TEXT_MARGIN + STONE_IMG_SIZE + 2, 8);
        g2.drawString(valText, 2 * TEXT_MARGIN + STONE_IMG_SIZE + SWATCH_WIDTH + 2, 8);
        g2.drawString(bldr.toString(), 3 * TEXT_MARGIN + STONE_IMG_SIZE + 2 * SWATCH_WIDTH, 8);
    }


    private void drawCellBackground(Graphics g) {
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
    }

}

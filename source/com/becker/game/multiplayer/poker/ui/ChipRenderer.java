// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.multiplayer.poker.ui;

import com.becker.common.geometry.Location;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

import static com.becker.game.multiplayer.poker.ui.PokerPlayerRenderer.FONT_SIZE;
import static com.becker.game.multiplayer.poker.ui.PokerPlayerRenderer.POKER_FONT;

/**
 * Renders a players poker chips
 * @author Barry Becker
 */
public class ChipRenderer {

    private static final Color BLACK_COLOR   = Color.black;

    private static final double CHIP_PILE_WIDTH = 0.9;
    private static final double CHIP_HEIGHT = 0.15;

    /**
     * Constructor
     */
    public ChipRenderer() {}


    /**
     * this draws the players chips at the specified location.
     */
    public void render( Graphics2D g2, Location location, int amount, int cellSize) {

        int[] numChips = PokerChip.getChips(amount);
        int i,x, width, height=0, firstNonZeroPile=0;
        int y = (cellSize * (location.getRow()));
        GameContext.log(3,"chips stacks = "+numChips[1]+" "+numChips[2] + " "+numChips[3] + " "+numChips[4]);
        for (i=0; i<numChips.length; i++) {

            if (numChips[i] > 0) {
                if (firstNonZeroPile == 0) {
                   firstNonZeroPile = i;
                }
                height = (int)(cellSize * numChips[i] * CHIP_HEIGHT);
                width = (int)(CHIP_PILE_WIDTH * cellSize);
                g2.setColor(PokerChip.values()[i].getColor());
                x = (int)(((float)i*CHIP_PILE_WIDTH + location.getCol() +1) * cellSize);
                y = location.getRow() * cellSize - height;
                g2.fillRect(x, y, width, height);
                g2.setColor(BLACK_COLOR);
                g2.drawRect(x, y, width, height);
                for (int j=1; j<numChips[i]; j++) {
                     y = (int)(cellSize * (location.getRow() - j * CHIP_HEIGHT));
                     g2.drawLine(x, y, (int)(x + cellSize*CHIP_PILE_WIDTH), y);
                }
            }

        }
        // amount of cash represented by chips
        g2.setColor(BLACK_COLOR);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(JComponent.getDefaultLocale());
        String cashAmount = currencyFormat.format(amount);
        x = (int)((location.getCol() +1 +firstNonZeroPile*CHIP_PILE_WIDTH) * cellSize);
        Font f = POKER_FONT.deriveFont((float) cellSize /
                 TwoPlayerBoardRenderer.MINIMUM_CELL_SIZE * FONT_SIZE);
        g2.setFont(f);
        g2.drawString(cashAmount, x , (int)(y + height + cellSize/1.2));
    }
}

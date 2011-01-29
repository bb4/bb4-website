package com.becker.game.twoplayer.common.ui;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerController;

import java.text.MessageFormat;

/**
 * Represents the message shown when the game is over.
 *
 * @author Barry Becker
 */
public class GameOverMessage {

    /** Used to get the score values. */
    protected TwoPlayerController controller_ = null;

    /**
     * Constructor.
     */
    public GameOverMessage(TwoPlayerController controller) {

        controller_ = controller;
    }

    /**
     * @return the message to display at the completion of the game.
     */
    public String getText() {

        PlayerList players = controller_.getPlayers();
        String text;

        if ( players.anyPlayerWon())    {
            Player winningPlayer = players.getPlayer1().hasWon() ? players.getPlayer1() : players.getPlayer2();
            Player losingPlayer = players.getPlayer1().hasWon() ? players.getPlayer2() : players.getPlayer1();

            MessageFormat formatter = new MessageFormat(GameContext.getLabel("WON_MSG"));
            Object[] args = new String[5];
            if (players.allPlayersHuman()) {
                args[0] = "";
            } else {
                args[0] = winningPlayer.isHuman() ? GameContext.getLabel("YOU") : GameContext.getLabel("THE_COMPUTER");
            }
            args[1] = winningPlayer.getName();
            args[2] = Integer.toString(controller_.getNumMoves());
            args[3] = Util.formatNumber(controller_.getStrengthOfWin());
            text = formatter.format(args);

            assert(!losingPlayer.hasWon()) : "Both players should not win. Players=" + players;
        }
        else {
            text = GameContext.getLabel("TIE_MSG");
        }
        return text;
    }


    public String toString() {
        return getText();
    }
}
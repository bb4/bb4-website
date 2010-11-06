package com.becker.game.twoplayer.common.ui;

import com.becker.common.util.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.Player;
import com.becker.game.common.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerController;

import java.text.MessageFormat;

/**
 * Represents the message shown when the game is over.
 * Immutable.
 *
 * @author Barry Becker
 */
class GameOverMessage {

    private String text_;

    /**
     * Constructor.
     */
    public GameOverMessage(TwoPlayerController controller) {

        PlayerList players = controller.getPlayers();

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
            args[2] = Integer.toString(controller.getNumMoves());
            args[3] = Util.formatNumber(controller.getStrengthOfWin());
            text_ = formatter.format(args);

            assert(!losingPlayer.hasWon()) : "Both players should not win. Players=" + players;
        }
        else {
            text_ = GameContext.getLabel("TIE_MSG");
        }
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    public String toString() {
       return text_;
    }
}
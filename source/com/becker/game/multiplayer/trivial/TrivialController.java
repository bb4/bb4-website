package com.becker.game.multiplayer.trivial;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.trivial.player.*;

import java.util.*;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Trivial game.
 *
 * @author Barry Becker
 */
public class TrivialController extends MultiGameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    private static final int DEFAULT_NUM_COLS = 32;

    private static final int TRIVIAL_SERVER_PORT = 4447;

 
    /**
     *  Construct the game controller
     */
    public TrivialController()
    {
        super(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS);
    }

    /**
     *  Construct the game controller given an initial board size
     */
    protected Board createTable(int nrows, int ncols )
    {
        return new TrivialTable(nrows, ncols);
    }


    public GameOptions createOptions() {
        return new TrivialOptions();      
    }

    /**
     * by default we start with one human and one robot player.
     */
    protected void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new ArrayList<TrivialPlayer>(2);
             List<TrivialPlayer> pplayers = (List<TrivialPlayer>)players_;

            pplayers.add(TrivialPlayer.createTrivialPlayer("Player 1",
                                  MultiGamePlayer.getNewPlayerColor(pplayers), true));

            pplayers.add(TrivialPlayer.createTrivialPlayer("Player 2",
                                  TrivialPlayer.getNewPlayerColor(pplayers), false));
            players_.get(1).setName(pplayers.get(1).getName()+'('+((TrivialRobotPlayer)players_.get(1)).getType()+')');
        }

        currentPlayerIndex_ = 0;
        ((TrivialTable)board_).initPlayers(players_, this);
    }


    public int getServerPort() {
        return TRIVIAL_SERVER_PORT;
    }

    /**
     * Game is over when only one player has enough money left to play
     *
     * @return true if the game is over.
     */
    public boolean isDone()
    {
      
        int numPlayersStillHidden = 0;
        for (Player p : players_) {
            TrivialPlayer tp  = null;
            if (p.isSurrogate()) {
                tp = (TrivialPlayer)((SurrogateMultiPlayer) p).getPlayer();
            }
            else {
                tp = (TrivialPlayer) p;
            }
            if (!tp.isRevealed())
                numPlayersStillHidden++;
        }
        return (numPlayersStillHidden == 0);
    }


    /**
     * @return the player with the best Trivial hand
     */
    public MultiGamePlayer determineWinner() {
        List<TrivialPlayer> players = (List<TrivialPlayer>)getPlayers();
        TrivialPlayer winner = null;
        int first=0;
        int maxValue = -1;    

        for (int i = 0; i < players.size(); i++) {
            TrivialPlayer p = players.get(i);
            if (p.getValue()  > maxValue) {
                maxValue = p.getValue();
                winner = p;
            }
        }
        return winner;
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    public int advanceToNextPlayerIndex()
    {
        playIndex_++;
        MultiGamePlayer player;
        do {            
            // if the current player has revealed, then advance to the next player.                     
            currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.size();
            player = getPlayer(currentPlayerIndex_);
            if (player.isSurrogate()) {
                player = ((SurrogateMultiPlayer)player).getPlayer();
            }
        }  while (((TrivialPlayer)player).isRevealed());

        return currentPlayerIndex_;
    }

}

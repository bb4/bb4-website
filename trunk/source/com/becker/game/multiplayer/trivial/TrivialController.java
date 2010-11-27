package com.becker.game.multiplayer.trivial;

import com.becker.game.common.board.Board;
import com.becker.game.common.GameOptions;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.MultiGamePlayer;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.trivial.player.TrivialPlayer;
import com.becker.game.multiplayer.trivial.player.TrivialRobotPlayer;

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
    @Override
    protected Board createTable(int nrows, int ncols )
    {
        return new TrivialTable(nrows, ncols);
    }


    @Override
    public GameOptions createOptions() {
        return new TrivialOptions();      
    }

    /**
     * by default we start with one human and one robot player.
     */
    @Override
    protected void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new PlayerList();

            players_.add(TrivialPlayer.createTrivialPlayer("Player 1",
                                  MultiGamePlayer.getNewPlayerColor(players_), true));

            players_.add(TrivialPlayer.createTrivialPlayer("Player 2",
                                  TrivialPlayer.getNewPlayerColor(players_), false));
            players_.get(1).setName(players_.get(1).getName()+'('+((TrivialRobotPlayer)players_.get(1)).getType()+')');
        }

        currentPlayerIndex_ = 0;
        ((TrivialTable)board_).initPlayers(players_, this);
    }


    @Override
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
    @Override
    public MultiGamePlayer determineWinner() {
        PlayerList players = getPlayers();
        TrivialPlayer winner = null;

        int maxValue = -1;

        for (Player player : players) {
            TrivialPlayer p = (TrivialPlayer)player;
            if (p.getValue() > maxValue) {
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
    @Override
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

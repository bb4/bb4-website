package com.becker.game.multiplayer.galactic;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.multiplayer.galactic.ui.GalaxyViewer;
import com.becker.optimization.ParameterArray;

import java.util.*;

/**
 * Defines everything the computer needs to know to play Galactic Empire.
 * @@ todo:
 *  - mousing over menu item should highlight planet in the viewer
 *  - mouse over should still work when entering orders (non-modal?)
 *  - have robot players with different strategy.
 *  - have button to show stats dialog (basically same as tally dialog) on toolbar.
 *  - in addition to order dialog should be able to directly click on source, then dest planet,
 *    then enter number of ships.
 *
 * @@ bugs
 *  - after click on fight, it should change immediately to close
 *  - remove selected source planet from dest list.
 *  - summary dialog should show number of years.
 *
 * at java.lang.Integer.parseInt(Integer.java:468)
	at java.lang.Integer.parseInt(Integer.java:497)
	at com.becker.game.multiplayer.galactic.ui.OrderDialog.getFleetSize(OrderDialog.java:228)
	at com.becker.game.multiplayer.galactic.ui.OrderDialog.getOrder(OrderDialog.java:210)
	at com.becker.game.multiplayer.galactic.ui.OrderDialog.actionPerformed(OrderDialog.java:169)
 *
 * fixed:
 *    don't allow computer or players to send ships to distant planets if they will not arrive before end of the game.
 *    the number of ships available to send does not change when you change the source planet in orders dlg.
 *    after a battle the board does not refresh to show the captured planet.
 *    order dialog should always be in front (modal?)
 *    highlight the planet where a battle is occurring.
 *    window not popping up in the right spot.
 *
 * @author Barry Becker
 */
public class GalacticController extends GameController
{

    private static final int DEFAULT_NUM_ROWS = 16;
    protected static final int DEFAULT_NUM_COLS = 16;

    private static final int DEFAULT_PLANET_PRODUCTION_RATE = 2;
    private static final int DEFAULT_PLANET_FLEET_SIZE = 10;
    private static final int DEFAULT_MAX_YEARS = 10;

    private int planetProductionRate_ = DEFAULT_PLANET_PRODUCTION_RATE;
    private int initialFleetSize_ = DEFAULT_PLANET_FLEET_SIZE;
    private int maxYearsToPlay_ = DEFAULT_MAX_YEARS;
    private boolean neutralsBuild_ = false;

    private int currentPlayerIndex_;

    /**
     *  Construct the Galactic game controller
     */
    public GalacticController()
    {
        board_ = new Galaxy( DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );
        initializeData();
    }

    /**
     *  Construct the Galactic game controller given an initial board size
     */
    public GalacticController(int nrows, int ncols )
    {
        board_ = new Galaxy( nrows, ncols );
        initializeData();
    }


    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        super.reset();
        initializeData();
    }

    protected void initializeData()
    {
        initPlayers();
        ((Galaxy)board_).initPlanets((GalacticPlayer[])players_, this);
    }

     /**
     * by default we start with one human and one robot player.
     */
    private void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new GalacticPlayer[2];
            GalacticPlayer[] gplayers = (GalacticPlayer[])players_;
            Planet homePlanet = new Planet('A', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new Location(5, 5));
            players_[0] = GalacticPlayer.createGalacticPlayer("Admiral 1",
                                      homePlanet, GalacticPlayer.getNewPlayerColor(gplayers), true);
            homePlanet.setOwner((GalacticPlayer)players_[0]);

            homePlanet = new Planet('B', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new Location(10, 10));
            players_[1] = GalacticPlayer.createGalacticPlayer("Admiral 2",
                                      homePlanet, GalacticPlayer.getNewPlayerColor(gplayers), false);
            homePlanet.setOwner((GalacticPlayer)players_[1]);
        }
        currentPlayerIndex_ = 0;
    }

    /**
     *
     * @return the player whos turn it is now.
     */
    public Player getCurrentPlayer()
    {
        return players_[currentPlayerIndex_];
    }

    public void computerMovesFirst()
    {
        GalaxyViewer gviewer  = (GalaxyViewer)this.getViewer();
        gviewer.doComputerMove(getCurrentPlayer());
    }

    /**
     *
     * @return the number of years (turns) remaining in the game.
     */
    public int getNumberOfYearsRemaining() {
        Move m = board_.getLastMove();
        int years = maxYearsToPlay_ - ((m != null)? this.getNumMoves() : 0) - 2;
        return years;
    }
    /**
     *
     * @return true if the game is over.
     */
    public boolean isDone()
    {
        if (board_.getLastMove()==null)
            return false;
        // add one so indexed by 1 instead of 0, add 1 because its the "last" move
        if ((this.getNumMoves() + 2) >= maxYearsToPlay_)
            return true; // done
        if (Galaxy.allPlanetsOwnedByOnePlayer())
            return true;
        return false;
    }


    /**
     * advance to the next player turn in order.
     * @return the index of the next player to play.
     */
    public int advanceToNextPlayer()
    {
        GalaxyViewer gviewer  = (GalaxyViewer)this.getViewer();

        // show message when done.
        if (isDone()) {
            System.out.println( "advanceToNextPlayer done" );
            ((GameBoardViewer)getViewer()).sendGameChangedEvent(null);
            return 0;
        }


        int nextIndex = advanceToNextPlayerIndex();


        if (getCurrentPlayer() == getFirstPlayer()) {

            // @@ I would really like to
            // Precalculate the battle sequence on the server and store it in the move, then send
            // the result in the move to the client.
            // however, there are problems with that, so I just calculate it in the veiwer for now.

            GalacticTurn gmove = gviewer.createMove(board_.getLastMove());
            //gviewer.showMove(gmove);

            // records the result on the board.
            makeMove(gmove);
        }

        if (!getCurrentPlayer().isHuman()) {
            gviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        ((GameBoardViewer)getViewer()).sendGameChangedEvent(null);

        return nextIndex;
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    private int advanceToNextPlayerIndex()
    {
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;
        return currentPlayerIndex_;
    }

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_[0];
    }


    ////////// some Galactic specific methods ///////////////////

    /**
     * @return number of planets in the galaxy
     */
    public int getNumPlanets()
    {
        return Galaxy.getNumPlanets();
    }

    public void setNumPlanets(int numPlanets)
    {
        ((Galaxy)board_).setNumPlanets(numPlanets);
    }

    /**
     * @return base production rate for planets
     */
    public int getPlanetProductionRate()
    {
        return planetProductionRate_;
    }

    public void setPlanetProductionRate( int planetProductionRate )
    {
        this.planetProductionRate_ = planetProductionRate;
    }

    /**
     * @return base fleet size for planets
     */
    public int getInitialFleetSize()
    {
        return initialFleetSize_;
    }

    public void setInitialFleetSize( int initialFleetSize )
    {
        this.initialFleetSize_ = initialFleetSize;
    }

    /**
     * @return upper limit on years (turns) to play.
     * The game could be over sooner if only one player exists at some point.
     */
    public int getMaxYearsToPlay()
    {
        return maxYearsToPlay_;
    }

    public void setMaxYearsToPlay( int maxYearsToPlay )
    {
        maxYearsToPlay_ = maxYearsToPlay;
    }

    /**
     * @return true if neutral planets are allowed to build up their defending fleets
     */
    public boolean getNeutralsBuild()
    {
        return neutralsBuild_;
    }

    public void setNeutralsBuild(boolean build)
    {
        neutralsBuild_ = build;
    }



    /**
     *  Statically evaluate the board position
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        return lastMove.getValue();
    }

    /*
     * generate all possible next moves.
     * impossible for this game.
     */
    public List generateMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        LinkedList moveList = new LinkedList();
        return moveList;
    }

    /**
     * return any moves that result in a win
     */
    public List generateUrgentMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        return null;
    }

    /**
     * @param m
     * @param weights
     * @param player1sPerspective
     * @return true if the last move created a big change in the score
     */
    public boolean inJeopardy( Move m, ParameterArray weights, boolean player1sPerspective )
    {
        return false;
    }



}

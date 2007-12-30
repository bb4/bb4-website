package com.becker.game.common;

import com.becker.game.common.online.*;

import java.util.*;

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for n player games.
 * It is a much more general form of the TwoPlayerController subclass.
 *
 * Instance of this class process requests from the GameViewer.
 *
 *  @author Barry Becker
 */
public abstract class GameController
           implements GameControllerInterface, OnlineChangeListener
{

    /** the board has the layout of the pieces. */
    protected Board board_;

    /** sometimes we want to draw directly to the ui while thinking (for debugging purposes) . */
    protected ViewerCallbackInterface viewer_;


    /** the list of players actively playing the game, in the order that they move. */
    protected List<? extends Player> players_;

    /** collections of game specific options.  They may be modified through the ui (see GameOptionsDialog)*/
    protected GameOptions gameOptions_;

    /**
     * Optional. Only present if we are online
     * this allows us to talk with the game server (if it is available). null if not
     */
    protected ServerConnection serverConnection_;
    
    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    protected static final Random RANDOM = new Random(1);


    /**
     * Construct the game controller
     */
    public GameController()
    {
        GameContext.log( 2, " mem=" + Runtime.getRuntime().freeMemory() );
    }

    /**
     * optionally set a viewer for the controller.
     * @param viewer
     */
    public void setViewer(ViewerCallbackInterface viewer)
    {
       viewer_ = viewer;
    }

    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        board_.reset();
    }

    public int getNumMoves() {
        return board_.getNumMoves();
    }

    /**
     * @return the class which shows the current state of the game board.
     * May be null if the viewer was never set.
     */
    public ViewerCallbackInterface getViewer()
    {
        return viewer_;
    }

    public LinkedList<Move> getMoveList() {
        return board_.getMoveList();
    }

    /**
     * If called before the end of the game it just reutrns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public double getStrengthOfWin()
    {
        return 0.0;
    }


    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return getPlayers().get(0);
    }


    /**
     * @return the board representation object.
     */
    public final Board getBoard()
    {
        return board_;
    }

    /**
     * Setup the initial game state.
     */
    protected abstract void initializeData();


    public void makeMove(Move move) {
        board_.makeMove(move);
    }

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    public Move undoLastMove()
    {
        return board_.undoMove();
    }


    /**
     * clean things up to avoid memory leaks.
     */
    public final void dispose()
    {
        board_.dispose();
    }

    /**
     * save the current state of the game to a file
     * Use this version when an error occurred and you want to dump the state.
     * There is no default implementation (other than to say it is not implemented).
     * You must override if you want it to work.
     * @param fileName the file to save the state to
     * @param rte exception that occurred upon failure
     */
    public void saveToFile( String fileName, AssertionError rte )
    {
        GameContext.log(0,  "Error: saveToFile(name, rte) not implemented yet for " + getClass().getName());
    }

    /**
     * save the current state of the game to a file.
     * You must override if you want it to work.
     */
    public void saveToFile( String fileName )
    {
        GameContext.log(0,  "Error: saveToFile("+fileName+") not implemented yet" );
    }

    /**
     * Restore the current state of the game from a file.
     * @param fileName
     */
    public void restoreFromFile( String fileName)
    {
        GameContext.log(0,  "Error: restoreFromFile("+fileName+") not implemented yet" );
    }


    /**
     *
     * @return a list of the players playing the game (in the order that they move).
     */
    public List<? extends Player> getPlayers()
    {
        return players_;
    }

    /**
     * Maybe use list of players rather than array.
     * @param players the players currently playing the game
     */
    public void setPlayers( List<? extends Player> players )
    {
       players_ = players;
    }

    /**
     * @return  number of active players.
     */
    public int getNumPlayers()
    {
        return players_.size();
    }

    /**
     * @return true if there are only human players
     */
    public boolean allPlayersHuman()
    {
       for (int i = 0; i < getNumPlayers(); i++)  {
           if (!players_.get(i).isHuman()) return false;
       }
       return true;
    }

    /**
     * @return true if there are only computer players
     */
    public boolean allPlayersComputer()
    {
       for (int i=0; i<getNumPlayers(); i++)  {
           if (players_.get(i).isHuman()) return false;
       }
       return true;
    }

    public void setOptions(GameOptions options) {
        gameOptions_ = options;
    }

    public abstract GameOptions getOptions();

    /**
     * You should probably check to see if online play is available before calling this.
     * @return a server connection if it is possible to get one.
     */
    public ServerConnection getServerConnection() {

        if (serverConnection_ == null) {
            serverConnection_ = createServerConnection();
        }
        return serverConnection_;
    }


    /**
     * Most games do not support online play so returning null is the default
     * @return the server connection if one can be created, else null.
     */
    protected ServerConnection createServerConnection() {
        System.out.println("Cannot create a server connection for "+ this.getClass().getName()
                           +". Online play not supported");
        return null;
    }

    public void handleServerUpdate(GameCommand cmd) {
        // @@ need to put something here for.
        //System.out.println("Need controller implmentation for handleServerUpdate");
    }

    /**
     *
     * @return true if online pay is supported, and the server is available.
     */
    public abstract boolean isOnlinePlayAvailable();

    /**
     * If a derived class supports online play it must override this.
     * @return server port number
     */
    public int getServerPort() {
        return -1;
    }

}
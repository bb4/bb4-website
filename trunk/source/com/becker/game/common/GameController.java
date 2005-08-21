package com.becker.game.common;

import com.becker.common.Util;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoStone;
import com.becker.game.twoplayer.go.GoBoardUtil;

import java.util.*;

import ca.dj.jigo.sgf.SGFGame;
import ca.dj.jigo.sgf.SGFTree;
import ca.dj.jigo.sgf.SGFLeaf;
import ca.dj.jigo.sgf.Point;
import ca.dj.jigo.sgf.tokens.*;

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
           implements GameControllerInterface
{

    // these are the default game constants
    // they may be modified through the ui (see GameOptionsDialog)

    // the board has the layout of the pieces
    protected Board board_;

    // sometimes we want to draw directly to the ui while thinking (for debugging purposes)
    protected ViewerCallbackInterface viewer_;


    // the list of players actively playing the game, in the order that they move.
    protected Player[] players_;


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

    public LinkedList getMoveList() {
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
        return getPlayers()[0];
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
        GameContext.log(0,  "Error: saveToFile(name, rte) not implemented yet" );
    }

    /**
     * save the current state of the game to a file.
     * You must override if you want it to work.
     */
    public void saveToFile( String fileName )
    {
        GameContext.log(0,  "Error: saveToFile("+fileName+") not implemented yet" );
    }

    public void restoreFromFile( String fileName)
    {
        GameContext.log(0,  "Error: restoreFromFile("+fileName+") not implemented yet" );
    }


    /**
     * This will retore a game from an SGF structure
     */
    protected void restoreGame( SGFGame game )
    {
        parseSGFGameInfo(game);

        List moveSequence = new LinkedList();
        extractMoveList( game.getTree(), moveSequence );
        GameContext.log( 2, "move sequence= " + moveSequence );
        reset();

        Iterator it = moveSequence.iterator();
        while ( it.hasNext() ) {
            Move m = (Move) it.next();
            makeMove( m );
            //sendGameChangedEvent( m );
            //refresh();
        }
    }

    protected void parseSGFGameInfo( SGFGame game) {
        Enumeration e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken)token;
                //System.out.println("info token size ="+sizeToken.getSize());
                size = sizeToken.getSize();
            }
        }
        getBoard().setSize(size, size);
    }

    /**
     * create a Move from an SGF token.
     */
    protected Move createMoveFromToken( MoveToken token ) {
        assert false : "createMoveFromToken not implemented for "+ getClass().getName();
        return null;
    }

    /**
     * Given an SGFTree and a place to store the moves of a game, this
     * method weeds out all the moves from the given SGFTree into a single
     * Vector of moves.  Variations are discarded.
     *
     * @param tree - The SGFTree containing an SGF variation tree.
     * @param moveList - The place to store the moves for the game's main
     * variation.
     */
    private void extractMoveList( SGFTree tree, List moveList )
    {
        Enumeration trees = tree.getTrees(), leaves = tree.getLeaves(), tokens;

        while ( leaves != null && leaves.hasMoreElements() ) {
            SGFToken token;
            tokens = ((SGFLeaf) leaves.nextElement()).getTokens();

            boolean found = false;

            // While a move token hasn't been found, and there are more tokens to
            // examine ... try and find a move token in this tree's leaves to add
            // to the collection of moves (moveList).
            while ( tokens != null && tokens.hasMoreElements() && !found ) {
                token = (SGFToken) tokens.nextElement();
                found = processToken(token, moveList);
            }
        }
        // If there are variations, use the first variation, which is
        // the entire game, without extraneous variations.
        if ( trees != null && trees.hasMoreElements() )
            extractMoveList( (SGFTree) trees.nextElement(), moveList );
    }


    protected boolean processToken(SGFToken token, List moveList) {

        boolean found = false;
        if (token instanceof MoveToken ) {
            moveList.add( createMoveFromToken( (MoveToken) token ) );
            found = true;
        } else {
            System.out.println("ignoring token "+token.getClass().getName());
        }
        return found;
    }


    /**
     *
     * @return an array of the players playing the game (in the order that they move).
     */
    public Player[] getPlayers()
    {
        return players_;
    }

    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( Player[] players )
    {
       players_ = players;
    }

    /**
     * @return  number of active players.
     */
    public int getNumPlayers()
    {
        return players_.length;
    }

    /**
     * @return true if there are only human players
     */
    public boolean allPlayersHuman()
    {
       for (int i=0; i<getNumPlayers(); i++)  {
           if (!players_[i].isHuman()) return false;
       }
       return true;
    }

    /**
     * @return true if there are only computer players
     */
    public boolean allPlayersComputer()
    {
       for (int i=0; i<getNumPlayers(); i++)  {
           if (players_[i].isHuman()) return false;
       }
       return true;
    }

}
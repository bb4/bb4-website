package com.becker.game.common.persistence;

import ca.dj.jigo.sgf.*;
import ca.dj.jigo.sgf.tokens.*;
import com.becker.game.common.*;

import java.util.*;

/**
 * Import the state of a game from a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public abstract class GameImporter {

    protected GameController controller_;


    public GameImporter(GameController controller) {
        controller_ = controller;
    }

    /**
     * Restore the state of a game from a file.
     * @param fileName to restore from.
     */
    public abstract void restoreFromFile(String fileName);

    /**
     * This will retore a game from an SGF structure to the controller
     */
    protected void restoreGame( SGFGame game )
    {
        parseSGFGameInfo(game);

        List<Move> moveSequence = new LinkedList<Move>();
        extractMoveList( game.getTree(), moveSequence );
        GameContext.log( 1, "move sequence= " + moveSequence );
        controller_.reset();

        Iterator it = moveSequence.iterator();
        while ( it.hasNext() ) {
            Move m = (Move) it.next();
            GameContext.log(1, "now making:"+ m);
            controller_.makeMove( m );
        }
    }

    protected abstract SGFLoader createLoader();

    /**
     * @param game to parse
     */
    protected void parseSGFGameInfo( SGFGame game) {
        Enumeration e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken)token;
                GameContext.log(2, "info token size ="+sizeToken.getSize());
                size = sizeToken.getSize();
            }
        }
        controller_.getBoard().setSize(size, size);
    }

    /**
     * create a Move from an SGF token.
     */
    protected abstract Move createMoveFromToken( SGFToken token );

    /**
     * Given an SGFTree and a place to store the moves of a game, this
     * method weeds out all the moves from the given SGFTree into a single
     * Vector of moves.  Variations are discarded.
     *
     * @param tree - The SGFTree containing an SGF variation tree.
     * @param moveList - The place to store the moves for the game's main
     * variation.
     */
    private void extractMoveList( SGFTree tree, List<Move> moveList )
    {
        Enumeration trees = tree.getTrees();
        Enumeration leaves = tree.getLeaves();
        Enumeration tokens;
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
        if ( trees != null && trees.hasMoreElements() ) {
            extractMoveList( (SGFTree) trees.nextElement(), moveList );
        }
    }

    /**
     * @param token to process
     * @param moveList to add the processed token to
     * @return true if the token is an instance of PlacementToken.
     */
    protected boolean processToken(SGFToken token, List<Move> moveList) {

        boolean found = false;
        if (token instanceof PlacementToken ) {
            Move move = createMoveFromToken( token );
            GameContext.log(2, "creating move="+ move);
            moveList.add( move );
            found = true;
        } else {
            GameContext.log(0, "ignoring token "+token.getClass().getName());
        }
        return found;
    }

}

package com.becker.game.twoplayer.blockade.persistence;

import com.becker.game.common.*;
import ca.dj.jigo.sgf.tokens.*;
import ca.dj.jigo.sgf.*;
import com.becker.game.twoplayer.blockade.*;
import com.becker.game.twoplayer.blockade.persistence.tokens.*;

import com.becker.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.becker.game.twoplayer.common.persistence.tokens.Player1NameToken;
import com.becker.game.twoplayer.common.persistence.tokens.Player2NameToken;
import com.becker.game.twoplayer.common.persistence.tokens.Size2Token;
import java.util.*;

/**
 * Imports the stat of a Go game from a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public class BlockadeGameImporter extends TwoPlayerGameImporter {

    public BlockadeGameImporter(BlockadeController controller) {
        super(controller);
    }

    @Override
     protected SGFLoader createLoader() {
        return new BlockadeSGFLoader();
    }


    /**
     * Initialize the board based on the SGF game.
     */
    @Override
    protected void parseSGFGameInfo( SGFGame game) {

        BlockadeController gc = (BlockadeController) controller_;

        Enumeration e = game.getInfoTokens();
        int numRows = 15; // default unless specified
        int numCols = 12; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof Size2Token) {
                Size2Token sizeToken = (Size2Token)token;
                numRows = sizeToken.getNumRows();
                numCols = sizeToken.getNumColumns();
            }
            else if (token instanceof Player2NameToken) {
                Player2NameToken nameToken = (Player2NameToken) token;
                gc.getPlayer2().setName(nameToken.getName());
            }
            else if (token instanceof Player1NameToken) {
                Player1NameToken nameToken = (Player1NameToken) token;
                gc.getPlayer1().setName(nameToken.getName());
            }
        }
        gc.getBoard().setSize(numRows, numCols);
    }

    /**
     *
     */
    @Override
    protected boolean processToken(SGFToken token, List<Move> moveList) {

        boolean found = false;
        if (token instanceof BlockadeMoveToken ) {
            moveList.add( createMoveFromToken( token ) );
            found = true;
        }
        else if (token instanceof TextToken ) {
            TextToken textToken = (TextToken) token;
            GameContext.log(1, "text="+textToken.getText());
        } else {
            GameContext.log(1, "\nignoring token "+token.getClass().getName());
        }
        return found;
    }


    /**
     * Create a com.becker.game.twoplayer.blockade more from the MoveToken
     */
    @Override
    protected Move createMoveFromToken( SGFToken token)
    {
         BlockadeMoveToken mvToken = (BlockadeMoveToken) token;

         boolean player1 = token instanceof Player1BlockadeMoveToken;
         BlockadeWall wall = null;
         if (mvToken.hasWall())
             wall = new BlockadeWall(new BlockadeBoardPosition(mvToken.getWallPoint1().y, mvToken.getWallPoint1().x),
                                                       new BlockadeBoardPosition(mvToken.getWallPoint2().y, mvToken.getWallPoint2().x));

         BlockadeMove move = BlockadeMove.createMove(mvToken.getFromY(), mvToken.getFromX(),
                                                                                        mvToken.getToY(), mvToken.getToX(),
                                                                                        0, new GamePiece(player1), wall);
         return move;
    }

}

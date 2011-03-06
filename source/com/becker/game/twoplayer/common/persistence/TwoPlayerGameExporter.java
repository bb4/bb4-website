package com.becker.game.twoplayer.common.persistence;

import com.becker.game.common.GameContext;
import com.becker.game.common.IGameController;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.persistence.GameExporter;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerList;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Exports the state of a two player game to a file.
 *
 * @author Barry Becker
 */
public class TwoPlayerGameExporter extends GameExporter {

    protected PlayerList players;
    
    public TwoPlayerGameExporter(IGameController controller) {
        super(controller.getBoard());
        players = controller.getPlayers();
    }

    /**
     * Use this version if you have only the board and not the controller.
     * @param board
     */
    public TwoPlayerGameExporter(TwoPlayerBoard board) {
        super(board);
        players = new PlayerList();
        players.add(new Player("player1", Color.BLACK, false));
        players.add(new Player("player2", Color.WHITE, false));
    }

    /**
     * save the current state of the game to a file in SGF (4) format.
     * SGF stands for Smart Game Format. Its text based but should be xml.
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae ) {

        GameContext.log( 1, "saving state to :" + fileName );
        TwoPlayerBoard b = (TwoPlayerBoard) board_;

        try {
            Writer out = createWriter(fileName);
            // SGF header info
            out.write( "(;\n" );
            out.write( "FF[4]\n" );
            out.write( "GM[1]\n" );
            //out.write( "CA[UTF-8]\n" );
            out.write( "SZ2[" + b.getNumRows() + "][" + b.getNumCols() + "]\n" );
            out.write( "Player1[" + players.getPlayer1().getName() + "]\n" );
            out.write( "Player2[" + players.getPlayer2().getName() + "]\n" );
            out.write( "GN[test1]\n" );

            writeMoves(b.getMoveList(), out);
            writeExceptionIfAny(ae, out);

            out.write( ')' );
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected void writeMoves(MoveList moves, Writer out) throws IOException {
        Iterator<Move> it = moves.iterator();
        GameContext.log( 0, "movelist size= " + moves.size() );
        while ( it.hasNext() ) {
            Move move = it.next();
            out.write( getSgfForMove(move) );
        }
    }

    protected void writeExceptionIfAny(AssertionError ae, Writer out) throws IOException {
        // include error info and stack trace in the comments to help debug
        if ( ae != null ) {
            out.write( "C[" );
            if ( ae.getMessage() != null ) {
                out.write( ae.getMessage() );
                //out would need to be a PrintWriter for this to work
                //rte.printStackTrace(out);
            }
            out.write( "]\n" );
        }
    }

    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(Move move) {
        TwoPlayerMove m = (TwoPlayerMove) move;
        // passes are not represented in SGF - so just skip it if the piece is null.
    
        StringBuilder buf = new StringBuilder("");
        String player = "P2";
        if ( m.isPlayer1() )
        {
            player = "P1";
        }
        buf.append( ';' );
        buf.append( player );
        serializePosition(m.getToLocation(), buf);
        buf.append( '\n' );
        return buf.toString();
    }
}

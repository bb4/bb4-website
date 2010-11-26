package com.becker.game.twoplayer.go.persistence;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.PlayerList;
import com.becker.game.common.persistence.GameExporter;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoOptions;
import com.becker.game.twoplayer.go.board.GoBoard;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Exports the state of a Go game to a file.
 *
 * @author Barry Becker
 */
public class GoGameExporter extends GameExporter {

    public GoGameExporter(GoController controller)
    {
        super(controller);
    }


    /**
     * save the current state of the go game to a file in SGF (4) format
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile( String fileName, AssertionError ae )
    {
        GameContext.log( 1, "saving state to :" + fileName );
        GoController gc = (GoController) controller_;
        GoBoard board = (GoBoard) gc.getBoard();
        PlayerList players = gc.getPlayers();

        try {
            FileWriter out = new FileWriter( fileName );
            //PrintWriter foo;
            // SGF header info
            out.write( "(;\n" );
            out.write( "FF[4]\n" );
            out.write( "GM[1]\n" );
            out.write( "CA[UTF-8]\n" );
            out.write( "ST[2]\n" );
            out.write( "RU[japanese]\n" );
            out.write( "SZ[" + gc.getBoard().getNumRows() + "]\n" );
            out.write( "PB[" + players.getPlayer1().getName() + "]\n" );
            out.write( "PW[" + players.getPlayer2().getName() + "]\n" );
            out.write( "KM[" + ((GoOptions) gc.getOptions()).getKomi() + "]\n" );
            out.write( "PC[US]\n" );
            out.write( "HA[" + board.getHandicap() + "]\n" );
            out.write( "GN[test1]\n" );
            // out.write("PC[US]"); ?? add the handicap stones if present
            Iterator it = gc.getMoveList().iterator();
            GameContext.log( 2, "movelist size= " + gc.getMoveList().size() );
            while ( it.hasNext() ) {
                GoMove move = (GoMove) it.next();
                out.write( getSgfForMove(move) );
            }
            // include error info and stack trace in the comments to help debug
            if ( ae != null ) {
                out.write( "C[" );
                out.write(  board.getGroups().toString()  );
                if ( ae.getMessage() != null ) {
                    out.write( ae.getMessage() );
                    //out would need to be a PrintWriter for this to work
                    //rte.printStackTrace(out);
                }
                out.write( "]\n" );
            }
            out.write( ')' );
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(Move move) {
        GoMove m = (GoMove) move;
        // passes are not represented in SGF - so just skip it if the piece is null.
        if (m.getPiece() == null)
             return "[]";
        StringBuilder buf = new StringBuilder("");
        char player = 'W';
        if ( m.getPiece().isOwnedByPlayer1() ) {
            player = 'B';
        }
        buf.append( ';' );
        buf.append( player );
        buf.append( '[' );
        buf.append( (char) ('a' + m.getToCol() - 1) );
        buf.append( (char) ('a' + m.getToRow() - 1) );
        buf.append( ']' );
        buf.append( '\n' );
        return buf.toString();
    }
}

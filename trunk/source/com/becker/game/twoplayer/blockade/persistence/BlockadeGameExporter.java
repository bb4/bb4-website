package com.becker.game.twoplayer.blockade.persistence;

import com.becker.game.common.*;
import com.becker.game.common.persistence.GameExporter;
import com.becker.game.twoplayer.blockade.*;

import java.io.*;
import java.util.*;

/**
 * Exports the state of a Go game to a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public class BlockadeGameExporter extends GameExporter {

    

    public BlockadeGameExporter(BlockadeController controller)
    {
        super(controller);
    }


    /**
     * save the current state of the blockade game to a file in SGF (4) format.
     * SGF stands for Smart Game Format. Its text based but should be xml.
     * @param fileName name of the file to save the state to
     * @param ae the exception that occurred causing us to want to save state
     */
    public void saveToFile( String fileName, AssertionError ae )
    {
        GameContext.log( 1, "saving state to :" + fileName );
        BlockadeController gc = (BlockadeController) controller_;
        BlockadeBoard board = (BlockadeBoard) gc.getBoard();

        try {
            FileWriter out = new FileWriter( fileName );
            // SGF header info
            out.write( "(;\n" );
            out.write( "FF[4]\n" );
            out.write( "GM[1]\n" );
            //out.write( "CA[UTF-8]\n" );
            out.write( "SZ2[" + gc.getBoard().getNumRows() + "][" + gc.getBoard().getNumCols() + "]\n" );
            out.write( "Player1[" + gc.getPlayer1().getName() + "]\n" );
            out.write( "Player2[" + gc.getPlayer2().getName() + "]\n" );
            out.write( "GN[test1]\n" );

            Iterator it = gc.getMoveList().iterator();
            GameContext.log( 2, "movelist size= " + gc.getMoveList().size() );
            while ( it.hasNext() ) {
                BlockadeMove move = (BlockadeMove) it.next();
                out.write( getSgfForMove(move) );
            }
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
    protected String getSgfForMove(Move move) {
        BlockadeMove m = (BlockadeMove) move;
        // passes are not represented in SGF - so just skip it if the piece is null.
        if (m.getPiece() == null)
             return "[]";
        StringBuffer buf = new StringBuffer("");
        String player = "P2";
        if ( m.getPiece().isOwnedByPlayer1() )
        {
            player = "P1";
        }
        buf.append( ';' );
        buf.append( player );
         buf.append( '[' );
        buf.append( (char) ('a' + m.getFromCol() - 1) );
        buf.append( (char) ('a' + m.getFromRow() - 1) );
        buf.append( ']' );
        buf.append( '[' );
        buf.append( (char) ('a' + m.getToCol() - 1) );
        buf.append( (char) ('a' + m.getToRow() - 1) );
        buf.append( ']' );
        // also print the wall placement if there is one
        if (m.getWall() != null) {
            buf.append("wall");           
            for (BlockadeBoardPosition pos : m.getWall().getPositions()) {               
                serializePosition(pos, buf);
            }
        }
        else {
            buf.append("nowall");
        }
            
        buf.append( '\n' );
        return buf.toString();
    }
    
}

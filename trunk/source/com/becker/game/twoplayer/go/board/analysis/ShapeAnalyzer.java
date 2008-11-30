/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.*;
import com.becker.game.common.BoardPosition;

/**
 * Checks the shape of the string just formed on the board
 * to determine if it is good or bad shape. 
 * 
 * @author Barry Becker
 */
public class ShapeAnalyzer {

    private GoBoard board_;
    
    public ShapeAnalyzer(GoBoard board) {
        board_ =board;
    }
    
    /**
     * @return a number corresponding to the number of clumps of 4 or empty triangles that this stone is connected to.
     * returns 0 if does not form bad shape at all. Large numbers indicate worse shape.
     * Possible bad shapes are :
     *  SHAPE_EMPTY_TRIANGLE :  X -   ,   SHAPE_CLUMP_OF_4 :  X X
     *                          X X                           X X
     */
    public int formsBadShape(GoBoardPosition position)
    {
        GoStone stone = (GoStone)position.getPiece();
        int r = position.getRow();
        int c = position.getCol();

        int severity =
             checkBadShape(stone, r, c,  1,-1, 1) +
             checkBadShape(stone, r, c, -1,-1, 1) +
             checkBadShape(stone, r, c,  1, 1, 1) +
             checkBadShape(stone, r, c, -1, 1, 1) +

             checkBadShape(stone, r, c,  1,-1, 2) +
             checkBadShape(stone, r, c, -1,-1, 2) +
             checkBadShape(stone, r, c,  1, 1, 2) +
             checkBadShape(stone, r, c, -1, 1, 2) +

             checkBadShape(stone, r, c,  1,-1, 3) +
             checkBadShape(stone, r, c, -1,-1, 3) +
             checkBadShape(stone, r, c,  1, 1, 3) +
             checkBadShape(stone, r, c, -1, 1, 3);

        return severity;
    }

    private int checkBadShape(GoStone stone, int r, int c, int incr, int incc, int type) {
        boolean player1 = stone.isOwnedByPlayer1();
        if ( board_.inBounds( r + incr, c + incc ) ) {
            BoardPosition adjacent1 = board_.getPosition( r + incr, c );
            BoardPosition adjacent2 = board_.getPosition( r , c + incc);
            BoardPosition diagonal = board_.getPosition( r + incr, c + incc);
            // there are 3 cases:
            //       a1 diag    X     XX    X
            //        X a2      XX    X    XX
            switch (type) {
                case 1 :
                    if (adjacent1.isOccupied() && adjacent2.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && adjacent2.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(diagonal, player1);
                    }  break;
                case 2 :
                    if (adjacent1.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent1.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(adjacent2, player1);
                    }  break;
                case 3 :
                    if (adjacent2.isOccupied() && diagonal.isOccupied())  {
                        if (   adjacent2.getPiece().isOwnedByPlayer1() == player1
                            && diagonal.getPiece().isOwnedByPlayer1() == player1)
                            return getBadShapeAux(adjacent1, player1);
                    }  break;
               default : assert false;

            }
        }
        return 0;
    }
    
    
    private static int getBadShapeAux( BoardPosition adjacent1, boolean player1 )
    {
        if ( adjacent1.isUnoccupied() || adjacent1.getPiece().isOwnedByPlayer1() == player1 ) {
            return 1;
        }
        return 0;
    }
}

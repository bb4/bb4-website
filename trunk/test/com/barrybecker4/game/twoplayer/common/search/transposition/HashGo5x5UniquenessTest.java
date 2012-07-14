/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;


/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
public class HashGo5x5UniquenessTest extends HashGoBase {

    @Override
    public GoBoard createBoard() {
        return new GoBoard(5, 0);
    }

    /**
     *     12345
     *  01|X XO |     <--- Test if the key is the same wit/without the 1,1 position.
     *  02|  XO |
     *  03| XXXO|
     *  04| O O |
     *  05|    O|
     */
    public void testBoard5x5Uniqueness()  {
          GoMove[] moves1 = {
            new GoMove(new Location(1, 3), 0, black()),
            new GoMove(new Location(1, 4), 0, white()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(3, 2), 0, black()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(3, 5), 0, white()),
            new GoMove(new Location(4, 2), 0, white()),
            new GoMove(new Location(4, 4), 0, white()),
            new GoMove(new Location(5, 5), 0, white()),
            new GoMove(new Location(1, 1), 0, black()) // only one that is different
        };

        GoMove[] moves2 = {
            new GoMove(new Location(1, 3), 0, black()),
            new GoMove(new Location(1, 4), 0, white()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(3, 2), 0, black()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(3, 5), 0, white()),
            new GoMove(new Location(4, 2), 0, white()),
            new GoMove(new Location(4, 4), 0, white()),
            new GoMove(new Location(5, 5), 0, white())
            //new GoMove(new Location(1, 1), 0, black())
        };

        verifyGoBoardsDistinct(moves1, moves2);
    }

    /** The order in which the moves are placed should not matter */
    public void testBoard5x5UniquenessReordered()  {
        GoMove[] moves1 = {
            new GoMove(new Location(1, 1), 0, black()),
            new GoMove(new Location(1, 4), 0, white()),
            new GoMove(new Location(1, 3), 0, black()),
            new GoMove(new Location(3, 5), 0, white()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(3, 2), 0, black()),
            new GoMove(new Location(4, 2), 0, white()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(4, 4), 0, white()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(5, 5), 0, white())
        };

        GoMove[] moves2 = {
            //new GoMove(new Location(1, 1), 0, black()),
            new GoMove(new Location(1, 4), 0, white()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(3, 5), 0, white()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(4, 4), 0, white()),
            new GoMove(new Location(3, 2), 0, black()),
            new GoMove(new Location(5, 5), 0, white()),
            new GoMove(new Location(1, 3), 0, black()),
            new GoMove(new Location(4, 2), 0, white()),
        };

        verifyGoBoardsDistinct(moves1, moves2);
    }




    /**
     *       12345
     *      -------
     *    01|  OX |        < note snapback here
     *    02|  XOO|
     *    03| OXXX|
     *    04| X   |
     *    05| O   |
     */
    public void testBoard5x5Snapback()  {
          GoMove[] moves1 = {
            new GoMove(new Location(1, 3), 0, white()),
            new GoMove(new Location(1, 4), 0, black()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(2, 5), 0, white()),
            new GoMove(new Location(3, 2), 0, white()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(3, 5), 0, black()),
            new GoMove(new Location(4, 2), 0, black()),
            new GoMove(new Location(5, 2), 0, white())
        };

        // now after snapback, we get back to the same position
        // I think when we remove in the capture, we are not updating the hashkey.
        // We need to apply the moves when removing and again when restoring.
        GoMove[] moves2 = {
            new GoMove(new Location(1, 3), 0, white()),
            new GoMove(new Location(1, 4), 0, black()),
            new GoMove(new Location(2, 3), 0, black()),
            new GoMove(new Location(2, 4), 0, white()),
            new GoMove(new Location(2, 5), 0, white()),
            new GoMove(new Location(3, 2), 0, white()),
            new GoMove(new Location(3, 3), 0, black()),
            new GoMove(new Location(3, 4), 0, black()),
            new GoMove(new Location(3, 5), 0, black()),
            new GoMove(new Location(4, 2), 0, black()),
            new GoMove(new Location(5, 2), 0, white()),
                new GoMove(new Location(1, 5), 0, white()),
                new GoMove(new Location(1, 4), 0, black()),
                new GoMove(new Location(2, 4), 0, white()),
                new GoMove(new Location(2, 5), 0, white())

        };

        verifyGoBoardSame(moves1, moves2);
    }

}

/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.blockade;

import com.becker.common.geometry.Location;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.blockade.board.BlockadeBoard;
import com.becker.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.board.move.BlockadeMove;
import com.becker.game.twoplayer.blockade.board.move.BlockadeWall;
import com.becker.game.twoplayer.blockade.board.move.MoveGenerator;

import java.util.List;

/**
 * Test methods on the com.becker.game.twoplayer.blockade controller
 * Created on June 2, 2007, 7:08 AM
 * @author Barry Beckerecker
 */
public class MoveGeneratorTest extends BlockadeTestCase {


    /**
     * Constructor
     */
    public MoveGeneratorTest() {
    }

    /**
     * common initialization for all test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }


    public void  testGetWallsForMove() {
        restore("whitebox/moveList1");

        // List<BlockadeWall> walls = generator.getWallsForMove(move, paths);
        // verify that the list of walls is what we expect.
        //GameContext.log(2, "Walls=" + walls);
    }

    public void testGenerateMoves() {

        restore("whitebox/noMoves2");

        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLastMove();
        MoveGenerator generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights(), board);

        List moves = generator.generateMoves(lastMove);
        int expectedNumMoves = 56;
        assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size() +" moves="+ moves, moves.size() == expectedNumMoves);
    }

    public void testGenerateMoves2() {

        restore("whitebox/noMoves2");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        GamePiece piece1 = new GamePiece(true); // player 1
        GamePiece piece2 = new GamePiece(false);  // player 2
        BlockadeWall wall1 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(8, 10), (BlockadeBoardPosition) board.getPosition(9, 10));
        BlockadeWall wall2 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(12, 6), (BlockadeBoardPosition) board.getPosition(12, 7));

        BlockadeMove move1 = BlockadeMove.createMove(new Location(8, 11), new Location(6, 11),  1 /*0.1*/, piece2, wall2);
        BlockadeMove move2 = BlockadeMove.createMove(new Location(12,6), new Location(10, 6), 1 /*0.1*/, piece1, wall1);

        controller_.makeMove(move1);
        controller_.makeMove(move2);

        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLastMove();
        GameContext.log(2, "lastMove="+lastMove);

        MoveGenerator generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights(), board);
        List moves = generator.generateMoves(lastMove);

        int expectedNumMoves = 60;
        assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size() +" moves="+ moves, moves.size() == expectedNumMoves);
    }

}

package com.becker.game.twoplayer.pente.analysis;

import com.becker.game.common.board.GamePiece;
import com.becker.game.common.GameWeights;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.PenteBoard;
import com.becker.game.twoplayer.pente.PentePatterns;
import com.becker.game.twoplayer.pente.PenteWeights;
import junit.framework.TestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

import java.util.List;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class MoveEvaluatorTest extends TestCase  {

    MoveEvaluator evaluator;
    GameWeights weights;
    TwoPlayerBoard board;
    LineFactoryRecorder lineFactory;

    /**
     * common initialization for all go test cases.
     * Override setOptionOverrides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        board = new PenteBoard();
        weights = new PenteWeights();
        evaluator = new MoveEvaluator(board, new PentePatterns());
        lineFactory = new LineFactoryRecorder();
        evaluator.setLineFactory(lineFactory);
    }

    /**
     * This will fail if exceptions are on when running test.
     */
    //@Test(expected=IllegalArgumentException.class) junit 4 only
    public void testInvalidEvaluation() {

        initRow(board, 1, "___");
        Move lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"______", "______", "_", "______"});
    }

    public void testX__Evaluation() {

        initRow(board, 1, "X__");
        Move lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"X_____", "X_____", "X", "X_____"});
    }

    public void test___X__Evaluation() {

        initRow(board, 1, "___X__");
        Move lastMove = TwoPlayerMove.createMove(1, 4, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"___X_____", "X_____", "___X", "X_____"});
    }

    public void test_XX__Evaluation() {

        initRow(board, 1, "_XX__");
        Move lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX____", "X_____", "_X", "X_____"});
    }

    public void test_XX__2Evaluation() {

        initRow(board, 2, "_XX__");
        Move lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX____", "_X_____", "_X_", "_X_____"});
    }

    public void test_XX_X_X_Evaluation() {

        initRow(board, 1, "_XX_X__X_");
        Move lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__", "X_____", "_X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__X", "X_____", "__X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__X__", "X_____", "____X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"X_X__X_____", "X_____", "_____X", "X_____"});
    }

     public void test_XX_X_X_5Evaluation() {

        initRow(board, 5, "_XX_X__X_");
        Move lastMove = TwoPlayerMove.createMove(5, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__", "____X_____", "_X____", "_X_____"});

        lastMove = TwoPlayerMove.createMove(5, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__X", "____X_____", "__X____", "__X_____"});

        lastMove = TwoPlayerMove.createMove(5, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__X__", "____X_____", "____X____", "____X_____"});

        lastMove = TwoPlayerMove.createMove(5, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"X_X__X_____", "____X_____", "_____X____", "____X_____"});
    }


    public void test_XO_X_X_Evaluation() {

        initRow(board, 1, "_XO_X__X_");
        Move lastMove;

        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(false));
        checkResultLines(lastMove, new String[] {"_XO_X__X", "O_____", "__O", "O_____"});

        // maybe we should check that the moved game piece matches what is on the board.
        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XO_X__X", "O_____", "__O", "O_____"});

        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XO_X__", "X_____", "_X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XO_X__X__", "X_____", "____X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"O_X__X_____", "X_____", "_____X", "X_____"});
    }

    public void test3RowEvaluation() {

        initRow(board, 2, "_OX_X__X_");
        initRow(board, 3, "_XX_X__X_");
        initRow(board, 4, "_XX_X__X_");
        Move lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(false));
        checkResultLines(lastMove, new String[] {"_OX_X__", "_OXX___", "_O_", "_OX____"});

        lastMove = TwoPlayerMove.createMove(3, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, new String[] {"_XX_X__X", "_XXX____", "_XX__", "_OX_____"});
    }


    /**
     *
     * The 4 expected lines are - | / \
     */
    private void checkResultLines(Move lastMove, String[] expectedLines) {
        lineFactory.clearLines();
        evaluator.worth(lastMove, weights.getDefaultWeights());
        List<Line> lines = lineFactory.getCreatedLines();
        TstUtil.printLines(lines);
        assertEquals(expectedLines.length, lines.size());
        int i=0;
        for (String expPattern : expectedLines) {
             assertEquals(expPattern, lines.get(i++).toString());
        }
    }


    private void initRow(TwoPlayerBoard board, int row, String pattern) {
        assert row>0;
        for (int i=0; i<pattern.length(); i++)  {
            char c= pattern.charAt(i);

            if (c == 'X' || c == 'O')  {
                boolean player1 = c == 'X';
                TwoPlayerMove move = TwoPlayerMove.createMove(row, i+1, 0, new GamePiece(player1));
                board.makeMove(move);
            }
        }
    }

    public static Test suite() {
        return new TestSuite(MoveEvaluatorTest.class);
    }
}

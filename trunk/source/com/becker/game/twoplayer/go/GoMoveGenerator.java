package com.becker.game.twoplayer.go;

import com.becker.common.Location;
import com.becker.game.common.Move;
import com.becker.game.common.board.Board;
import com.becker.game.common.board.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.BestMoveFinder;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
public final class GoMoveGenerator {

    private GoSearchable searchable_;


    /**
     * Constructor.
     */
    public GoMoveGenerator(GoSearchable controller) {
        searchable_ = controller;
    }

    /**
     * @return all reasonably good next moves with statically evaluated scores.
     */
    public final MoveList generateEvaluatedMoves(TwoPlayerMove lastMove, ParameterArray weights,
                                                 boolean player1sPerspective ) {
        assert player1sPerspective;
        GoProfiler prof = GoProfiler.getInstance();
        prof.startGenerateMoves();

        GoBoard board = (GoBoard)searchable_.getBoard();
        MoveList moveList = generatePossibleMoves(lastMove, player1sPerspective);

        for (Move move : moveList)  {
            setMoveValue(weights, player1sPerspective, board, (GoMove)move);
        }
        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();
        BestMoveFinder finder = new BestMoveFinder(searchable_.getSearchOptions().getBestMovesSearchOptions());
        moveList = finder.getBestMoves(player1, moveList, player1sPerspective);

        addPassingMoveIfNeeded(lastMove, moveList, player1);

        prof.stopGenerateMoves();
        return moveList;
    }

    /**
     * @return all possible reasonable next moves. We try to limit to reasonable moves as best we can, but that
     * is difficult without static evaluation. At least no illegal moves will be returned.
     */
    public final MoveList generatePossibleMoves(TwoPlayerMove lastMove, boolean player1sPerspective ) {
        assert player1sPerspective;
        GoBoard board = (GoBoard)searchable_.getBoard();
        MoveList moveList = new MoveList();
        int nCols = board.getNumCols();
        int nRows = board.getNumRows();
        assert (nRows == nCols) : " rows and cols must be the same in go";

        CandidateMoveAnalyzer candidateMoves = new CandidateMoveAnalyzer(board);

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();
        int lastMoveValue = (lastMove== null) ? 0 : lastMove.getValue();

        for (int i = 1; i <= nCols; i++ )  {
            for (int j = 1; j <= nRows; j++ )  {
                // if its a candidate move and not an immediate take-back (which would break the rule of ko)
                if ( candidateMoves.isCandidateMove( j, i ) && !isTakeBack( j, i, (GoMove) lastMove, board ) ) {
                    GoMove m = GoMove.createGoMove( new Location(j, i), lastMoveValue, new GoStone(player1) );

                    if ( m.isSuicidal(board) ) {
                        GameContext.log( 2, "The move was a suicide (can't add it to the list): " + m );
                    }
                    else {
                        moveList.add( m );
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * Make the generated move, determine its value, set it into the move, and undo the move on the board.
     */
    private void setMoveValue(ParameterArray weights, boolean player1sPerspective, GoBoard board, GoMove m) {
        GoProfiler prof = GoProfiler.getInstance();
        prof.stopGenerateMoves();
        board.makeMove( m );

        m.setValue(searchable_.worth( m, weights, player1sPerspective ));

        // now revert the board
        board.undoMove();
        prof.startGenerateMoves();
    }

    /**
     * If we are well into the game, include a passing move.
     * if none of the generated moves have an inherited value better than the passing move
     * (which just uses the value of the current move) then we should pass.
     */
    private void addPassingMoveIfNeeded(TwoPlayerMove lastMove, MoveList moveList, boolean player1) {

        Board b = searchable_.getBoard();
        if (searchable_.getNumMoves() > (b.getNumCols() + b.getNumRows()))  {
            moveList.add(moveList.size(), GoMove.createPassMove(lastMove.getValue(), player1));
        }
    }

    /**
     * It is a take-back move if the proposed move position (row,col) would immediately replace the last captured piece
     *  and capture the stone that did the capturing.
     * @return true of this is an immediate take-back (not allowed in go - see "rule of ko")
     */
    public static boolean isTakeBack( int row, int col, GoMove lastMove, GoBoard board ) {
        if ( lastMove == null ) return false;

        CaptureList captures = lastMove.getCaptures();
        if ( captures != null && captures.size() == 1 ) {
            GoBoardPosition capture = (GoBoardPosition) captures.getFirst();
            if ( capture.getRow() == row && capture.getCol() == col ) {
                GoBoardPosition lastStone =
                        (GoBoardPosition) board.getPosition( lastMove.getToRow(), lastMove.getToCol() );
                if ( lastStone.getNumLiberties( board ) == 1 && lastStone.getString().getMembers().size() == 1 ) {
                    GameContext.log( 2, "it is a takeback " );
                    return true;
                }
            }
        }
        return false;
    }
}







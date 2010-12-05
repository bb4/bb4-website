package com.becker.game.twoplayer.go;

import com.becker.common.Location;
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
     * @return all reasonably good next moves.
     */
    public final MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights,
                                        boolean player1sPerspective ) {
        assert player1sPerspective;
        GoProfiler prof = GoProfiler.getInstance();
        prof.startGenerateMoves();
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
                        setMoveValue(weights, player1sPerspective, board, m);
                        moveList.add( m );
                    }
                }
            }
        }
        BestMoveFinder finder = new BestMoveFinder(searchable_.getSearchOptions().getBestMovesSearchOptions());
        moveList = finder.getBestMoves(player1, moveList, player1sPerspective);

        addPassingMoveIfNeeded(lastMove, moveList, player1);

        prof.stopGenerateMoves();
        return moveList;
    }

    /**
     * Make the generated move, determine its value, set it into the move, and undo the move on the baord.
     */
    private void setMoveValue(ParameterArray weights, boolean player1sPerspective, GoBoard board, GoMove m) {
        GoProfiler prof = GoProfiler.getInstance();
        prof.stopGenerateMoves();
        board.makeMove( m );
        prof.startGenerateMoves();

        // this value is not likely to change much except local to last move,
        // anyway we could cache that?
        prof.startCalcWorth();
        m.setValue(searchable_.worth( m, weights, player1sPerspective ));
        prof.stopCalcWorth();

        // now revert the board
        prof.stopGenerateMoves();
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
     * It is a takeback move if the proposed move position (row,col) would immediately replace the last captured piece
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







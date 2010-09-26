package com.becker.game.twoplayer.pente;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.twoplayer.common.BestMoveFinder;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Iterator;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
public final class PenteMoveGenerator {

    PenteController controller_;

    /**
     * Constructor.
     */
    public PenteMoveGenerator(PenteController controller)
    {
        controller_ = controller;
    }

    /**
     * @return all reasonably good next moves.
     */
    public final MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights,
                                        boolean player1sPerspective ) {
        MoveList moveList = new MoveList();

        PenteBoard pb = (PenteBoard) controller_.getBoard();
        pb.determineCandidateMoves();

        boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());

        int ncols = pb.getNumCols();
        int nrows = pb.getNumRows();

        for (int i = 1; i <= ncols; i++ ) {
            for (int j = 1; j <= nrows; j++ ) {
                if ( pb.isCandidateMove( j, i )) {
                    TwoPlayerMove m;
                    if (lastMove == null)
                       m = TwoPlayerMove.createMove( j, i, 0, new GamePiece(player1));
                    else
                       m = TwoPlayerMove.createMove( j, i, lastMove.getValue(), new GamePiece(player1));
                    pb.makeMove( m );
                    m.setValue(controller_.worth( m, weights, player1sPerspective ));
                    // now revert the board
                    pb.undoMove();
                    moveList.add( m );
                }
            }
        }
        BestMoveFinder finder = new BestMoveFinder(controller_.getTwoPlayerOptions().getSearchOptions());
        return finder.getBestMoves( player1, moveList, player1sPerspective );
    }

    /**
     * @return a list of urgent moves (i.e positions that can result in a win for either player.
     */
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective) {
        // no urgent moves at start of game.
        if (lastMove == null)  {
            return new MoveList();
        }
        MoveList allMoves = findMovesForBothPlayers(lastMove, weights, player1sPerspective);

        // now keep only those that result in a win or loss.
        Iterator<Move> it = allMoves.iterator();
        MoveList urgentMoves = new MoveList();
        while ( it.hasNext() ) {
            TwoPlayerMove move = (TwoPlayerMove)it.next();
            // if its not a winning move or we already have it, then skip
            if ( Math.abs(move.getValue()) >= WINNING_VALUE  && !contains(move, urgentMoves) ) {
                move.setUrgent(true);
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    /**
     * Consider both our moves and and opponent moves.
     * @return Set of all next moves.
     */
    private MoveList findMovesForBothPlayers(TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective) {
        MoveList allMoves = new MoveList();
        MoveList moves = generateMoves( lastMove, weights, player1sPerspective );
        allMoves.addAll(moves);

        TwoPlayerMove oppLastMove = lastMove.copy();
        oppLastMove.setPlayer1(!lastMove.isPlayer1());
        MoveList opponentMoves =
                generateMoves( oppLastMove, weights, !player1sPerspective );
        for (Move m : opponentMoves){
            TwoPlayerMove move = (TwoPlayerMove) m;
            move.setPlayer1(!lastMove.isPlayer1());
            move.setPiece(new GamePiece(!lastMove.isPlayer1()));
            allMoves.add(move);
        }

        return allMoves;
    }

    /**
     * @return true of the movelist contains the specified move.
     */
    private boolean contains(TwoPlayerMove move, MoveList moves)
    {
        for (Move m : moves) {
            Location moveLocation = ((TwoPlayerMove)m).getToLocation();
            if (moveLocation.equals(move.getToLocation())) {
                return true;
            }
        }
        return false;
    }
}
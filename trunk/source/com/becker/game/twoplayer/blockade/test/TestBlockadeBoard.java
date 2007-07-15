package com.becker.game.twoplayer.blockade.test;

import com.becker.common.*;
import junit.framework.*;
import com.becker.game.common.*;
import com.becker.game.twoplayer.blockade.*;
import java.util.HashMap;
import java.util.*;

/**
 * @author Barry Becker Date: Mar 3, 2007
 */
public class TestBlockadeBoard extends BlockadeTestCase {  

    public void testPositionStates() {

        BlockadeBoardPosition p = new BlockadeBoardPosition(1, 1);

        Assert.assertTrue("no piece or walls", p.getStateIndex() == 0);
        p.setPiece(new GamePiece(true));

        Assert.assertTrue("p1 piece and no walls", p.getStateIndex() == 1);
        p.setEastWall(new BlockadeWall(true));

        System.out.println(p.getStateIndex());
        Assert.assertTrue("p1 piece and east wall", p.getStateIndex() == 3);

        p.setSouthWall(new BlockadeWall(false));
        Assert.assertTrue("p1 piece and both walls", p.getStateIndex() == 7);

        p.setPiece(new GamePiece(false));
        Assert.assertTrue("p2 piece and both walls", p.getStateIndex() == 11);
    }
    
    /** 
     * Expected results for possible next move list.
     * For certain locations we expect other than the default.
     * We make a hashMap for these special locations
     * map from location to expected number of moves.
     */
    private static final Map<Location, Integer> p1NumMap = new HashMap<Location, Integer>() {
        {
             put(new Location(3, 4), 9);
             put(new Location(3, 4), 9);
             put(new Location(3, 8), 9);
             put(new Location(4, 3), 9);   
             put(new Location(4, 5), 9);
             put(new Location(4, 9), 9);
             put(new Location(5, 4), 9);  
             put(new Location(5, 3), 9);
             put(new Location(5, 7), 6);
             put(new Location(5, 8), 7);
             put(new Location(6, 7), 6);
             put(new Location(6, 8), 6);
             put(new Location(5, 5), 9); 
             put(new Location(7, 3), 9);
             put(new Location(7, 5), 9);
             put(new Location(7, 8), 7);
             put(new Location(7, 9), 9);
             put(new Location(8, 3), 9);
             put(new Location(8, 4), 7);
             put(new Location(8, 5), 7);    
             put(new Location(8, 6), 6);
             put(new Location(8, 7), 7);
             put(new Location(8, 8), 7);
             put(new Location(8, 9), 9);    
             put(new Location(9, 4), 6);
             put(new Location(9, 5), 5);
             put(new Location(9, 6), 6);
             put(new Location(9, 7), 5);
             put(new Location(9, 8), 6);
             put(new Location(10, 3), 8);
             put(new Location(10, 4), 6);
             put(new Location(10, 5), 6);
             put(new Location(10, 7), 7);
             put(new Location(10, 8), 6);
             put(new Location(10, 9), 9);
             put(new Location(10, 10), 7);
             put(new Location(11, 5), 7);
             put(new Location(11, 7), 6);
             put(new Location(11, 8), 6);
             put(new Location(12, 7), 7);
             put(new Location(12, 8), 7);
             put(new Location(13, 10), 6);
         }
    };


    /**
     * Test the list of candidate next moves.
     */
    public void testPossibleMoveList() {
                
         restore("whitebox/moveList1");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();        
       
         // for each position on the board. determine the possible movelist for each player
         int numRows = board.getNumRows();
         int numCols = board.getNumCols();
         for ( int row = 1; row <= numRows; row++ ) {
             for ( int col = 1; col <= numCols; col++ ) {
                  BoardPosition position = board.getPosition(row, col);
                  List list1 = board.getPossibleMoveList( position, false);
                  //List list2 = board.getPossibleMoveList( position, true);                
                  
                  if (position.isOnEdge(board)) {
                     
                      if (position.isInCorner(board)) {
                           // if in corner, we expect 3 moves
                          verifyMoves(position, list1,  3, p1NumMap);                          
                      }                     
                      else if (row == 2 || col == 2 || (row == numRows-1) || (col == numCols-1)) {
                          // if on edge and one space to corner, we expect 4 moves
                          verifyMoves(position, list1, 4, p1NumMap);   
                      }
                      else {
                           // if the pos is at the edge we expect 6 moves
                          verifyMoves(position, list1,  5, p1NumMap);   
                      }                                          
                  }                  
                  else if (row == 2 || col == 2 || (row == numRows-1) || (col == numCols-1)) {
                      if (row == col || (row == 2 && col == numCols-1) || (col == 2 && row == numRows-1)) {
                           // if one space out from corder we expect 6 moves
                          verifyMoves(position, list1, 6, p1NumMap);   
                      } 
                      else {
                           // if the pos is one space from the edge (but not in corner) we expect 7 moves, 
                           verifyMoves(position, list1, 7, p1NumMap);   
                      }
                  } 
                  else {
                      // if the pos is in the middle we expect 8 moves
                      verifyMoves(position, list1, 8, p1NumMap);   
                  }                                  
              }
         }        
    }
    
    /**
     *
     */
    private static final void verifyMoves(BoardPosition position, List player1Moves,  int expectedNumMoves, Map<Location, Integer> p1NumMap) {       
   
        if (p1NumMap.containsKey(position.getLocation())) {
             expectedNumMoves = p1NumMap.get(position.getLocation());
        }
        //if (player1Moves.size() != expectedNumMoves) {
        //    System.out.println("Expected "+expectedNumMoves+" moves for player1, but got "+player1Moves.size() +":" + player1Moves);
        //}
        Assert.assertTrue("Expected "+expectedNumMoves+" moves for player1, but got "+player1Moves.size() +":" + player1Moves, player1Moves.size() == expectedNumMoves);
    }

    
    private static final String p1PathsExpected = 
        "[[Player 2 val:0 inhrtd:0 piece:p2 x(8, 4) (no wall placed) (6, 4)->(8, 4)],[ val:0 inhrtd:0(10, 4) (no wall placed) (8, 4)->(10, 4)],[ val:0 inhrtd:0(11, 5) (no wall placed) (10, 4)->(11, 5)],[ val:0 inhrtd:0(11, 7) (no wall placed) (11, 5)->(11, 7)],[ val:0 inhrtd:0(11, 8) (no wall placed) (11, 7)->(11, 8)]\n"+
        ", [Player 2 val:0 inhrtd:0 piece:p2 x(8, 4) (no wall placed) (6, 4)->(8, 4)],[ val:0 inhrtd:0(10, 4) (no wall placed) (8, 4)->(10, 4)],[ val:0 inhrtd:0(10, 2) (no wall placed) (10, 4)->(10, 2)],[ val:0 inhrtd:0(11, 3) (no wall placed) (10, 2)->(11, 3)],[ val:0 inhrtd:0(11, 4) (no wall placed) (11, 3)->(11, 4)]\n" + 
        ", [Player 2 val:0 inhrtd:0 piece:p2 x(10, 8) (no wall placed) (8, 8)->(10, 8)],[ val:0 inhrtd:0(11, 9) (no wall placed) (10, 8)->(11, 9)],[ val:0 inhrtd:0(11, 8) (no wall placed) (11, 9)->(11, 8)]\n" +
        ", [Player 2 val:0 inhrtd:0 piece:p2 x(10, 8) (no wall placed) (8, 8)->(10, 8)],[ val:0 inhrtd:0(10, 6) (no wall placed) (10, 8)->(10, 6)],[ val:0 inhrtd:0(12, 6) (no wall placed) (10, 6)->(12, 6)],[ val:0 inhrtd:0(13, 5) (no wall placed) (12, 6)->(13, 5)],[ val:0 inhrtd:0(12, 4) (no wall placed) (13, 5)->(12, 4)],[ val:0 inhrtd:0(11, 4) (no wall placed) (12, 4)->(11, 4)]\n" + 
        "]"; 
 
    private static final String p2PathsExpected =
          "[[Player 1 val:0 inhrtd:0 piece:p1 x(6, 3) (no wall placed) (8, 3)->(6, 3)],[ val:0 inhrtd:0(5, 4) (no wall placed) (6, 3)->(5, 4)],[ val:0 inhrtd:0(4, 4) (no wall placed) (5, 4)->(4, 4)]\n"+
          ", [Player 1 val:0 inhrtd:0 piece:p1 x(6, 3) (no wall placed) (8, 3)->(6, 3)],[ val:0 inhrtd:0(5, 4) (no wall placed) (6, 3)->(5, 4)],[ val:0 inhrtd:0(4, 4) (no wall placed) (5, 4)->(4, 4)],[ val:0 inhrtd:0(4, 6) (no wall placed) (4, 4)->(4, 6)],[ val:0 inhrtd:0(4, 8) (no wall placed) (4, 6)->(4, 8)]\n"+
          ", [Player 1 val:0 inhrtd:0 piece:p1 x(8, 9) (no wall placed) (9, 8)->(8, 9)],[ val:0 inhrtd:0(6, 9) (no wall placed) (8, 9)->(6, 9)],[ val:0 inhrtd:0(4, 9) (no wall placed) (6, 9)->(4, 9)],[ val:0 inhrtd:0(4, 8) (no wall placed) (4, 9)->(4, 8)]\n"+
          ", [Player 1 val:0 inhrtd:0 piece:p1 x(7, 8) (no wall placed) (9, 8)->(7, 8)],[ val:0 inhrtd:0(7, 6) (no wall placed) (7, 8)->(7, 6)],[ val:0 inhrtd:0(7, 4) (no wall placed) (7, 6)->(7, 4)],[ val:0 inhrtd:0(5, 4) (no wall placed) (7, 4)->(5, 4)],[ val:0 inhrtd:0(4, 4) (no wall placed) (5, 4)->(4, 4)]\n"+
          "]";

    
    /**
     * Test that we can accurately determine all the opponent shortest paths.
     */  
    public void testFindOpponentShortestPaths() {
         restore("whitebox/shortestPaths1");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
                    
         BlockadeController c = (BlockadeController) controller_;
         List<Path> p1Paths = board.findAllOpponentShortestPaths(true);
         List<Path> p2Paths = board.findAllOpponentShortestPaths(false);
          
         String sP1Paths = p1Paths.toString();
         String sP2Paths =p2Paths.toString();
         
         // verify that the list of walls is what we expect.
         //System.out.println("p1Paths="+sP1Paths.length() +" actual len="+p1PathsExpected.length());
         //System.out.println("p2Paths="+sP2Paths.length() +" actual len="+p2PathsExpected.length());
         
         Assert.assertTrue("Expected \n"+ p1PathsExpected +"\n but got \n" + sP1Paths,  sP1Paths.equals(p1PathsExpected));
         Assert.assertTrue("Expected \n"+ p2PathsExpected +"\n but got \n" + sP2Paths,  sP2Paths.equals(p2PathsExpected));
    }
    
    
    public void testShortestPathLength() {
         restore("whitebox/noMoves2");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
         BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLast(); 
         
         PlayerPathLengths pLengths = board.findPlayerPathLengths( lastMove);
         System.out.println(pLengths);
         Assert.assertTrue("Player Path lengths were not valid - "+ pLengths, pLengths.isValid());
    }
    
     public void testShortedPaths2() {
        
        restore("whitebox/noMoves2");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        
        GamePiece piece1 = new GamePiece(true); // player 1
        GamePiece piece2 = new GamePiece(false);  // player 2
        BlockadeWall wall1 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(8, 10), (BlockadeBoardPosition) board.getPosition(9, 10));
        BlockadeWall wall2 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(12, 6), (BlockadeBoardPosition) board.getPosition(12, 7));
        
        BlockadeMove move1 = BlockadeMove.createMove(8, 11, 6, 11, 0.1, piece2, wall2);
        BlockadeMove move2 = BlockadeMove.createMove(12,6, 10, 6, 0.1, piece1, wall1);
        
        controller_.makeMove(move1);
        controller_.makeMove(move2);
        
         PlayerPathLengths pLengths = board.findPlayerPathLengths(move2);
         System.out.println(pLengths);
         Assert.assertTrue("Player Path lengths were not valid - "+ pLengths, pLengths.isValid());
    }
     
     
     private static final int[] EXPECTED_PATHS_LENGTHS = { 0,  1,  11, 12};
     
     public void testFindShortestPaths() {
         restore("whitebox/shortestPathsCheck");
         BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
         BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLast(); 
         
         BlockadeBoardPosition pos1 = (BlockadeBoardPosition) board.getPosition(2, 2);
         BlockadeBoardPosition pos2 = (BlockadeBoardPosition) board.getPosition(5, 2);
                 
         List<Path> pLengths1 = board.findShortestPaths(pos1);
         List<Path> pLengths2 = board.findShortestPaths(pos2);
         //System.out.println("paths for "+pos1+ " are = "+ pLengths1);
         //System.out.println("pLengths2 = "+ pLengths2);
         
         int size = 4;
         int[] lengths = new int[size];
         Assert.assertTrue(pLengths1.size() == 2);
         Assert.assertTrue(pLengths2.size() == 2);
         
         lengths[0] = pLengths1.get(0).getLength();
         lengths[1] = pLengths1.get(1).getLength();
         lengths[2] = pLengths2.get(0).getLength();
         lengths[3] = pLengths2.get(1).getLength();
         
         for (int i=0; i<size; i++) {
             Assert.assertTrue("Expected len "+  EXPECTED_PATHS_LENGTHS[i] + " but got  "+ lengths[i] ,  lengths[i] == EXPECTED_PATHS_LENGTHS[i]);
         }
    }
}

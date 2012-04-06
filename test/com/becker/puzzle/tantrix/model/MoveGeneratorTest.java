// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import junit.framework.TestCase;
import java.util.List;

import static com.becker.puzzle.tantrix.model.TantrixTstUtil.*;

/**
 * @author Barry Becker
 */
public class MoveGeneratorTest extends TestCase {

    /** instance under test */
    MoveGenerator generator;
    TantrixBoard board;


    public void testMoveGenerationFromTwoOfThreeTilesA() {
        board = place2of3TilesA();
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        System.out.println("moves = " + moves);
        assertEquals("Unexpected number of next moves.", 8, moves.size());
    }

    public void testMoveGenerationFromTwoOfThreeTilesB() {
        board = place2of3TilesB();
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        System.out.println("moves = " + moves);

        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=1, column=0) ANGLE_0,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=2, column=0) ANGLE_60,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=3, column=0) ANGLE_0,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=3, column=1) ANGLE_0]
        assertEquals("Unexpected number of next moves.", 7, moves.size());
    }

    public void testMoveGenerationFromFirstTileOfThree() {
        board = new TantrixBoard(threeTiles);
        generator = new MoveGenerator(board);

        List<TilePlacement> moves = generator.generateMoves();

        System.out.println("moves = " + moves);
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=1, column=2) ANGLE_240,
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=0, column=2) ANGLE_60,
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=0, column=1) ANGLE_0,
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=1, column=0) ANGLE_0,
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=2, column=1) ANGLE_0,
        // tileNum=2 colors: [B, Y, Y, B, R, R] at (row=2, column=2) ANGLE_0,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=1, column=2) ANGLE_0,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=0, column=2) ANGLE_180,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=0, column=1) ANGLE_120,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=1, column=0) ANGLE_0,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=2, column=1) ANGLE_120,
        // tileNum=3 colors: [B, B, R, R, Y, Y] at (row=2, column=2) ANGLE_180]
        assertEquals("Unexpected number of next moves.", 12, moves.size());
    }

}
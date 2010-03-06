package com.becker.puzzle.sudoku;

/**
 * Some sample sudoku test puzzle data
 * @@ make these into separate date files eventually
 *
 * @author Barry Becker Date: Jul 3, 2006
 */
public class Data {

    // simple test of a 9*9 puzzle
    //
    public static final int[][] SAMPLE1 = {
        {0, 0, 9,  0, 0, 0,  0, 0, 7},
        {2, 8, 6,  4, 7, 3,  0, 0, 0},
        {0, 0, 0,  5, 9, 0,  0, 0, 0},

        {0, 2, 1,  0, 8, 0,  0, 5, 6},
        {4, 0, 0,  0, 0, 0,  0, 0, 1},
        {8, 9, 0,  0, 6, 0,  3, 4, 0},

        {0, 0, 0,  0, 5, 2,  0, 0, 0},
        {0, 0, 0,  3, 1, 6,  7, 8, 4},
        {1, 0, 0,  0, 0, 0,  6, 0, 0}
    };

    // simple test of a 9*9 puzzle
    // (inconsistent. use only for testing)
    //
    public static final  int[][] SAMPLE2 = {
        {0, 0, 3,  7, 0, 0,  0, 2, 0},
        {0, 8, 0,  9, 0, 0,  4, 0, 1},
        {0, 9, 0,  0, 2, 1,  0, 6, 3},

        {0, 5, 2,  0, 7, 0,  0, 0, 9},
        {0, 0, 6,  1, 0, 9,  7, 0, 0},
        {8, 0, 0,  0, 6, 0,  3, 1, 0},

        {5, 3, 0,  0, 4, 0,  0, 8, 2},
        {9, 0, 7,  0, 0, 3,  0, 5, 0},
        {4, 0, 0,  7, 0, 5,  1, 0, 0}
    };


    // you should never instantiate this static class.
    private Data() {}
}

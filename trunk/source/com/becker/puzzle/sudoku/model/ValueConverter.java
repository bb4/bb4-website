package com.becker.puzzle.sudoku.model;

/**
 * Helps with showing large numbers on the board. Two digit numbers are converted to letters.
 * @author Barry Becker
 */
public class ValueConverter {

    private ValueConverter() {
    }

    /**
     * Get a one character symbol for the value.
     * @param value
     * @return
     */
    public static String getSymbol(int value) {

        String sValue = "-";
        switch (value) {
            case 10 : sValue = "X"; break;
            case 11 : sValue = "A"; break;
            case 12 : sValue = "B"; break;
            case 13 : sValue = "C"; break;
            case 14 : sValue = "D"; break;
            case 15 : sValue = "E"; break;
            case 16 : sValue = "F"; break;
            case 17 : sValue = "G"; break;
            case 18 : sValue = "H"; break;
            case 19 : sValue = "I"; break;
            case 20 : sValue = "J"; break;
            case 21 : sValue = "K"; break;
            case 22 : sValue = "L"; break;
            case 23 : sValue = "M"; break;
            case 24 : sValue = "N"; break;
            case 25 : sValue = "O"; break;
            default : sValue = Integer.toString(value);
        }
        return sValue;
    }

}

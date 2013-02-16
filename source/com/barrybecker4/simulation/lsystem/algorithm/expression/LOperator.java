// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.algorithm.expression;

import com.barrybecker4.common.expression.Operator;

/**
 * The expected binary operators in the text expression.
 * @author Barry Becker
 */
public enum LOperator implements Operator {

    NONE('.');

    private char symbol;

    LOperator(char c)  {
        symbol = c;
    }

    public char getSymbol() {
        return symbol;
    }

    public double operate(double operand1, double operand2) {
        return 0;
    }

}